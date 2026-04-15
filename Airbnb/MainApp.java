package Airbnb;

import java.util.Scanner;

public class MainApp {
    private static Scanner sc = new Scanner(System.in);
    private static MainApp Users;

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n...Airbnb System...");
            System.out.println("1.User Management");
            System.out.println("2.Listings Management");
            System.out.println("3. Exit");
            System.out.println("Choose");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> Users.main(null);
                case 2 -> Listings.main(null);
                case 3 -> {
                    System.out.println("Goodbye");
                    return;
                }
                default -> System.out.println("Choice not valid.");
            }
        }
    }
}