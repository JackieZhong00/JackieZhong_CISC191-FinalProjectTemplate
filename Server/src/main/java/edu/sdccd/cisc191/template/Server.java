package edu.sdccd.cisc191.template;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;

/**
 * Server is a Study Application that launches a GUI for users to scroll through quiz questions/answers
 */
public class Server extends Application {
    private StudyAppController logic = new StudyAppController();
    private Label questionAnswersLabel = new Label("");
    private Button revealButton = new Button("Reveal Answer");
    public Question currentQuestion = new Question("","");

    /**
     * @param stage
     * contains and formats GUI components that make up the app
     */
    @Override
    public void start(Stage stage) {
        //start with loading from file called StudyAppContent
        //if file doesn't exist, means it's the user's first time using the app
        try {
            logic.loadQuestionsMapFromFile("StudyAppContent");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No previous data found or failed to load data.");
        }

        //creates label to display app instructions, styled with specific font and font-weight
        Label instructions = new Label("To use: submit a .txt file w/ questions and answers that you want to quiz yourself with. \n File must be formatted like this: \n");
        instructions.setFont(new Font(18));
        instructions.setStyle("-fx-font-weight: bold");

        //displays how files should be formatted for proper processing after user submission
        Label fileFormatDemoLine = new Label("Topic: Recursion \nQuestion1\nAnswer1\nQuestion2\nAnswer2\n" +
                "Topic: Data Structures\nQuestion2\nAnswer2\n");
        fileFormatDemoLine.setFont(new Font(18));
        VBox fileDemoVBox = new VBox(fileFormatDemoLine);
        VBox.setMargin(fileDemoVBox, new Insets(10, 0, 0, 0));

        //button for user to start quiz and to scroll to next question
        Button nextButton = new Button("Start/Next");

        //styling for label that is used to display question/answer, including margin, border color, height
        questionAnswersLabel.setFont(new Font(18));
        VBox questionAnswerVBox = new VBox(questionAnswersLabel);
        VBox.setMargin(questionAnswerVBox, new Insets(90, 0, 100, 0));
        questionAnswerVBox.setStyle("-fx-border-color: black");
        questionAnswersLabel.setPrefHeight(300.0);

        //button used to open dialog box to allow user to submit file
        Button submitFileButton = new Button("Submit a File");

        //all buttons are grouped together into HBox and aligned to center of window
        HBox buttonBar = new HBox(20, submitFileButton, nextButton, revealButton);
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
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        //set action handlers to nextButton and revealButton - functions are class methods
        nextButton.setOnAction(e -> nextButtonAction());
        revealButton.setOnAction(e -> revealButtonAction() );

        //use VBox to organize all components vertically, then set it as root of Scene
        VBox root = new VBox(instructions, fileDemoVBox, questionAnswerVBox, buttonBar);
        Scene scene = new Scene(root, 750, 750);

        //set Scene to the stage and show Stage
        stage.setScene(scene);
        stage.setTitle("Study Tool");
        stage.show();
    }

    //updates the questionAnswersLabel with the correct message,
    //can be question/answer or error or insstruction
    private void updateLabel() {
        questionAnswersLabel.setText(logic.getMessage());
    }

    //generates random question and sets questionAnswersLabel with the question upon action
    private void nextButtonAction() {
        try {
            currentQuestion = logic.generateRandomQuestion();
            logic.setMessage(currentQuestion.getQuestion());
            updateLabel();
        } catch (Exception e){
            System.out.println("error: " + e);
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
