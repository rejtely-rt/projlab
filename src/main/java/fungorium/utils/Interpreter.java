package fungorium.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
//loadhoz kellenek
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import fungorium.gui.EntityController;
import fungorium.gui.EntityViewModel;
import fungorium.gui.InsectViewModel;
import fungorium.gui.MushroomViewModel;
import fungorium.gui.SporeViewModel;
import fungorium.gui.TectonViewModel;
import fungorium.gui.ThreadViewModel;
import fungorium.model.Insect;
import fungorium.model.Thread;
import fungorium.spores.CannotCutSpore;
import fungorium.spores.CloneSpore;
import fungorium.spores.ParalyzeSpore;
import fungorium.spores.SlowlySpore;
import fungorium.spores.SpeedySpore;
import fungorium.spores.Spore;
import fungorium.model.Mushroom;
import fungorium.tectons.NoMushTecton;
import fungorium.tectons.SingleThreadTecton;
import fungorium.tectons.Tecton;
import fungorium.tectons.ThreadAbsorberTecton;
import fungorium.tectons.ThreadKeeperTecton;
import fungorium.model.Mycologist;
import fungorium.model.Insectist;
import fungorium.utils.HLogPrintStream;

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

    public static Map<String, Object> getObjectNames() {
        return objectNames;
    }
    // Map to keep counters for each unique prefix.
    private static final Map<String, Integer> prefixCounters = new HashMap<>();
    // Map to store already computed unique prefixes for classes.
    private static final Map<Class<?>, String> computedPrefixes = new HashMap<>();

    private static HLogPrintStream hlps = new HLogPrintStream();

    private static EntityController controller;
    private static final Map<String, Mycologist> mycologistByName = new HashMap<>();


    public static void setController(EntityController controller) {
        Interpreter.controller = controller;
    }

    public static EntityController getController() {
        return controller;
    }
    
    static {
        // ez csak minta
        commands.put("print_args", (x) -> {
            for (int i = 0; i < x.length; i++){
                System.out.println(x[i] + " printed");
            }
        });

        commands.put("help", (x) -> {
            for (String cname : commands.keySet()){
                System.out.println("/" + cname);
            }
            System.out.println("e | exit | q | quit -- to exit");
        });

        commands.put("time", (x) -> {
            String o = null;
            for (int i = 0; i < x.length - 1; i++) {
                if (x[i].equals("-o")) {
                    o = x[i + 1];
                }
            }
            if (o != null) {
                if (objectNames.keySet().contains(o) && objectNames.get(o) instanceof Tickable){
                    Tickable toTick = (Tickable)objectNames.get(o);
                    toTick.tick();
                    System.out.println("Time passed for all applicable objects.");
                    EntityController.instance.appendInfo("Time passed for all applicable objects.");
                } else {
                    System.out.println("Object with name -o [OBJECT] == " + o + "not found");
                    System.out.println("Object with name -o [OBJECT] == " + o + "not a Tickable");
                }
            } else{
                List<Tickable> uniqueTickables = objectNames.values().stream().filter(v -> v instanceof Tickable).map(v -> (Tickable) v).distinct().collect(Collectors.toList());
                TimeManager tm = new TimeManager(uniqueTickables);
                tm.tickAll();
                System.out.println("Time passed for all applicable objects.");
                EntityController.instance.appendInfo("Time passed for all applicable objects.");

            }
        });

        commands.put("addpm", (x) -> {
            String name = null;
            for (int i = 0; i < x.length - 1; i++) {
                if (x[i].equals("-n")) {
                    name = x[i + 1];
                }
            }
            if (name != null) {
                Mycologist mycologist = new Mycologist("Default");
                Interpreter.objectNames.put(name, mycologist);
                System.out.println("(" + name + ") Mycologist created successfully.");
                EntityController.instance.appendInfo("(" + name + ") Mycologist created successfully.");
            } else {
                System.out.println("Error: Missing -n [NAME] parameter.");
                EntityController.instance.appendInfo("Error: Missing -n [NAME] parameter.");
            }
        });

        commands.put("addpi", (x) -> {
            String name = null;
            for (int i = 0; i < x.length - 1; i++) {
                if (x[i].equals("-n")) {
                    name = x[i + 1];
                }
            }
            if (name != null) {
                Insectist insectist = new Insectist("Default");
                Interpreter.objectNames.put(name, insectist);
                System.out.println("(" + name + ") Insectist created successfully.");
                EntityController.instance.appendInfo("(" + name + ") Insectist created successfully.");
            } else {
                System.out.println("Error: Missing -n [NAME] parameter.");
                EntityController.instance.appendInfo("Error: Missing -n [NAME] parameter.");
            }
        });

        commands.put("addm", (x) -> {
            String id = null;
            String tectonName = null;
            String mycologistName = null;
            int level = 1; // default level
        
            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-id":
                        id = x[i + 1];
                        break;
                    case "-lvl":
                        try {
                            level = Integer.parseInt(x[i + 1]);
                            if (level < 1 || level > 2) {
                                System.out.println("Error: Level must be between 1 and 2.");
                                EntityController.instance.appendInfo("Error: Level must be between 1 and 2.");
                                return;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Error: The specified level is not a number.");
                            EntityController.instance.appendInfo("Error: The specified level is not a number.");
                            return;
                        }
                        break;
                    case "-t":
                        tectonName = x[i + 1];
                        break;
                    case "-my":
                        mycologistName = x[i + 1];
                        break;
                }
            }
        
            if (id != null && tectonName != null && mycologistName != null) {
                Object tectonObj = Interpreter.getObject(tectonName);
                // Itt keresd név alapján a mycologistet a mycologistByName mapből:
                Mycologist mycologist = mycologistByName.get(mycologistName);

                if (tectonObj instanceof Tecton && mycologist != null) {
                    Tecton tecton = (Tecton) tectonObj;

                    Mushroom mushroom = new Mushroom(level);
                    boolean success = tecton.addMushroom(mushroom);
                    if (success) {
                        mycologist.addMushroom(mushroom); // Add to the mycologist's list
                        Interpreter.objectNames.put(id, mushroom); // Save the identifier
                        System.out.println("Mushroom created: " + id + " with level " + level + " on tecton " + tectonName + ", added to mycologist: " + mycologistName);
                        EntityController.instance.appendInfo("Mushroom created: " + id + " with level " + level + " on tecton " + tectonName + ", added to mycologist: " + mycologistName);
                    } else {
                        System.out.println("Error: Failed to add mushroom to tecton.");
                        EntityController.instance.appendInfo("Error: Failed to add mushroom to tecton.");
                    }
                } else {
                    if (!(tectonObj instanceof Tecton)) {
                        System.out.println("Error: Invalid tecton identifier: " + tectonName);
                        EntityController.instance.appendInfo("Error: Invalid tecton identifier: " + tectonName);
                    }
                    if (mycologist == null) {
                        System.out.println("Error: No mycologist found with name: " + mycologistName + ". (Did you create it in the menu?)");
                        EntityController.instance.appendInfo("Error: No mycologist found with name: " + mycologistName + ". (Did you create it in the menu?)");
                    }
                }
            } else {
                System.out.println("Error: Missing required parameter(s) (-id, -t, or -my).");
                EntityController.instance.appendInfo("Error: Missing required parameter(s) (-id, -t, or -my).");
            }
        });

        commands.put("addi", (x) -> {
            String id = null;
            String tectonName = null;
            String insectistName = null;
        
            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-id":
                        id = x[i + 1];
                        break;
                    case "-t":
                        tectonName = x[i + 1];
                        break;
                    case "-in":
                        insectistName = x[i + 1];
                        break;
                }
            }
        
            if (id != null && tectonName != null && insectistName != null) {
                Object tectonObj = Interpreter.getObject(tectonName);
                Object insectistObj = Interpreter.getObject(insectistName);
        
                if (tectonObj instanceof Tecton && insectistObj instanceof Insectist) {
                    Tecton tecton = (Tecton) tectonObj;
                    Insectist insectist = (Insectist) insectistObj;
        
                    Insect insect = new Insect();
                    insect.setLocation(tecton);
                    insectist.addInsect(insect);
        
                    Interpreter.objectNames.put(id, insect); // Azonosító mentése
                    System.out.println("Insect created: "+id+" on tecton " + tectonName + ", added to insectist: " + insectistName);
                    EntityController.instance.appendInfo("Insect created: "+id+" on tecton " + tectonName + ", added to insectist: " + insectistName);
                } else {
                    System.out.println("Error: Invalid tecton or insectist identifier.");
                    EntityController.instance.appendInfo("Error: Invalid tecton or insectist identifier.");
                }
            } else {
                System.out.println("Error: Missing required parameter(s) -id, -t or -in.");
                EntityController.instance.appendInfo("Error: Missing required parameter(s) -id, -t or -in.");
            }
        });

        commands.put("addt", (x) -> {
            String id = null;
            String type = null;

            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-id":
                        id = x[i + 1];
                        break;
                    case "-t":
                        type = x[i + 1];
                        break;
                }
            }

            if (id != null && type != null) {
                Tecton tecton = null;
                switch (type) {
                    case "tecton":
                        tecton = new Tecton();
                        objectNames.put(id, tecton);
                        break;
                    case "nomushtecton":
                        tecton = new NoMushTecton();
                        objectNames.put(id, tecton);
                        break;
                    case "singlethreadtecton":
                        tecton = new SingleThreadTecton();
                        objectNames.put(id, tecton);
                        break;
                    case "threadabsorbertecton":
                        tecton = new ThreadAbsorberTecton();
                        objectNames.put(id, tecton);
                        break;
                    case "threadkeepertecton":
                        tecton = new ThreadKeeperTecton();
                        objectNames.put(id, tecton);
                        break;
                    default:
                        System.out.println("Error: Unknown tecton type.");
                        EntityController.instance.appendInfo("Error: Unknown tecton type.");
                        return;
                }

                System.out.println("("+id+") Tecton of type default created successfully.");
                EntityController.instance.appendInfo("("+id+") Tecton of type default created successfully.");
            } else {
                System.out.println("Error: Missing required parameter(s) -id or -t.");
                EntityController.instance.appendInfo("Error: Missing required parameter(s) -id or -t.");
            }
        });

        commands.put("addth", (x) -> {
            String id = null;
            String t1Name = null;
            String t2Name = null;

            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-id":
                        id = x[i + 1];
                        break;
                    case "-t1":
                        t1Name = x[i + 1];
                        break;
                    case "-t2":
                        t2Name = x[i + 1];
                        break;
                }
            }

            if (id != null && t1Name != null && t2Name != null) {
                Object t1Obj = Interpreter.getObject(t1Name);
                Object t2Obj = Interpreter.getObject(t2Name);

                if (t1Obj instanceof Tecton && t2Obj instanceof Tecton) {
                    System.out.println("Thread grown: " + id + " from " + t1Name + " to " + t2Name);
                    EntityController.instance.appendInfo("Thread grown: " + id + " from " + t1Name + " to " + t2Name);
                } else {
                    System.out.println("Error: One or both tectons do not exist or are of invalid type.");
                    EntityController.instance.appendInfo("Error: One or both tectons do not exist or are of invalid");
                }
            } else {
                System.out.println("Error: Missing required parameter(s) -id, -t1 or -t2.");
                EntityController.instance.appendInfo("Error: Missing required parameter(s) -id, -t1 or -t2.");
            }
        });

        commands.put("addsp", (x) -> {
            String mushroomName = null;
            String sporeType = null;

            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-m":
                        mushroomName = x[i + 1];
                        break;
                    case "-tp":
                        sporeType = x[i + 1];
                        break;
                }
            }

            if (mushroomName != null && sporeType != null) {
                Object mushroomObj = Interpreter.getObject(mushroomName);
                if (mushroomObj instanceof Mushroom) {
                    Spore spore = null;
                    switch (sporeType) {
                        case "speedyspore":
                            spore = new SpeedySpore();
                            break;
                        case "slowyspore":
                            spore = new SlowlySpore();
                            break;
                        case "paralyzespore":
                            spore = new ParalyzeSpore();
                            break;
                        case "cannotcutspore":
                            spore = new CannotCutSpore();
                            break;
                        case "clonespore":
                            spore = new CloneSpore();
                            break;
                        default:
                            System.out.println("Error: Unknown spore type: " + sporeType);
                            EntityController.instance.appendInfo("Error: Unknown spore type: " + sporeType);
                            return;
                    }

                    ((Mushroom) mushroomObj).addSpore(spore);
                    System.out.println("Spore added to mushroom " + mushroomName);
                    EntityController.instance.appendInfo("Spore added to mushroom " + mushroomName);
                } else {
                    System.out.println("Error: The given identifier is not a mushroom.");
                    EntityController.instance.appendInfo("Error: The given identifier is not a mushroom.");
                }
            } else {
                System.out.println("Error: Missing required parameter(s) -m or -tp.");
                EntityController.instance.appendInfo("Error: Missing required parameter(s) -m or -tp.");
            }
        });

        commands.put("getm", (x) -> {
            String playerName = null;
        
            for (int i = 0; i < x.length - 1; i++) {
                if (x[i].equals("-n")) {
                    playerName = x[i + 1];
                }
            }
        
            if (playerName != null) {
                Object playerObj = Interpreter.getObject(playerName);
                // TODO: Mycologist osztály meg nincsen
                // Egy gombász pontszámainak lekérdezése a játékban
                System.out.println("TODO: Querying the score of a mycologist in the game.");
            } else {
                System.out.println("Error: Missing -n parameter.");
                EntityController.instance.appendInfo("Error: Missing -n parameter.");
            }
        });

        commands.put("geti", (x) -> {
            String playerName = null;
        
            for (int i = 0; i < x.length - 1; i++) {
                if (x[i].equals("-n")) {
                    playerName = x[i + 1];
                }
            }
        
            if (playerName != null) {
                Object playerObj = Interpreter.getObject(playerName);
                // TODO: Instectist osztály meg nincsen
                //  Egy gombász pontszámainak lekérdezése a játékban. Ez a gombászhoz tartozó összes gomba számát jelenti.
                System.out.println("TODO: Querying the score of an insectist in the game.");
            } else {
                System.out.println("Error: Missing -n parameter.");
                EntityController.instance.appendInfo("Error: Missing -n parameter.");
            }
        });

        commands.put("win", (x) -> { 
            // TODO: Mycologist , Instectist class not implemented
            // A parancs hatására véget ér a játék és mindkét fajta játékosnál a legtöbb pontot szerzett lesz a nyertes. Kiírja minden játékoshoz tartozó pontszámot. 
            System.out.println("TODO: The game ends and the player with the highest score wins.");
        });

        commands.put("altm", (x) -> {
            String id = null;
            Integer level = null;
        
            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-id":
                        id = x[i + 1];
                        break;
                    case "-lvl":
                        try {
                            level = Integer.parseInt(x[i + 1]);
                            if (level < 1 || level > 2) {
                                System.out.println("Error: Level must be between 1 and 2.");
                                EntityController.instance.appendInfo("Error: Level must be between 1 and 2.");
                                return;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Error: The specified level is not a number.");
                            EntityController.instance.appendInfo("Error: The specified level is not a number.");
                            return;
                        }
                        break;
                }
            }
        
            if (id == null || level == null) {
                System.out.println("Error: Missing required parameter(s) (-id or -lvl).");
                EntityController.instance.appendInfo("Error: Missing required parameter(s) (-id or -lvl).");
                return;
            }
        
            Object obj = Interpreter.getObject(id);
            if (obj instanceof Mushroom) {
                Mushroom mushroom = (Mushroom) obj;
                if (level == 2 && mushroom.getLevel() == 1) {
                    mushroom.evolve();
                } else if (level == 1 && mushroom.getLevel() == 2) {
                    System.out.println("Error: The mushroom is already at level 2.");
                    EntityController.instance.appendInfo("Error: The mushroom is already at level 2.");
                    return;
                } else {
                    return;
                }
                System.out.println("Mushroom modified: " + id + ", new level: " + level);
                EntityController.instance.appendInfo("Mushroom modified: " + id + ", new level: " + level);
            } else {
                System.out.println("Error: No mushroom found with identifier: " + id);
                EntityController.instance.appendInfo("Error: No mushroom found with identifier: " + id);
            }
        });

        commands.put("alti", (x) -> {
            String id = null;
        
            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-id":
                        id = x[i + 1];
                        break;
                }
            }
        
            if (id == null) {
                System.out.println("Error: Missing required parameter -id.");
                EntityController.instance.appendInfo("Error: Missing required parameter -id.");
                return;
            }
        
            Object obj = Interpreter.getObject(id);
            if (obj instanceof Insect) {
                Insect insect = (Insect) obj;
                // TODO: No state to modify according to documentation
                return;
            } else {
                System.out.println("Error: No insect found with identifier: " + id);
                EntityController.instance.appendInfo("Error: No insect found with identifier: " + id);
            }
        });

        commands.put("altt", (x) -> {
            String id = null;
            String addNeighbor = null;
            String removeNeighbor = null;

            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-id":
                        id = x[i + 1];
                        break;
                    case "-addn":
                        addNeighbor = x[i + 1];
                        break;
                    case "-remn":
                        removeNeighbor = x[i + 1];
                        break;
                }
            }

            if (id == null) {
                System.out.println("Error: Missing required parameter: -id");
                EntityController.instance.appendInfo("Error: Missing required parameter: -id");
                return;
            }

            Object tectonObj = Interpreter.getObject(id);
            if (!(tectonObj instanceof Tecton)) {
                System.out.println("Error: No tecton found with identifier: " + id);
                EntityController.instance.appendInfo("Error: No tecton found with identifier: " + id);
                return;
            }

            Tecton tecton = (Tecton) tectonObj;

            if (addNeighbor != null) {
                Object neighborObj = Interpreter.getObject(addNeighbor);
                if (neighborObj instanceof Tecton) {
                    Tecton neighbor = (Tecton) neighborObj;
                    tecton.addNeighbour(neighbor);
                    neighbor.addNeighbour(tecton); // Ensure mutual addition
                    System.out.println("Tecton " + id + ": neighbor " + addNeighbor + " added");
                    EntityController.instance.appendInfo("Tecton " + id + ": neighbor " + addNeighbor + " added");
                } else {
                    System.out.println("Error: No neighbor tecton found with identifier: " + addNeighbor);
                    EntityController.instance.appendInfo("Error: No neighbor tecton found with identifier: " + addNeighbor);
                }
            }

            if (removeNeighbor != null) {
                Object neighborObj = Interpreter.getObject(removeNeighbor);
                if (neighborObj instanceof Tecton) {
                    Tecton neighbor = (Tecton) neighborObj;
                    tecton.removeNeighbour(neighbor);
                    neighbor.removeNeighbour(tecton); // Ensure mutual removal
                    System.out.println("Neighbor removed: " + removeNeighbor + " from tecton: " + id);
                    EntityController.instance.appendInfo("Neighbor removed: " + removeNeighbor + " from tecton: " + id);
                } else {
                    System.out.println("Error: No neighbor tecton found with identifier: " + removeNeighbor);
                    EntityController.instance.appendInfo("Error: No neighbor tecton found with identifier: " + removeNeighbor);
                }
            }

            if (addNeighbor == null && removeNeighbor == null) {
                System.out.println("Warning: No changes made, as no -addn or -remn parameter was provided.");
                EntityController.instance.appendInfo("Warning: No changes made, as no -addn or -remn parameter was provided.");
            }
        });

        commands.put("exec", (x) -> {
            String name = null;
            for (int i = 0; i < x.length - 1; i++) {
                if ("-n".equals(x[i])) {
                    name = x[i + 1];
                    break;
                }
            }

            if (name == null) {
                System.out.println("Error: Missing filename (-n).");
                EntityController.instance.appendInfo("Error: Missing filename (-n).");
                return;
            }

            try {
                reset();
                String filepath = "tests/" + name + "/input.txt";
                BufferedReader reader = new BufferedReader(new FileReader(filepath));
                String line;
                HLogPrintStream.resetLog();
                while ((line = reader.readLine()) != null) {
                    processCommand(line); // <- EZ MEGY
                }
                HLogPrintStream.saveLog("tests/" + name + "/output.txt");
                String outputPath   = System.getProperty("user.dir") + File.separator + "tests" + File.separator + name + File.separator + "output.txt";
                String expectedPath = System.getProperty("user.dir") + File.separator + "tests" + File.separator + name + File.separator + "expected.txt";

                ProcessBuilder pb = new ProcessBuilder("fc", outputPath, expectedPath);
                pb.redirectErrorStream(true);

                Process p = pb.start();

                // read the tool's output
                try (BufferedReader procOut = new BufferedReader(
                        new InputStreamReader(p.getInputStream()))) {
                    while ((line = procOut.readLine()) != null) {
                        System.out.println(line);
                    }
                }

                // wait for it to finish and check exit code
                int exitCode = p.waitFor();
                if (exitCode == 0) {
                    System.out.println("output.txt matches expected.txt");
                } else {
                    System.out.println("output.txt differs from expected.txt  (exit code " + exitCode + ")");
                }
                reader.close();
            } catch (Exception e) {
                System.out.println("Error during loading: " + e.getMessage());
            }
        });

        commands.put("execall", (x) -> {
            for (int i = 1; i <= 30; i++) {
                String testName = "test" + i;
                System.out.println("Executing: " + testName);
                try {
                    reset(); // Reset state before each test
                    String filepath = "tests/" + testName + "/input.txt";
                    BufferedReader reader = new BufferedReader(new FileReader(filepath));
                    String line;
                    HLogPrintStream.resetLog();
                    while ((line = reader.readLine()) != null) {
                        processCommand(line); // Process each command in the test file
                    }
                    HLogPrintStream.saveLog("tests/" + testName + "/output.txt");
                    String outputPath = System.getProperty("user.dir") + File.separator + "tests" + File.separator
                            + testName + File.separator + "output.txt";
                    String expectedPath = System.getProperty("user.dir") + File.separator + "tests" + File.separator
                            + testName + File.separator + "expected.txt";

                    ProcessBuilder pb = new ProcessBuilder("fc", outputPath, expectedPath);
                    pb.redirectErrorStream(true);

                    Process p = pb.start();

                    // read the tool's output
                    try (BufferedReader procOut = new BufferedReader(
                            new InputStreamReader(p.getInputStream()))) {
                        while ((line = procOut.readLine()) != null) {
                            System.out.println(line);
                        }
                    }

                    // wait for it to finish and check exit code
                    int exitCode = p.waitFor();
                    if (exitCode == 0) {
                        System.out.println("output.txt matches expected.txt");
                    } else {
                        System.out.println("output.txt differs from expected.txt  (exit code " + exitCode + ")");
                    }
                    reader.close();
                    System.out.println("Test " + testName + " executed successfully.\n\n");
                } catch (Exception e) {
                    System.out.println("Error executing " + testName + ": " + e.getMessage());
                }
            }
        });

        commands.put("hlog", (x) -> {
            String name = null;
        
            for (int i = 0; i < x.length - 1; i++) {
                if ("-n".equals(x[i])) {
                    name = x[i + 1];
                }
            }
            if (name == null) {
                System.out.println("Error: Missing required parameter: -n");
                return;
            }
        
            HLogPrintStream.saveLog(name);
        });

        commands.put("lstm", (x) -> {
            removeAutoDuplicates(objectNames);
            System.out.println("Listing mushrooms:");
            for (Map.Entry<String, Object> entry : Interpreter.objectNames.entrySet()) {
                if (entry.getValue() instanceof Mushroom) {
                    Mushroom mushroom = (Mushroom) entry.getValue();
                    System.out.println(entry.getKey() + " - Level: " + mushroom.getLevel());
                    EntityController.instance.appendInfo(entry.getKey() + " - Level: " + mushroom.getLevel());
                }
            }
        });

        commands.put("lsts", (x) -> {
            removeAutoDuplicates(objectNames);
            System.out.println("Listing spores and their associated tectons:");
            EntityController.instance.appendInfo("Listing spores and their associated tectons:");
            for (Map.Entry<String, Object> entry : Interpreter.objectNames.entrySet()) {
                if (entry.getValue() instanceof Tecton) {
                    Tecton tecton = (Tecton) entry.getValue();
                    List<Spore> spores = tecton.getSpores(); // Assuming Tecton class has a getSpores() method
                    if (!spores.isEmpty()) {
                        System.out.println("Tecton: " + entry.getKey());
                        EntityController.instance.appendInfo("Tecton: " + entry.getKey());
                        for (Spore spore : spores) {
                            System.out.println("  - Spore: " + spore.getClass().getSimpleName());
                            EntityController.instance.appendInfo("  - Spore: " + spore.getClass().getSimpleName());
                        }
                    }
                }
            }
        });

        commands.put("lsti", (x) -> {
            removeAutoDuplicates(objectNames);
            System.out.println("List insects:");
            EntityController.instance.appendInfo("List insects:");
            for (Map.Entry<String, Object> entry : Interpreter.objectNames.entrySet()) {
                if (entry.getValue() instanceof Insect) {
                    Insect insect = (Insect) entry.getValue();
                    System.out.println("ID: " + entry.getKey());
                    System.out.println("  Speed: " + insect.getSpeed());
                    System.out.println("  Cut: " + insect.getCut());
                    System.out.println("  Spores:");
                    EntityController.instance.appendInfo("ID: " + entry.getKey());
                    EntityController.instance.appendInfo("  Speed: " + insect.getSpeed());
                    EntityController.instance.appendInfo("  Cut: " + insect.getCut());
                    EntityController.instance.appendInfo("  Spores:");
                    List<Spore> spores = insect.getSpores(); // Assuming Insect class has getSpores() method
                    if (spores.isEmpty()) {
                        System.out.println("    None");
                        EntityController.instance.appendInfo("    None");
                    } else {
                        for (Spore spore : spores) {
                            System.out.println("    - " + spore.getClass().getSimpleName());
                            EntityController.instance.appendInfo("    - " + spore.getClass().getSimpleName());
                        }
                    }
                }
            }
        });

        commands.put("lstth", (x) -> {
            removeAutoDuplicates(objectNames);
            System.out.println("Listing threads:");
            EntityController.instance.appendInfo("Listing threads:");
            for (Map.Entry<String, Object> entry : Interpreter.objectNames.entrySet()) {
                if (entry.getValue() instanceof Thread) {
                    Thread thread = (Thread) entry.getValue();
                    System.out.println(entry.getKey() + " - Size: " + thread.getSize() + ", isKept: " + thread.isKept() + ", isCutOff: " + thread.isCutOff());
                    EntityController.instance.appendInfo(entry.getKey() + " - Size: " + thread.getSize() + ", isKept: " + thread.isKept() + ", isCutOff: " + thread.isCutOff());
                }
            }
        });

        commands.put("lstt", (x) -> {
            removeAutoDuplicates(objectNames);
            System.out.println("Listing tectons:");
            EntityController.instance.appendInfo("Listing tectons:");
            for (Map.Entry<String, Object> entry : Interpreter.objectNames.entrySet()) {
                if (entry.getValue() instanceof Tecton) {
                    Tecton tecton = (Tecton) entry.getValue();
                    System.out.println(entry.getKey() + " - Number of neighbors: " + tecton.getNeighbors().size());
                    EntityController.instance.appendInfo(entry.getKey() + " - Number of neighbors: " + tecton.getNeighbors().size());
                }
            }
        });

        commands.put("clone", (x) -> {
            String id = null;
        
            for (int i = 0; i < x.length - 1; i++) {
                if ("-id".equals(x[i])) {
                    id = x[i + 1];
                }
            }
        
            if (id == null) {
                System.out.println("Error: An insect must be specified with the -id switch.");
                EntityController.instance.appendInfo("Error: An insect must be specified with the -id switch.");
                return;
            }
        
            Object insectObj = Interpreter.getObject(id);
            if (insectObj instanceof Insect) {
                Insect original = (Insect) insectObj;
                Insect clone = original.clone();
                System.out.println("Insect cloned: " + id);
                EntityController.instance.appendInfo("Insect cloned: " + id);
            } else {
                System.out.println("Error: No such insect found.");
                EntityController.instance.appendInfo("Error: No such insect found.");
            }
        });

    commands.put("move", (x) -> {
    String insectId = null;
    String tectonId = null;

    for (int i = 0; i < x.length - 1; i++) {
        switch (x[i]) {
            case "-i":
                insectId = x[i + 1];
                break;
            case "-t":
                tectonId = x[i + 1];
                break;
        }
    }

    if (insectId != null && tectonId != null) {
        Object insectObj = Interpreter.getObject(insectId);
        Object tectonObj = Interpreter.getObject(tectonId);

        if (insectObj instanceof Insect && tectonObj instanceof Tecton) {
            Insect insect = (Insect) insectObj;
            Tecton tecton = (Tecton) tectonObj;

            // Keressük meg az Insectist-et, amelyhez a rovar tartozik
            for (Object obj : Interpreter.objectNames.values()) {
                if (obj instanceof Insectist) {
                    Insectist insectist = (Insectist) obj;
                    List<Insect> insects = insectist.getInsects();
                    if (insects.contains(insect)) {
                        // Mozgatás az Insectist moveInsect() metódusával
                        int index = insects.indexOf(insect);
                        insectist.moveInsect(index, tecton);
                        System.out.println("Insect moved: " + insectId + " -> " + tectonId);
                        EntityController.instance.appendInfo("Insect moved: " + insectId + " -> " + tectonId);
                        return;
                    }
                }
            }
            System.out.println("Error: The insect does not belong to any insectist.");
            EntityController.instance.appendInfo("Error: The insect does not belong to any insectist.");
        } else {
            System.out.println("Error: Invalid insect or tecton ID.");
            EntityController.instance.appendInfo("Error: Invalid insect or tecton ID.");
        }
        } else {
            System.out.println("Error: Missing required parameter(s) (-i or -t).");
            EntityController.instance.appendInfo("Error: Missing required parameter(s) (-i or -t).");
        }
    });

    commands.put("growm", (x) -> {
        String tectonId = null;
        String mycologistId = null;

        for (int i = 0; i < x.length - 1; i++) {
            switch (x[i]) {
                case "-t":
                    tectonId = x[i + 1];
                    break;
                case "-my":
                    mycologistId = x[i + 1];
                    break;
            }
        }

        if (tectonId != null && mycologistId != null) {
            Object tectonObj = Interpreter.getObject(tectonId);
            // Helyesen: a mycologist-et a mycologistByName map-ből szedd ki!
            Mycologist mycologist = mycologistByName.get(mycologistId);

            if (tectonObj instanceof Tecton && mycologist != null) {
                Tecton tecton = (Tecton) tectonObj;

                boolean success = mycologist.addMushroom(tecton); // Grow mushroom by the Mycologist
                if (success) {
                    System.out.println("Mushroom grown on tecton " + tectonId + " by mycologist " + mycologistId);
                    EntityController.instance.appendInfo("Mushroom grown on tecton " + tectonId + " by mycologist " + mycologistId);
                } else {
                    System.out.println("Error: Failed to grow mushroom on tecton " + tectonId);
                    EntityController.instance.appendInfo("Error: Failed to grow mushroom on tecton " + tectonId);
                }
            } else {
                System.out.println("Error: Invalid tecton or mycologist ID.");
                EntityController.instance.appendInfo("Error: Invalid tecton or mycologist ID.");
            }
        } else {
            System.out.println("Error: Missing required parameter(s) (-t or -my).");
            EntityController.instance.appendInfo("Error: Missing required parameter(s) (-t or -my).");
        }
    });

    commands.put("growt", (x) -> {
        String mushroomId = null;
        String targetTectonId = null;
    
        // Parse command arguments
        for (int i = 0; i < x.length - 1; i++) {
            switch (x[i]) {
                case "-m":
                    mushroomId = x[i + 1];
                    break;
                case "-tt":
                    targetTectonId = x[i + 1];
                    break;
            }
        }
    
        // Check if required parameters are provided
        if (mushroomId == null || targetTectonId == null) {
            System.out.println("Error: Missing required parameter(s) (-m or -tt).");
            EntityController.instance.appendInfo("Error: Missing required parameter(s) (-m or -tt).");
            return;
        }
    
        // Retrieve objects by ID
        Object mushroomObj = Interpreter.getObject(mushroomId);
        Object targetTectonObj = Interpreter.getObject(targetTectonId);
    
        // Validate object types
        if (!(mushroomObj instanceof Mushroom)) {
            System.out.println("Error: Invalid mushroom ID: " + mushroomId);
            EntityController.instance.appendInfo("Error: Invalid mushroom ID: " + mushroomId);
            return;
        }
        if (!(targetTectonObj instanceof Tecton)) {
            System.out.println("Error: Invalid target tecton ID: " + targetTectonId);
            EntityController.instance.appendInfo("Error: Invalid target tecton ID: " + targetTectonId);
            return;
        }
    
        Mushroom mushroom = (Mushroom) mushroomObj;
        Tecton targetTecton = (Tecton) targetTectonObj;
    
        // Find the Mycologist associated with the mushroom
        Mycologist mycologist = null;
        for (Object obj : Interpreter.objectNames.values()) {
            if (obj instanceof Mycologist) {
                Mycologist potentialMycologist = (Mycologist) obj;
                if (potentialMycologist.getMushrooms().contains(mushroom)) {
                    mycologist = potentialMycologist;
                    break;
                }
            }
        }
    
        if (mycologist == null) {
            System.out.println("Error: No Mycologist found for mushroom " + mushroomId);
            EntityController.instance.appendInfo("Error: No Mycologist found for mushroom " + mushroomId);
            return;
        }
    
        // Attempt to add a thread from the mushroom to the target tecton
        boolean success = mushroom.addThread(targetTecton);
        if (success) {
            System.out.println("Thread successfully grown from mushroom " + mushroomId + " to tecton " + targetTectonId);
            EntityController.instance.appendInfo("Thread successfully grown from mushroom " + mushroomId + " to tecton " + targetTectonId);
        } else {
            System.out.println("Error: Failed to grow thread from mushroom " + mushroomId + " to tecton " + targetTectonId);
            EntityController.instance.appendInfo("Error: Failed to grow thread from mushroom " + mushroomId + " to tecton " + targetTectonId);
        }
    });

        commands.put("shoot", (x) -> {
            String mushroomId = null;
            String tectonId = null;
        
            // Parse command arguments
            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-m":
                        mushroomId = x[i + 1];
                        break;
                    case "-t":
                        tectonId = x[i + 1];
                        break;
                }
            }
        
            // Check if required parameters are provided
            if (mushroomId == null || tectonId == null) {
                System.out.println("Error: Missing required parameter(s) (-m or -t).");
                EntityController.instance.appendInfo("Error: Missing required parameter(s) (-m or -t).");
                return;
            }
        
            // Retrieve objects by ID
            Object mushroomObj = Interpreter.getObject(mushroomId);
            Object tectonObj = Interpreter.getObject(tectonId);
        
            // Validate object types
            if (!(mushroomObj instanceof Mushroom)) {
                System.out.println("Error: Invalid mushroom ID: " + mushroomId);
                EntityController.instance.appendInfo("Error: Invalid mushroom ID: " + mushroomId);
                return;
            }
            if (!(tectonObj instanceof Tecton)) {
                System.out.println("Error: Invalid tecton ID: " + tectonId);
                EntityController.instance.appendInfo("Error: Invalid tecton ID: " + tectonId);
                return;
            }
        
            // Cast objects to their respective types
            Mushroom mushroom = (Mushroom) mushroomObj;
            Tecton tecton = (Tecton) tectonObj;
        
            // Call the shootSpores method
            boolean successfulShoot = mushroom.shootSpores(tecton);
            if (successfulShoot) {
                System.out.println("Spore shoot from " + mushroomId + " to " + tectonId);
                EntityController.instance.appendInfo("Spore shoot from " + mushroomId + " to " + tectonId);
            } else {
                System.out.println("Error: Failed to shoot spores from " + mushroomId + " to " + tectonId);
                EntityController.instance.appendInfo("Error: Failed to shoot spores from " + mushroomId + " to " + tectonId);
            }
        });

        commands.put("consume", (x) -> {
            String insectId = null;

            for (int i = 0; i < x.length - 1; i++) {
                if ("-i".equals(x[i])) {
                    insectId = x[i + 1];
                    break;
                }
            }

            if (insectId != null) {
                Object insectObj = Interpreter.getObject(insectId);
                if (insectObj instanceof Insect) {
                    Insect insect = (Insect) insectObj;
                    boolean success = insect.consumeSpore();
                    if (success) {
                        System.out.println(insectId + " consumed the spore.");
                        EntityController.instance.appendInfo(insectId + " consumed the spore.");
                    } else {
                        System.out.println("Error: No spore at the location.");
                        EntityController.instance.appendInfo("Error: No spore at the location.");
                    }
                } else {
                    System.out.println("Error: The given ID does not correspond to an insect or does not exist: " + insectId);
                    EntityController.instance.appendInfo("Error: The given ID does not correspond to an insect or does not exist: " + insectId);
                }
            } else {
                System.out.println("Error: Missing -i parameter.");
                EntityController.instance.appendInfo("Error: Missing -i parameter.");
            }
        });

        commands.put("eat", (x) -> {
            String insectId = null;
            String threadId = null;
            String mycologistId = null;
        
            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-id":
                        insectId = x[i + 1];
                        break;
                    case "-th":
                        threadId = x[i + 1];
                        break;
                    case "-my":
                        mycologistId = x[i + 1];
                        break;
                }
            }
        
            if (insectId != null && threadId != null && mycologistId != null) {
                Object insectObj = Interpreter.getObject(insectId);
                Object threadObj = Interpreter.getObject(threadId);
                Object mycologistObj = Interpreter.getObject(mycologistId);
        
                if (insectObj instanceof Insect && threadObj instanceof Thread && mycologistObj instanceof Mycologist) {
                    Insect insect = (Insect) insectObj;
                    Thread thread = (Thread) threadObj;
                    Mycologist mycologist = (Mycologist) mycologistObj;
        
                    // Call the Mycologist's eatInsect method
                    mycologist.eatInsect(thread, insect);
                } else {
                    System.out.println("Error: Invalid insect, thread, or mycologist identifier.");
                    EntityController.instance.appendInfo("Error: Invalid insect, thread, or mycologist identifier.");
                }
            } else {
                System.out.println("Error: Missing parameter(s) (-id, -th, or -my).");
                EntityController.instance.appendInfo("Error: Missing parameter(s) (-id, -th, or -my).");
            }
        });

        commands.put("cut", (x) -> {
            String insectId = null;
            String threadId = null;
        
            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-i":
                        insectId = x[i + 1];
                        break;
                    case "-th":
                        threadId = x[i + 1];
                        break;
                }
            }
        
            if (insectId != null && threadId != null) {
                Object insectObj = Interpreter.getObject(insectId);
                Object threadObj = Interpreter.getObject(threadId);
        
                if (insectObj instanceof Insect && threadObj instanceof Thread) {
                    Insect insect = (Insect) insectObj;
                    Thread thread = (Thread) threadObj;
        
                    // Keressük meg az Insectist-et, amelyhez a rovar tartozik
                    for (Object obj : Interpreter.objectNames.values()) {
                        if (obj instanceof Insectist) {
                            Insectist insectist = (Insectist) obj;
                            List<Insect> insects = insectist.getInsects();
                            if (insects.contains(insect)) {
                                // Fonal elvágása az Insectist cutThread() metódusával
                                int index = insects.indexOf(insect);
                                insectist.cutThread(index, thread);
                                System.out.println("Thread cut: " + threadId + " by insect: " + insectId);
                                EntityController.instance.appendInfo("Thread cut: " + threadId + " by insect: " + insectId);
                                return;
                            }
                        }
                    }
        
                    System.out.println("Error: The insect does not belong to any insectist.");
                    EntityController.instance.appendInfo("Error: The insect does not belong to any insectist.");
                } else {
                    System.out.println("Error: Invalid insect or thread identifier.");
                    EntityController.instance.appendInfo("Error: Invalid insect or thread identifier.");
                }
            } else {
                System.out.println("Error: Missing required parameter(s) (-i or -th).");
                EntityController.instance.appendInfo("Error: Missing required parameter(s) (-i or -th).");
            }
        });

        commands.put("break", (x) -> {
            String id = null;
            String newId = null;
        
            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-id":
                        id = x[i + 1];
                        break;
                    case "-newid":
                        newId = x[i + 1];
                        break;
                }
            }
        
            if (id != null && newId != null) {
                Object tectonObj = Interpreter.getObject(id);
                if (tectonObj instanceof Tecton) {
                    Tecton original = (Tecton) tectonObj;
                    Tecton newTecton = original.breakTecton(); 
                    if (newTecton != null) {
                        //Interpreter.putObject(newId, newTecton);
                        System.out.println("Tecton " + id + " broken, new tecton created: " + newId);
                        EntityController.instance.appendInfo("Tecton " + id + " broken, new tecton created: " + newId);
                    } else {
                        System.out.println("Error: Break failed.");
                        EntityController.instance.appendInfo("Error: Break failed.");
                    }
                } else {
                    System.out.println("Error: No tecton found with identifier: " + id);
                    EntityController.instance.appendInfo("Error: No tecton found with identifier: " + id);
                }
            } else {
                System.out.println("Error: Missing parameter(s) (-id or -newid).");
                EntityController.instance.appendInfo("Error: Missing parameter(s) (-id or -newid).");
            }
        });
    }

    // Segédfüggvény a sorok feldolgozására
    public static void processCommand(String value) {
        value = value.toLowerCase();
        String[] parts = value.trim().split("\\s+");
        if (parts.length == 0) return;

        int argCount = 0;
        String command = parts[0].substring(1); // Első karakter levágása
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        int fasz = 0;

        var cmd = commands.get(command);
        if (cmd != null) {
            cmd.execute(args);
        } else {
            System.out.println("Unknown command: " + command);
            EntityController.instance.appendInfo("Unknown command: " + command);
        }
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

    public static void remove(Object obj) {
        if (obj == null) {
            return;
        }
        String nameToRemove = null;
        for (Map.Entry<String, Object> entry : objectNames.entrySet()) {
            if (entry.getValue() == obj) {
                nameToRemove = entry.getKey();
                break;
            }
        }
        if (nameToRemove != null) {
            objectNames.remove(nameToRemove);
        }
    }


    public static Object getObject(String name) {
        // Check if the name exists in the objectNames map.
        if (objectNames.containsKey(name)) {
            return objectNames.get(name);
        // If not maybe because it is an auto-generated name.
        } else if (objectNames.containsKey(name + "_auto")) {
            return objectNames.get(name + "_auto");
        }
        return null;
    }

    public static Map<String, Object> getObjects() {
        return objectNames;
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

    public static void removeAutoDuplicates(Map<String, Object> map) {
        // 1) Count how many times each value appears
        Map<Object, Integer> counts = new HashMap<>();
        for (Object v : map.values()) {
            counts.put(v, counts.getOrDefault(v, 0) + 1);
        }

        // 2) Remove entries whose value is duplicated AND whose key ends with "_auto"
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> e = it.next();
            if (counts.get(e.getValue()) > 1
                    && e.getKey().endsWith("_auto")) {
                it.remove();
            }
        }
    }

    public static void executeCommand(String inputString) {
        inputString = inputString.trim().toLowerCase();
        System.out.println("Executing command: " + inputString);
        EntityController.instance.appendInfo(inputString);
        inputString = inputString.replace("/", "");
        String[] args = inputString.split(" ");
        if (commands.containsKey(args[0])) {
            commands.get(args[0]).execute(Arrays.copyOfRange(args, 1, args.length));
        } else {
            System.out.println("Command not found.");
            EntityController.instance.appendInfo("Command not found.");
        }
        removeAutoDuplicates(objectNames);
        controller.refreshController(objectNames);
    }

    public static void setMycologists(List<Mycologist> mycologists) {
        mycologistByName.clear();
        for (Mycologist m : mycologists) {
            mycologistByName.put(m.getName(), m);
        }
    }
}
