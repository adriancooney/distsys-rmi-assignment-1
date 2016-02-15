package com.distsys;

import com.distsys.exc.InvalidOptionNumber;
import com.distsys.exc.InvalidQuestionNumber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by adrian on 15/02/2016.
 */
public class CourseAssessment implements Assessment {
    private int studentid;
    private String code;
    private String name;
    private Date closingDate;
    private ArrayList<Question> questions = new ArrayList<>();

    public CourseAssessment(int studentid, String courseCode, String name) {
        this(studentid, courseCode, name, null);

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 5);  // number of days to add
        this.closingDate = c.getTime();
    }

    public CourseAssessment(int studentid, String courseCode, String name, Date closingDate) {
        this.studentid = studentid;
        this.code = courseCode;
        this.name = name;
        this.closingDate = closingDate;
    }

    public String getCourseCode() {
        return code;
    }

    @Override
    public String getInformation() {
        return String.format("%s - %s", this.code, this.name);
    }

    @Override
    public Date getClosingDate() {
        return this.closingDate;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    @Override
    public List<Question> getQuestions() {
        return this.questions;
    }

    @Override
    public Question getQuestion(int questionNumber) throws InvalidQuestionNumber {
        return this.questions.get(questionNumber);
    }

    @Override
    public void selectAnswer(int questionNumber, int optionNumber) throws InvalidQuestionNumber, InvalidOptionNumber {
        ((AssessmentQuestion) this.getQuestion(questionNumber)).select(optionNumber);
    }

    @Override
    public int getSelectedAnswer(int questionNumber) throws InvalidQuestionNumber {
        return ((AssessmentQuestion) this.getQuestion(questionNumber)).getSelected();
    }

    @Override
    public int getAssociatedID() {
        return this.studentid;
    }

    public String summary() {
        return String.format(this.getInformation() + String.format("\n Questions: %d\t Student: %d", this.questions.size(), this.studentid));
    }
}
