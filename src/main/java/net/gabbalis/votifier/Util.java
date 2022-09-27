package net.gabbalis.votifier;

public class Util {
    //Stolen from https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexStringBuilder = new StringBuilder();
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int left = (bytes[i]&0xF0) >>> 4;
            int right = (bytes[i]&0x0F);
            hexStringBuilder.append(HEX_DIGITS[left]);
            hexStringBuilder.append(HEX_DIGITS[right]);
        }
        return hexStringBuilder.toString();
    }
}
