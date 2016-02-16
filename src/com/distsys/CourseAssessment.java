package com.distsys;

import com.distsys.exc.InvalidOptionNumber;
import com.distsys.exc.InvalidQuestionNumber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CourseAssessment implements Assessment {
    private int studentid;
    private String code;
    private String name;
    private Date closingDate;
    private ArrayList<Question> questions = new ArrayList<>();

    /**
     * Create a new course assessment that's due five days from now.
     * @param studentid
     * @param courseCode
     * @param name
     */
    public CourseAssessment(int studentid, String courseCode, String name) {
        this(studentid, courseCode, name, null);

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 5);  // number of days to add
        this.closingDate = c.getTime();
    }

    /**
     * Create a new course assessment for a student.
     * @param studentid
     * @param courseCode
     * @param name
     * @param closingDate
     */
    public CourseAssessment(int studentid, String courseCode, String name, Date closingDate) {
        this.studentid = studentid;
        this.code = courseCode;
        this.name = name;
        this.closingDate = closingDate;
    }

    /**
     * Get the course code string.
     * @return String
     */
    public String getCourseCode() {
        return code;
    }

    @Override
    public String getInformation() {
        SimpleDateFormat fmt = new SimpleDateFormat("E, d-M-y");
        return String.format("%s - %s (due on %s)", this.code, this.name, fmt.format(this.closingDate));
    }

    @Override
    public Date getClosingDate() {
        return this.closingDate;
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

    /**
     * Add a question to the assessment.
     * @param question
     */
    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    /**
     * Return a simple summary of the course.
     * @return String
     */
    public String summary() {
        return String.format(this.getInformation(), this.studentid);
    }
}
