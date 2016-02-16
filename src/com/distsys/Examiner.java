package com.distsys;

import com.distsys.ExamServer;
import com.distsys.exc.InvalidOptionNumber;
import com.distsys.exc.NoMatchingAssessment;
import com.distsys.exc.UnauthorizedAccess;
import com.sun.tools.javah.Util;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
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

    /**
     * Run the program.
     */
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
        "List your assessments",
        "Complete an assessment",
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
            case "List your assessments":
                this.getSummary();
                break;

            case "Complete an assessment":
                this.getAssessmentForCourse();
                break;

            case "Exit":
                this.print("Exiting.");
                return true;

            default:
                this.print("Unknown option.");
        }

        return false;
    }

    /**
     * Get an assessment for a course.
     * @throws IOException
     * @throws UnauthorizedAccess
     */
    private void getAssessmentForCourse() throws IOException, UnauthorizedAccess {
        String courseCode = this.input.getString("Course code:");

        try {
            CourseAssessment assessment = (CourseAssessment) this.server.getAssessment(this.token, this.id, courseCode);
            this.completeAssessment(assessment);
            this.server.submitAssessment(this.token, this.id, assessment);
        } catch (NoMatchingAssessment noMatchingAssessment) {
            this.print("No matching assessments for that course.");
        }
    }

    /**
     * Complete an assessment for a student.
     * @param assessment
     * @throws IOException
     */
    private void completeAssessment(CourseAssessment assessment) throws IOException {
        List<Question> assessmentQuestions = assessment.getQuestions();
        LinkedList<Question> questions = new LinkedList<>(assessmentQuestions);

        this.print("Starting assessment for:");
        this.print(assessment.summary());
        for(Question question : assessmentQuestions) {
            this.askQuestion((AssessmentQuestion) question);
        }

        this.print("Assessment complete.");
        int correct = 0;
        int incorrect = 0;
        int total = assessmentQuestions.size();

        for(int i = 0; i < total; i++) {
            AssessmentQuestion completedQuestion = (AssessmentQuestion) assessmentQuestions.get(i);

            if(completedQuestion.isCorrect()) correct++;
            else incorrect++;
        }

        this.print(String.format("Correct: %d, incorrect: %d, total: %d, grade: %s", correct, incorrect, total, Examiner.grade(correct/(double) total)));
    }

    /**
     * Simple grading function to a letter for a numeric grade.
     * @param score
     * @return
     */
    private static String grade(double score) {
        if(score < 0.4) return "D";
        else if(score < 0.5) return "C";
        else if(score < 0.6) return "B";
        else return "A";
    }

    /**
     * Ask a question and recieve update the answer (on the question object).
     * @param question
     * @throws IOException
     */
    private void askQuestion(AssessmentQuestion question) throws IOException {
        this.input.print(String.format("[%d] %s", question.getQuestionNumber(), question.getQuestionDetail()));

        int i = 0;
        String[] answers = question.getAnswerOptions();
        for(String answer : answers) {
            this.input.print(String.format("\t(%d) %s" + (question.getCorrectAnswer() == i ? " *" : ""), ++i, answer));
        }

        int answer = this.input.getNumber("Answer number:");

        if(answer < 1 || answer > answers.length) {
            this.print("Invalid option. Please select try again.");
            this.askQuestion(question);
            return;
        }

        question.select(answer - 1);
    }

    /**
     * Return a summary of all the assessments.
     * @throws NoMatchingAssessment
     * @throws UnauthorizedAccess
     * @throws RemoteException
     */
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
        this.input.print(">> " + String.join("\n>> ", message.split("\n")));
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
