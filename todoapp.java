package todo;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ToDo {

    private static final Path DB = Paths.get("tasks.txt");

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java todo.ToDo [list|add|remove] [task/index]");
            return;
        }

        String cmd = args[0].toLowerCase();

        try {
            switch (cmd) {
                case "list":
                    list();
                    break;
                case "add":
                    if (args.length < 2) {
                        System.out.println("Please provide a task to add.");
                        return;
                    }
                    add(joinArgs(args, 1));
                    break;
                case "remove":
                    if (args.length < 2) {
                        System.out.println("Please provide an index to remove.");
                        return;
                    }
                    remove(Integer.parseInt(args[1]));
                    break;
                case "clear":
                    clear();
                    break;
                default:
                    System.out.println("Unknown command: " + cmd);
                    System.out.println("Commands: list, add, remove, clear");
            }
        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format. Use: remove <index>");
        }
    }

    // Combine arguments into a single string starting from index 'from'
    private static String joinArgs(String[] args, int from) {
        return String.join(" ", Arrays.copyOfRange(args, from, args.length));
    }

    // Display all tasks
    private static void list() throws IOException {
        if (!Files.exists(DB)) {
            System.out.println("No tasks yet. Add one with 'add <task>'.");
            return;
        }

        List<String> lines = Files.readAllLines(DB);
        if (lines.isEmpty()) {
            System.out.println("Your to-do list is empty.");
            return;
        }

        System.out.println("Your To-Do List:");
        for (int i = 0; i < lines.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, lines.get(i));
        }
    }

    // Add a new task
    private static void add(String task) throws IOException {
        Files.createDirectories(DB.getParent() == null ? Paths.get(".") : DB.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(DB,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(task);
            writer.newLine();
        }
        System.out.println("✅ Added: " + task);
    }

    // Remove a task by index
    private static void remove(int index) throws IOException {
        if (!Files.exists(DB)) {
            System.out.println("No tasks to remove.");
            return;
        }

        List<String> lines = new ArrayList<>(Files.readAllLines(DB));

        if (index < 1 || index > lines.size()) {
            System.out.println("Invalid index. Use 'list' to view tasks first.");
            return;
        }

        String removed = lines.remove(index - 1);
        Files.write(DB, lines);
        System.out.println("🗑️ Removed: " + removed);
    }

    // Clear all tasks
    private static void clear() throws IOException {
        if (!Files.exists(DB) || Files.readAllLines(DB).isEmpty()) {
            System.out.println("Your task list is already empty.");
            return;
        }
        Files.delete(DB);
        System.out.println("🧹 All tasks cleared!");
    }
}

