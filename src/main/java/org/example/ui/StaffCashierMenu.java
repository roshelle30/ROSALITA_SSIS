package org.example.ui;

import org.example.dao.SalesDao;
import org.example.model.Products;
import org.example.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class CartItem {
    Products product;
    int quantity;

    public CartItem(Products product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}
public class StaffCashierMenu {
    private static final SalesDao salesDao = new SalesDao();

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
                System.out.println("Invalid input! Please enter a valid number.\n");
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
                    System.out.println("Error: Amount cannot be negative. Please try again.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid amount.");
            }
        }
    }

    public static void cashierDashboardMenu(Scanner sc, User currentCashier) {
        boolean inCashianMenu = true;

        Dashboard.printCashierMenuHeader();

        while (inCashianMenu) {
            String cashierChoice = "";
            while (true) {
                System.out.print("\nChoose an Option: ");
                cashierChoice = sc.nextLine().trim();

                if (cashierChoice.equals("1") || cashierChoice.equals("2") || cashierChoice.equals("3") ||
                        cashierChoice.equals("4") || cashierChoice.equals("5")) {
                    break;
                } else {
                    System.out.println("Invalid option! Please choose between 1 to 5.");
                }
            }

            switch (cashierChoice) {
                case "1":
                    System.out.println("\n+---------------------------------------------------------------------------------------------------+");
                    System.out.println("|                                     NEW SALES TRANSACTION                                         |");
                    List<Products> allProds = salesDao.getAllProducts();
                    displayProductTable(allProds);

                    if (allProds.isEmpty()) {
                        System.out.println("No products available for checkout.");
                        Dashboard.printCashierMenuHeader();
                        break;
                    }

                    List<CartItem> cart = new ArrayList<>();
                    boolean addingItems = true;

                    while (addingItems) {
                        Products selectedProduct = null;
                        while (true) {
                            int productId = readIntInput(sc, "\nEnter Product ID to checkout (Enter 0 to finish/cancel): ");
                            if (productId == 0) {
                                if (cart.isEmpty()) {
                                    System.out.println("Sales transaction cancelled.");
                                    Dashboard.printCashierMenuHeader();
                                }
                                addingItems = false;
                                break;
                            }

                            for (Products p : allProds) {
                                if (p.productId() == productId) {
                                    selectedProduct = p;
                                    break;
                                }
                            }

                            if (selectedProduct == null) {
                                System.out.println("Error: Product ID " + productId + " does not exist!");
                                continue;
                            }
                            break;
                        }

                        if (!addingItems || selectedProduct == null) {
                            break;
                        }

                        int quantity = 0;
                        while (true) {
                            quantity = readIntInput(sc, "Enter Quantity to buy: ");
                            if (quantity <= 0) {
                                System.out.println("Error: Quantity must be greater than 0.");
                                continue;
                            }

                            if (quantity > selectedProduct.stockQuantity()) {
                                System.out.println("Error: Insufficient stock! Available stock is only " + selectedProduct.stockQuantity() + "\n");
                                continue;
                            }
                            break;
                        }

                        cart.add(new CartItem(selectedProduct, quantity));
                        System.out.println("Successfully added to cart!");

                        while (true) {
                            System.out.print("Do you want to add another item? (y/n): ");
                            String addMore = sc.nextLine().trim().toLowerCase();
                            if (addMore.equals("y")) {
                                break;
                            } else if (addMore.equals("n")) {
                                addingItems = false;
                                break;
                            } else {
                                System.out.println("Invalid input! Please enter 'y' or 'n'.");
                            }
                        }
                    }

                    if (cart.isEmpty()) {
                        break;
                    }

                    double subtotalAmount = 0;
                    for (CartItem item : cart) {
                        subtotalAmount += item.product.unitPrice() * item.quantity;
                    }

                    double discountThreshold = 1000.00;
                    double discountRate = 0.10;
                    double discountAmount = 0.0;

                    if (subtotalAmount >= discountThreshold) {
                        discountAmount = subtotalAmount * discountRate;
                    }

                    double finalTotalAmount = subtotalAmount - discountAmount;

                    double cashTendered = 0;
                    while (true) {
                        System.out.format("\nSubtotal: PHP %.2f\n", subtotalAmount);
                        if (discountAmount > 0) {
                            System.out.format("Discount (10%%): -PHP %.2f\n", discountAmount);
                        }
                        System.out.format("Final Total Amount to Pay: PHP %.2f\n", finalTotalAmount);
                        cashTendered = readDoubleInput(sc, "Enter Cash Tendered (PHP): ");

                        if (cashTendered < finalTotalAmount) {
                            System.out.println("Error: Insufficient cash! The amount entered is less than total.\n");
                            continue;
                        }
                        break;
                    }

                    double change = cashTendered - finalTotalAmount;

                    int cashierId = (currentCashier != null) ? currentCashier.user_id() : 1;
                    String cashierFullName = (currentCashier != null && currentCashier.full_name() != null)
                            ? currentCashier.full_name()
                            : "Cashier #" + cashierId;
                    String paymentMethod = "CASH";

                    boolean allSuccess = true;
                    for (CartItem item : cart) {
                        double itemSub = item.product.unitPrice() * item.quantity;
                        double itemFinalSub = itemSub - (itemSub * (discountAmount / subtotalAmount));

                        boolean success = salesDao.processCheckout(item.product.productId(), item.quantity, cashierId, paymentMethod, itemFinalSub);
                        if (!success) {
                            allSuccess = false;
                        }
                    }

                    if (allSuccess) {
                        java.time.LocalDateTime now = java.time.LocalDateTime.now();
                        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        long receiptNo = System.currentTimeMillis() % 1000000;

                        System.out.println("\n+------------------------------------------------------------------+");
                        System.out.println("|                 SCHOOL SUPPLIES INVENTORY SYSTEM                 |");
                        System.out.println("|                    OFFICIAL SALES RECEIPT                        |");
                        System.out.println("+------------------------------------------------------------------+");
                        System.out.format("| Receipt No  : %-18d Date: %-25s |\n", receiptNo, dtf.format(now));
                        System.out.format("| Cashier     : %-50s |\n", cashierFullName);
                        System.out.format("| Mode of Pay : %-50s |\n", paymentMethod);
                        System.out.println("+------------------------------------------------------------------+");
                        System.out.println("| Product Name              | Unit Price   | Qty   | Subtotal      |");
                        System.out.println("+------------------------------------------------------------------+");
                        for (CartItem item : cart) {
                            double itemSub = item.product.unitPrice() * item.quantity;
                            System.out.format("| %-25s | PHP %-8.2f | %-5d | PHP %-9.2f |\n",
                                    item.product.productName(), item.product.unitPrice(), item.quantity, itemSub);
                        }
                        System.out.println("+------------------------------------------------------------------+");
                        System.out.format("| SUBTOTAL     : PHP %-45.2f |\n", subtotalAmount);
                        if (discountAmount > 0) {
                            System.out.format("| DISCOUNT(10%%): -PHP %-44.2f |\n", discountAmount);
                        } else {
                            System.out.println("| DISCOUNT     : NONE (Min. spend PHP 1,000 for 10%)               |");
                        }
                        System.out.format("| FINAL TOTAL  : PHP %-45.2f |\n", finalTotalAmount);
                        System.out.format("| Cash Tendered: PHP %-45.2f |\n", cashTendered);
                        System.out.format("| CHANGE       : PHP %-45.2f |\n", change);
                        System.out.println("+------------------------------------------------------------------+");
                        System.out.println("|            Thank you for your purchase!                          |");
                        System.out.println("|            Please come again!                                    |");
                        System.out.println("+------------------------------------------------------------------+");
                        System.out.println("\nSuccess: Transaction completed successfully! Stock updated and logged.");
                    } else {
                        System.out.println("\nError: Some items in the transaction failed.");
                    }

                    Dashboard.printCashierMenuHeader();
                    break;

                case "2":
                    System.out.println("\n+---------------------------------------------------------------------------------------------------+");
                    System.out.println("|                                    SEARCH / VIEW SUPPLIES                                         |");
                    List<Products> searchProds = salesDao.getAllProducts();
                    displayProductTable(searchProds);

                    pressEnterToContinue(sc);
                    Dashboard.printCashierMenuHeader();
                    break;

                case "3":
                    System.out.println("\n+--------------------------------------------------------------------------------+");
                    System.out.println("|                                VIEW SALES HISTORY                              |");
                    System.out.println("+--------------------------------------------------------------------------------+");
                    System.out.println("| ID  | Date                | Product            | Qty   | Total         | Pay   |");
                    System.out.println("+-----+---------------------+--------------------+-------+---------------+-------+");

                    List<String> salesHistory = salesDao.getSalesHistory();

                    if (salesHistory.isEmpty()) {
                        System.out.println("|                                No sales transactions found yet.                                  |");
                    } else {
                        for (String record : salesHistory) {
                            System.out.println(record);
                        }
                    }
                    System.out.println("+--------------------------------------------------------------------------------+");

                    pressEnterToContinue(sc);
                    Dashboard.printCashierMenuHeader();
                    break;

                case "4":
                    System.out.println("\n+--------------------------------------------------------------------------------+");
                    System.out.println("|                        CANCEL / VOID TRANSACTION HISTORY                       |");
                    System.out.println("+--------------------------------------------------------------------------------+");
                    System.out.println("| ID  | Date                | Product            | Qty   | Total         | Pay   |");
                    System.out.println("+-----+---------------------+--------------------+-------+---------------+-------+");

                    List<String> voidHistory = salesDao.getSalesHistory();

                    if (voidHistory.isEmpty()) {
                        System.out.println("|                                No sales transactions found to void.                              |");
                        System.out.println("+--------------------------------------------------------------------------------+");
                        Dashboard.printCashierMenuHeader();
                        break;
                    } else {
                        for (String record : voidHistory) {
                            System.out.println(record);
                        }
                    }
                    System.out.println("+--------------------------------------------------------------------------------+");

                    boolean voidingTransactions = true;
                    while (voidingTransactions) {
                        int voidTransId = readIntInput(sc, "\nEnter Transaction ID to void (Enter 0 to cancel): ");
                        if (voidTransId == 0) {
                            System.out.println("Void transaction cancelled.");
                            break;
                        }

                        boolean transIdExists = false;
                        for (String record : voidHistory) {
                            String[] parts = record.split("\\|");
                            if (parts.length > 1) {
                                try {
                                    int parsedId = Integer.parseInt(parts[1].trim());
                                    if (parsedId == voidTransId) {
                                        transIdExists = true;
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                }
                            }
                        }

                        if (!transIdExists) {
                            System.out.println("Error: Transaction ID " + voidTransId + " does not exist in the table!");
                            continue;
                        }

                        boolean confirmed = false;
                        while (true) {
                            System.out.print("\nAre you sure you want to void transaction ID " + voidTransId + "? (y/n): ");
                            String confirm = sc.nextLine().trim().toLowerCase();

                            if (confirm.equals("y")) {
                                confirmed = true;
                                break;
                            } else if (confirm.equals("n")) {
                                System.out.println("Void cancelled for Transaction ID " + voidTransId + ".");
                                confirmed = false;
                                break;
                            } else {
                                System.out.println("Invalid input! Please enter 'y' for yes or 'n' for no.");
                            }
                        }

                        if (confirmed) {
                            int voidCashierId = (currentCashier != null) ? currentCashier.user_id() : 1;
                            boolean voidSuccess = salesDao.voidTransaction(voidTransId, voidCashierId);

                            if (voidSuccess) {
                                System.out.println("\nSuccess: Transaction ID " + voidTransId + " has been voided and stock restored!");
                                voidHistory = salesDao.getSalesHistory();
                            } else {
                                System.out.println("Error: Failed to void transaction. Please check the Transaction ID.\n");
                            }
                        }

                        while (true) {
                            System.out.print("\nDo you want to void another transaction? (y/n): ");
                            String another = sc.nextLine().trim().toLowerCase();
                            if (another.equals("y")) {
                                break;
                            } else if (another.equals("n")) {
                                voidingTransactions = false;
                                break;
                            } else {
                                System.out.println("Invalid input! Please enter 'y' or 'n'.");
                            }
                        }
                    }

                    Dashboard.printCashierMenuHeader();
                    break;

                case "5":
                    System.out.println("Logging out from Cashier Dashboard...");
                    inCashianMenu = false;
                    break;
            }
        }
    }

    private static void pressEnterToContinue(Scanner sc) {
        while (true) {
            System.out.print("\nEnter 0 to go back to Cashier menu: ");
            String back = sc.nextLine().trim();
            if (back.equals("0")) {
                break;
            } else {
                System.out.println("Invalid input! Please enter 0 to go back.");
            }
        }
    }
}