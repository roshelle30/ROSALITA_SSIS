package org.example.ui;

import org.example.dao.UserDao;
import org.example.model.User;
import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private static final Scanner sc = new Scanner(System.in);
    private static final UserDao userDao = new UserDao();

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
    private static int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextInt()) {
                int value = sc.nextInt();
                sc.nextLine();
                return value;
            } else {
                System.out.println("Invalid input! Please enter a valid number.\n");
                sc.nextLine();
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
            System.out.println( "Invalid role! Please enter a valid role (e.g., ADMIN, STAFF/CASHIER, INVENTORY CLERK).");
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
            System.out.println( "Invalid status! Please enter APPROVED or PENDING.");
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
                                        System.out.println("Error: User ID " + targetId + " does not exist in the pending list! Please try again.\n");
                                        continue;
                                    }

                                    boolean success = userDao.approveUser(targetId);
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
                                        System.out.println("WARNING: Modifying the currently active admin account is restricted.\n");
                                        continue;
                                    }

                                    boolean exists = false;
                                    for (User u : allUsersForUpdate) {
                                        if (u.user_id() == targetId) {
                                            exists = true;
                                            break;
                                        }
                                    }

                                    if (!exists) {
                                        System.out.println("Error: User ID " + targetId + " does not exist! Please try again.\n");
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

                                    boolean success = userDao.updateUserRoleAndStatus(targetId, newRole, newStatus);
                                    if (success) {
                                        System.out.println("Success: User ID " + targetId + " has been successfully updated!");
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
                    System.out.println("\n[Manage Products feature goes here]");
                    break;

                case "3":
                    System.out.println("\n[View Reports feature goes here]");
                    break;

                case "4":
                    System.out.println("\n[Audit Logs feature goes here]");
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