package org.example.ui;

import java.util.Scanner;

public class StaffCashierMenu {
    private static final Scanner sc = new Scanner(System.in);

    public static void showStaffCashierMenu() {
        boolean inStaffCashier = true;
        while (inStaffCashier) {
            System.out.println("\n|============================================================|");
            System.out.println("|                  STORE STAFF / CASHIER MENU                |");
            System.out.println("|============================================================|");
            System.out.println("| 1. New Sales Transaction (Checkout)                        |");
            System.out.println("| 2. Search / View School Supplies                           |");
            System.out.println("| 3. View Sales History                                      |");
            System.out.println("| 4. Cancel / Void Transaction Item                          |");
            System.out.println("| 5. Logout                                                  |");
            System.out.println("|============================================================|");
            System.out.print("Choose an Option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    boolean inTransaction = true;
                    while (inTransaction) {
                        System.out.println("\n--- NEW SALES TRANSACTION ---");
                        System.out.println("[Displaying Products Table here...]");
                        System.out.println("1. Add Product to Cart");
                        System.out.println("2. Finish & Checkout");
                        System.out.println("3. Cancel Transaction");
                        System.out.print("Choose action: ");
                        String transChoice = sc.nextLine();

                        if (transChoice.equals("2") || transChoice.equals("3")) {
                            System.out.println("Returning to Staff/Cashier Menu...");
                            inTransaction = false;
                        }
                    }
                    break;

                case "2":
                    System.out.println("\n--- SEARCH / VIEW SCHOOL SUPPLIES ---");
                    break;

                case "3":
                    System.out.println("\n--- SALES TRANSACTION HISTORY ---");
                    break;

                case "4":
                    System.out.println("\n--- CANCEL / VOID TRANSACTION ITEM ---");
                    break;

                case "5":
                    System.out.println("Logging out from Staff/Cashier Dashboard...");
                    inStaffCashier = false;
                    break;

                default:
                    System.out.println("Invalid option! Please choose between 1 to 5.");
                    break;
            }
        }
    }
}