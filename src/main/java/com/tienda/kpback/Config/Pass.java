package com.tienda.kpback.Config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Pass {
    public static String encrip(String pass) throws NoSuchAlgorithmException{
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(pass.getBytes());
        StringBuffer hexString = new StringBuffer();

        for(byte b : hash){
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
