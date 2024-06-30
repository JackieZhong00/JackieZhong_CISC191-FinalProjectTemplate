package edu.sdccd.cisc191.template;


import java.io.*;

import javafx.geometry.Pos;
import javafx.scene.*;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
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
 * This program is a server that takes connection requests on
 * the port specified by the constant LISTENING_PORT.  When a
 * connection is opened, the program sends the current time to
 * the connected socket.  The program will continue to receive
 * and process connections until it is killed (by a CONTROL-C,
 * for example).  Note that this server processes each connection
 * as it is received, rather than creating a separate thread
 * to process the connection.
 */
public class Server extends Application {
//    private ServerSocket serverSocket;
//    private Socket clientSocket;
//    private PrintWriter out;
//    private BufferedReader in;

//    public void start(int port) throws Exception {
//        serverSocket = new ServerSocket(port);
//        clientSocket = serverSocket.accept();
//        out = new PrintWriter(clientSocket.getOutputStream(), true);
//        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//
//        String inputLine;
//        while ((inputLine = in.readLine()) != null) {
//            CustomerRequest request = CustomerRequest.fromJSON(inputLine);
//            CustomerResponse response = new CustomerResponse(request.getId(), "Jane", "Doe");
//            out.println(CustomerResponse.toJSON(response));
//        }
//    }

//    public void stop() throws IOException {
//        in.close();
//        out.close();
//        clientSocket.close();
//        serverSocket.close();
//    }

    public void start(Stage stage) {
        Label message = new Label
                ("Study tool - To use: submit a .txt file with questions and answers that you want to quiz yourself with. " +
                        "File must be formatted like this: ");
        message.setFont( new Font(20) );
        message.setStyle("-fx-font-weight: bold");
        message.setStyle("-fx-display: block");
        Label fileFormatDemoLine1 = new Label("Topic: Recursion");
        Label fileFormatDemoLine2 = new Label("Question1 Answer1 Question2 Answer2");
        fileFormatDemoLine1.setFont( new Font(20));
        fileFormatDemoLine2.setFont( new Font(20));
        fileFormatDemoLine1.setStyle(" -fx-display: block");
        fileFormatDemoLine2.setStyle(" -fx-display: block");
        VBox fileDemoVBox = new VBox(50, fileFormatDemoLine1, fileFormatDemoLine2);

        //sorting comes in useful for searching purposes later on
        TreeMap<String, ArrayList<Question>> treeMap = new TreeMap<>();


        Button submitFileButton = new Button("Submit a File");

        submitFileButton.setOnAction(e -> {
            treeMap.clear();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            File file = fileChooser.showOpenDialog(new Stage());
            try {
                Scanner scanner = new Scanner(file);
                while(scanner.hasNextLine()) {
                    String[] inputArray = scanner.nextLine().split(" ");
                    if(inputArray[0].equals("Topic ")) {
                        //this is the key portion of the key-value pair to be added to TreeMap
                        String topic = inputArray[1];
                        //array that holds all question objects, will be the value assigned to they key
                        ArrayList<Question> questions = new ArrayList<>();
                        //array to hold list of questions and answers
                        String[] listOfQuestions = scanner.nextLine().split(" ");
                        for(int i = 0; i < listOfQuestions.length; i+=2) {
                            if (i+1 < listOfQuestions.length) {
                                Question newQuestion = new Question(listOfQuestions[i],listOfQuestions[i+1]);
                                questions.add(newQuestion);
                            }
                        }
                        //insert the key-value pair into TreeMap
                        treeMap.put(topic, questions);
                    }
                }
            } catch (FileNotFoundException error) {
                System.out.println("File not found");
            }});

        Button nextButton = new Button("Start/Next");
        Button revealButton = new Button("Reveal Answer");
        Label questionAnswerLabel = new Label(" ");
        HBox buttonBar = new HBox( 20, submitFileButton, nextButton, revealButton );
        buttonBar.setAlignment(Pos.CENTER);


        nextButton.setOnAction(e -> {
            //generate random number to use to select key from keyList ArrayList
            if (!treeMap.isEmpty()) {
                ArrayList<String> keyList= new ArrayList<>(treeMap.keySet());
                Random random = new Random();
                String key = keyList.get(random.nextInt());
                //use the key to get list of Question objects from treeMap
                ArrayList<Question> questions = treeMap.get(key);
                //generate a random int to get random Question from list of Questions
                Question quizQuestion = questions.get(random.nextInt());
                //set questionAnswerLabel to the question
                questionAnswerLabel.setText(quizQuestion.getQuestion());
                //upon pressing reveal button, set questionAnswerLabel to answer
                revealButton.setOnAction(event -> {
                    questionAnswerLabel.setText(quizQuestion.getAnswer());
                });
            }
        });

        BorderPane root = new BorderPane();
        root.setTop(message);
        root.setCenter(fileDemoVBox);
        root.setCenter(questionAnswerLabel);
        root.setBottom(buttonBar);

        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.setTitle("Study Tool");
        stage.show();
    }

    public static void main(String[] args) {
//        Server server = new Server();
//        try {
//            server.start(4444);
//            server.stop();
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
        launch();

    }
} //end class Server
