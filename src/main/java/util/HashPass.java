package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashPass {
    
    /**
     * Hàm băm mật khẩu bằng thuật toán SHA-256
     * Dùng chung cho Đăng nhập, Đăng ký và Thêm nhân sự.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = String.format("%02x", b);
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi thuật toán mã hóa", e);
        }
    }

    public static void main(String[] args) {
        String pass = "123456";
        System.out.println(HashPass.hashPassword(pass));
    }
}