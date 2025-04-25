package fungorium.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@FunctionalInterface
interface Command {
    void execute(String[] args);
}

public class Interpreter {
    private Interpreter() {
        throw new UnsupportedOperationException("Interpreter is a static utility class and cannot be instantiated.");
    }

    
    private static Map<String, Command> commands = new HashMap<>();
    // Map to store created objects and their generated names.
    private static Map<String, Object> objectNames = new HashMap<>();
    // Map to keep counters for each unique prefix.
    private static final Map<String, Integer> prefixCounters = new HashMap<>();
    // Map to store already computed unique prefixes for classes.
    private static final Map<Class<?>, String> computedPrefixes = new HashMap<>();
    
    static {
        // ez csak minta
        commands.put("print_args", x -> {
            for (int i = 0; i < x.length; i++){
                System.out.println(x[i] + " printed");
            }
        }
        );

        commands.put("help", x -> {
            for (String cname : commands.keySet()){
                System.out.println("/" + cname);
            }
            System.out.println("e | exit | q | quit -- to exit");
        }
        );
    }

    
    public static void create(Object obj) {
        if (obj == null) {
            return;
        }
        Class<?> clazz = obj.getClass();
        String prefix = getUniquePrefix(clazz);
        int count = prefixCounters.getOrDefault(prefix, 0) + 1;
        prefixCounters.put(prefix, count);
        String name = prefix + count;
        objectNames.put(name + "_auto", obj);
    }

    private static String getUniquePrefix(Class<?> clazz) {
        if (computedPrefixes.containsKey(clazz)) {
            return computedPrefixes.get(clazz);
        }
        String fullName = clazz.getSimpleName().toLowerCase();
        // First, check if fullName ends with any previously registered class's simple
        // name.
        for (Map.Entry<Class<?>, String> entry : computedPrefixes.entrySet()) {
            String existingSimpleName = entry.getKey().getSimpleName().toLowerCase();
            if (fullName.endsWith(existingSimpleName)) {
                String prefix = entry.getValue();
                computedPrefixes.put(clazz, prefix);
                return prefix;
            }
        }
        // If no matching suffix is found, compute a new unique prefix.
        int candidateLength = 1;
        String candidate = fullName.substring(0, candidateLength);
        boolean unique;
        do {
            unique = true;
            for (Map.Entry<Class<?>, String> entry : computedPrefixes.entrySet()) {
                String otherPrefix = entry.getValue();
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

    public static void reset() {
        objectNames.clear();
        prefixCounters.clear();
        computedPrefixes.clear();
    }

    public static void executeCommand(String inputString) {
        inputString = inputString.replace("/", "");
        String[] args = inputString.split(" ");
        if (commands.containsKey(args[0])) {
            commands.get(args[0]).execute(Arrays.copyOfRange(args, 1, args.length));
        } else {
            System.out.println("Command not found.");
        }
    }
}
