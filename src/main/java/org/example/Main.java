package org.example;

import org.example.dao.DatabaseInitializer;
import org.example.dao.UserDao;
import org.example.ui.Authentication;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        UserDao userDao = new UserDao();
        Scanner sc = new Scanner(System.in);

        Authentication auth = new Authentication(userDao, sc);
        auth.start();
    }
}