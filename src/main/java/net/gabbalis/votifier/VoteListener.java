package net.gabbalis.votifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.common.MinecraftForge;

import java.security.PrivateKey;
import java.time.Duration;
import java.io.*;
import java.lang.Thread;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.concurrent.*;

public class VoteListener extends Thread{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Config CONFIG = new Config();
    private static final RSAHelper rsa = new RSAHelper();
    private ServerSocket server = null;
    private volatile boolean stopping = false;

    public void run(){
        while(stopping == false) {
            try {
                LOGGER.info("Starting Vote Listener Thread...");
                server = new ServerSocket(CONFIG.getPort());
                while (stopping == false) {
                    AcceptAndHandleConnection(server);
                }
            } catch (java.io.IOException e) {
                LOGGER.info("VoteListener Thread has crashed with " + e.getMessage());
                if (server != null) {
                    try {
                        server.close();
                    }
                    catch (java.io.IOException e2){
                        LOGGER.info("Error Caught while attempting to close crashed ServerSocket: " + e.getMessage());
                    }
                }
            }
        }
        LOGGER.info("Votifier thread terminated.");
    }
    public void AcceptAndHandleConnection(ServerSocket socket){
        try {
            Socket s = socket.accept();
            InputStream in = s.getInputStream();
            OutputStream out = s.getOutputStream();
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(out));

            //Send version string.
            w.write("VOTIFIER");
            w.newLine();
            w.flush();

            LOGGER.info("Recieved Data From Vote Listener:");

            ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
            Duration timeout = Duration.ofSeconds(5);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            byte[] buffer = new byte[4096];
            Future<byte[]> fData = executor.submit(new Callable() {
                @Override
                public byte[] call() throws Exception {
                    int num;
                    while ((num = in.read(buffer, 0, buffer.length))!=-1) {
                        outBytes.write(buffer, 0, num);
                    }
                    return outBytes.toByteArray();
                }
            });
            byte[] bData;
            try {
                bData = fData.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                fData.cancel(true);
                bData = outBytes.toByteArray();
            }

            PrivateKey key = rsa.getPrivateKey();

            byte[] oData = rsa.decrypt(bData, key);
            if (oData != null){
                processVoteData(oData);
            }
            else{
                LOGGER.error("Decription Failed.");
                if (Config.logKeysInsecurelyOnFailure() == true) {
                    LOGGER.error("Public Key in use, converted to Base64 format: " +
                            Base64.getEncoder().encodeToString(rsa.getPublicKey().getEncoded()));
                    LOGGER.error("Private Key in use, converted to Base64 format: " +
                            Base64.getEncoder().encodeToString(rsa.getPrivateKey().getEncoded()));
                    LOGGER.error("Raw encrypted Vote packet data converted to HEX format: " + Util.bytesToHex(bData));
                }
            }
            LOGGER.info("Closing socket");

            s.close();


        }
        catch(java.io.IOException e){
            LOGGER.info("VoteListener Socket has crashed with " + e.getMessage());
        }
    };
    public void processVoteData(byte[] data){
        StringBuilder b = new StringBuilder();
        for (int i=0; i<data.length; i++){
            b.append((char)data[i]);
        }
        String dataStr = b.toString();
        LOGGER.info("Processing Vote Data:");
//Format:
//        VOTE
//        votifier-tester
//        gabbalis
//        127.0.0.1
//        Thu Sep 08 2022 07:12:57 GMT-0400 (Eastern Daylight Time)
        //Split into lines
        String[] lines = dataStr.split("\n");

        if (lines.length<5){
            LOGGER.error("Vote packet recieved: but contained less than 5 lines.");
            LOGGER.error(lines.length);
            LOGGER.error(dataStr);
            return;
        }
        if (!lines[0].equals("VOTE")){
            LOGGER.warn("Warning: Line 1 in voting packet was not 'VOTE'");
            LOGGER.warn("It was instead: " + lines[0]);
        }
        String site = lines[1];
        String user = lines[2];
        String sourceIp = lines[3];
        String dateString = lines [4];
        LOGGER.info(user + " has voted using the site: " + site);
        PlayerList players = ServerLifecycleHooks.getCurrentServer().getPlayerList();
        ServerPlayer player = players.getPlayerByName(user);
        if (player != null) {
            LOGGER.info("Player found, issuing reward event.");
            VoteEvent e = new VoteEvent(site, sourceIp, player, dateString);
            MinecraftForge.EVENT_BUS.post(e);
        }
        else{
            LOGGER.error("Player: " + user + ", could not be found. Reward not distributed.");
        }

    }
    public void shutdown(){

        LOGGER.info("Shutting down Votifier thread...");
        if (server != null){
            stopping = true;
            try {
                server.close();
            } catch (IOException e) {
                LOGGER.error("Failed to close Votifier socket: " + e.getMessage());
            }
        }
    }
}
