package fungorium;

import fungorium.utils.Interpreter;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        String inputString;
        while (true) {
            System.out.print("> ");

            // Bemenet beolvasása
            inputString = scanner.nextLine().trim().toLowerCase();

            // Kilépési feltételek
            if (inputString.equals("exit")
                    || inputString.equals("e")
                    || inputString.equals("quit")
                    || inputString.equals("q")) {
                System.out.println("Goodbye!");
                break;
            }

            // Parancs végrehajtása
            Interpreter.executeCommand(inputString);
        }
        scanner.close();
    }
}