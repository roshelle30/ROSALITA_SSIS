package org.example.ui;

import java.util.Scanner;

public class InventoryClerkMenu {
    private static final Scanner sc = new Scanner(System.in);

    public static void showInventoryClerkMenu() {
        boolean inInventory = true;
        while (inInventory) {
            System.out.println("\n|============================================================|");
            System.out.println("|                  INVENTORY CLERK DASHBOARD                 |");
            System.out.println("|============================================================|");
            System.out.println("| 1. Add New School Supply Product                           |");
            System.out.println("| 2. Update Product Details & Pricing                        |");
            System.out.println("| 3. Record Stock Restock Shipments / Adjustments            |");
            System.out.println("| 4. View Inventory Stock Levels                             |");
            System.out.println("| 5. Logout                                                  |");
            System.out.println("|============================================================|");
            System.out.print("Choose an Option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n[Feature: Add a new school supply product into the database]");
                    break;
                case "2":
                    System.out.println("\n[Feature: Update product details, unit pricing, and re-order levels]");
                    break;
                case "3":
                    System.out.println("\n[Feature: Modify stock quantities during supplier restocks / adjustments]");
                    break;
                case "4":
                    System.out.println("\n[Feature: View current inventory stock levels]");
                    break;
                case "5":
                    System.out.println("Logging out from Inventory Clerk Dashboard...");
                    inInventory = false;
                    break;
                default:
                    System.out.println("Invalid option! Please choose between 1 to 5.");
                    break;
            }
        }
    }
}