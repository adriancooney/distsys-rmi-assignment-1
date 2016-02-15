package com.distsys;

import com.distsys.ExamServer;
import com.distsys.exc.NoMatchingAssessment;
import com.distsys.exc.UnauthorizedAccess;
import com.sun.tools.javah.Util;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian on 15/02/2016.
 */
public class Examiner {
    private static final String LOGIN_USER = "admin";
    private static final String LOGIN_PASS = "root";

    // The internal reference to the server
    private ExamServer server;
    private Input input;

    // Session information
    private int token;
    private int id;

    public Examiner(ExamServer server) {
        this.server = server;
        this.input = new Input();

        this.run();
    }

    private void run() {
        try {
            // Log in first
            this.attemptLogin();

            boolean exit = false;
            while(!exit) {
                // Display the menu
                try {
                    exit = this.menu();
                } catch (UnauthorizedAccess unauthorizedAccess) {
                    this.run();
                } catch (NoMatchingAssessment noMatchingAssessment) {
                    noMatchingAssessment.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * The menu functions available.
     */
    private static final String[] menuItems = new String[] {
            "Get your assessments",
            "Get assessment for course",
            "Exit"
    };

    /**
     * Print the menu and handle the functions.
     * @return Returns whether to exit the program or not.
     * @throws IOException
     */
    private boolean menu() throws IOException, UnauthorizedAccess, NoMatchingAssessment {
        this.print("Menu: ");
        int i = 0;
        for(String item : Examiner.menuItems) {
            i++;
            this.print(String.format("\t(%d) %s", i, item));
        }

        int selection = this.input.getNumber("Menu item: ");

        switch(Examiner.menuItems[selection - 1]) {
            case "Get your assessments":
                this.getSummary();
                break;

            case "Get assessment for course":
                this.getAssesmentForCourse();
                break;

            case "Exit":
                this.print("Exiting.");
                return true;

            default:
                this.print("Unknown option.");
        }

        return false;
    }

    private void getAssesmentForCourse() throws IOException, UnauthorizedAccess {
        String courseCode = this.input.getString("Course code:");

        try {
            Assessment assessment = this.server.getAssessment(this.token, this.id, courseCode);
            System.out.println(assessment);
        } catch (NoMatchingAssessment noMatchingAssessment) {
            this.print("No matching assessments for that course.");
        }
    }

    private void getSummary() throws NoMatchingAssessment, UnauthorizedAccess, RemoteException {
        ArrayList<String> summaries = (ArrayList<String>) this.server.getAvailableSummary(this.token, this.id);

        for(String summary : summaries) {
            this.print(summary);
        }
    }

    /**
     * Print examiner output.
     * @param message
     */
    private void print(String message) {
        this.input.print(">> " + message);
    }

    /**
     * Print the welcome screen.
     */
    private void welcome() {
        this.input.print("Welcome to the examiner.");
    }

    /**
     * Asks the user for input via the command line.
     * @throws UnauthorizedAccess - If login failed with the server.
     */
    private void login() throws UnauthorizedAccess, IOException {
        this.print("Please Login. Enter your details.");

        int id = this.input.getNumber("Student ID:");
        String password = this.input.getString("Password:");

        this.token = this.server.login(id, password);
        this.id = id;
    }

    /**
     * Repeat logins until successful.
     * @throws IOException
     */
    private void attemptLogin() throws IOException {
        try {
            this.login();
        } catch (UnauthorizedAccess unauthorizedAccess) {
            this.print(String.format("Incorrect credentials. Please try again. Hint: %d/%s", 47200, "root"));
            this.attemptLogin();
        }
    }

    public static void main(String[] args) {
        // Add in the policy file
        System.setProperty("java.security.policy", "global.policy");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            String name = "ExamServer";
            Registry registry = LocateRegistry.getRegistry();

            // Grab our reference to the remote exam server instance
            ExamServer server = (ExamServer) registry.lookup(name);

            // Start the examiner interface
            new Examiner(server);
        } catch (Exception e) {
            System.err.println("ExamServer exception:");
            e.printStackTrace();
        }
    }
}
