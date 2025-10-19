package org.example;

import org.example.commonComponents.Application;
import org.example.commonComponents.DefaultApplication;
import org.example.databases.Database;
import org.example.server.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting application...");
        try {
            //Database.printAllUsers();
            createUsers();
            int port = 8080;
            Application application = new DefaultApplication();
            Server server = new Server(port, application);
            server.start();
            System.out.println("Server started on port " + port);

            // Add graceful shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to start application. Make sure the database is running and config is correct.");
            System.exit(1);
        }
    }

    private static void createUsers() {
        try {
            System.out.println("Creating test users...");

            // Lösche vorhandene Test-User um Duplikate zu vermeiden
            cleanTestUsers();

            // Erstelle Test-User
            Database.insertUser("alice", "password123");
            Database.insertUser("bob", "secret456");
            Database.insertUser("charlie", "test789");

            Database.printAllUsers();

        } catch (Exception e) {
            System.err.println("Error why user creation: " + e.getMessage());
        }
    }

    private static void cleanTestUsers() {
        try {
            // Einfache Bereinigung - in Production würde man das anders machen
            // Hier könntest du spezifische Test-User löschen falls needed
            Database.deleteUserByUsername("alice");
            Database.deleteUserByUsername("bob");
            Database.deleteUserByUsername("charlie");

        } catch (Exception e) {
            System.out.println("No cleanup needed");
        }
    }
}

