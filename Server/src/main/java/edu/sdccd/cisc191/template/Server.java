package edu.sdccd.cisc191.template;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

import javafx.application.Application;
import org.postgresql.jdbc2.ArrayAssistant;

/**
 * Server is a Study Application that launches a GUI for users to scroll through quiz questions/answers
 */
public class Server extends Application {
    private final StudyAppController logic = new StudyAppController();
    private Label questionAnswersLabel = new Label("");
    private Button revealButton = new Button("Reveal Answer");
    public Question currentQuestion = new Question("","");
    private ExecutorService executorService;

    /**
     * @param stage
     * contains and formats GUI components that make up the app
     */
    @Override
    public void start(Stage stage) {
        //start with loading from file called StudyAppContent
        //if file doesn't exist, means it's the user's first time using the app

        //load first from db, then from file if db doesn't have any content
        try {
            logic.getQuestionsFromDb();
        }
        catch (Exception e){
            System.out.println("No data exists in db");
        }
        if(logic.questionsMap.isEmpty()){
            try {
                logic.loadQuestionsMapFromFile("StudyAppContent");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("No previous data found or failed to load data.");
            }
        }

        //create text field to concurrently generate random topics out of hashmap for user to select
        TextField topicField = new TextField();
        topicField.setEditable(false); //user should not be able to edit this field

        //implement the callable thread, pass in the hashmap and text field for manipulation
        TopicDisplayCallable callable = new TopicDisplayCallable(logic.questionsMap, topicField);
        //executive this single thread
        executorService = Executors.newSingleThreadExecutor();
        Future<Void> future = executorService.submit(callable);


        //labels for textfields for entering in new question and answers
        Label newQuestionAnswerLabel = new Label("Input a new question + answer");
        Label newTopicLabel = new Label("Topic");
        TextField newTopicTextField = new TextField();
        Label questionTextFieldLabel = new Label("Question");
        TextField questionTextField = new TextField();
        Label answerTextFieldLabel = new Label("Answer");
        TextField answerTextField = new TextField();

        //buttons for adding question to session, to database, selecting topics
        Button addButton = new Button("Add to session");
        Button saveToDbButton = new Button("Save to database");
        Button pickTopicButton = new Button("Pick topic");

        //organize buttons
        HBox newQuestionButtonBox = new HBox(addButton, saveToDbButton, topicField, pickTopicButton);
        VBox newQuestionAnswerBox = new VBox(newQuestionAnswerLabel,newTopicLabel,newTopicTextField,
                questionTextFieldLabel, questionTextField, answerTextFieldLabel, answerTextField, newQuestionButtonBox);

        //sets topicSpecificList in controller to ArrayList<Question> of the topic
        //toggle sortedTopicScroll so that nextButton's action knows which process to use
        //change text of button text to represent toggle state
        pickTopicButton.setOnAction( e -> {
            logic.topicSpecificList = logic.questionsMap.get(topicField.getText());
            logic.sortedTopicScroll = !logic.sortedTopicScroll;
            logic.sortedScroll = false;
            if(logic.sortedTopicScroll) {
                pickTopicButton.setText("Reset");
            }
            else {
                pickTopicButton.setText("Pick topic");
            }
        });

        //save the hashmap's content to file and add content to db
        saveToDbButton.setOnAction( e -> {
            //if the input field is empty, just save the current content to file
            if(questionTextField.getText().equals("")){
                try {
                    logic.saveQuestionsMapToFile("StudyAppContent");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            //if input field isn't empty, extract it to create new question, clear fields
            else{
                Question newQuestion = new Question(newTopicTextField.getText(),
                        questionTextField.getText(),answerTextField.getText());
                logic.newlyAddedQuestions.add(newQuestion);
                newTopicTextField.clear();
                questionTextField.clear();
                answerTextField.clear();
                if(!logic.questionsMap.containsKey(newQuestion.getTopic())) {
                    ArrayList<Question> arr = new ArrayList<>();
                    arr.add(newQuestion);
                    logic.questionsMap.put(newQuestion.getTopic(), arr);
                }
                else {
                    logic.questionsMap.get(newQuestion.getTopic()).add(newQuestion);
                }
                //save the question to db
                logic.questionToImmediatelyAdd = newQuestion;
                logic.saveOneQuestionToDatabase();
            }
        });

        //creates label to display app instructions, styled with specific font and font-weight
        Label instructions = new Label("To use: submit a .txt file w/ questions and answers that you want to quiz yourself with. \n File must be formatted like this: \n");
        instructions.setFont(new Font(18));
        instructions.setStyle("-fx-font-weight: bold");

        //displays how files should be formatted for proper processing after user submission
        Label fileFormatDemoLine = new Label("Topic: Recursion \nQuestion1\nAnswer1\nQuestion2\nAnswer2\n" +
                "Topic: Data Structures\nQuestion2\nAnswer2\n");
        fileFormatDemoLine.setFont(new Font(18));
        VBox fileDemoVBox = new VBox(fileFormatDemoLine);
//        VBox.setMargin(fileDemoVBox, new Insets(10, 0, 0, 0));

        //button for user to start quiz and to scroll to next question
        Button nextButton = new Button("Start/Next");
        //sort button
        Button sortButton = new Button("Sort/Unsort");
        sortButton.setOnAction(e -> sortedButtonAction());


        //styling for label that is used to display question/answer, including margin, border color, height
        questionAnswersLabel.setFont(new Font(18));
        VBox questionAnswerVBox = new VBox(questionAnswersLabel);
        VBox.setMargin(questionAnswerVBox, new Insets(0, 0, 20, 0));
        questionAnswerVBox.setStyle("-fx-border-color: black");
        questionAnswersLabel.setPrefHeight(300.0);

        //button used to open dialog box to allow user to submit file
        Button submitFileButton = new Button("Submit a File");

        //all buttons are grouped together into HBox and aligned to center of window
        HBox buttonBar = new HBox(20, submitFileButton, nextButton, revealButton, sortButton);
        buttonBar.setAlignment(Pos.CENTER);

        //action handler that opens File Chooser dialog box to allow user submission
        //ends with saving map containing study content to StudyAppContent file on disk
        submitFileButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                logic.processInput(file);
                updateLabel();
            }
            try {
                logic.saveQuestionsMapToFile("StudyAppContent");
                logic.saveQuestionsToDatabase();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        //adds the inputted question to current session
        addButton.setOnAction( e -> {
            Question newQuestion = new Question(newTopicTextField.getText(),
                    questionTextField.getText(),answerTextField.getText());
            logic.newlyAddedQuestions.add(newQuestion);
            newTopicTextField.clear();
            questionTextField.clear();
            answerTextField.clear();
            try {
                if(logic.questionsMap.containsKey(newQuestion.getTopic())) {
                    ArrayList<Question> arr = logic.questionsMap.get(newQuestion.getTopic());
                    arr.add(newQuestion);
                }
                else {
                    ArrayList<Question> arr = new ArrayList<>();
                    arr.add(newQuestion);
                    logic.questionsMap.put(newQuestion.getTopic(),arr);
                }
            }
            catch (Exception error){
                System.out.println("Couldn't add question to queue, error was: " + e);
            }
        });

        //set action handlers to nextButton and revealButton - functions are class methods
        nextButton.setOnAction(e -> nextButtonAction());
        revealButton.setOnAction(e -> revealButtonAction() );


        //use VBox to organize all components vertically, then set it as root of Scene
        VBox root = new VBox(instructions, fileDemoVBox, newQuestionAnswerBox, questionAnswerVBox, buttonBar);
        Scene scene = new Scene(root, 750, 750);

        stage.setOnCloseRequest(event -> {
            executorService.shutdownNow(); // Stop the thread when the application closes
        });
        //set Scene to the stage and show Stage
        stage.setScene(scene);
        stage.setTitle("Study Tool");
        stage.show();
    }

    //updates the questionAnswersLabel with the correct message,
    //can be question/answer or error or instruction
    private void updateLabel() {
        questionAnswersLabel.setText(logic.getMessage());
    }

    //toggles the sorted button state
    private void sortedButtonAction() {
        logic.toggleSortedScroll();
    }
    //generates random question and sets questionAnswersLabel with the question upon action
    private void nextButtonAction() {
        //if sort button is pressed, topics are alphabetically sorted
        //and questions are generated in that order
        if(logic.sortedScroll) {
            try {
                currentQuestion = logic.nextSortedQuestion();
                logic.setMessage(currentQuestion.getQuestion());
                updateLabel();
            } catch (Exception e){
                System.out.println("error: " + e);
            }
        }
        //if topic is selected, only questions of that topic is generated
        else if (logic.sortedTopicScroll) {
            try {
                Random rand = new Random();
                currentQuestion = logic.topicSpecificList.get(rand.nextInt(logic.topicSpecificList.size()));
                logic.setMessage(currentQuestion.getQuestion());
                updateLabel();
            } catch (Exception e){
                System.out.println("error: " + e);
            }
        }
        //generate random question from entire pool of questions, disregarding topic
        else {
            try {
                currentQuestion = logic.generateRandomQuestion();
                logic.setMessage(currentQuestion.getQuestion());
                updateLabel();
            } catch (Exception e){
                System.out.println("error: " + e);
            }
        }
    }

    //sets questionAnswersLabel with the answer of specific question upon action
    private void revealButtonAction() {
        logic.setMessage(currentQuestion.getAnswer());
        updateLabel();
    }


    //creates and launches GUI object
    public static void main(String[] args) {
        launch(args);
    }
}
