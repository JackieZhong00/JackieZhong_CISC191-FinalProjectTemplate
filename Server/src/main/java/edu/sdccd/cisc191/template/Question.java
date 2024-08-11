package edu.sdccd.cisc191.template;

import java.io.Serializable;

/**
 * class that represents what a question object should contain
 */
public class Question implements Serializable {
    private String topic;
    private String question;
    private String answer;

    public Question(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
    public Question(String topic, String question, String answer) {
        this.topic = topic;
        this.question = question;
        this.answer = answer;
    }

    public String getTopic() {
        return topic;
    }
    public String getQuestion() {
        return question;
    }
    public void setQuestion(String question) {
        this.question = question;
    }
    public String getAnswer() {
        return answer;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }
}
