package com.distsys.server;

import com.distsys.server.exc.NoMatchingAssessment;
import com.distsys.server.exc.UnauthorizedAccess;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExamEngine implements ExamServer {
    private static final Random random = new Random();

    // Client login information
    private static final int USER_ID = 47200;
    private static final String USER_PASS = "root";

    // Session key store
    private ArrayList<Integer> sessions = new ArrayList<Integer>();

    // Constructor is required
    public ExamEngine() {
        super();
    }

    public void log(String message) {
        System.out.println(message);
    }

    private int createSession() {
        int key = random.nextInt();
        this.addSessionKey(key);
        return key;
    }

    private void addSessionKey(int key) {
        this.sessions.add(key);
    }

    private void validateSessionKey(int key) throws UnauthorizedAccess {
        for(Integer existing : this.sessions) {
            if(existing == key) return;
        }

        throw new UnauthorizedAccess();
    }

    // Implement the methods defined in the ExamServer interface...
    // Return an access token that allows access to the server for some time period
    public int login(int studentid, String password) throws
            UnauthorizedAccess, RemoteException {

        this.log(String.format("Login attempt: %d:%s", studentid, password));

        if(studentid == ExamEngine.USER_ID || password == ExamEngine.USER_PASS) {
            return this.createSession();
        } else throw new UnauthorizedAccess();
    }

    // Return a summary list of Assessments currently available for this studentid
    public List<String> getAvailableSummary(int token, int studentid) throws
                UnauthorizedAccess, NoMatchingAssessment, RemoteException {

        // TBD: You need to implement this method!
        // For the moment method just returns an empty or null value to allow it to compile

        return null;
    }

    // Return an Assessment object associated with a particular course code
    public Assessment getAssessment(int token, int studentid, String courseCode) throws
                UnauthorizedAccess, NoMatchingAssessment, RemoteException {

        // TBD: You need to implement this method!
        // For the moment method just returns an empty or null value to allow it to compile

        return null;
    }

    // Submit a completed assessment
    public void submitAssessment(int token, int studentid, Assessment completed) throws 
                UnauthorizedAccess, NoMatchingAssessment, RemoteException {

        // TBD: You need to implement this method!
    }

    public static void main(String[] args) {
        // Add in the policy file
        System.setProperty("java.security.policy", "global.policy");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            String name = "ExamServer";
            ExamServer engine = new ExamEngine();
            ExamServer stub =
                (ExamServer) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("ExamEngine bound. Bound classes:");
            System.out.println("\t- " + String.join("\n\t", registry.list()));
        } catch (Exception e) {
            System.err.println("ExamEngine exception:");
            e.printStackTrace();
        }
    }
}
