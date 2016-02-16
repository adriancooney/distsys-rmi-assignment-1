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

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    @Override
    public int getQuestionNumber() {
        return this.index;
    }

    @Override
    public String getQuestionDetail() {
        return this.content;
    }

    @Override
    public String[] getAnswerOptions() {
        return this.answers;
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
