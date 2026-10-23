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
                    System.out.print("Enter your Fullname: ");
                    String name = sc.nextLine();
                    System.out.print("Enter new Username: ");
                    String user = sc.nextLine();
                    System.out.print("Enter Password: ");
                    String pass = sc.nextLine();

                    System.out.println("Role: ");
                    System.out.println(" 1. Admin");
                    System.out.println(" 2. Staff/Cashier");
                    System.out.println(" 3. Inventory Clerk");
                    System.out.print("Enter choice (1-3): ");

                    int roleChoice = 0;
                    try {
                        roleChoice = Integer.parseInt(sc.nextLine());
                    } catch (NumberFormatException e) {
                        roleChoice = 0;
                    }

                    String role;
                    switch (roleChoice) {
                        case 1: role = "Admin"; break;
                        case 2: role = "Staff/Cashier"; break;
                        case 3: role = "Inventory Clerk"; break;
                        default: role = null;
                    }

                    if (role != null) {
                        userDao.registerUser(name, user, pass, role);
                    } else {
                        System.out.println("Invalid role selection!");
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
                            dashboard.adminDashboard();
                        } else if ("STAFF/CASHIER".equalsIgnoreCase(userRole)) {
                            dashboard.cashierDashboard();
                        } else if ("INVENTORY CLERK".equalsIgnoreCase(userRole)) {
                            dashboard.inventoryDashboard();
                        }
                    }
                    break;

                case "3":
                    clearScreen();
                    System.out.println("\n|============================================================|");
                    System.out.println("|      Thank you for using School Supplies Inventory System! |");
                    System.out.println("|                   System Closed Successfully.              |");
                    System.out.println("|============================================================|\n");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option! Please choose between 1, 2, or 3.");
                    break;
            }
        }
    }
}