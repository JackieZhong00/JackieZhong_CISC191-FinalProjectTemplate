package edu.sdccd.cisc191.template;


import java.io.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;
import javafx.application.Application;





/**
 * This is Server/Root class that represents the Study Tool application,
 * which allows users to pass in files with test questions and answers
 * that they want to use to test themselves with.
 */
public class Server extends Application {

    /**
     *
     * @param stage is window used to display GUI of application
     * starts up the application in a new window
     */
    public void start(Stage stage) {
        Label instructions = new Label
                ("To use: submit a .txt file w/ questions and answers that you want to quiz yourself with. \n " +
                        "File must be formatted like this: ");
        instructions.setFont( new Font(20) );
        instructions.setStyle("-fx-font-weight: bold");
        instructions.setStyle("-fx-display: block");

        //label that demonstrates to user how to format file for submission
        Label fileFormatDemoLine = new Label("Topic: Recursion \n" + "Question1\n" + "Answer1\n" +
                "Topic: Data Structures\n"+"Question2\n" + "Answer2\n");
        //set Font, display, and margin property for fileFormatDemo label
        fileFormatDemoLine.setFont( new Font(18));
        fileFormatDemoLine.setStyle(" -fx-display: block");
        VBox fileDemoVBox = new VBox(fileFormatDemoLine);
        VBox.setMargin(fileDemoVBox, new Insets(25, 0, 0, 0));

        //visit next quiz question and reveal answer buttons
        Button nextButton = new Button("Start/Next");
        Button revealButton = new Button("Reveal Answer");

        //empty label that will later on be assigned quiz question/answer
        Label questionAnswerLabel = new Label(" ");
        questionAnswerLabel.setFont(new Font(18));
        VBox questionAnswerVBox = new VBox(questionAnswerLabel);
        VBox.setMargin(questionAnswerVBox, new Insets(100, 0, 100, 0));
        questionAnswerVBox.setStyle("-fx-border-color: black");
        questionAnswerLabel.setPrefHeight(300.0);

        //button to allow user to submit file with questions and answers
        Button submitFileButton = new Button("Submit a File");
        HBox buttonBar = new HBox( 20, submitFileButton, nextButton, revealButton );
        buttonBar.setAlignment(Pos.CENTER);


        //sorting comes in useful for searching purposes later on
        TreeMap<String, ArrayList<Question>> treeMap = new TreeMap<>();



        submitFileButton.setOnAction(e -> {
            treeMap.clear();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            File file = fileChooser.showOpenDialog(new Stage());
            try {
                Scanner scanner = new Scanner(file);
                while(scanner.hasNextLine()) {
                    String[] inputArray = scanner.nextLine().split(" ");
                    System.out.println(inputArray[0]);
                    System.out.println(inputArray[1]);
                    if(inputArray[0].equals("Topic:")) {
                        //this is the key portion of the key-value pair to be added to TreeMap
                        String topic = inputArray[1];
                        //array that holds the Question objects
                        if(treeMap.containsKey(topic)) {
                            ArrayList<Question> questionArr = treeMap.get(topic);
                            String questionLine = scanner.nextLine();
                            String answerLine = scanner.nextLine();
                            questionArr.add(new Question(questionLine, answerLine));
                            treeMap.put(topic, questionArr);
                        }
                        else {
                            String questionLine = scanner.nextLine();
                            String answerLine = scanner.nextLine();
                            Question question = new Question(questionLine, answerLine);
                            ArrayList<Question> questionArr = new ArrayList<>();
                            questionArr.add(question);
                            treeMap.put(topic, questionArr);
                        }
                    }
                    else {
                        System.out.println("Incorrect format detected");
                    }
                    questionAnswerLabel.setText("press start/next button to start quiz");
                }
            } catch (FileNotFoundException error) {
                System.out.println("File not found");
            }});




        nextButton.setOnAction(e -> {
            //generate random number to use to select key from keyList ArrayList
            if (!treeMap.isEmpty()) {
                ArrayList<String> keyList= new ArrayList<>(treeMap.keySet());
                Random random = new Random();
                String key = keyList.get(random.nextInt(keyList.size()));
                //use the key to get list of Question objects from treeMap
                ArrayList<Question> questions = treeMap.get(key);
                //generate a random int to get random Question from list of Questions
                Question quizQuestion = questions.get(random.nextInt(questions.size()));
                //set questionAnswerLabel to the question
                questionAnswerLabel.setText(quizQuestion.getQuestion());
                //upon pressing reveal button, set questionAnswerLabel to answer
                revealButton.setOnAction(event -> {
                    questionAnswerLabel.setText(quizQuestion.getAnswer());
                });
            }
        });

        VBox root = new VBox(instructions, fileDemoVBox, questionAnswerVBox, buttonBar);


        Scene scene = new Scene(root, 750, 750);
        stage.setScene(scene);
        stage.setTitle("Study Tool");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
} //end class Server
