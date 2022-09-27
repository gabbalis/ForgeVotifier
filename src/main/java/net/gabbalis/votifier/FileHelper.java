package net.gabbalis.votifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    public static boolean saveFile(Path dirPath, String name, String data){
        try {
            Files.createDirectories(dirPath);
        }
        catch(Exception e){
            LOGGER.error("Failed to create directory: " + dirPath);
        }
        Path filePath = Paths.get(dirPath.toString(), name);
        try {
            String pName = filePath.toString();
            File f = new File(pName);
            f.delete();
            f.createNewFile();
            FileWriter f2 = new FileWriter(pName);
            f2.write(data);
            f2.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            LOGGER.error("An error occurred. " + e.getMessage());
            return false;
        }
        return true;
    }
    public static String loadFile(Path dirPath, String name) throws FileNotFoundException {
        Path filePath = Paths.get(dirPath.toString(), name);
        String data = null;
        try {
            String pName = filePath.toString();
            FileReader f = new FileReader(pName);
            StringBuilder d = new StringBuilder();
            int ch = -1;
            while ((ch = f.read())!= -1) {
                d.append((char)ch);
            }
            data = d.toString();
        } catch (IOException e) {
            LOGGER.error("An error occurred. " + e.getMessage());
            throw new FileNotFoundException(e.getMessage());
        }
        return data;
    }
}
