package fungorium.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Logger {
    private Logger() {
        throw new UnsupportedOperationException("Logger is a static utility class and cannot be instantiated.");
    }

    // Map to store created objects and their generated names.
    private static Map<Object, String> objectNames = new HashMap<>();
    // Map to keep counters for each class to generate unique names.
    private static Map<Class<?>, Integer> counters = new HashMap<>();
    // Map to store already computed unique prefixes for classes.
    private static final Map<Class<?>, String> computedPrefixes = new HashMap<>();
    // Current indentation level.
    private static int indentLevel = 0;
    // Indentation unit (e.g., 4 spaces).
    private static final String INDENT_UNIT = "    ";

    private static Scanner scanner = new Scanner(System.in);

    /**
     * Creates a name for the given object and stores it.
     * The name is generated using a unique prefix for the object's class
     * (computed to avoid collisions with classes sharing the same starting letters)
     * followed by a counter. For example, if a Tecton is created first, its name
     * may be "t1",
     * and if a Thread is created later, its name will be "th1" (not "t1") so they
     * remain distinct.
     */
    public static void create(Object obj) {
        if (obj == null) {
            return;
        }
        Class<?> clazz = obj.getClass();
        int count = counters.getOrDefault(clazz, 0) + 1;
        counters.put(clazz, count);
        String prefix = getUniquePrefix(clazz);
        String name = prefix + count;
        objectNames.put(obj, name);
        enter(null, clazz.getSimpleName());
        System.out.println(getIndent() + name + " created");
        exit("");
    }

    /**
     * Computes a unique prefix for the given class based on its simple name.
     * If another class has already been assigned a prefix that would conflict (i.e.
     * they share the same initial letters),
     * this method increases the number of letters used until the prefixes are
     * distinct.
     */
    private static String getUniquePrefix(Class<?> clazz) {
        if (computedPrefixes.containsKey(clazz)) {
            return computedPrefixes.get(clazz);
        }
        String fullName = clazz.getSimpleName().toLowerCase();
        int candidateLength = 1;
        String candidate = fullName.substring(0, candidateLength);
        boolean unique;
        do {
            unique = true;
            for (Map.Entry<Class<?>, String> entry : computedPrefixes.entrySet()) {
                String otherPrefix = entry.getValue();
                // If both classes share the same starting letter(s) up to candidateLength,
                // then there is a conflict.
                if (otherPrefix.equals(candidate)) {
                    unique = false;
                    break;
                }
            }
            if (!unique) {
                candidateLength++;
                candidate = fullName.substring(0, candidateLength);
            }
        } while (!unique);
        computedPrefixes.put(clazz, candidate);
        return candidate;
    }

    /**
     * Logs the entering of a function by printing an indented line
     * with the function name and "()" preceded by "-->".
     * Then increases the indentation for nested calls.
     */
    public static void enter(Object clazz, String functionName) {
        if (clazz == null){
            System.out.println(getIndent() + "--> " + functionName + "()");
        }
        else{
            System.out.println(getIndent() + "--> " + objectNames.get(clazz) + "." + functionName + "()");
        }
        indentLevel++;
    }

    /**
     * Logs the exit of a function by decreasing the indentation level
     * and printing an indented line with "<--". If a return value is provided,
     * and it is an object that has been registered, its generated name is printed.
     * Otherwise, the return value's toString() is used.
     */
    public static void exit(Object returnValue) {
        indentLevel = Math.max(0, indentLevel - 1);
        String retStr = "";
        if (returnValue != null) {
            if (objectNames.containsKey(returnValue)) {
                retStr = objectNames.get(returnValue);
            } else {
                retStr = returnValue.toString();
            }
        }
        System.out.println(getIndent() + "<--" + (retStr.isEmpty() ? "" : " " + retStr));
    }

    /**
     * Asks the user a yes/no question (provided in questionText) using the current
     * indentation.
     * Re-prompts if the answer is not "y" or "n".
     * Returns true for "y" and false for "n".
     */
    public static boolean question(String questionText) {
        System.out.print(getIndent() + questionText + " (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        while (!input.equals("y") && !input.equals("n")) {
            System.out.print(getIndent() + "Please answer with y/n: ");
            input = scanner.nextLine().trim().toLowerCase();
        }
        return input.equals("y");
    }

    /**
     * Asks the user a question about a number (provided in questionText) using the current
     * indentation.
     * Re-prompts if the answer is not a number.
     * Returns the number.
     */
    public static int questionNumber(String questionText) {
        System.out.print(getIndent() + questionText + " (number): ");
        int input;
        while (true) {
            try {
                input = Integer.parseInt(scanner.nextLine().trim());
                break;
            } catch (NumberFormatException e) {
                System.out.print(getIndent() + "Please answer with a number: ");
            }
        }
        return input;
    }

    /**
     * Helper method to build the current indent string.
     */
    private static String getIndent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            sb.append(INDENT_UNIT);
        }
        return sb.toString();
    }
}
