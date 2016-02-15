package com.distsys.client;

import com.distsys.server.ExamServer;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Created by adrian on 15/02/2016.
 */
public class Examiner {
    private static final String LOGIN_USER = "admin";
    private static final String LOGIN_PASS = "root";

    public static void main(String[] args) {
        // Add in the policy file
        System.setProperty("java.security.policy", "global.policy");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "ExamServer";
            Registry registry = LocateRegistry.getRegistry();
            ExamServer server = (ExamServer) registry.lookup(name);

            System.out.println(server.login(12453, "root"));
        } catch (Exception e) {
            System.err.println("ExamServer exception:");
            e.printStackTrace();
        }

    }
}
