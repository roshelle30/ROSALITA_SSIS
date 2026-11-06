package org.example.ui;

import org.example.model.User;
import java.util.Scanner;

public class Dashboard {
    private final Scanner sc;
    private final User currentUser;

    public Dashboard(Scanner sc) {
        this.sc = sc;
        this.currentUser = null;
    }

    public Dashboard(Scanner sc, User currentUser) {
        this.sc = sc;
        this.currentUser = currentUser;
    }

    public void adminDashboard() {
        boolean inAdminMenu = true;

        printAdminHeader();

        while (inAdminMenu) {
            System.out.print("\nChoose an Option: ");
            String adminChoice = sc.nextLine().trim();

            switch (adminChoice) {
                case "1":
                    AdminMenu.manageUsersMenu(currentUser);
                    printAdminHeader();
                    break;

                case "2":
                    AdminMenu.manageProductsMenu(currentUser);
                    printAdminHeader();
                    break;

                case "3":
                    AdminMenu.viewReportsMenu();
                    printAdminHeader();
                    break;

                case "4":
                    AdminMenu.viewLogsMenu();
                    printAdminHeader();
                    break;

                case "5":
                    System.out.println("Logging out from Admin Dashboard...");
                    inAdminMenu = false;
                    break;

                default:
                    System.out.println("Invalid option! Please choose between 1-5.");
                    break;
            }
        }
    }

    private void printAdminHeader() {
        System.out.println("\n|============================================================|");
        System.out.println("|                      ADMIN DASHBOARD                       |");
        System.out.println("|============================================================|");
        System.out.println("| 1. Manage Users (Approve / Deactivate Accounts)            |");
        System.out.println("| 2. Manage Products (Add / Edit / Delete Inventory)         |");
        System.out.println("| 3. View Daily Sales & Low-Stock Reports                    |");
        System.out.println("| 4. View System Activity & Audit Logs                       |");
        System.out.println("| 5. Logout                                                  |");
        System.out.println("|============================================================|");
    }

    public void cashierDashboard() {
        StaffCashierMenu.cashierDashboardMenu(sc, currentUser);
    }

    public void inventoryDashboard() {
        InventoryClerkMenu.showInventoryClerkMenu(sc, currentUser);
    }

    public static void printManageUsersHeader() {
        System.out.println("\n+-----+-----------------+-----------------+--------------------+--------------+");
        System.out.println("|                                MANAGE USERS                                 |");
        System.out.println("|=============================================================================|");
        System.out.println("| 1. View All System Users                                                    |");
        System.out.println("| 2. View Pending Registrations                                               |");
        System.out.println("| 3. Approve User Account                                                     |");
        System.out.println("| 4. Update User Role and Status                                              |");
        System.out.println("| 5. Delete User Account                                                      |");
        System.out.println("| 6. Back to Admin Dashboard                                                  |");
        System.out.println("+-----+-----------------+-----------------+--------------------+--------------+");
    }

    public static void printManageProductsHeader() {
        System.out.println("\n+--------------------------------------------------------------------------------------------------+");
        System.out.println("|                                   MANAGE PRODUCTS (INVENTORY)                                    |");
        System.out.println("+--------------------------------------------------------------------------------------------------+");
        System.out.println("| 1. View All Inventory Products                                                                   |");
        System.out.println("| 2. Add New Product                                                                               |");
        System.out.println("| 3. Update Existing Product Details / Stock                                                       |");
        System.out.println("| 4. Delete Product                                                                                |");
        System.out.println("| 5. Back to Admin Dashboard                                                                       |");
        System.out.println("+--------------------------------------------------------------------------------------------------+");
    }

    public static void printReportsHeader() {
        System.out.println("\n+--------------------------------------------------------------------------------------------------+");
        System.out.println("|                              VIEW DAILY SALES & LOW-STOCK REPORTS                                |");
        System.out.println("+--------------------------------------------------------------------------------------------------+");
        System.out.println("| 1. View Low-Stock Inventory Report                                                               |");
        System.out.println("| 2. View Daily Sales Report                                                                       |");
        System.out.println("| 3. Back to Admin Dashboard                                                                       |");
        System.out.println("+--------------------------------------------------------------------------------------------------+");
    }

    public static void printLogsHeader() {
        System.out.println("\n+--------------------------------------------------------------------------+");
        System.out.println("|                         SYSTEM AUDIT LOGS MENU                           |");
        System.out.println("+--------------------------------------------------------------------------+");
        System.out.println("| 1. View All System Logs                                                  |");
        System.out.println("| 2. Filter Logs by User ID                                                |");
        System.out.println("| 3. Back to Admin Dashboard                                               |");
        System.out.println("+--------------------------------------------------------------------------+");
    }

    public static void printCashierMenuHeader() {
        System.out.println("\n|============================================================|");
        System.out.println("|                 STORE STAFF / CASHIER MENU                 |");
        System.out.println("|============================================================|");
        System.out.println("| 1. New Sales Transaction (Checkout)                        |");
        System.out.println("| 2. Search / View School Supplies                           |");
        System.out.println("| 3. View Sales History                                      |");
        System.out.println("| 4. Cancel / Void Transaction Item                          |");
        System.out.println("| 5. Logout                                                  |");
        System.out.println("|============================================================|");
    }

    public static void printInventoryClerkMenuHeader() {
        System.out.println("\n|============================================================|");
        System.out.println("|                  INVENTORY CLERK DASHBOARD                 |");
        System.out.println("|============================================================|");
        System.out.println("| 1. Add New School Supply Product                           |");
        System.out.println("| 2. Update Product Details & Pricing                        |");
        System.out.println("| 3. Record Stock Restock Shipments / Adjustments            |");
        System.out.println("| 4. Delete Product                                          |");
        System.out.println("| 5. View Inventory Stock Levels                             |");
        System.out.println("| 6. Logout                                                  |");
        System.out.println("|============================================================|");
    }
}