package fungorium;

import fungorium.utils.Initialize;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Program elindult!");

        List<String> testCases = List.of(
                "Shoot spores on neighbor",
                "Shoot spores on neighbor's neighbor",
                "Tecton break",
                "Thread grow next to a thread",
                "Thread grow next to a thread on a SingleThreadTecton",
                "Thread grow first of a Mushroom",
                "Thread grow first of a Mushroom on a SingleThreadTecton",
                "Insect consume spore that affects its speed",
                "Insect consume cannotCutSpore",
                "Thread cut with insect",
                "Thread absorb",
                "Insect move",
                "Effect check",
                "Mushroom grow"
        );

        Scanner scanner = new Scanner(System.in);
        String choice;

        class Tests extends Initialize {
            public Tests() {
                initialize();
            }

            public void test1() { System.out.println("[Teszt 1] Shoot spores on neighbor"); }
            public void test2() { System.out.println("[Teszt 2] Shoot spores on neighbor's neighbor"); }
            public void test3() { System.out.println("[Teszt 3] Tecton break"); }
            public void test4() { System.out.println("[Teszt 4] Thread grow next to a thread"); }
            public void test5() { System.out.println("[Teszt 5] Thread grow next to a thread on a SingleThreadTecton"); }
            public void test6() { System.out.println("[Teszt 6] Thread grow first of a Mushroom"); }
            public void test7() { System.out.println("[Teszt 7] Thread grow first of a Mushroom on a SingleThreadTecton"); }
            public void test8() { System.out.println("[Teszt 8] Insect consume spore that affects its speed"); }
            public void test9() { System.out.println("[Teszt 9] Insect consume cannotCutSpore"); }
            public void test10() { System.out.println("[Teszt 10] Thread cut with insect"); }
            public void test11() { System.out.println("[Teszt 11] Thread absorb"); }
            public void test12() { System.out.println("[Teszt 12] Insect move"); }
            public void test13() { System.out.println("[Teszt 13] Effect check"); }
            public void test14() { System.out.println("[Teszt 14] Mushroom grow"); }
        }

        do {
            // Menü megjelenítése
            System.out.println("\nVálassz egy tesztet a futtatáshoz:");
            for (int i = 0; i < testCases.size(); i++) {
                System.out.println((i + 1) + ". " + testCases.get(i));
            }
            System.out.println("0. Kilépés");
            System.out.print("Teszt száma: ");

            // Bemenet beolvasása
            choice = scanner.nextLine();

            // Tesztek futtatása
            switch (choice) {
                case "1" -> new Tests().test1();
                case "2" -> new Tests().test2();
                case "3" -> new Tests().test3();
                case "4" -> new Tests().test4();
                case "5" -> new Tests().test5();
                case "6" -> new Tests().test6();
                case "7" -> new Tests().test7();
                case "8" -> new Tests().test8();
                case "9" -> new Tests().test9();
                case "10" -> new Tests().test10();
                case "11" -> new Tests().test11();
                case "12" -> new Tests().test12();
                case "13" -> new Tests().test13();
                case "14" -> new Tests().test14();
                case "0" -> System.out.println("Kilépés...");
                default -> System.out.println("Érvénytelen választás. Kérlek, próbáld újra.");
            }

            if (!choice.equals("0")) {
                System.out.println("\nTeszt befejeződött.");
                System.out.print("Nyomj ENTER-t a folytatáshoz...");
                scanner.nextLine(); // Vár, amíg a felhasználó lenyomja az ENTER-t
            }            

        } while (!choice.equals("0"));

        scanner.close();
    }
}