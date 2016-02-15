package com.distsys;

/**
 * Created by adrian on 15/02/2016.
 */
public class AssessmentQuestion implements Question {
    private int index;
    private String content;
    private String[] answers;
    private int correctAnswer;
    private int selected;

    public AssessmentQuestion(int index, String content, String[] answers, int correctAnswer) {
        this.index = index;
        this.content = content;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    @Override
    public int getQuestionNumber() {
        return 0;
    }

    @Override
    public String getQuestionDetail() {
        return null;
    }

    @Override
    public String[] getAnswerOptions() {
        return new String[0];
    }

    public void select(int selection) {
        this.selected = selection;
    }

    public boolean isCorrect() {
        return this.selected == this.correctAnswer;
    }

    public int getSelected() {
        return selected;
    }
}
