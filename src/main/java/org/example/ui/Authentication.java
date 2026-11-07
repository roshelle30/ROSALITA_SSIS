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

    private void printMainMenuHeader() {
        System.out.println("\n|============================================================|");
        System.out.println("|               SCHOOL SUPPLIES INVENTORY SYSTEM             |");
        System.out.println("|============================================================|");
        System.out.println("|1. Register                                                 |");
        System.out.println("|2. Login                                                    |");
        System.out.println("|3. Exit                                                     |");
        System.out.println("|============================================================|");
    }

    public void start() {
        boolean running = true;

        printMainMenuHeader();

        while (running) {
            System.out.print("\nChoose an Option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    String name;
                    while (true) {
                        System.out.print("\nEnter your Fullname: ");
                        name = sc.nextLine().trim();
                        if (name.equals("0")) {
                            System.out.println("Registration cancelled.");
                            break;
                        }
                        if (name.isEmpty() || name.length() < 2) {
                            System.out.println( "Error: Please enter a valid Fullname.");
                            continue;
                        }
                        break;
                    }
                    if (name.equals("0")) {
                        printMainMenuHeader();
                        break;
                    }

                    String user;
                    while (true) {
                        System.out.print("Enter new Username: ");
                        user = sc.nextLine().trim();
                        if (user.equals("0")) {
                            System.out.println("Registration cancelled.");
                            break;
                        }
                        if (user.isEmpty() || user.length() < 3) {
                            System.out.println( "Error: Username must be at least 3 characters long.");
                            continue;
                        }
                        break;
                    }
                    if (user.equals("0")) {
                        printMainMenuHeader();
                        break;
                    }

                    String pass;
                    while (true) {
                        System.out.print("Enter Password: ");
                        pass = sc.nextLine().trim();
                        if (pass.equals("0")) {
                            System.out.println("Registration cancelled.");
                            break;
                        }
                        if (pass.isEmpty() || pass.length() < 4) {
                            System.out.println( "Error: Password must be at least 4 characters long.");
                            continue;
                        }
                        break;
                    }
                    if (pass.equals("0")) {
                        printMainMenuHeader();
                        break;
                    }

                    String role = null;
                    System.out.println("\nRole: ");
                    System.out.println(" 1. Admin");
                    System.out.println(" 2. Staff/Cashier");
                    System.out.println(" 3. Inventory Clerk");

                    while (true) {
                        System.out.print("\nEnter choice (1-3): ");

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
                            break;
                        } else {
                            System.out.println( "Invalid role selection! Please choose between 1-3.");
                        }
                    }

                    if (role != null) {
                        userDao.registerUser(name, user, pass, role);
                    }

                    printMainMenuHeader();
                    break;

                case "2":
                    System.out.print("\nEnter Username: ");
                    String loginUser = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String loginPass = sc.nextLine();

                    String userRole = userDao.loginUser(loginUser, loginPass);

                    if (userRole != null) {
                        clearScreen();
                        System.out.println("\nLogged in as: " + userRole);

                        if ("ADMIN".equalsIgnoreCase(userRole)) {
                            dashboard.adminDashboard();
                        } else if ("STAFF/CASHIER".equalsIgnoreCase(userRole)) {
                            dashboard.cashierDashboard();
                        } else if ("INVENTORY CLERK".equalsIgnoreCase(userRole)) {
                            dashboard.inventoryDashboard();
                        }
                    }
                    printMainMenuHeader();
                    break;

                case "3":
                    clearScreen();
                    System.out.println("   Thank you for using School Supplies Inventory System!    ");
                    System.out.println("               System Closed Successfully                   ");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option! Please choose between 1-3.");
                    break;
            }
        }
    }
}