package org.example.ui;

import org.example.dao.UserDao;
import java.util.Scanner;

public class Authentication {
    private final UserDao userDao;
    private final Scanner sc;
    private final Dashboard dashboard;

    public Authentication(UserDao userDao, Scanner sc) {
        this.userDao = userDao;
        this.sc = sc;
        this.dashboard = new Dashboard(sc);
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n|============================================================|");
            System.out.println("|               SCHOOL SUPPLIES INVENTORY SYSTEM             |");
            System.out.println("|============================================================|");
            System.out.println("|1. Register                                                 |");
            System.out.println("|2. Login                                                    |");
            System.out.println("|3. Exit                                                     |");
            System.out.println("|============================================================|");
            System.out.print("Choose an Option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    // 1. Validate Fullname
                    String name;
                    while (true) {
                        System.out.print("Enter your Fullname: ");
                        name = sc.nextLine().trim();
                        if (name.equals("0")) {
                            System.out.println("Registration cancelled.");
                            break;
                        }
                        if (name.isEmpty() || name.length() < 2) {
                            System.out.println("Error: Please enter a valid Fullname.");
                            continue;
                        }
                        break;
                    }
                    if (name.equals("0")) break;

                    // 2. Validate Username
                    String user;
                    while (true) {
                        System.out.print("Enter new Username: ");
                        user = sc.nextLine().trim();
                        if (user.equals("0")) {
                            System.out.println("Registration cancelled.");
                            break;
                        }
                        if (user.isEmpty() || user.length() < 3) {
                            System.out.println("Error: Username must be at least 3 characters long.");
                            continue;
                        }
                        break;
                    }
                    if (user.equals("0")) break;

                    // 3. Validate Password
                    String pass;
                    while (true) {
                        System.out.print("Enter Password (or type '0' to cancel): ");
                        pass = sc.nextLine().trim();
                        if (pass.equals("0")) {
                            System.out.println("Registration cancelled.");
                            break;
                        }
                        if (pass.isEmpty() || pass.length() < 4) {
                            System.out.println("Error: Password must be at least 4 characters long.");
                            continue;
                        }
                        break;
                    }
                    if (pass.equals("0")) break;

                    // 4. Validate Role Selection (Loops until 1, 2, or 3 is selected)
                    String role = null;
                    while (true) {
                        System.out.println("\nRole: ");
                        System.out.println(" 1. Admin");
                        System.out.println(" 2. Staff/Cashier");
                        System.out.println(" 3. Inventory Clerk");
                        System.out.print("Enter choice (1-3) (or type '0' to cancel): ");

                        String roleInput = sc.nextLine().trim();
                        if (roleInput.equals("0")) {
                            System.out.println("Registration cancelled.");
                            break;
                        }

                        int roleChoice = 0;
                        try {
                            roleChoice = Integer.parseInt(roleInput);
                        } catch (NumberFormatException e) {
                            roleChoice = 0;
                        }

                        switch (roleChoice) {
                            case 1: role = "Admin"; break;
                            case 2: role = "Staff/Cashier"; break;
                            case 3: role = "Inventory Clerk"; break;
                            default: role = null;
                        }

                        if (role != null) {
                            break; // Husto ang pili, mogawas sa loop
                        } else {
                            System.out.println("Invalid role selection! Please choose between 1 to 3.");
                        }
                    }

                    if (role != null) {
                        userDao.registerUser(name, user, pass, role);
                        System.out.println("Success: Registered successfully!");
                    }
                    break;

                case "2":
                    System.out.print("Enter Username: ");
                    String loginUser = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String loginPass = sc.nextLine();

                    String userRole = userDao.loginUser(loginUser, loginPass);

                    if (userRole != null) {
                        clearScreen();
                        System.out.println("\nLogged in as: " + userRole);

                        if ("ADMIN".equalsIgnoreCase(userRole)) {
                            AdminMenu.showAdminMenu();
                        } else if ("STAFF/CASHIER".equalsIgnoreCase(userRole)) {
                            dashboard.cashierDashboard();
                        } else if ("INVENTORY CLERK".equalsIgnoreCase(userRole)) {
                            dashboard.inventoryDashboard();
                        }
                    }
                    break;

                case "3":
                    clearScreen();
                    System.out.println("   Thank you for using School Supplies Inventory System!    ");
                    System.out.println("               System Closed Successfully                   ");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option! Please choose between 1, 2, or 3.");
                    break;
            }
        }
    }
}