import java.io.*;
import java.util.Map;

class Export {
    public static void exportTasks(Map<String, Task> tasks, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(tasks); //Export tasks to a file using ObjectOutputStream, write the tasks map to the file using same method
            System.out.println("Tasks have been successfully exported");
        } catch (IOException e) {
            e.printStackTrace(); //Handles any exceptions if errors occur with writing
        }
    }
}