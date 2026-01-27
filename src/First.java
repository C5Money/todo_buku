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
                case 4 -> deleteTask();
                case 5 -> {
                    exitToDo();
                    return;
                }
            }

            System.out.println("end end");
            break;
        }
    }

    public static void addingTask(Scanner sc, HashMap<Integer, String> tasks) {
        System.out.println("Add a number: ");
        int taskNumber = sc.nextInt();

        System.out.println("Add a task: ");
        String task = sc.next();

        tasks.put(taskNumber, task);

        toDoMenu(sc);
//        return tasks;
    }

    public static void viewingAllTasks(HashMap<Integer,String> tasks) {

        System.out.println("View all tasks");
        System.out.println(tasks);
    }

    public static void markAsCompleted(){
        System.out.println("Mark all tasks as completed");
    }

    public static void deleteTask(){
        System.out.println("Delete a task");
    }

    public static void exitToDo(){
        System.out.println("Exit the To-do app");
    }
}

