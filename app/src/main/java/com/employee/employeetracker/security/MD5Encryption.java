package com.employee.employeetracker.security;

import java.security.MessageDigest;

public class MD5Encryption {
    //create a method to encrypt the data
    public static byte[] encryptData(byte[] data) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        messageDigest.update(data);

        return messageDigest.digest();
    }


}
