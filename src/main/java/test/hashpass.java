package test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class hashpass {
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi thuật toán mã hoá!", e);
        }
    }

    public static void main(String[] args) {
        System.out.println(hashpass.hashPassword("123456"));
    }
}
