package edu.sdccd.cisc191;

import edu.sdccd.cisc191.template.Question;
import edu.sdccd.cisc191.template.QuestionsLinkedList;
import edu.sdccd.cisc191.template.StudyAppController;
import edu.sdccd.cisc191.template.TopicDisplayCallable;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;



public class TestApplication {

    //tests constructor of Question class
    @Test
    public void testQuestionObject() {
        Question question = new Question("What is one pitfall of recursion","stack overflow");
        assertEquals("What is one pitfall of recursion", question.getQuestion());
        assertEquals("stack overflow", question.getAnswer());
    }


    //tests to see if random question that is generated is part of map and is under the correct topic
    @Test
    public void testGenerateRandomQuestion() {
        try {
            File temporaryFile = File.createTempFile("test", ".txt");
            try (PrintWriter writer = new PrintWriter(temporaryFile)) {
                writer.println("Topic: React");
                writer.println("When should you use useContext?");
                writer.println("when you need to pass props to deeply nested component");
                writer.println("What is the dependency array of useEffect used for?");
                writer.println("upon values changing, useEffect's assigned function is called");
            }

            StudyAppController logic = new StudyAppController();
            logic.processInput(temporaryFile);
            Question question = logic.generateRandomQuestion();
            ArrayList<Question> questionList = logic.questionsMap.get("React");
            assertTrue(logic.questionsMap.containsKey("React"));
            assertTrue(questionList.contains(question));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //test the processInput func to see if it correctly parses the file input into topics, questions, answers
    @Test
    public void testProcessInput() throws Exception {
        File tempFile = File.createTempFile("test", ".txt");
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println("Topic: React");
            writer.println("When should you use useContext?");
            writer.println("when you need to pass props to deeply nested component");
            writer.println("What is the dependency array of useEffect used for?");
            writer.println("upon values changing, useEffect's assigned function is called");
        }

        StudyAppController logic = new StudyAppController();
        logic.processInput(tempFile);

        assertFalse(logic.questionsMap.isEmpty());
        assertTrue(logic.questionsMap.containsKey("React"));
        assertEquals(2, logic.questionsMap.get("React").size());
        assertEquals("When should you use useContext?", logic.questionsMap.get("React").get(0).getQuestion());
        assertEquals("when you need to pass props to deeply nested component", logic.questionsMap.get("React").get(0).getAnswer());
        assertEquals("What is the dependency array of useEffect used for?", logic.questionsMap.get("React").get(1).getQuestion());
        assertEquals("upon values changing, useEffect's assigned function is called", logic.questionsMap.get("React").get(1).getAnswer());
    }
    //test linkedlist's add functionality, which prepends to linkedlist
    @Test public void testLinkedList(){
        QuestionsLinkedList questionsList = new QuestionsLinkedList();
        Question q1 = new Question("test1", "test1");
        Question q2 = new Question("test2", "test2");
        Question q3 = new Question("test3", "test3");
        questionsList.add(q1);
        assertEquals(0,questionsList.getIndexOfQuestion(q1));
        questionsList.add(q2);
        assertEquals(0,questionsList.getIndexOfQuestion(q2));
        questionsList.add(q3);
        assertEquals(0,questionsList.getIndexOfQuestion(q3));
        assertEquals(2,questionsList.getIndexOfQuestion(q1));
        assertEquals(1,questionsList.getIndexOfQuestion(q2));
        assertEquals(0,questionsList.getIndexOfQuestion(q3));
    }

    //test to see if storing item into db then fetching from db, will return the correct object
    @Test
    public void testDbFetch() {
        StudyAppController logic = new StudyAppController();
        ArrayList<Question> questions = new ArrayList<>();
        Question q1 = new Question("test1", "test1");
        questions.add(q1);
        logic.questionsMap.put("testingDb",questions);
        logic.saveQuestionsToDatabase();
        logic.getQuestionsFromDb();
        assertTrue(logic.questionsMap.containsKey("testingDb"));
        assertEquals("test1", logic.questionsMap.get("testingDb").get(0).getQuestion());
    }


    //test if stream function returns the correctly sorted array after applying flatMap to the stream
    @Test
    public void testStream() {
        StudyAppController logic = new StudyAppController();
        ArrayList<Question> questions1 = new ArrayList<>();
        ArrayList<Question> questions2 = new ArrayList<>();
        ArrayList<Question> questions3 = new ArrayList<>();
        Question q1 = new Question("test1", "test1");
        Question q2 = new Question("test2", "test2");
        Question q3 = new Question("test3", "test3");
        questions1.add(q1);
        questions2.add(q2);
        questions3.add(q3);
        logic.questionsMap.put("AtestingDb",questions1);
        logic.questionsMap.put("BtestingDb2",questions2);
        logic.questionsMap.put("CtestingDb3",questions3);
        logic.topicSortedQuestions();
        assertEquals("test1",logic.sortedList[0].getQuestion());
        assertEquals("test2",logic.sortedList[1].getQuestion());
        assertEquals("test3",logic.sortedList[2].getQuestion());
    }
    //test to see if mock data is equal to deserialized data upon loading,
    // after serializing and saving mock data to file
    @Test
    public void testSaveAndLoadQuestionsMap() throws IOException, ClassNotFoundException {
        // Create a temporary file to save the TreeMap
        File tempFile = File.createTempFile("questionsMap", ".ser");
        tempFile.deleteOnExit();

        // Create a TreeMap with mock data
        TreeMap<String, ArrayList<Question>> originalMap = new TreeMap<>();
        ArrayList<Question> questionsList = new ArrayList<>();
        questionsList.add(new Question("Q1", "A1"));
        questionsList.add(new Question("Q2", "A2"));
        originalMap.put("Topic1", questionsList);

        // Create the instance of controller, put our mocked map into controller map instance
        StudyAppController logic = new StudyAppController();
        logic.getQuestionsMap().putAll(originalMap);

        // Save the TreeMap from logic instance to file by fetching and using path of tempFile
        logic.saveQuestionsMapToFile(tempFile.getAbsolutePath());

        // Clear the TreeMap in the logic instance
        logic.getQuestionsMap().clear();
        assertTrue(logic.getQuestionsMap().isEmpty());

        // Load the TreeMap from the file back to our logic instance
        logic.loadQuestionsMapFromFile(tempFile.getAbsolutePath());

        // Verify that the loaded TreeMap matches the original TreeMap
        assertEquals(originalMap.size(), logic.getQuestionsMap().size());
        assertTrue(logic.getQuestionsMap().containsKey("Topic1"));
        assertEquals(2, logic.getQuestionsMap().get("Topic1").size());
        assertEquals("Q1", logic.getQuestionsMap().get("Topic1").get(0).getQuestion());
        assertEquals("A1", logic.getQuestionsMap().get("Topic1").get(0).getAnswer());
        assertEquals("Q2", logic.getQuestionsMap().get("Topic1").get(1).getQuestion());
        assertEquals("A2", logic.getQuestionsMap().get("Topic1").get(1).getAnswer());
    }
}
