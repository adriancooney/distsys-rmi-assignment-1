package com.distsys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 * Created by adrian on 15/02/2016.
 */
public class Input {
    BufferedReader line;

    public Input() {
        this.line = new BufferedReader(new InputStreamReader(System.in));
    }

    public void print(String message) {
        this.print(message, true);
    }

    public void print(String message, boolean newline) {
        if(newline) System.out.println(message);
        else System.out.print(message);
    }

    public String ask(String message) throws IOException {
        this.print(message);
        return this.line.readLine();
    }

    public int getNumber(String message) throws IOException {
        return this.getValue(message, "Number", Integer::parseInt);
    }

    public String getString(String message) throws IOException {
        return this.getValue(message, "String", (str) -> str);
    }

    public <T> T getValue(String message, String typeName, InputParser<T> parser) throws IOException {
        this.print(message + " ", false);
        String line = this.line.readLine();

        try {
            return parser.parse(line);
        } catch (Exception e) {
            this.print(String.format("Invalid input. Please provide a %s.", typeName));
            return this.getValue(message, typeName, parser);
        }
    }
}

interface InputParser<T> {
    T parse(String input);
}
