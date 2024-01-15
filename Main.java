import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.io.Serializable;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;

class Task implements Serializable {
    String name;
    String description;
    String priority;
    String dueDate;
    String status;

    public Task(String name, String description, String priority, String dueDate) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.status = "Incomplete";
    }
}
//Class to manage all tasks
class TaskManager {
    private final Map<String, Task> tasks;
    private final DefaultListModel<String> taskListModel;
//Constructor to initialize a TaskManager object
    public TaskManager(DefaultListModel<String> taskListModel) {
        this.tasks = new HashMap<>();
        this.taskListModel = taskListModel;
    }
//Adds a task to the list
    public void addTask(Task task) {
        tasks.put(task.name, task); //Add to HashMap
        taskListModel.addElement(getTaskString(task)); //Updates the UI
    }
//Mark any selected task as complete
    public void completeTask(String taskName) {
        Task task = tasks.get(taskName);
        if (task != null) {
            task.status = "Complete"; //Sets status as complete
            updateTaskList(); //Updates the UI
        }
    }
//Deletes any selected task
    public void deleteTask(String taskName) {
        if (tasks.containsKey(taskName)) {
            tasks.remove(taskName); //Removes the task from the HashMap
            updateTaskList(); //Updates the UI
        }
    }
//Updates the task list UI
    private void updateTaskList() {
        taskListModel.clear(); //Clears the UI
        for (Task task : tasks.values()) {
            taskListModel.addElement(getTaskString(task)); //Adds the updated tasks to the UI
        }
    }
//Formats the task information when its being displayed
    private String getTaskString(Task task) {
        return task.name + ": " + task.status +
                " | Description: " + task.description +
                " | Priority: " + task.priority +
                " | Due Date: " + task.dueDate;
    }
    //Exports the list of tasks to a text file
    public void exportTasks(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) { //Iterates through each task and writes its info to the file
            for (Task task : tasks.values()) {
                //Writes task information to the file in specified format
                writer.write(task.name + "," +
                        task.description + "," +
                        task.priority + "," +
                        task.dueDate + "," +
                        task.status);
                writer.newLine(); //Moves to the next line for the next task listed
            }
            System.out.println("Tasks exported successfully.");
        } catch (IOException e) {
            e.printStackTrace(); //Prints out the stack trace if an IOException occurs
        }
    }
    //Imports tasks from a text file
    public void importTasks(String fileName) {
        Map<String, Task> importedTasks = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line; //Reads each line from the file that represents a task
            while ((line = reader.readLine()) != null) {
                String[] taskProperties = line.split(",");

                if (taskProperties.length == 5) { //Checks if the lien contains all the appropriate task information in the right format
                    String name = taskProperties[0];
                    String description = taskProperties[1];
                    String priority = taskProperties[2];
                    String dueDate = taskProperties[3];
                    String status = taskProperties[4]; //Extracts task properties from the specified format which is the same as export

                    Task newTask = new Task(name, description, priority, dueDate);
                    newTask.status = status; //Creates a new task with the information thats extracted from the file

                    importedTasks.put(name, newTask); //Adds the task to the imported tasks
                }
            }

            System.out.println("Tasks imported successfully.");
        } catch (IOException e) {
            e.printStackTrace(); //Prints stack trace if an IOException occurs
        }

        tasks.clear(); //Clears all existing tasks
        tasks.putAll(importedTasks); //Adds all imported tasks to the task manager
        updateTaskList(); //Updates the task list in the UI
    }
}
//UI for the Task Management
class TaskGUI extends JFrame {
    private final TaskManager taskManager;
    private final DefaultListModel<String> taskListModel;

    public TaskGUI() {
        this.taskListModel = new DefaultListModel<>();
        this.taskManager = new TaskManager(taskListModel);

        initComponents(); //Initializes components of the GUI
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Task Management System"); //Sets title and close operation

        JList<String> taskList = new JList<>(taskListModel);
        JButton completeButton = new JButton("Mark as Complete");
        JButton deleteButton = new JButton("Delete Task");
        JButton addButton = new JButton("Add Task");
        JButton exportButton = new JButton("Export Tasks");
        JButton importButton = new JButton("Import Tasks"); //Adds various buttons to be used

        completeButton.addActionListener(new ActionListener() { //Action listener for "Mark as Complete" button
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTask = taskList.getSelectedValue(); //Gets selected task from list
                if (selectedTask != null) {
                    String taskName = selectedTask.split(":")[0].trim();
                    taskManager.completeTask(taskName); //Marks the task as complete
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() { //Action listener for "Delete Task" button
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTask = taskList.getSelectedValue();
                if (selectedTask != null) {
                    String taskName = selectedTask.split(":")[0].trim();
                    taskManager.deleteTask(taskName); //Deletes the selected task
                }
            }
        });

        addButton.addActionListener(new ActionListener() { //Action listener for "Add Task" button
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddTaskDialog(); //Displays the dialog for adding new task
            }
        });

        exportButton.addActionListener(new ActionListener() { //Action listener for "Export Tasks" button
            @Override
            public void actionPerformed(ActionEvent e) {
                showExportDialog(); //Displays dialog for exporting tasks
            }
        });

        importButton.addActionListener(new ActionListener() { //Action listener for "Import Tasks" button
            @Override
            public void actionPerformed(ActionEvent e) {
                showImportDialog(); //Displays dialog for importing tasks
            }
        });

        JPanel buttonPanel = new JPanel(); //Panel to hold the buttons
        buttonPanel.setLayout(new FlowLayout()); //Sets the layout for the button panel
        buttonPanel.add(completeButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(importButton); //Adds all the various buttons to the panel

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(taskList), BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH); //Sets layout for the tasks list, adds scroll pane, and button panel at the bottom

        pack(); //Packs the components to its optimal size
        setLocationRelativeTo(null); //Centers GUI on screen
    }
    //Method to show the dialog for adding a new task
    private void showAddTaskDialog() { //Creates text fields and combo box that asks for user input
        JTextField nameField = new JTextField();
        JTextField descriptionField = new JTextField();
        JComboBox<String> priorityComboBox = new JComboBox<>(new String[]{"High", "Medium", "Low"}); //Selection box for priority
        JTextField dueDateField = new JTextField();
    //Creates a panel that has a grid layout to organize all the relevant components
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Task Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Priority:"));
        panel.add(priorityComboBox);
        panel.add(new JLabel("Due Date:"));
        panel.add(dueDateField); //Appropriate labels and text fields to enter the components
    //Shows confirmation dialog to either OK it or cancel
        int result = JOptionPane.showConfirmDialog(null, panel, "Add Task",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    //Checks if OK option was selected
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String description = descriptionField.getText();
            String priority = (String) priorityComboBox.getSelectedItem();
            String dueDate = dueDateField.getText(); //Gets all relevant info from the text fields and priority
    //Checks if the task name is empty
            if (!name.isEmpty()) {
                Task newTask = new Task(name, description, priority, dueDate);
                taskManager.addTask(newTask); //Adds new task to the manager
            }
        }
    }
    //Method to show the dialog for exporting tasks
    private void showExportDialog() {
        JFileChooser fileChooser = new JFileChooser(); //File chooser to select export location
        int result = fileChooser.showSaveDialog(null); //Shows save dialog

        if (result == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath(); //Gets the selected file path
            taskManager.exportTasks(fileName); //Exports the tasks to the selected file
        }
    }
    //Method to show the dialog for importing tasks
    private void showImportDialog() {
        JFileChooser fileChooser = new JFileChooser(); //File chooser to select the export location
        int result = fileChooser.showOpenDialog(null); //Shows the open dialog

        if (result == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath(); //Gets the selected file path
            taskManager.importTasks(fileName); //Imports tasks from the selected file
        }
    }
    //Main method to launch the GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TaskGUI().setVisible(true); //Creates and displays the GUI
            }
        });
    }
}