import java.io.*;
import java.util.HashMap;
import java.util.Map;

class Import {
    public static Map<String, Task> importTasks(String fileName) {
        Map<String, Task> tasks = new HashMap<>(); //Imports tasks from a file and returns the map of the tasks
        //Then creates a new map to store the imported tasks

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            Object obj = ois.readObject();

            if (obj instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, Task> importedTasks = (Map<String, Task>) obj;
                tasks.putAll(importedTasks);
            } //Reads the object from the file, checks if its a map, casts the object to a map of a string, and then puts all the imported tasks into the tasks map

            System.out.println("Tasks has been successfully imported");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } //Handles any exceptions if any issues occur with file reading or object casting

        return tasks; //Returns the map of the tasks
    }
}