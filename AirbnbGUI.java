package Airbnb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AirbnbGUI extends Component {
    private static final String USERS_FILE = "users.txt";
    private static final String LISTINGS_FILE = "listings.txt";
    private static final String TRANSACTIONS_FILE = "transactions.txt";
    private static final String MESSAGES_FILE = "messages.txt";

    private String currentUser = null;
    private String currentRole = null;

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String listingId;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AirbnbGUI::new);
    }

    public AirbnbGUI() {
        frame = new JFrame("Airbnb System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(loginPanel(), "login");
        mainPanel.add(registerPanel(), "register");
        mainPanel.add(hostPanel(), "host");
        mainPanel.add(guestPanel(), "guest");
        mainPanel.add(adminPanel(), "admin");
        mainPanel.add(messagePanel(), "messages");

        frame.add(mainPanel);
        frame.setVisible(true);
        cardLayout.show(mainPanel, "login");
    }

    // LOGIN
    private JPanel loginPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2));
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(loginBtn);
        panel.add(registerBtn);

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String hash = hashPassword(password);

            String role = authenticateUser(username, hash);
            if (role == null) {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.");
                return;
            }

            currentUser = username;
            currentRole = role;
            JOptionPane.showMessageDialog(frame, "Logged in as " + role);

            switch (role) {
                case "host" -> cardLayout.show(mainPanel, "host");
                case "guest" -> cardLayout.show(mainPanel, "guest");
                case "admin" -> cardLayout.show(mainPanel, "admin");
            }
        });

        registerBtn.addActionListener(e -> cardLayout.show(mainPanel, "register"));
        return panel;
    }

    // REGISTER
    private JPanel registerPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2));
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        String[] roles = {"guest", "host"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        JButton regBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(new JLabel("Role:"));
        panel.add(roleBox);
        panel.add(regBtn);
        panel.add(backBtn);

        regBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String role = (String) roleBox.getSelectedItem();
            String hashed = hashPassword(password);

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Enter valid username/password.");
                return;
            }

            if (userExists(username)) {
                JOptionPane.showMessageDialog(frame, "Username already exists!");
                return;
            }

            saveUser(username, hashPassword(password), role);
            JOptionPane.showMessageDialog(frame, "Registered successfully as " + role);
            cardLayout.show(mainPanel, "login");
        });

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        return panel;
    }
    private String formaListing(String line) {
        String[] parts = line.split(";");
        if (parts.length <4) return line;
        return "ID: " + parts[0] +
                " | Host: " + parts[1] +
                " | Title: " + parts[2] +
                " | Price: R" + parts[3];
    }

    private boolean userExists(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) return true;
            }
        } catch (IOException ignored) {}
        return false;
    }

    private void saveUser(String username, String hash, String role) {
        try (PrintWriter out = new PrintWriter(new FileWriter(USERS_FILE, true))) {
            out.println(username + "," + hash + "," + role);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String authenticateUser(String username, String hash) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equals(username) && parts[1].equals(hash))
                    return parts[2];
            }
        } catch (IOException ignored) {}
        return null;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // HOST PANEL
    private JPanel hostPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea listingArea = new JTextArea();
        listingArea.setEditable(false);

        JTextField titleField = new JTextField();
        JTextField priceField = new JTextField();
        JButton addBtn = new JButton("Add Listing");
        JButton viewBookingsBtn = new JButton("View Bookings");
        JButton updateBookingBtn = new JButton("Update Booking");
        JButton deleteBookingBtn = new JButton("Delete Booking");
        JButton logoutBtn = new JButton("Logout");
        JButton messagesBtn = new JButton("Messages");


        JPanel topPanel = new JPanel(new GridLayout(2, 4));
        topPanel.add(new JLabel("Title:"));
        topPanel.add(titleField);
        topPanel.add(new JLabel("Price:"));
        topPanel.add(priceField);
        topPanel.add(addBtn);
        topPanel.add(viewBookingsBtn);
        topPanel.add(updateBookingBtn);
        topPanel.add(deleteBookingBtn);
        topPanel.add(messagesBtn);
        topPanel.add(logoutBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(listingArea), BorderLayout.CENTER);

        addBtn.addActionListener(e -> {
            saveListing(currentUser, titleField.getText(), priceField.getText());
            titleField.setText("");
            priceField.setText("");
            refreshListingsHost(listingArea);
        });

        viewBookingsBtn.addActionListener(e -> showBookings());
        updateBookingBtn.addActionListener(e -> updateBooking());
        deleteBookingBtn.addActionListener(e -> deleteBooking());
        messagesBtn.addActionListener(e -> cardLayout.show(mainPanel, "messages"));
        logoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        refreshListingsHost(listingArea);
        return panel;
    }

    private void refreshListingsHost(JTextArea area) {
        List<String> listings = loadListings().stream()
                .filter(line -> line.split(";")[1].equals(currentUser))
                .map(this::formaListing)
                .collect(Collectors.toList());
        area.setText(String.join("\n", listings));
    }

    private void showBookings() {
        JTextArea area = new JTextArea(2, 40);
        area.setEditable(false);
        try (BufferedReader bre = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            String line;
            while ((line = bre.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 3) {
                    area.append("Listing ID: " + parts[0] +
                            " | Guest: " + parts [1] +
                            " | Date: " + parts[2] + "\n");
                } else {
                    System.out.println("Skipped malformed tramsaction: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Bookings", JOptionPane.INFORMATION_MESSAGE);
    }
    // Update booking
    private void updateBooking() {
        String listingId = JOptionPane.showInputDialog(frame, "Enter Listing ID to update booking: ");
        if (listingId == null || listingId.isEmpty()) return;

        String newStatus = JOptionPane.showInputDialog(frame, "Enter new status (true = available, false = booked):");
        if (newStatus == null || (!newStatus.equals("true") && !newStatus.equals("false"))) {
            JOptionPane.showMessageDialog(frame, "Invalid status.");
            return;
        }
        List<String> lines = new ArrayList<>();
        boolean updated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(LISTINGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data[0].equals(listingId)) {
                    data[5] = newStatus;
                    line = String.join(";", data);
                    updated = true;
                    logTransaction("UPDATE_Booking", listingId, data[1]);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter pw =new PrintWriter(new FileWriter(LISTINGS_FILE))) {
            for (String l : lines) pw.println(l);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(updated) JOptionPane.showMessageDialog(frame, "Booking updated successfully.");
        else JOptionPane.showMessageDialog(frame, "Booking not found");

        refreshListingsHost(new JTextArea());
    }

    // delete booking
    private void deleteBooking() {
        String listingId = JOptionPane.showInputDialog(frame, "Enter Listing ID to delete booking: ");
        if (listingId == null || listingId.isEmpty()) return;

        List<String> lines = new ArrayList<>();
        boolean deleted = false;

        try (BufferedReader br = new BufferedReader(new FileReader(LISTINGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data  = line.split(";");
                if (data[0].equals(listingId) && data[5].equals("false")) {
                    data[5] = "true";
                    line = String.join(";", data);
                    deleted = true;
                    logTransaction("Delete Booking", listingId, data[1]);
                }
                lines.add(line);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter pw = new PrintWriter(new FileWriter(LISTINGS_FILE))) {
            for (String l : lines) pw.println(l);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (deleted) JOptionPane.showMessageDialog(frame, "Booking deleted");
        else JOptionPane.showMessageDialog(frame, "Booking not found");

        refreshListingsHost(new JTextArea());
    }

    private void logTransaction(String action, String listingId, String user) {
        try (PrintWriter out = new PrintWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
            out.println(action + ";" + listingId + ";" + user + ";" + System.currentTimeMillis());
        } catch (IOException e) {
            System.out.println("Error saving transaction log.");
        }

    }


    // GUEST PANEL
    private JPanel guestPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel();
        JTextField searchField = new JTextField(10);
        JTextField dateField = new JTextField(10);
        JButton searchBtn = new JButton("Search");
        JButton bookBtn = new JButton("Book by ID");
        JButton logoutBtn = new JButton("Logout");
        JButton messagesBtn = new JButton("Messages");

        top.add(new JLabel("Search by location:"));
        top.add(searchField);
        top.add(new JLabel("Date (YYYY-MM-DD):"));
        top.add(dateField);
        top.add(searchBtn);
        top.add(bookBtn);
        top.add(messagesBtn);
        top.add(logoutBtn);

        JTextArea resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        final String[] selectedListing = {null};

        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            String date = dateField.getText().trim();
            List<String> matches = loadListings().stream()
                    .filter(line -> line.toLowerCase().contains(keyword))
                    .filter(line -> isAvailable(line, date))
                    .map(this::formaListing)
                    .collect(Collectors.toList());
            resultsArea.setText(String.join("\n", matches));
        });

        resultsArea.addCaretListener(e -> {
            String sel = resultsArea.getSelectedText();
            if (sel != null && !sel.isEmpty()) selectedListing[0] = sel.split(";")[0];
        });

        bookBtn.addActionListener(e -> {
            String id = JOptionPane.showInputDialog(frame, "Enter Listing ID to book: ");
            if(id == null || id.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Select a listing first!");
                return;
            }
            String date = dateField.getText().trim();
            if (bookListing(selectedListing[0], currentUser, date))
                JOptionPane.showMessageDialog(frame, "Booking successful!");
            else
             JOptionPane.showMessageDialog(frame, "Booking failed.");
        });

        messagesBtn.addActionListener(e -> cardLayout.show(mainPanel, "messages"));
        logoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultsArea), BorderLayout.CENTER);
        return panel;
    }

    // ADMIN PANEL
    private JPanel adminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea display = new JTextArea();
        display.setEditable(false);

        JButton usersBtn = new JButton("View Users");
        JButton listingsBtn = new JButton("View Listings");
        JButton transactionsBtn = new JButton("View Transactions");
        JButton logoutBtn = new JButton("Logout");

        JPanel top = new JPanel();
        top.add(usersBtn);
        top.add(listingsBtn);
        top.add(transactionsBtn);
        top.add(logoutBtn);

        usersBtn.addActionListener(e -> display.setText(loadFile(USERS_FILE)));
        listingsBtn.addActionListener(e -> {
            List<String> listings = loadListings().stream()
                    .map(this::formaListing)
                    .collect(Collectors.toList());
            display.setText(String.join("\n", listings));
        });
        transactionsBtn.addActionListener(e -> display.setText(loadFile(TRANSACTIONS_FILE)));
        logoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "login"));

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(display), BorderLayout.CENTER);
        return panel;
    }

    // MESSAGING PANEL
    private JPanel messagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JTextField toField = new JTextField(10);
        JTextField msgField = new JTextField(20);
        JButton sendBtn = new JButton("Send");
        JButton backBtn = new JButton("Back");

        JPanel top = new JPanel();
        top.add(new JLabel("To:"));
        top.add(toField);
        top.add(new JLabel("Message:"));
        top.add(msgField);
        top.add(sendBtn);
        top.add(backBtn);

        sendBtn.addActionListener(e -> {
            String to = toField.getText().trim();
            String msg = msgField.getText().trim();
            if (to.isEmpty() || msg.isEmpty()) return;

            appendToFile(MESSAGES_FILE, currentUser + ";" + to + ";" + msg);
            msgField.setText("");
            chatArea.setText(loadMessages(currentUser));
        });

        backBtn.addActionListener(e -> {
            if ("host".equals(currentRole)) cardLayout.show(mainPanel, "host");
            else if ("guest".equals(currentRole)) cardLayout.show(mainPanel, "guest");
            else cardLayout.show(mainPanel, "admin");
        });

        chatArea.setText(loadMessages(currentUser));

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        return panel;
    }

    private String loadMessages(String user) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(MESSAGES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].equals(user) || parts[1].equals(user)) {
                    sb.append(parts[0]).append(" -> ").append(parts[1]).append(": ").append(parts[2]).append("\n");
                }
            }
        } catch (IOException ignored) {}
        return sb.toString();
    }

    //FILE UTILITIES
    private void saveListing(String host, String title, String price) {
        appendToFile(LISTINGS_FILE, UUID.randomUUID() + ";" + host + ";" + title + ";" + price);
    }

    private List<String> loadListings() {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(LISTINGS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) list.add(line);
        } catch (IOException ignored) {}
        return list;
    }

    private void appendToFile(String file, String content) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            pw.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String loadFile(String file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
        } catch (IOException ignored) {}
        return sb.toString();
    }

    private boolean isAvailable(String listingLine, String date) {
        String listingId = listingLine.split(";")[0];
        try (BufferedReader br = new BufferedReader(new FileReader(TRANSACTIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(";");
                if (parts.length < 3 ) continue;
                if (parts[0].equals(listingId) && parts[2].equals(date)) return false;
            }
        } catch (IOException ignored) {}
        return true;
    }

    private boolean bookListing(String listingId, String guest, String date) {
        try {

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE, true))) {
                bw.write(listingId + ";" + guest + ";" + date);
                bw.newLine();
            }
            return  true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
