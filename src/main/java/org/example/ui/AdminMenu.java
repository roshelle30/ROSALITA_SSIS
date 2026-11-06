package org.example.ui;

import org.example.dao.ActivityLogDao;
import org.example.dao.ProductsDao;
import org.example.dao.SalesDao;
import org.example.dao.UserDao;
import org.example.model.Activity_Logs;
import org.example.model.Products;
import org.example.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private static final Scanner sc = new Scanner(System.in);
    private static final UserDao userDao = new UserDao();
    private static final ProductsDao productsDao = new ProductsDao();
    private static final SalesDao salesDao = new SalesDao();
    private static final ActivityLogDao logDao = new ActivityLogDao();

    private static final String LOG_FORMAT = "| %-5s | %-8s | %-25s | %-85s |\n";
    private static final String LOG_BORDER = "+-------+----------+--------------------------+----------------------------------------------------------------------------------------+\n";

    public static void displayUserTable(List<User> users) {
        String format = "| %-3s | %-15s | %-15s | %-18s | %-10s |\n";
        String border = "+-----+-----------------+-----------------+--------------------+------------+\n";

        System.out.print(border);
        System.out.format(format, "ID", "Fullname", "Username", "Role", "Status");
        System.out.print(border);

        if (users.isEmpty()) {
            System.out.println("|                       No users found.                                      |");
        } else {
            for (User u : users) {
                System.out.format(format, u.user_id(), u.full_name(), u.username(), u.role(), u.status());
            }
        }
        System.out.print(border);
    }

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

    private static int readIntInput(String prompt) {
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

    private static double readDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value <= 0) {
                    System.out.println("Error: Price must be greater than 0. Please try again.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input! Letters or symbols are not allowed. Please enter a valid number.");
            }
        }
    }

    private static String readValidCategory() {
        System.out.println("\nSelect Product Category:");
        System.out.println("  1. Writing Materials");
        System.out.println("  2. Paper Products");
        System.out.println("  3. Art & Craft Supplies");
        System.out.println("  4. Office & Filing Supplies");
        System.out.println("  5. General School Supplies");

        while (true) {
            System.out.print("Enter choice (1-5) (or type '0' to cancel): ");
            String choice = sc.nextLine().trim();

            if (choice.equals("0")) {
                return "CANCEL";
            }

            switch (choice) {
                case "1": return "Writing Materials";
                case "2": return "Paper Products";
                case "3": return "Art & Craft Supplies";
                case "4": return "Office & Filing Supplies";
                case "5": return "General School Supplies";
                default:
                    System.out.println("Error: Invalid category selection! Please choose between 1 and 5.");
                    break;
            }
        }
    }

    private static String readValidRole() {
        System.out.println("\nRole:");
        System.out.println("  1. Admin");
        System.out.println("  2. Staff/Cashier");
        System.out.println("  3. Inventory Clerk");

        while (true) {
            System.out.print("Enter choice (1-3) (or type '0' to cancel): ");
            String choice = sc.nextLine().trim();

            if (choice.equals("0")) {
                return "0";
            }

            switch (choice) {
                case "1": return "ADMIN";
                case "2": return "STAFF/CASHIER";
                case "3": return "INVENTORY CLERK";
                default:
                    System.out.println("Invalid role selection! Please choose between 1 to 3.");
                    break;
            }
        }
    }

    private static String readValidStatus() {
        System.out.println("\nStatus:");
        System.out.println("  1. APPROVED");
        System.out.println("  2. PENDING");

        while (true) {
            System.out.print("Enter choice (1-2) (or type '0' to cancel): ");
            String choice = sc.nextLine().trim();

            if (choice.equals("0")) {
                return "0";
            }

            switch (choice) {
                case "1": return "APPROVED";
                case "2": return "PENDING";
                default:
                    System.out.println("Invalid status selection! Please choose 1 or 2.");
                    break;
            }
        }
    }

    public static void manageUsersMenu(User currentAdmin) {
        boolean inManageUsers = true;

        Dashboard.printManageUsersHeader();

        while (inManageUsers) {
            System.out.print("\nChoose an Option: ");
            String manageChoice = sc.nextLine().trim();

            switch (manageChoice) {
                case "1":
                    System.out.println("\n+-----+-----------------+-----------------+--------------------+------------+");
                    System.out.println("|                            ALL SYSTEM USERS                               |");
                    List<User> allUsers = userDao.getAllUsers();
                    displayUserTable(allUsers);

                    while (true) {
                        System.out.print("\nEnter 0 to go back to Manage Users menu: ");
                        String back = sc.nextLine().trim();
                        if (back.equals("0")) {
                            break;
                        } else {
                            System.out.println("Invalid input! Please enter 0 to go back.");
                        }
                    }
                    Dashboard.printManageUsersHeader();
                    break;

                case "2":
                    System.out.println("\n+-----+-----------------+-----------------+--------------------+------------+");
                    System.out.println("|                         PENDING REGISTRATIONS                             |");
                    List<User> pendingUsers = userDao.getPendingUsers();
                    displayUserTable(pendingUsers);

                    while (true) {
                        System.out.print("\nEnter 0 to go back to Manage Users menu: ");
                        String back = sc.nextLine().trim();
                        if (back.equals("0")) {
                            break;
                        } else {
                            System.out.println("Invalid input! Please enter 0 to go back.");
                        }
                    }
                    Dashboard.printManageUsersHeader();
                    break;

                case "3":
                    System.out.println("\n+-----+-----------------+-----------------+--------------------+------------+");
                    System.out.println("|                          APPROVE USER ACCOUNT                             |");
                    List<User> pendingsApprove = userDao.getPendingUsers();
                    if (pendingsApprove.isEmpty()) {
                        System.out.println("No pending accounts found.");
                        Dashboard.printManageUsersHeader();
                        break;
                    }
                    displayUserTable(pendingsApprove);

                    while (true) {
                        int targetId = readIntInput("\nEnter Target User ID to approve (Enter 0 to cancel): ");
                        if (targetId == 0) {
                            System.out.println("Approval action cancelled.");
                            break;
                        }

                        boolean existsInPending = false;
                        for (User u : pendingsApprove) {
                            if (u.user_id() == targetId) {
                                existsInPending = true;
                                break;
                            }
                        }

                        if (!existsInPending) {
                            System.out.println("Error: User ID " + targetId + " does not exist in the pending list!");
                            continue;
                        }

                        int adminId = (currentAdmin != null) ? currentAdmin.user_id() : 1;
                        boolean success = userDao.approveUser(targetId, adminId);
                        if (success) {
                            System.out.println("\nSuccess: User ID " + targetId + " has been approved!");
                        } else {
                            System.out.println("Error: Failed to approve user.");
                        }
                        break;
                    }
                    Dashboard.printManageUsersHeader();
                    break;

                case "4":
                    System.out.println("\n+-----+-----------------+-----------------+--------------------+------------+");
                    System.out.println("|                          UPDATE USER ACCOUNT                              |");
                    List<User> allUsersForUpdate = userDao.getAllUsers();
                    if (allUsersForUpdate.isEmpty()) {
                        System.out.println("No users found.");
                        Dashboard.printManageUsersHeader();
                        break;
                    }
                    displayUserTable(allUsersForUpdate);

                    while (true) {
                        int targetId = readIntInput("\nEnter Target User ID to update (Enter 0 to cancel): ");
                        if (targetId == 0) {
                            System.out.println("Update action cancelled.");
                            break;
                        }
                        if (currentAdmin != null && targetId == currentAdmin.user_id()) {
                            System.out.println("WARNING: Modifying active admin account is restricted.\n");
                            continue;
                        }

                        boolean userExists = false;
                        for (User u : allUsersForUpdate) {
                            if (u.user_id() == targetId) {
                                userExists = true;
                                break;
                            }
                        }

                        if (!userExists) {
                            System.out.println("Error: User ID " + targetId + " does not exist!");
                            continue;
                        }

                        String newRole = readValidRole();
                        if (newRole.equals("0")) {
                            System.out.println("Update action cancelled.");
                            break;
                        }

                        String newStatus = readValidStatus();
                        if (newStatus.equals("0")) {
                            System.out.println("Update action cancelled.");
                            break;
                        }

                        int adminId = (currentAdmin != null) ? currentAdmin.user_id() : 1;
                        boolean success = userDao.updateUserRoleAndStatus(targetId, newRole, newStatus, adminId);
                        if (success) {
                            System.out.println("Success: User ID " + targetId + " updated successfully!");
                        } else {
                            System.out.println("Error: Failed to update user.");
                        }
                        break;
                    }
                    Dashboard.printManageUsersHeader();
                    break;

                case "5":
                    System.out.println("\n+-----+-----------------+-----------------+--------------------+------------+");
                    System.out.println("|                          DELETE USER ACCOUNT                              |");

                    List<User> allUsersForDelete = userDao.getAllUsers();
                    if (allUsersForDelete.isEmpty()) {
                        System.out.println("No users found.");
                        Dashboard.printManageUsersHeader();
                        break;
                    }
                    displayUserTable(allUsersForDelete);

                    boolean deletingUsers = true;
                    while (deletingUsers) {
                        int targetId = readIntInput("\nEnter Target User ID to delete (Enter 0 to cancel): ");
                        if (targetId == 0) {
                            System.out.println("Delete action cancelled.");
                            break;
                        }
                        if (currentAdmin != null && targetId == currentAdmin.user_id()) {
                            System.out.println("WARNING: You cannot delete your own active admin account!");
                            continue;
                        }

                        boolean userExists = false;
                        for (User u : allUsersForDelete) {
                            if (u.user_id() == targetId) {
                                userExists = true;
                                break;
                            }
                        }

                        if (!userExists) {
                            System.out.println("Error: User ID " + targetId + " does not exist!");
                            continue;
                        }

                        boolean confirmed = false;
                        while (true) {
                            System.out.print("\nAre you sure you want to delete user ID " + targetId + "? (y/n): ");
                            String confirm = sc.nextLine().trim().toLowerCase();

                            if (confirm.equals("y")) {
                                confirmed = true;
                                break;
                            } else if (confirm.equals("n")) {
                                System.out.println("Deletion cancelled for User ID " + targetId + ".");
                                confirmed = false;
                                break;
                            } else {
                                System.out.println("Invalid input! Please enter 'y' for yes or 'n' for no.");
                            }
                        }

                        if (confirmed) {
                            int adminId = (currentAdmin != null) ? currentAdmin.user_id() : 1;
                            boolean success = userDao.deleteUser(targetId, adminId);
                            if (success) {
                                System.out.println("\nSuccess: User ID " + targetId + " has been deleted!");
                                allUsersForDelete = userDao.getAllUsers();
                            } else {
                                System.out.println("Error: Failed to delete user.");
                            }
                        }
                        while (true) {
                            System.out.print("\nDo you want to delete another user? (y/n): ");
                            String another = sc.nextLine().trim().toLowerCase();
                            if (another.equals("y")) {
                                break;
                            } else if (another.equals("n")) {
                                deletingUsers = false;
                                break;
                            } else {
                                System.out.println("Invalid input! Please enter 'y' or 'n'.");
                            }
                        }
                    }
                    Dashboard.printManageUsersHeader();
                    break;

                case "6":
                    inManageUsers = false;
                    break;

                default:
                    System.out.println("Invalid option! Please choose between 1 to 6.");
                    break;
            }
        }
    }

    public static void manageProductsMenu(User currentAdmin) {
        boolean inManageProducts = true;

        Dashboard.printManageProductsHeader();

        while (inManageProducts) {
            System.out.print("\nChoose an Option: ");
            String productChoice = sc.nextLine().trim();

            switch (productChoice) {
                case "1":
                    System.out.println("\n+--------------------------------------------------------------------------------------------------+");
                    System.out.println("|                                     ALL INVENTORY PRODUCTS                                       |");
                    List<Products> products = productsDao.getAllProducts();
                    displayProductTable(products);

                    while (true) {
                        System.out.print("\nEnter 0 to go back to Manage Products menu: ");
                        String back = sc.nextLine().trim();
                        if (back.equals("0")) {
                            break;
                        } else {
                            System.out.println("Invalid input! Please enter 0 to go back.");
                        }
                    }
                    Dashboard.printManageProductsHeader();
                    break;

                case "2":
                    System.out.println("\n+--------------------------------------------------------------------------+");
                    System.out.println("|                          ADD NEW PRODUCT                                 |");

                    String name;
                    while (true) {
                        System.out.print("\nEnter Product Name (or type '0' to cancel): ");
                        name = sc.nextLine().trim();

                        if (name.equals("0")) {
                            System.out.println("Add product cancelled.");
                            break;
                        }

                        if (name.isEmpty()) {
                            System.out.println("Error: Product name cannot be empty. Please try again.");
                            continue;
                        }

                        boolean productExists = false;
                        List<Products> existingList = productsDao.getAllProducts();
                        for (Products p : existingList) {
                            if (p.productName().equalsIgnoreCase(name)) {
                                productExists = true;
                                break;
                            }
                        }

                        if (productExists) {
                            System.out.println("Error: Product '" + name + "' already exists in the inventory! Please enter a different name.");
                        } else {
                            break;
                        }
                    }

                    if (name.equals("0")) {
                        Dashboard.printManageProductsHeader();
                        break;
                    }

                    String category = readValidCategory();
                    if (category.equals("CANCEL")) {
                        System.out.println("Add product cancelled.");
                        Dashboard.printManageProductsHeader();
                        break;
                    }

                    double price = readDoubleInput("Enter Unit Price: ");
                    int stock = readIntInput("Enter Initial Stock Quantity: ");
                    int reorder = readIntInput("Enter Reorder Level: ");

                    Products newProduct = new Products(0, name, category, price, stock, reorder);
                    int adminId = (currentAdmin != null) ? currentAdmin.user_id() : 1;
                    boolean added = productsDao.addProduct(newProduct, adminId);

                    if (added) {
                        System.out.println("\nSuccess: Product '" + name + "' added successfully and logged!");
                    } else {
                        System.out.println("Error: Failed to add product.");
                    }
                    Dashboard.printManageProductsHeader();
                    break;

                case "3":
                    System.out.println("\n+--------------------------------------------------------------------------------------------------+");
                    System.out.println("|                                         UPDATE PRODUCT                                           |");
                    List<Products> allProdsForUpdate = productsDao.getAllProducts();
                    if (allProdsForUpdate.isEmpty()) {
                        System.out.println("No products found in inventory.");
                        Dashboard.printManageProductsHeader();
                        break;
                    }
                    displayProductTable(allProdsForUpdate);

                    while (true) {
                        int targetId = readIntInput("\nEnter Product ID to update (Enter 0 to cancel): ");
                        if (targetId == 0) {
                            System.out.println("Update action cancelled.");
                            break;
                        }

                        Products existingProduct = null;
                        for (Products p : allProdsForUpdate) {
                            if (p.productId() == targetId) {
                                existingProduct = p;
                                break;
                            }
                        }

                        if (existingProduct == null) {
                            System.out.println("Error: Product ID " + targetId + " does not exist!");
                            continue;
                        }

                        String uName;
                        while (true) {
                            System.out.print("Enter New Product Name [" + existingProduct.productName() + "]: ");
                            uName = sc.nextLine().trim();
                            if (uName.isEmpty()) {
                                uName = existingProduct.productName();
                                break;
                            }

                            boolean nameExists = false;
                            for (Products p : allProdsForUpdate) {
                                if (p.productId() != targetId && p.productName().equalsIgnoreCase(uName)) {
                                    nameExists = true;
                                    break;
                                }
                            }

                            if (nameExists) {
                                System.out.println("Error: Product '" + uName + "' already exists in the inventory! Please enter a different name.");
                            } else {
                                break;
                            }
                        }

                        System.out.println("Current Category: " + existingProduct.category());
                        String uCategory = readValidCategory();
                        if (uCategory.equals("CANCEL")) {
                            System.out.println("Update product cancelled.");
                            break;
                        }

                        double uPrice = readDoubleInput("Enter New Unit Price [" + existingProduct.unitPrice() + "]: ");
                        int uStock = readIntInput("Enter New Stock Quantity [" + existingProduct.stockQuantity() + "]: ");
                        int uReorder = readIntInput("Enter New Reorder Level [" + existingProduct.reorderLevel() + "]: ");

                        Products updatedProduct = new Products(targetId, uName, uCategory, uPrice, uStock, uReorder);
                        int uAdminId = (currentAdmin != null) ? currentAdmin.user_id() : 1;
                        boolean updated = productsDao.updateProduct(updatedProduct, uAdminId);

                        if (updated) {
                            System.out.println("\nSuccess: Product ID " + targetId + " updated successfully and logged!");
                        } else {
                            System.out.println("Error: Failed to update product.");
                        }
                        break;
                    }
                    Dashboard.printManageProductsHeader();
                    break;

                case "4":
                    System.out.println("\n+--------------------------------------------------------------------------------------------------+");
                    System.out.println("|                                         DELETE PRODUCT                                           |");
                    List<Products> allProdsForDelete = productsDao.getAllProducts();
                    if (allProdsForDelete.isEmpty()) {
                        System.out.println("No products found in inventory.");
                        Dashboard.printManageProductsHeader();
                        break;
                    }
                    displayProductTable(allProdsForDelete);

                    boolean deletingProducts = true;
                    while (deletingProducts) {
                        int deleteId = readIntInput("Enter Product ID to delete (Enter 0 to cancel): ");
                        if (deleteId == 0) {
                            System.out.println("Delete action cancelled.");
                            break;
                        }

                        String deleteName = "";
                        boolean foundProd = false;
                        for (Products p : allProdsForDelete) {
                            if (p.productId() == deleteId) {
                                deleteName = p.productName();
                                foundProd = true;
                                break;
                            }
                        }

                        if (!foundProd) {
                            System.out.println("Error: Product ID " + deleteId + " does not exist!\n");
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
                            int dAdminId = (currentAdmin != null) ? currentAdmin.user_id() : 1;
                            boolean deleted = productsDao.deleteProduct(deleteId, deleteName, dAdminId);

                            if (deleted) {
                                System.out.println("Success: Product ID " + deleteId + " deleted successfully and logged!");
                                allProdsForDelete = productsDao.getAllProducts();
                            } else {
                                System.out.println("Error: Failed to delete product.");
                            }
                        }

                        while (true) {
                            System.out.print("\nDo you want to delete another product? (y/n): ");
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
                    Dashboard.printManageProductsHeader();
                    break;

                case "5":
                    inManageProducts = false;
                    break;

                default:
                    System.out.println("Invalid option! Please choose between 1 to 5.");
                    break;
            }
        }
    }

    public static void viewReportsMenu() {
        boolean inReports = true;

        Dashboard.printReportsHeader();

        while (inReports) {
            System.out.print("\nChoose an Option: ");
            String reportChoice = sc.nextLine().trim();

            switch (reportChoice) {
                case "1":
                    System.out.println("\n+---------------------------------------------------------------------------------------------------+");
                    System.out.println("|                                   LOW-STOCK PRODUCTS REPORT                                       |");
                    List<Products> allProds = productsDao.getAllProducts();
                    List<Products> lowStockProds = new java.util.ArrayList<>();
                    for (Products p : allProds) {
                        if (p.stockQuantity() <= p.reorderLevel()) {
                            lowStockProds.add(p);
                        }
                    }
                    displayProductTable(lowStockProds);
                    if (lowStockProds.isEmpty()) {
                        System.out.println("Great! No products are currently at or below their reorder level.");
                    }

                    Dashboard.printReportsHeader();
                    break;

                case "2":
                    System.out.println("\n+--------------------------------------------------------------------------------+");
                    System.out.println("|                                DAILY SALES REPORT                              |");
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

                    Dashboard.printReportsHeader();
                    break;

                case "3":
                    inReports = false;
                    break;

                default:
                    System.out.println("Invalid option! Please choose between 1 to 3.");
                    break;
            }
        }
    }

    public static void viewLogsMenu() {
        boolean inLogs = true;

        Dashboard.printLogsHeader();

        while (inLogs) {
            System.out.print("\nChoose an Option: ");
            String logChoice = sc.nextLine().trim();

            switch (logChoice) {
                case "1":
                    System.out.print(LOG_BORDER);
                    System.out.format(LOG_FORMAT, "ID", "User ID", "Timestamp", "Action");
                    System.out.print(LOG_BORDER);
                    List<Activity_Logs> allLogs = logDao.getAllLogs();
                    if (allLogs.isEmpty()) {
                        System.out.println("No audit logs found.");
                    } else {
                        for (Activity_Logs log : allLogs) {
                            System.out.format(LOG_FORMAT, log.logId(), log.userId(), log.timestamp(), log.action());
                        }
                    }
                    System.out.print(LOG_BORDER);

                    Dashboard.printLogsHeader();
                    break;

                case "2":
                    while (true) {
                        int filterId = readIntInput("\nEnter User ID to filter (Enter 0 to cancel): ");
                        if (filterId == 0) {
                            System.out.println("Filter action cancelled.");
                            break;
                        }

                        List<Activity_Logs> userLogs = logDao.getLogsByUserId(filterId);
                        if (userLogs.isEmpty()) {
                            System.out.println("No logs found for User ID " + filterId + ".\n");
                            continue;
                        } else {
                            System.out.print(LOG_BORDER);
                            System.out.format(LOG_FORMAT, "ID", "User ID", "Timestamp", "Action");
                            System.out.print(LOG_BORDER);
                            for (Activity_Logs log : userLogs) {
                                System.out.format(LOG_FORMAT, log.logId(), log.userId(), log.timestamp(), log.action());
                            }
                            System.out.print(LOG_BORDER);
                            break;
                        }
                    }
                    Dashboard.printLogsHeader();
                    break;

                case "3":
                    inLogs = false;
                    break;

                default:
                    System.out.println("Invalid option! Please choose between 1 to 3.");
                    break;
            }
        }
    }
}