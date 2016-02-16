package com.distsys;

import com.distsys.exc.NoMatchingAssessment;
import com.distsys.exc.UnauthorizedAccess;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ExamEngine implements ExamServer {
    private static final Random random = new Random();

    // Client login information
    private static final int USER_ID = 47200;
    private static final String USER_PASS = "root";

    // Session key store
    private ArrayList<Session> sessions = new ArrayList<>();

    // Hold the assessments
    private ArrayList<CourseAssessment> assessments = new ArrayList<>();

    // Constructor is required
    public ExamEngine() {
        super();
    }

    public void log(String message) {
        System.out.println(message);
    }

    /**
     * Create a new session for a student.
     * @param id The student id.
     * @return The new session object.
     */
    private Session createSession(int id) {
        Session session = new Session(random.nextInt(), id);
        this.addSession(session);
        return session;
    }

    /**
     * Add a session object to the sessions list.
     * @param session
     */
    private void addSession(Session session) {
        this.sessions.add(session);
    }

    /**
     * Return a session by key.
     * @param key
     * @return Session
     * @throws UnauthorizedAccess if session does not exist.
     */
    private Session validateSession(int key) throws UnauthorizedAccess {
        for(Session session : this.sessions) {
            if(session.token == key) return session;
        }

        throw new UnauthorizedAccess();
    }

    /**
     * Log a student in a return their token.
     * @param studentid The student id.
     * @param password The password.
     * @return The session token (int).
     * @throws UnauthorizedAccess If login fails.
     * @throws RemoteException
     */
    public int login(int studentid, String password) throws
            UnauthorizedAccess, RemoteException {

        this.log(String.format("Login attempt: %d:%s", studentid, password));

        if(studentid == ExamEngine.USER_ID && password.equals(ExamEngine.USER_PASS)) {
            Session userSession = this.createSession(studentid);

            this.generateData(studentid);

            return userSession.token;
        } else throw new UnauthorizedAccess();
    }

    // Return a summary list of Assessments currently available for this studentid
    public List<String> getAvailableSummary(int token, int studentid) throws
                UnauthorizedAccess, NoMatchingAssessment, RemoteException {

        // Validate the session
        this.validateSession(token);

        ArrayList<String> summaries = new ArrayList<>();

        for(CourseAssessment assessment : this.assessments) {
            summaries.add(assessment.summary());
        }

        return summaries;
    }

    public void addAssessment(CourseAssessment assessment) {
        this.assessments.add(assessment);
    }

    // Return an Assessment object associated with a particular course code
    public Assessment getAssessment(int token, int studentid, String courseCode) throws
                UnauthorizedAccess, NoMatchingAssessment, RemoteException {

        // Validate the session
        this.validateSession(token);

        for(CourseAssessment assessment : this.assessments) {
            if(assessment.getAssociatedID() == studentid && assessment.getCourseCode().equals(courseCode))
                return assessment;
        }

        throw new NoMatchingAssessment();
    }

    // Submit a completed assessment
    public void submitAssessment(int token, int studentid, Assessment completed) throws 
                UnauthorizedAccess, NoMatchingAssessment, RemoteException {

        // Validate the session
        this.validateSession(token);

        CourseAssessment assessment = (CourseAssessment) this.getAssessment(token, studentid, ((CourseAssessment) completed).getCourseCode());
        this.assessments.remove(assessment);
    }

    /**
     * Generate courses assessments and questions for a student.
     * @param studentid
     */
    private void generateData(int studentid) {
        // Quick check to see if we have already generated data for this user
        for(CourseAssessment assessment : this.assessments) {
            if(studentid == assessment.getAssociatedID()) return;
        }

        String[] courseNames = new String[] {
            "Maths",
            "Dyamaths",
            "Interopology",
            "Skelotosis",
            "Mixology",
            "Particoxonomy"
        };

        String[] courseCodes = new String[] {
            "MA",
            "FO",
            "PT",
            "CT",
            "RM"
        };

        String[] answers = new String[] {
            "Yes",
            "No",
            "None of the above",
            "All of the above" // Hehe, paradoxial
        };

        String[] questions = new String[] {
            "How many is one of many?",
            "How long is a piece of string?",
            "How much wood does a wood chuck chuck?",
            "Why is Kanye West such an idiot?",
            "Why do so many people eat bananas?"
        };

        for(int i = 0; i < courseCodes.length; i++) {
            CourseAssessment assessment = new CourseAssessment(studentid, courseCodes[i], courseNames[i]);

            for(int u = 0; u < 5; u++) {
                assessment.addQuestion(new AssessmentQuestion(u, questions[u], answers, ExamEngine.random.nextInt(4)));
            }

            this.addAssessment(assessment);
        }
    }

    public static void main(String[] args) {
        // Add in the policy file instead of using a command flag
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

    class Session {
        public int token;
        public int id;

        public Session(int token, int id) {
            this.token = token;
            this.id = id;
        }
    }
}
