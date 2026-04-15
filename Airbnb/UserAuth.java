package Airbnb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;


public class UserAuth {
    private static final String USERS_FILE = "users.txt";
    private static String currentUser = null;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println(" Phase 1: User Authentication");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> registerUser(sc);
                case 2 -> loginUser(sc);
                case 3 -> {System.out.println("Exiting, Goodbye"); return; }
                default -> System.out.println("Invalid option.");

            }
        }
    }


    //Registering the user
    private static void registerUser(Scanner sc) {
        try{
            System.out.print("Enter username: ");
            String username = sc.nextLine();
            System.out.println("Enter password: ");
            String password = sc.nextLine();

            String unsername = new String();
            if (userExists(unsername)) {
                System.out.println("Username already exists!");
                return;
            }

            System.out.println("User registered Successfully.");
        } catch (Exception e) {
            System.out.println("Error registration: " + e.getMessage());
        }
    }
    //User login
    private static void loginUser(Scanner sc) {
        try{
            System.out.print("Enter username: ");
            String username = sc.nextLine();
            System.out.print("Enter password: ");
            String password = sc.nextLine();

            String hashedPassword = hashPassword(password);

            try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String [] data = line.split(",");
                    if (data[0].equals(username)&& data [1].equals(hashedPassword)) {
                        currentUser = username;
                        System.out.println("Login successful, Welcome! " +currentUser);
                        return;
                    }
                }
            }
            System.out.println("Invalid username or invalid password.");
        } catch (Exception e) {
            System.out.println("Error logging in: " + e.getMessage());
        }
    }
    // Password hashing
    private static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA - 256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02", b));
        return sb.toString();
    }

    // Check if the user already exists
    private static boolean userExists(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.split(";")[0].equals(username)) return true;
            }
        } catch (IOException ignored) {

        }
        return false;
    }
}
