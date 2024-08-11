package edu.sdccd.cisc191.template;

import javafx.application.Platform;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * callable thread that takes in text field and hashmap to pick random topic to display
 * loops indefinitely, only shuts down when application closes
 */
public class TopicDisplayCallable implements Callable<Void> {
    private final HashMap<String, ArrayList<Question>> questionsMap;
    private final TextField textField;

    //constructor for this thread
    public TopicDisplayCallable(HashMap<String, ArrayList<Question>> questionsMap, TextField textField) {
        this.questionsMap = questionsMap;
        this.textField = textField;
    }

    /**
     * is method that is called when thread is run
     * @return void
     */
    @Override
    public Void call() {
        Random random = new Random();

        while (true) {
            ArrayList<String> keyList = new ArrayList<>(questionsMap.keySet());
            if (!keyList.isEmpty()) {
                String key = keyList.get(random.nextInt(keyList.size()));

                // Update the TextField on the JavaFX Application Thread
                Platform.runLater(() -> textField.setText(key));
            }

            try {
                // Sleep for 5 seconds before updating again
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // Handle the interruption, if needed
                Thread.currentThread().interrupt();
                break;
            }
        }
        return null;
    }
}
