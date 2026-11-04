package org.example.ui;

import org.example.dao.ActivityLogDao;
import org.example.dao.ProductsDao;
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
    private static final ActivityLogDao logDao = new ActivityLogDao();

    private static final String LOG_FORMAT = "| %-5s | %-8s | %-22s | %-65s |\n";
    private static final String LOG_BORDER = "+-------+----------+------------------------+-----------------------------------------------------------------+\n";

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
        String format = "| %-5s | %-25s | %-15s | %-12s | %-7s | %-7s |\n";
        String border = "+-------+---------------------------+-----------------+--------------+---------+---------+\n";

        System.out.print(border);
        System.out.format(format, "ID", "Product Name", "Category", "Unit Price", "Stock", "Reorder");
        System.out.print(border);

        if (products.isEmpty()) {
            System.out.println("|                       No products found in inventory.                          |");
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
            if (sc.hasNextInt()) {
                int value = sc.nextInt();
                sc.nextLine();
                if (value < 0) {
                    System.out.println("Error: Value cannot be negative. Please try again.\n");
                    continue;
                }
                return value;
            } else {
                System.out.println("Invalid input! Please enter a valid number.\n");
                sc.nextLine();
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
        while (true) {
            System.out.println("\nSelect Product Category:");
            System.out.println("  1. Writing Materials");
            System.out.println("  2. Paper Products");
            System.out.println("  3. Art & Craft Supplies");
            System.out.println("  4. Office & Filing Supplies");
            System.out.println("  5. General School Supplies");
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
        while (true) {
            System.out.print("\nEnter new role (ADMIN, STAFF/CASHIER, INVENTORY CLERK) [Enter 0 to cancel]: ");
            String role = sc.nextLine().trim().toUpperCase();
            if (role.equals("0")) return "0";
            if (role.equals("ADMIN") || role.equals("STAFF/CASHIER") || role.equals("INVENTORY CLERK")) {
                return role;
            }
            System.out.println("Invalid role! Please enter a valid role.");
        }
    }

    private static String readValidStatus() {
        while (true) {
            System.out.print("\nEnter new status (APPROVED, PENDING) [Enter 0 to cancel]: ");
            String status = sc.nextLine().trim().toUpperCase();
            if (status.equals("0")) return "0";
            if (status.equals("APPROVED") || status.equals("PENDING")) {
                return status;
            }
            System.out.println("Invalid status! Please enter APPROVED or PENDING.");
        }
    }

    public static void showAdminMenu() {
        showAdminMenu(null);
    }

    public static void showAdminMenu(User currentAdmin) {
        boolean inAdmin = true;
        while (inAdmin) {
            System.out.println("\n+-----+-----------------+-----------------+--------------------+------------+");
            System.out.println("|                           ADMIN DASHBOARD                                 |");
            System.out.println("+-----+-----------------+-----------------+--------------------+------------+");
            System.out.println("| 1. Manage Users (Approve / Deactivate Accounts)                           |");
            System.out.println("| 2. Manage Products (Add / Edit / Delete Inventory)                        |");
            System.out.println("| 3. View Daily Sales & Low-Stock Reports                                   |");
            System.out.println("| 4. View System Activity & Audit Logs                                      |");
            System.out.println("| 5. Logout                                                                 |");
            System.out.println("+-----+-----------------+-----------------+--------------------+------------+");
            System.out.print("Choose an Option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    boolean inManageUsers = true;
                    while (inManageUsers) {
                        System.out.println("\n+-----+-----------------+-----------------+--------------------+--------------+");
                        System.out.println("|                                MANAGE USERS                                 |");
                        System.out.println("|=============================================================================|");
                        System.out.println("| 1. View All System Users                                                    |");
                        System.out.println("| 2. View Pending Registrations                                               |");
                        System.out.println("| 3. Approve User Account                                                     |");
                        System.out.println("| 4. Update User Role and Status                                              |");
                        System.out.println("| 5. Back to Admin Dashboard                                                  |");
                        System.out.println("+-----+-----------------+-----------------+--------------------+--------------+");
                        System.out.print("Choose an Option: ");
                        String manageChoice = sc.nextLine();

                        switch (manageChoice) {
                            case "1":
                                System.out.println("+-----+-----------------+-----------------+--------------------+------------+");
                                System.out.println("|                            ALL SYSTEM USERS                               |");
                                List<User> allUsers = userDao.getAllUsers();
                                displayUserTable(allUsers);
                                break;

                            case "2":
                                System.out.println("+-----+-----------------+-----------------+--------------------+------------+");
                                System.out.println("|                         PENDING REGISTRATIONS                             |");
                                List<User> pendingUsers = userDao.getPendingUsers();
                                displayUserTable(pendingUsers);
                                break;

                            case "3":
                                while (true) {
                                    System.out.println("+-----+-----------------+-----------------+--------------------+------------+");
                                    System.out.println("|                          APPROVE USER ACCOUNT                             |");
                                    List<User> pendings = userDao.getPendingUsers();
                                    if (pendings.isEmpty()) {
                                        System.out.println("No pending accounts found.");
                                        break;
                                    }
                                    displayUserTable(pendings);

                                    int targetId = readIntInput("Enter Target User ID to approve (Enter 0 to cancel): ");
                                    if (targetId == 0) {
                                        System.out.println("Approval action cancelled.");
                                        break;
                                    }

                                    boolean existsInPending = false;
                                    for (User u : pendings) {
                                        if (u.user_id() == targetId) {
                                            existsInPending = true;
                                            break;
                                        }
                                    }

                                    if (!existsInPending) {
                                        System.out.println("Error: User ID " + targetId + " does not exist in the pending list!\n");
                                        continue;
                                    }

                                    int adminId = (currentAdmin != null) ? currentAdmin.user_id() : 1;
                                    boolean success = userDao.approveUser(targetId, adminId);
                                    if (success) {
                                        System.out.println("Success: User ID " + targetId + " has been approved!");
                                        break;
                                    } else {
                                        System.out.println("Error: Failed to approve user.");
                                    }
                                }
                                break;

                            case "4":
                                while (true) {
                                    System.out.println("+-----+-----------------+-----------------+--------------------+------------+");
                                    System.out.println("|                          UPDATE USER ACCOUNT                              |");
                                    List<User> allUsersForUpdate = userDao.getAllUsers();
                                    if (allUsersForUpdate.isEmpty()) {
                                        System.out.println("No users found.");
                                        break;
                                    }
                                    displayUserTable(allUsersForUpdate);

                                    int targetId = readIntInput("Enter Target User ID to update (Enter 0 to cancel): ");
                                    if (targetId == 0) {
                                        System.out.println("Update action cancelled.");
                                        break;
                                    }
                                    if (currentAdmin != null && targetId == currentAdmin.user_id()) {
                                        System.out.println("WARNING: Modifying active admin account is restricted.\n");
                                        continue;
                                    }

                                    String newRole = readValidRole();
                                    if (newRole.equals("0")) break;

                                    String newStatus = readValidStatus();
                                    if (newStatus.equals("0")) break;

                                    int adminId = (currentAdmin != null) ? currentAdmin.user_id() : 1;
                                    boolean success = userDao.updateUserRoleAndStatus(targetId, newRole, newStatus, adminId);
                                    if (success) {
                                        System.out.println("Success: User ID " + targetId + " updated successfully!");
                                        break;
                                    } else {
                                        System.out.println("Error: Failed to update user.");
                                    }
                                }
                                break;

                            case "5":
                                inManageUsers = false;
                                break;

                            default:
                                System.out.println("Invalid option! Please choose between 1 to 5.");
                                break;
                        }
                    }
                    break;

                case "2":
                    boolean inManageProducts = true;
                    while (inManageProducts) {
                        System.out.println("\n+--------------------------------------------------------------------------+");
                        System.out.println("|                          MANAGE PRODUCTS (INVENTORY)                     |");
                        System.out.println("+--------------------------------------------------------------------------+");
                        System.out.println("| 1. View All Inventory Products                                           |");
                        System.out.println("| 2. Add New Product                                                       |");
                        System.out.println("| 3. Update Existing Product Details / Stock                               |");
                        System.out.println("| 4. Delete Product                                                        |");
                        System.out.println("| 5. Back to Admin Dashboard                                               |");
                        System.out.println("+--------------------------------------------------------------------------+");
                        System.out.print("Choose an Option: ");
                        String productChoice = sc.nextLine();

                        switch (productChoice) {
                            case "1":
                                System.out.println("\n+----------------------------------------------------------------------------------------+");
                                System.out.println("|                                 ALL INVENTORY PRODUCTS                                 |");
                                List<Products> products = productsDao.getAllProducts();
                                displayProductTable(products);
                                break;

                            case "2":
                                System.out.println("\n+--------------------------------------------------------------------------+");
                                System.out.println("|                          ADD NEW PRODUCT                                 |");

                                String name;
                                while (true) {
                                    System.out.print("|Enter Product Name (or type '0' to cancel): ");
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

                                if (name.equals("0")) break;

                                String category = readValidCategory();
                                if (category.equals("CANCEL")) {
                                    System.out.println("Add product cancelled.");
                                    break;
                                }

                                double price = readDoubleInput("|Enter Unit Price: |");
                                int stock = readIntInput("|Enter Initial Stock Quantity: |");
                                int reorder = readIntInput("|Enter Reorder Level: |");

                                Products newProduct = new Products(0, name, category, price, stock, reorder);
                                int adminId = (currentAdmin != null) ? currentAdmin.user_id() : 1;
                                boolean added = productsDao.addProduct(newProduct, adminId);

                                if (added) {
                                    System.out.println("Success: Product '" + name + "' added successfully and logged!");
                                } else {
                                    System.out.println("Error: Failed to add product.");
                                }
                                break;

                            case "3":
                                System.out.println("\n+--------------------------------------------------------------------------+");
                                System.out.println("|                          UPDATE PRODUCT                                  |");
                                List<Products> allProdsForUpdate = productsDao.getAllProducts();
                                if (allProdsForUpdate.isEmpty()) {
                                    System.out.println("No products found in inventory.");
                                    break;
                                }
                                displayProductTable(allProdsForUpdate);

                                int targetId = readIntInput("Enter Product ID to update (Enter 0 to cancel): ");
                                if (targetId == 0) break;

                                Products existingProduct = null;
                                for (Products p : allProdsForUpdate) {
                                    if (p.productId() == targetId) {
                                        existingProduct = p;
                                        break;
                                    }
                                }

                                if (existingProduct == null) {
                                    System.out.println("Error: Product ID " + targetId + " does not exist!");
                                    break;
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
                                    System.out.println("Success: Product ID " + targetId + " updated successfully and logged!");
                                } else {
                                    System.out.println("Error: Failed to update product.");
                                }
                                break;

                            case "4":
                                System.out.println("\n+--------------------------------------------------------------------------+");
                                System.out.println("|                          DELETE PRODUCT                                  |");
                                while (true) {
                                    List<Products> allProdsForDelete = productsDao.getAllProducts();
                                    if (allProdsForDelete.isEmpty()) {
                                        System.out.println("No products found in inventory.");
                                        break;
                                    }
                                    displayProductTable(allProdsForDelete);

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

                                    int dAdminId = (currentAdmin != null) ? currentAdmin.user_id() : 1;
                                    boolean deleted = productsDao.deleteProduct(deleteId, deleteName, dAdminId);

                                    if (deleted) {
                                        System.out.println("Success: Product ID " + deleteId + " deleted successfully and logged!");
                                        break;
                                    } else {
                                        System.out.println("Error: Failed to delete product.");
                                        break;
                                    }
                                }
                                break;

                            case "5":
                                inManageProducts = false;
                                break;

                            default:
                                System.out.println("Invalid option! Please choose between 1 to 5.");
                                break;
                        }
                    }
                    break;

                case "3":
                    System.out.println("\n[View Reports feature goes here]");
                    break;

                case "4":
                    boolean inLogs = true;
                    while (inLogs) {
                        System.out.println("\n+--------------------------------------------------------------------------+");
                        System.out.println("|                         SYSTEM AUDIT LOGS MENU                           |");
                        System.out.println("+--------------------------------------------------------------------------+");
                        System.out.println("| 1. View All System Logs                                                  |");
                        System.out.println("| 2. Filter Logs by User ID                                                |");
                        System.out.println("| 3. Back to Admin Dashboard                                               |");
                        System.out.println("+--------------------------------------------------------------------------+");
                        System.out.print("Choose an Option: ");
                        String logChoice = sc.nextLine();

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
                                break;

                            case "2":
                                while (true) {
                                    int filterId = readIntInput("Enter User ID to filter (Enter 0 to cancel): ");
                                    if (filterId == 0) break;

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
                                break;

                            case "3":
                                inLogs = false;
                                break;

                            default:
                                System.out.println("Invalid option! Please choose between 1 to 3.");
                                break;
                        }
                    }
                    break;

                case "5":
                    System.out.println("Logging out from Admin Dashboard...");
                    inAdmin = false;
                    break;

                default:
                    System.out.println("Invalid option! Please choose between 1 to 5.");
                    break;
            }
        }
    }
}