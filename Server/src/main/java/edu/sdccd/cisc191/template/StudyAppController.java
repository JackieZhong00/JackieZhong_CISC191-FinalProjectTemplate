package edu.sdccd.cisc191.template;

import java.io.*;
import java.util.*;

/**
 * contains logic that does not depend on GUI components
 */
public class StudyAppController {
    public TreeMap<String, ArrayList<Question>> questionsMap = new TreeMap<>();
    private String message = "";

    public TreeMap<String, ArrayList<Question>> getQuestionsMap() {
        return questionsMap;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    /**
     * reads file line by line to extract questions/answers and to categorize each question under specific topic
     * @param file takes in file that the function is supposed to read from
     * part of submit button's action handler - is called upon submit button click
     */
    public void processInput(File file) {
        this.questionsMap.clear(); //clear map so that new content can be loaded
        try {
            //create scanner and have it read from file
            Scanner scanner = new Scanner(file);
            String topic = null; // Initialize topic outside the loop

            //while scanner has next line, check to see if line is a topic or a question/answer
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                //set topic variable with topic
                //if topic is new to map, create new ArrayList for it
                if (input.startsWith("Topic:")) {
                    topic = input.substring(7).trim(); // Update topic
                    if (!this.questionsMap.containsKey(topic)) {
                        ArrayList<Question> emptyArray = new ArrayList<>();
                        this.questionsMap.put(topic, emptyArray); // Initialize list if new topic
                    }
                } else if (topic != null) {
                    //if line doesn't contain name of topic, must be question/answer
                    try {
                        String questionLine = input; // Current line is question
                        if (scanner.hasNextLine()) {
                            String answerLine = scanner.nextLine(); // Next line is answer
                            Question question = new Question(questionLine, answerLine);
                            this.questionsMap.get(topic).add(question); // Add question to the current topic
                        } else {
                            message = "Couldn't find line with question/answer";
                            System.out.println("Couldn't find line with question/answer");
                            break;
                        }
                    } catch (NoSuchElementException error) {
                        message = "Couldn't find line with question/answer";
                        System.out.println("error: " + error);
                        break;
                    }
                }
            }
            //if message is empty, load these instructions to message
            if (message.isEmpty()) {
                message = "Press start/next button to start quiz";
            }
        } catch (FileNotFoundException error) {
            message = "File not found";
            System.out.println("File not found");
        }
    }

    /**
     * @return random question for user upon a start/next button click
     */
    public Question generateRandomQuestion() {
        if (!questionsMap.isEmpty()) {
            //load all keys to ArrayList
            ArrayList<String> keyList = new ArrayList<>(questionsMap.keySet());
            Random random = new Random();
            //generate random number based on size of ArrayList to pick random key
            String key = keyList.get(random.nextInt(keyList.size()));
            ArrayList<Question> questions = questionsMap.get(key);
            //return random question based on size of ArrayList associated with key
            return questions.get(random.nextInt(questions.size()));
        }
        else {
            return null;
        }
    }

    /**
     * serialize questionsMap and save it to specified file using Java's default serialization
     * @param filename file to save content to
     * @throws IOException if user has no write access or file should not be written to
     */
    public void saveQuestionsMapToFile(String filename) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(filename))) {
            output.writeObject(questionsMap);
        }
    }

    /**
     * deserialize questionsMap from specified file using Java's default serialization
     * @param filename deserialize and load info from this file
     * @throws IOException if user has no read access to file
     * @throws ClassNotFoundException in case the object that is read does not match the object-type that
     * it is cast to
     */
    public void loadQuestionsMapFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(filename))) {
            questionsMap = (TreeMap<String, ArrayList<Question>>) input.readObject();}
    }
}




