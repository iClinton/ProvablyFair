package com.mycompany.mavenproject3;

import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Clinton Bates
 * 
 * This is Provably fair system to show that gambling isn't rigged,
 * This will explain the code: https://dicesites.com/provably-fair
 * This is all I can relase of this code, as rest is under copyright of person who I made it for.
 * The var naming in this project is terrible but it's an old project
 */
public class Prov {

    public static int num;
    public static String cs;
    private static String SHA = "SHA-256";
    private static String Hmac = "HmacSHA512";
    private static String RandomString = "";
    private static String severSeed = null;

    public static void resetSeed() {
        RandomString = randomString(3);
    }

    public Prov() {
    }

    public Prov(String ClientSeed) {
        cs = ClientSeed;
    }

    public static String SeverSeed() {
        return severSeed;
    }

    public static String seed() throws NoSuchAlgorithmException {
        byte[] input = (RandomString).getBytes();
        MessageDigest SMA256 = MessageDigest.getInstance(SHA);
        SMA256.update(input);
        byte[] digest = SMA256.digest();
        StringBuffer hexDigest = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            hexDigest.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }
        severSeed = String.valueOf(hexDigest);
        return severSeed;
    }

    public static String generate(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA);
            md.reset();
            byte[] buffer = input.getBytes("UTF-8");
            md.update(buffer);
            byte[] digest = md.digest();

            String hexStr = "";
            for (int i = 0; i < digest.length; i++) {
                hexStr += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
            }
            return hexStr;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException x) {
            Logger.getLogger(Prov.class.getName()).log(Level.SEVERE, null, x);
        }
        return null;
    }

    public double Roll() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        int nonce = 0;
        String ClientSeed = cs + "-" + nonce;
        String f = calculateHMAC(ClientSeed, seed());
        String upToNCharacters = f.substring(0, 5);
        int foo = Integer.parseInt(String.valueOf(upToNCharacters), 16);
        double doo = foo;
        return doo % (10000) / 100;
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    public static String calculateHMAC(String data, String key)
            throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), Hmac);
        Mac mac = Mac.getInstance(Hmac);
        mac.init(secretKeySpec);
        return toHexString(mac.doFinal(data.getBytes()));
    }

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }
}
