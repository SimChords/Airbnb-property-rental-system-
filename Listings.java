package Airbnb;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Listings {
    private static final String LISTINGS_FILE = "listings.txt";
    private static final String REVIEWS_FILE = "reviews.txt";
    private static Scanner sc = new Scanner(System.in);

    public static void main (String[] args) {
        while (true) {
            System.out.println(" Bookings and Airbnb.Listings");
            System.out.println("1. Add Airbnb.Listings");
            System.out.println("2. View Airbnb.Listings");
            System.out.println("3.Search Airbnb.Listings by Location");
            System.out.println("4. Book Airbnb.Listings");
            System.out.println("5. Delete");
            System.out.println("6. Update");
            System.out.println("7. Add review");
            System.out.println("8. View review");
            System.out.println("9. Exit");
            System.out.println("Select an option: ");

            int choice = sc.nextInt();
            sc.nextLine();

            // Switch cases for available choice selections

            switch (choice) {
                case 1 -> addListings();
                case 2 -> viewListings();
                case 3 -> searchListings();
                case 4 -> bookLisitngs();
                case 5 -> deleteListings();
                case 6 -> updateBooking();
                case 7 -> addReview();
                case 8 -> viewReviews();
                case 9 -> {System.out.println("Exiting, Goodbye."); return;}
                default -> System.out.println("Option not valid...");
            }
        }
    }

    private static void updateBooking() {
        System.out.println("Enter listing ID to update booking: ");
        String listingId = sc.nextLine();

        List<String> lines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(LISTINGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data[0].equals(listingId)) {
                    System.out.println("Current availability: " + data[5]);
                    System.out.print("Set availability (true = available, false = booked): ");
                    String newStatus = sc.nextLine();
                    if (!newStatus.isEmpty()) {
                        data[5] = newStatus;
                        line = String.join(";", data);
                        updated = true;
                    }
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //   Adding listings
    private static void addListings() {
        try {
            System.out.print("Enter host Username: ");
            String host = sc.nextLine();
            System.out.print("Enter lisitng name: ");
            String title = sc.nextLine();
            System.out.print("Enter loacation: ");
            String location = sc.nextLine();
            System.out.print("Enter price per day/ night: ");
            double price = sc.nextDouble();
            sc.nextLine();

            String id = UUID.randomUUID().toString(); // Creates a unique ID
            String line = id + ";" + host + ";" + title + ";" + location + ";" + price + ";" + "true";
            try(PrintWriter out = new PrintWriter(new FileWriter(LISTINGS_FILE, true))) {
                out.println(line);
            }
            System.out.println("Listing has been added successfully. ");
        } catch (Exception e) {
            System.out.println("error adding property: " + e.getMessage());
        }
    }
    private static void viewListings() {
        try (BufferedReader br = new BufferedReader(new FileReader(LISTINGS_FILE))) {
            String line;
            System.out.println("Viewing all property listings.");
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                System.out.println("ID: " + data [0] + " | Host: " + data[1]+
                                "|Title: " + data [2] + "| Location: " + data [3]+
                                "| Price: " + data[4] + "| Available: " + data [5]);
            }
        } catch (IOException e) {
            System.out.println("There are no listings ");
        }
    }
    private static void searchListings() {
        System.out.print("Search by loacation: ");
        String location = sc.nextLine();

        try (BufferedReader br = new BufferedReader(new FileReader(LISTINGS_FILE))) {
            System.out.println(" Finndings ");
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data [3].equalsIgnoreCase(location) && data[5].equals("true")) {
                    System.out.println("ID: " + data [0] + "|Title: " + data[2]+
                                    "|Price: "+ data [4] + "| Available: " + data[5]);
                }
            }
        } catch (IOException e) {
            System.out.println("Trouble searching for listings..");
        }
    }
    private static void bookLisitngs() {
        System.out.print("Enter lisiting ID to book: ");
        String lisitngId = sc.nextLine();

        List<String> lines = new ArrayList<>();
        boolean booked= false;
        try(BufferedReader br = new BufferedReader(new FileReader(LISTINGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data[0].equals(lisitngId) && data[5].equals("true")) {
                    data[5] = "false";
                    booked = true;
                    line = String.join(";", data);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Problem with reading listings. ");
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(LISTINGS_FILE))) {
            for (String l : lines ) pw.println(l);
        } catch (IOException e) {
            System.out.println("Lisitng not found or not available.");
        }
        if (booked) System.out.println("Booking successful.");
        else  System.out.println("Listing is not available.");
    }
    private static void deleteListings() {
        System.out.print("Enter liting ID to delete: ");
        String listingId = sc.nextLine();

        List<String> lines = new ArrayList<>();
        boolean deleted = false;

        try (BufferedReader br = new BufferedReader(new FileReader(LISTINGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data[0]. equals(listingId)) {
                    deleted = true;
                    continue; // Delete if it is found else no action if not found
                }
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("deletion failed!! ");
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(LISTINGS_FILE))) {
            for (String l : lines) pw.println(l);
        }catch (IOException e) {
            System.out.println("Trouble updating file..");
        }
        if (deleted) System.out.println("Deletion succeeded ");
        else System.out.println("Lisitng not found.");
    }
    // Reviews
    private static void addReview() {
        System.out.println("Enter lisitng ID: ");
        String listingId = sc.nextLine();
        System.out.println("Enter your username: ");
        String reviewer = sc.nextLine();
        System.out.println("Review: ");
        String text = sc.nextLine();
        System.out.println(" Rater between (1 -5): ");
        int rating = sc.nextInt();
        sc.nextLine();

        try (PrintWriter out = new PrintWriter(new FileWriter(REVIEWS_FILE, true))) {
            out.println(listingId + ";" + reviewer + ";" + text + ";" + rating);
        } catch (IOException e) {
            System.out.println("Filed to save review. ");
        }
        System.out.println("Review added, thank you for your review.");
    }
    private static void viewReviews() {
        System.out.println("Enter lisiting ID: ");
        String listingID = sc.nextLine();

        int total = 0, count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(REVIEWS_FILE))) {
            String line;
            System.out.println(" Check on reviews " + listingID + "--");
            while ((line = br.readLine()) !=null) {
                String [] data = line.split(";");
                if (data[0].equals(listingID)) {
                    System.out.println(data[1] + ": " + data [2] + "( Rating: " + data [3] + ")");
                    total += Integer.parseInt(data[3]);
                    count++;
                }
            }
        }catch (IOException e) {
            System.out.println("No reviews.");
        }
        if (count > 0) {
            double avg = (double) total / (count);
            System.out.println("Average rating: " + avg);
        }else {
            System.out.println("No reviews at the moment.");
        }
    }

}

