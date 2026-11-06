package org.example.ui;

import org.example.dao.InventoryDao;
import org.example.model.Products;
import org.example.model.User;

import java.util.List;
import java.util.Scanner;

public class InventoryClerkMenu {
    private static final InventoryDao inventoryDao = new InventoryDao();

    public static void displayProductTable(List<Products> products) {
        String format = "| %-5s | %-25s | %-26s | %-12s | %-7s | %-7s |\n";
        String border = "+-------+---------------------------+----------------------------+--------------+---------+---------+\n";

        System.out.print(border);
        System.out.format(format, "ID", "Product Name", "Category", "Unit Price", "Stock", "Reorder");
        System.out.print(border);

        if (products.isEmpty()) {
            System.out.println("|                               No products found in inventory.                                      |");
        } else {
            for (Products p : products) {
                System.out.format(format, p.productId(), p.productName(), p.category(), p.unitPrice(), p.stockQuantity(), p.reorderLevel());
            }
        }
        System.out.print(border);
    }

    private static int readIntInput(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < 0) {
                    System.out.println("Error: Value cannot be negative. Please try again.\n");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Letters or symbols are not allowed. Please enter a valid number.\n");
            }
        }
    }

    private static double readDoubleInput(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value < 0) {
                    System.out.println("Error: Amount cannot be negative. Please try again.\n");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Letters or symbols are not allowed. Please enter a valid amount.\n");
            }
        }
    }

    public static void showInventoryClerkMenu(Scanner sc, User currentClerk) {
        boolean inInventory = true;

        Dashboard.printInventoryClerkMenuHeader();

        while (inInventory) {
            String choice = "";
            while (true) {
                System.out.print("\nChoose an Option: ");
                choice = sc.nextLine().trim();

                if (choice.equals("1") || choice.equals("2") || choice.equals("3") ||
                        choice.equals("4") || choice.equals("5") || choice.equals("6")) {
                    break;
                } else {
                    System.out.println("Invalid option! Please choose between 1 to 6.");
                }
            }

            int clerkId = (currentClerk != null) ? currentClerk.user_id() : 1;

            switch (choice) {
                case "1":
                    System.out.println("\n+------------------------------------------------------------------+");
                    System.out.println("|                  ADD NEW SCHOOL SUPPLY PRODUCT                   |");
                    System.out.println("+------------------------------------------------------------------+");

                    List<Products> existingProds = inventoryDao.getAllInventory();

                    String productName = "";
                    while (true) {
                        System.out.print("\nEnter Product Name (or type 'cancel' to abort): ");
                        productName = sc.nextLine().trim();

                        if (productName.equalsIgnoreCase("cancel")) {
                            System.out.println("Add product cancelled.");
                            break;
                        }
                        if (productName.isEmpty()) {
                            System.out.println("Error: Product name cannot be empty.\n");
                            continue;
                        }

                        boolean nameExists = false;
                        for (Products p : existingProds) {
                            if (p.productName().equalsIgnoreCase(productName)) {
                                nameExists = true;
                                break;
                            }
                        }

                        if (nameExists) {
                            System.out.println("Error: Product name '" + productName + "' already exists in inventory.");
                            continue;
                        }
                        break;
                    }

                    if (productName.equalsIgnoreCase("cancel")) {
                        Dashboard.printInventoryClerkMenuHeader();
                        break;
                    }

                    System.out.println("\nSelect Category:");
                    System.out.println("[1] Writing Materials");
                    System.out.println("[2] Paper Products");
                    System.out.println("[3] Art & Craft Supplies");
                    System.out.println("[4] School Essentials");
                    System.out.println("[5] Office & General Supplies");

                    String category = "";
                    while (true) {
                        System.out.print("Choose Category (1-5): ");
                        String catChoice = sc.nextLine().trim();
                        switch (catChoice) {
                            case "1":
                                category = "Writing Materials";
                                break;
                            case "2":
                                category = "Paper Products";
                                break;
                            case "3":
                                category = "Art & Craft Supplies";
                                break;
                            case "4":
                                category = "School Essentials";
                                break;
                            case "5":
                                category = "Office & General Supplies";
                                break;
                            default:
                                System.out.println("Invalid choice! Please select between 1 and 5.");
                                continue;
                        }
                        break;
                    }

                    double unitPrice = readDoubleInput(sc, "Enter Unit Price (PHP): ");
                    int initialStock = readIntInput(sc, "Enter Initial Stock Quantity: ");
                    int reorderLevel = readIntInput(sc, "Enter Reorder Level: ");

                    boolean addSuccess = inventoryDao.addProduct(productName, category, unitPrice, initialStock, reorderLevel, clerkId);

                    if (addSuccess) {
                        System.out.println("\nSuccess: New product '" + productName + "' added successfully!");
                    } else {
                        System.out.println("\nError: Failed to add product.");
                    }

                    Dashboard.printInventoryClerkMenuHeader();
                    break;

                case "2":
                    System.out.println("\n+---------------------------------------------------------------------------------------------------+");
                    System.out.println("|                                 UPDATE PRODUCT DETAILS & PRICING                                  |");
                    List<Products> allProds = inventoryDao.getAllInventory();

                    if (allProds.isEmpty()) {
                        System.out.println("No products found in inventory.");
                        Dashboard.printInventoryClerkMenuHeader();
                        break;
                    }
                    displayProductTable(allProds);

                    int updateId = 0;
                    boolean idExists = false;
                    while (true) {
                        updateId = readIntInput(sc, "\nEnter Product ID to update (or type 0 to cancel): ");

                        if (updateId == 0) {
                            System.out.println("Update product cancelled.");
                            break;
                        }

                        idExists = false;
                        for (Products p : allProds) {
                            if (p.productId() == updateId) {
                                idExists = true;
                                break;
                            }
                        }

                        if (!idExists) {
                            System.out.println("Error: Product ID " + updateId + " does not exist in the inventory table. Please choose a valid ID.");
                            continue;
                        }
                        break;
                    }

                    if (updateId == 0) {
                        Dashboard.printInventoryClerkMenuHeader();
                        break;
                    }

                    String newName = "";
                    while (true) {
                        System.out.print("\nEnter New Product Name: ");
                        newName = sc.nextLine().trim();

                        if (newName.isEmpty()) {
                            System.out.println("Error: Product name cannot be empty.\n");
                            continue;
                        }

                        boolean nameExists = false;
                        for (Products p : allProds) {
                            if (p.productId() != updateId && p.productName().equalsIgnoreCase(newName)) {
                                nameExists = true;
                                break;
                            }
                        }

                        if (nameExists) {
                            System.out.println("Error: Product name '" + newName + "' already exists in inventory. Please use a unique name.");
                            continue;
                        }
                        break;
                    }

                    System.out.println("\nSelect New Category:");
                    System.out.println("[1] Writing Materials");
                    System.out.println("[2] Paper Products");
                    System.out.println("[3] Art & Craft Supplies");
                    System.out.println("[4] School Essentials");
                    System.out.println("[5] Office & General Supplies");

                    String newCategory = "";
                    while (true) {
                        System.out.print("Choose Category (1-5): ");
                        String catChoice = sc.nextLine().trim();
                        switch (catChoice) {
                            case "1":
                                newCategory = "Writing Materials";
                                break;
                            case "2":
                                newCategory = "Paper Products";
                                break;
                            case "3":
                                newCategory = "Art & Craft Supplies";
                                break;
                            case "4":
                                newCategory = "School Essentials";
                                break;
                            case "5":
                                newCategory = "Office & General Supplies";
                                break;
                            default:
                                System.out.println("Invalid choice! Please select between 1 and 5.");
                                continue;
                        }
                        break;
                    }

                    double newPrice = readDoubleInput(sc, "Enter New Unit Price (PHP): ");
                    int newReorder = readIntInput(sc, "Enter New Reorder Level: ");

                    boolean updateSuccess = inventoryDao.updateProduct(updateId, newName, newCategory, newPrice, newReorder, clerkId);

                    if (updateSuccess) {
                        System.out.println("\nSuccess: Product ID " + updateId + " updated successfully!");
                    } else {
                        System.out.println("\nError: Failed to update product.");
                    }

                    Dashboard.printInventoryClerkMenuHeader();
                    break;

                case "3":
                    System.out.println("\n+---------------------------------------------------------------------------------------------------+");
                    System.out.println("|                                RECORD RESTOCK SHIPMENTS / ADJUSTMENTS                             |");

                    List<Products> stockProds = inventoryDao.getAllInventory();
                    if (stockProds.isEmpty()) {
                        System.out.println("No products found in inventory.");
                        Dashboard.printInventoryClerkMenuHeader();
                        break;
                    }
                    displayProductTable(stockProds);

                    int stockId = 0;
                    boolean stockIdExists = false;
                    while (true) {
                        stockId = readIntInput(sc, "\nEnter Product ID to adjust/restock (or type 0 to cancel): ");

                        if (stockId == 0) {
                            System.out.println("Stock adjustment cancelled.");
                            break;
                        }

                        stockIdExists = false;
                        for (Products p : stockProds) {
                            if (p.productId() == stockId) {
                                stockIdExists = true;
                                break;
                            }
                        }

                        if (!stockIdExists) {
                            System.out.println("Error: Product ID " + stockId + " does not exist in the inventory table. Please choose a valid ID.");
                            continue;
                        }
                        break;
                    }

                    if (stockId == 0) {
                        Dashboard.printInventoryClerkMenuHeader();
                        break;
                    }

                    int qtyChange = readIntInput(sc, "Enter Quantity Change (+ for restock, - for damage/reduction): ");

                    System.out.println("\nSelect Remarks / Reason:");
                    System.out.println("[1] Supplier Delivery");
                    System.out.println("[2] Damaged Items");

                    String remarks = "";
                    while (true) {
                        System.out.print("Choose Reason (1-2): ");
                        String remChoice = sc.nextLine().trim();
                        switch (remChoice) {
                            case "1":
                                remarks = "Supplier Delivery";
                                break;
                            case "2":
                                remarks = "Damaged Items";
                                break;
                            default:
                                System.out.println("Invalid choice! Please select either 1 or 2.");
                                continue;
                        }
                        break;
                    }

                    boolean adjustSuccess = inventoryDao.adjustStock(stockId, qtyChange, clerkId, remarks);

                    if (adjustSuccess) {
                        System.out.println("\nSuccess: Stock adjusted successfully!");
                    } else {
                        System.out.println("\nError: Failed to adjust stock. Please check the stock bounds.");
                    }

                    Dashboard.printInventoryClerkMenuHeader();
                    break;

                case "4":
                    System.out.println("\n+---------------------------------------------------------------------------------------------------+");
                    System.out.println("|                                           DELETE PRODUCT                                            |");

                    List<Products> delProds = inventoryDao.getAllInventory();
                    if (delProds.isEmpty()) {
                        System.out.println("No products found in inventory.");
                        Dashboard.printInventoryClerkMenuHeader();
                        break;
                    }
                    displayProductTable(delProds);

                    boolean deletingProducts = true;
                    while (deletingProducts) {
                        int deleteId = readIntInput(sc, "Enter Product ID to delete (Enter 0 to cancel): ");
                        if (deleteId == 0) {
                            System.out.println("Delete product cancelled.");
                            break;
                        }

                        String deleteName = "";
                        boolean deleteIdExists = false;
                        for (Products p : delProds) {
                            if (p.productId() == deleteId) {
                                deleteName = p.productName();
                                deleteIdExists = true;
                                break;
                            }
                        }

                        if (!deleteIdExists) {
                            System.out.println("Error: Product ID " + deleteId + " does not exist in the inventory table!");
                            continue;
                        }

                        boolean confirmed = false;
                        while (true) {
                            System.out.print("Are you sure you want to delete product ID " + deleteId + " (" + deleteName + ")? (y/n): ");
                            String confirm = sc.nextLine().trim().toLowerCase();

                            if (confirm.equals("y")) {
                                confirmed = true;
                                break;
                            } else if (confirm.equals("n")) {
                                System.out.println("Deletion cancelled for Product ID " + deleteId + ".");
                                confirmed = false;
                                break;
                            } else {
                                System.out.println("Invalid input! Please enter 'y' for yes or 'n' for no.");
                            }
                        }

                        if (confirmed) {
                            boolean deleteSuccess = inventoryDao.deleteProduct(deleteId, clerkId);

                            if (deleteSuccess) {
                                System.out.println("\nSuccess: Product ID " + deleteId + " deleted successfully!");
                                delProds = inventoryDao.getAllInventory();
                            } else {
                                System.out.println("\nError: Failed to delete product.");
                            }
                        }
                        while (true) {
                            System.out.print("Do you want to delete another product? (y/n): ");
                            String another = sc.nextLine().trim().toLowerCase();
                            if (another.equals("y")) {
                                break;
                            } else if (another.equals("n")) {
                                deletingProducts = false;
                                break;
                            } else {
                                System.out.println("Invalid input! Please enter 'y' or 'n'.");
                            }
                        }
                    }

                    Dashboard.printInventoryClerkMenuHeader();
                    break;

                case "5":
                    System.out.println("\n+---------------------------------------------------------------------------------------------------+");
                    System.out.println("|                                   VIEW INVENTORY STOCK LEVELS                                     |");

                    List<Products> inventoryList = inventoryDao.getAllInventory();
                    displayProductTable(inventoryList);

                    pressEnterToContinue(sc);
                    Dashboard.printInventoryClerkMenuHeader();
                    break;

                case "6":
                    System.out.println("Logging out from Inventory Clerk Dashboard...");
                    inInventory = false;
                    break;
            }
        }
    }

    private static void pressEnterToContinue(Scanner sc) {
        while (true) {
            System.out.print("\nEnter 0 to go back to Inventory Clerk menu: ");
            String back = sc.nextLine().trim();
            if (back.equals("0")) {
                break;
            } else {
                System.out.println("Invalid input! Please enter 0 to go back.");
            }
        }
    }
}