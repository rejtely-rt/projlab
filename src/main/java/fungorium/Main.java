package fungorium;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final List<String> testList = new ArrayList<>();

    public static void main(String[] args) {
        // Tesztek hozzáadása a listához
        testList.add("Shoot spores on neighbor");
        testList.add("Shoot spores on neighbor's neighbor");
        testList.add("Tecton break");
        testList.add("Thread grow next to a thread");
        testList.add("Thread grow next to a thread on a SingleThreadTecton");
        testList.add("Thread grow first of a Mushroom");
        testList.add("Thread grow first of a Mushroom on a SingleThreadTecton");
        testList.add("Insect consume spore that affects its speed");
        testList.add("Insect consume cannotCutSpore");
        testList.add("Thread cut with insect");
        testList.add("Thread absorb");
        testList.add("Insect move");
        testList.add("Effect check");
        testList.add("Mushroom grow");

        Scanner scanner = new Scanner(System.in);
        String choice;

        do {
            // Menü megjelenítése
            System.out.println("\nVálassz egy tesztet a futtatáshoz:");
            for (int i = 0; i < testList.size(); i++) {
                System.out.println((i + 1) + ". " + testList.get(i));
            }
            System.out.println("0. Kilépés");
            System.out.print("Teszt száma: ");

            // Felhasználói választás beolvasása
            choice = scanner.nextLine();

            try {
                int index = Integer.parseInt(choice);
                if (index == 0) {
                    System.out.println("Kilépés...");
                } else if (index > 0 && index <= testList.size()) {
                    runTest(index);
                } else {
                    System.out.println("Érvénytelen választás. Kérlek, próbáld újra.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Érvénytelen input. Kérlek, számot adj meg.");
            }

        } while (!choice.equals("0"));

        scanner.close();
    }

    private static void runTest(int testNumber) {
        System.out.println("\nFuttatás: " + testList.get(testNumber - 1));
        switch (testNumber) {
            case 1 -> testShootSporesOnNeighbor();
            case 2 -> testShootSporesOnNeighborsNeighbor();
            case 3 -> testTectonBreak();
            case 4 -> testThreadGrowNextToThread();
            case 5 -> testThreadGrowNextToThreadOnSingleThreadTecton();
            case 6 -> testThreadGrowFirstOfMushroom();
            case 7 -> testThreadGrowFirstOfMushroomOnSingleThreadTecton();
            case 8 -> testInsectConsumeSpeedSpore();
            case 9 -> testInsectConsumeCannotCutSpore();
            case 10 -> testThreadCutWithInsect();
            case 11 -> testThreadAbsorb();
            case 12 -> testInsectMove();
            case 13 -> testEffectCheck();
            case 14 -> testMushroomGrow();
            default -> System.out.println("Ismeretlen tesztszám.");
        }
    }

    // Teszt metódusok – ide jöhet majd a tényleges logika

    private static void testShootSporesOnNeighbor() {
        System.out.println("[Teszt 1] Spores on neighbor logika itt...");
    }

    private static void testShootSporesOnNeighborsNeighbor() {
        System.out.println("[Teszt 2] Spores on neighbor's neighbor logika itt...");
    }

    private static void testTectonBreak() {
        System.out.println("[Teszt 3] Tecton break logika itt...");
    }

    private static void testThreadGrowNextToThread() {
        System.out.println("[Teszt 4] Thread grow next to a thread logika itt...");
    }

    private static void testThreadGrowNextToThreadOnSingleThreadTecton() {
        System.out.println("[Teszt 5] Thread grow next to a thread on a SingleThreadTecton...");
    }

    private static void testThreadGrowFirstOfMushroom() {
        System.out.println("[Teszt 6] Thread grow first of a Mushroom logika itt...");
    }

    private static void testThreadGrowFirstOfMushroomOnSingleThreadTecton() {
        System.out.println("[Teszt 7] Thread grow first of a Mushroom on a SingleThreadTecton...");
    }

    private static void testInsectConsumeSpeedSpore() {
        System.out.println("[Teszt 8] Insect consumes speed-affecting spore...");
    }

    private static void testInsectConsumeCannotCutSpore() {
        System.out.println("[Teszt 9] Insect consumes cannotCutSpore...");
    }

    private static void testThreadCutWithInsect() {
        System.out.println("[Teszt 10] Thread cut with insect logika itt...");
    }

    private static void testThreadAbsorb() {
        System.out.println("[Teszt 11] Thread absorb logika itt...");
    }

    private static void testInsectMove() {
        System.out.println("[Teszt 12] Insect move logika itt...");
    }

    private static void testEffectCheck() {
        System.out.println("[Teszt 13] Effect check logika itt...");
    }

    private static void testMushroomGrow() {
        System.out.println("[Teszt 14] Mushroom grow logika itt...");
    }
}
