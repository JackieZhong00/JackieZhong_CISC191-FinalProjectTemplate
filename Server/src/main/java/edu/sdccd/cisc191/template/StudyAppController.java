package edu.sdccd.cisc191.template;
import java.sql.*;
import java.io.*;
import java.util.*;

/**
 * contains logic that does not depend on GUI components
 */
public class StudyAppController {
    public HashMap<String, ArrayList<Question>> questionsMap = new HashMap<>();
    public QuestionsLinkedList newlyAddedQuestions = new QuestionsLinkedList();
    public Question questionToImmediatelyAdd;
    public Question[] sortedList;
    public ArrayList<Question> topicSpecificList;
    public int sortedListIndex = 0;
    private String message = "";
    public boolean sortedScroll = false;
    public boolean sortedTopicScroll = false;

    /**
     * @return returns the entire hashmap, keys = topics, arraylist of questions = questions
     */
    public HashMap<String, ArrayList<Question>> getQuestionsMap() {
        return questionsMap;
    }

    /**
     * @param message to set to a current question/answer
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * method to toggle sorted scroll
     */
    public void toggleSortedScroll() {
        this.sortedScroll = !this.sortedScroll;
        if (this.sortedScroll) {
            topicSortedQuestions();
        }
    }

    /**
     * @return message that current question should hold
     */
    public String getMessage() {
        return message;
    }

    /**
     * use stream to assign to sortedList instance variable of this controller
     * to array of questions organized in alphabetical order of their topics
     */
    public void topicSortedQuestions() {
        sortedList = questionsMap.values().stream().flatMap(Collection::stream)
                .toArray(Question[]::new);
    }


    /**
     * generates next question when in sort mode
     * @return question that is next is topic sorted order
     */
    public Question nextSortedQuestion() {
        if (this.sortedListIndex < this.sortedList.length) {
            Question question = this.sortedList[this.sortedListIndex];
            this.sortedListIndex ++;
            return question;
        }
        else {
            this.sortedListIndex = 0;
            return this.sortedList[0];
        }
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
            if(!questions.isEmpty()) {
                return questions.get(random.nextInt(questions.size()));
            }
            else {
                System.out.println("Question list is empty");
                return null;
            }
            //return random question based on size of ArrayList associated with key
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
            questionsMap = (HashMap<String, ArrayList<Question>>) input.readObject();}
    }

    /**
     * saves newly added question immediately to database
     */
    public void saveOneQuestionToDatabase() {
        String insertSQL = "INSERT INTO StudyQuestions (Topic, Question, Answer) VALUES (?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            // Set the values for the SQL query
            preparedStatement.setString(1, questionToImmediatelyAdd.getTopic());
            preparedStatement.setString(2, questionToImmediatelyAdd.getQuestion());
            preparedStatement.setString(3, questionToImmediatelyAdd.getAnswer());

            // Execute the insert operation
            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("A new question was inserted successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * save all questions in hashmap to database - only used when new file is submitted
     */
    public void saveQuestionsToDatabase() {
        String insertSQL = "INSERT INTO StudyQuestions (Topic, Question, Answer) VALUES (?, ?, ?)";
        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {

            // Iterate over the questionsMap
            for (Map.Entry<String, ArrayList<Question>> entry : questionsMap.entrySet()) {
                String topic = entry.getKey();
                ArrayList<Question> questions = entry.getValue();

                // Insert each question under the current topic
                for (Question question : questions) {
                    preparedStatement.setString(1, topic);
                    preparedStatement.setString(2, question.getQuestion());
                    preparedStatement.setString(3, question.getAnswer());
                    preparedStatement.addBatch();  // Add to batch
                }
            }

            // Execute batch insert
            preparedStatement.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * fetches questions from database and inputs questions into hashmap
     */
    public void getQuestionsFromDb() {
        String selectSQL = "SELECT Topic, Question, Answer FROM StudyQuestions";

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Clear the current map to avoid duplication
            questionsMap.clear();

            // Iterate through the ResultSet
            while (resultSet.next()) {
                String topic = resultSet.getString("Topic");
                String questionText = resultSet.getString("Question");
                String answerText = resultSet.getString("Answer");

                // Create a new Question object
                Question question = new Question(questionText, answerText);

                // Check if the topic already exists in the map
                if (!questionsMap.containsKey(topic)) {
                    questionsMap.put(topic, new ArrayList<>());
                }

                // Add the question to the corresponding topic list
                questionsMap.get(topic).add(question);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}




