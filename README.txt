Forge Votifier for 1.18.2

To install, add this mod to your mods folder then run the server.

This will produce a votifier config file: "votifier-common.toml" in your configs folder, as well as a .rsa subfolder in your config folder.
You can configure the command that will be run when players vote in the "votifier-common.toml" config file.

The RSA public key needed to subscribe to voting sites generates in "/config/.rsa/id_rsa.pub"
Your private key is stored in "/config/.rsa/id_rsa" Do not publish this key.

Deleting the contents of .rsa will cause these files to regenerate, at which point you will need to update the voting sites with the new public key.
