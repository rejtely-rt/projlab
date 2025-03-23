package fungorium;

import fungorium.utils.Initialize;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Program elindult!");
        System.out.println("Válasszon egy tesztesetet a listából:");

        // Tesztesetek listája
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

        // Kimenet megjelenítése
        for (int i = 0; i < testCases.size(); i++) {
            System.out.println((i + 1) + ". " + testCases.get(i));
        }

        System.out.print("Írja be a teszteset számát és nyomja meg az ENTER-t: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        scanner.nextLine(); // newline eltüntetése

        System.out.println("\nTeszt futtatása...\n");

        class Tests extends Initialize {
            public Tests() {
                initialize();
            }

            public void test1() {
                System.out.println("[Teszt 1] Shoot spores on neighbor");
                // Példa hívás
                // spores.shoot();
            }

            public void test2() {
                System.out.println("[Teszt 2] Shoot spores on neighbor's neighbor");
            }

            public void test3() {
                System.out.println("[Teszt 3] Tecton break");
            }

            public void test4() {
                System.out.println("[Teszt 4] Thread grow next to a thread");
            }

            public void test5() {
                System.out.println("[Teszt 5] Thread grow next to a thread on a SingleThreadTecton");
            }

            public void test6() {
                System.out.println("[Teszt 6] Thread grow first of a Mushroom");
            }

            public void test7() {
                System.out.println("[Teszt 7] Thread grow first of a Mushroom on a SingleThreadTecton");
            }

            public void test8() {
                System.out.println("[Teszt 8] Insect consume spore that affects its speed");
            }

            public void test9() {
                System.out.println("[Teszt 9] Insect consume cannotCutSpore");
            }

            public void test10() {
                System.out.println("[Teszt 10] Thread cut with insect");
            }

            public void test11() {
                System.out.println("[Teszt 11] Thread absorb");
            }

            public void test12() {
                System.out.println("[Teszt 12] Insect move");
            }

            public void test13() {
                System.out.println("[Teszt 13] Effect check");
            }

            public void test14() {
                System.out.println("[Teszt 14] Mushroom grow");
            }
        }

        Tests test = new Tests();

        // Tesztek futtatása
        switch (choice) {
            case 1 -> test.test1();
            case 2 -> test.test2();
            case 3 -> test.test3();
            case 4 -> test.test4();
            case 5 -> test.test5();
            case 6 -> test.test6();
            case 7 -> test.test7();
            case 8 -> test.test8();
            case 9 -> test.test9();
            case 10 -> test.test10();
            case 11 -> test.test11();
            case 12 -> test.test12();
            case 13 -> test.test13();
            case 14 -> test.test14();
            default -> System.out.println("Érvénytelen választás!");
        }

        System.out.println("\nTeszt befejeződött.");
    }
}
