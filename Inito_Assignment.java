import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class InMemoryFileSystem {
    private String currentPath;
    private Map<String, Object> fileSystem;

    public InMemoryFileSystem() {
        currentPath = "/";
        fileSystem = new HashMap<>();
    }

    public void mkdir(String directoryName) {
        // Creates a new directory in the current path
        fileSystem.put(currentPath + directoryName, new HashMap<>());
    }

    public void cd(String path) {
        // Changes the current directory based on the provided path
        if (path.equals("..")) {
            int lastSlashIndex = currentPath.lastIndexOf('/');
            currentPath = currentPath.substring(0, lastSlashIndex);
        } else if (path.equals("/")) {
            currentPath = "/";
        } else {
            currentPath = currentPath + path + "/";
        }
    }

    public void ls(String path) {
        // Lists the contents of the specified directory
        String fullPath = currentPath + path;
        if (fileSystem.containsKey(fullPath) && fileSystem.get(fullPath) instanceof Map) {
            Map<String, Object> contents = (Map<String, Object>) fileSystem.get(fullPath);
            for (String item : contents.keySet()) {
                System.out.println(item);
            }
        } else {
            System.out.println("Invalid path or not a directory.");
        }
    }

    public void touch(String fileName) {
        // Creates a new empty file in the current path
        fileSystem.put(currentPath + fileName, "");
    }

    public void echo(String fileName, String content) {
        // Writes text to a file
        fileSystem.put(currentPath + fileName, content);
    }

    public void mv(String source, String destination) {
        // Moves a file or directory to another location
        Object fileOrDir = fileSystem.remove(currentPath + source);
        fileSystem.put(currentPath + destination, fileOrDir);
    }

    public void cp(String source, String destination) {
        // Copies a file or directory to another location
        Object fileOrDir = fileSystem.get(currentPath + source);
        fileSystem.put(currentPath + destination, fileOrDir);
    }

    public void rm(String path) {
        // Removes a file or directory
        fileSystem.remove(currentPath + path);
    }

    public void saveState(String filePath) {
        // Saves the current state of the file system to a file
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(fileSystem);
            System.out.println("File system state saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving file system state: " + e.getMessage());
        }
    }

    public void loadState(String filePath) {
        // Loads the file system state from a file
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            fileSystem = (Map<String, Object>) ois.readObject();
            System.out.println("File system state loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading file system state: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        InMemoryFileSystem fileSystem = new InMemoryFileSystem();
        Scanner scanner = new Scanner(System.in);

        // Check if command-line arguments are provided
        if (args.length > 0) {
            String command = args[0];

            // Load file system state if the command is "loadState"
            if (command.equals("loadState")) {
                fileSystem.loadState(args[1]);
            }
        }

        // Main loop for user interaction
        while (true) {
            System.out.print(fileSystem.currentPath + "> ");
            String userInput = scanner.nextLine().trim();
            String[] commandArgs = userInput.split("\\s+");

            // Process user commands
            if (commandArgs.length > 0) {
                String command = commandArgs[0];

                if (command.equals("exit")) {
                    break;
                } else if (command.equals("saveState")) {
                    // Save file system state
                    fileSystem.saveState(commandArgs[1]);
                } else if (command.equals("loadState")) {
                    // Load file system state
                    fileSystem.loadState(commandArgs[1]);
                } else if (command.equals("mkdir")) {
                    fileSystem.mkdir(commandArgs[1]);
                } else if (command.equals("cd")) {
                    fileSystem.cd(commandArgs[1]);
                } else if (command.equals("ls")) {
                    fileSystem.ls(commandArgs.length > 1 ? commandArgs[1] : ".");
                } else if (command.equals("touch")) {
                    fileSystem.touch(commandArgs[1]);
                } else if (command.equals("echo")) {
                    fileSystem.echo(commandArgs[1], commandArgs[2]);
                } else if (command.equals("mv")) {
                    fileSystem.mv(commandArgs[1], commandArgs[2]);
                } else if (command.equals("cp")) {
                    fileSystem.cp(commandArgs[1], commandArgs[2]);
                } else if (command.equals("rm")) {
                    fileSystem.rm(commandArgs[1]);
                } else {
                    System.out.println("Invalid command. Try again.");
                }
            }
        }
    }
}
