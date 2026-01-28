import java.util.HashMap;
import java.util.Scanner;

public class First {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        toDoMenu(sc);
    }

    private static void toDoMenu(Scanner sc) {
        HashMap<Integer, String> tasks = new HashMap<>();

        while (true){

            Testje.show_menu();
            System.out.println("Voer een cijfer van 1 t/m 5 in: ");


            if (!sc.hasNextInt()) {
                System.out.println("❌ Ongeldige invoer... Probeer nog een keer.");
                sc.next();
                continue;
            }

            int userInput = sc.nextInt();
            if (userInput < 1 || userInput > 5) {
                System.out.println("❌ Verkeerde cijfer. Kies een getal van 1 t/m 5: ");
                continue;
            }
            switch (userInput) {
                case 1 -> addingTask(sc, tasks);
                case 2 -> viewingAllTasks(tasks);
                case 3 -> markAsCompleted();
                case 4 -> deleteTask(sc, tasks);
                case 5 -> {
                    exitToDo();
                    return;
                }
            }
        }
    }

    public static void addingTask(Scanner sc, HashMap<Integer, String> tasks) {
        System.out.println("Add a number: ");
        int taskNumber = sc.nextInt();
        sc.nextLine();

        System.out.println("Add a task: ");
        String task = sc.nextLine();

        if (tasks.containsKey(taskNumber)) {
            System.out.println("❌ Tasknumber " + taskNumber + " already exists.\n");
        } else {
            tasks.put(taskNumber, task);
            System.out.println("✅ Tasknumber " + taskNumber + " added.\n");
        }
    }

    public static void viewingAllTasks(HashMap<Integer,String> tasks) {
        System.out.println("These are all your tasks: \n");

        for (HashMap.Entry<Integer, String> entry : tasks.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue());
        }
    }

    public static void markAsCompleted(){
        System.out.println("Mark all tasks as completed");
    }

    public static void deleteTask(Scanner sc, HashMap<Integer,String> tasks) {
        System.out.println("Delete a task by entering the number of the task: ");
        tasks.remove(sc.nextInt());
        System.out.println("Task deleted.");
    }

    public static void exitToDo(){
        System.out.println("Have a nice day!");

    }
}

