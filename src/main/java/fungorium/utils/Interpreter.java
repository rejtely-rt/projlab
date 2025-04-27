package fungorium.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//loadhoz kellenek
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



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
import fungorium.tectons.OneThreadTecton;
import fungorium.tectons.Tecton;
import fungorium.tectons.ThreadAbsorberTecton;
import fungorium.tectons.ThreadKeeperTecton;
import fungorium.model.Mycologist;
import fungorium.model.Insectist;

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

        commands.put("addpm", (x) -> {
            String name = null;
            for (int i = 0; i < x.length - 1; i++) {
                if (x[i].equals("-n")) {
                    name = x[i + 1];
                }
            }
            if (name != null) {
                Mycologist mycologist = new Mycologist();
                Interpreter.objectNames.put(name, mycologist);
                System.out.println("(" + name + ") Mycologist created successfully.");
            } else {
                System.out.println("Hiba: Hiányzó -n [NAME] paraméter.");
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
                Insectist insectist = new Insectist();
                Interpreter.objectNames.put(name, insectist);
                System.out.println("Rovarász létrehozva: " + name);
            } else {
                System.out.println("Hiba: Hiányzó -n [NAME] paraméter.");
            }
        });

        commands.put("addm", (x) -> {
            String id = null;
            String tectonName = null;
            String mycologistName = null;
            int level = 1; // alapértelmezett szint
        
            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-id":
                        id = x[i + 1];
                        break;
                    case "-lvl":
                        try {
                            level = Integer.parseInt(x[i + 1]);
                            if (level < 1 || level > 2) {
                                System.out.println("Hiba: A szintnek 1-2 között kell lennie.");
                                return;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Hiba: A megadott szint nem szám.");
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
                Object mycologistObj = Interpreter.getObject(mycologistName);
        
                if (tectonObj instanceof Tecton && mycologistObj instanceof Mycologist) {
                    Tecton tecton = (Tecton) tectonObj;
                    Mycologist mycologist = (Mycologist) mycologistObj;
        
                    Mushroom mushroom = new Mushroom(level);
                    boolean success = tecton.addMushroom(mushroom);
                    if (success) {
                        mycologist.addMushroom(mushroom); // Hozzáadás a gombász listájához
                        Interpreter.objectNames.put(id, mushroom); // Azonosító mentése
                        System.out.println("Mushroom created: " + id + " with level " + level + " on tecton " + tectonName + ", added to mycologist: " + mycologistName);
                    } else {
                        System.out.println("Hiba: A gomba hozzáadása a tektonhoz nem sikerült.");
                    }
                } else {
                    System.out.println("Hiba: Hibás tekton vagy gombász azonosító.");
                }
            } else {
                System.out.println("Hiba: Hiányzó kötelező paraméter(ek) (-id, -t vagy -my).");
            }
        });

        commands.put("addi", (x) -> {
            String id = null;
            String tectonName = null;

            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-id":
                        id = x[i + 1];
                        break;
                    case "-t":
                        tectonName = x[i + 1];
                        break;
                }
            }

            if (id != null && tectonName != null) {
                Object tectonObj = Interpreter.getObject(tectonName);
                if (tectonObj instanceof Tecton) {
                    Insect insect = new Insect();
                    insect.setLocation((Tecton) tectonObj);
                    System.out.println("Rovar létrehozva: " + id);
                } else {
                    System.out.println("Hiba: Nem található a megadott tekton, vagy nem tekton típus.");
                }
            } else {
                System.out.println("Hiba: Hiányzó kötelező paraméter(ek) -id vagy -t.");
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
                        break;
                    case "nomushtecton":
                        tecton = new NoMushTecton();
                        break;
                    case "onethreadtecton":
                        tecton = new OneThreadTecton();
                        break;
                    case "threadabsorbertecton":
                        tecton = new ThreadAbsorberTecton();
                        break;
                    case "threadkeepertecton":
                        tecton = new ThreadKeeperTecton();
                        break;
                    default:
                        System.out.println("Hiba: Ismeretlen tekton típus.");
                        return;
                }

                System.out.println("("+id+") Tecton of type default created successfully.");
            } else {
                System.out.println("Hiba: Hiányzó kötelező paraméter(ek) -id vagy -t.");
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
                    Thread thread = new Thread();
                    System.out.println("Fonal létrehozva: " + id + " a tektonok között: " + t1Name + " és " + t2Name);
                } else {
                    System.out.println("Hiba: Az egyik vagy mindkét tekton nem létezik, vagy nem megfelelő típusú.");
                }
            } else {
                System.out.println("Hiba: Hiányzó kötelező paraméter(ek) -id, -t1 vagy -t2.");
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
                            System.out.println("Hiba: Ismeretlen spóra típus: " + sporeType);
                            return;
                    }

                    ((Mushroom) mushroomObj).addSpore(spore);
                    System.out.println("Spore added to mushroom " + mushroomName);
                } else {
                    System.out.println("Hiba: A megadott azonosító nem egy gomba.");
                }
            } else {
                System.out.println("Hiba: Hiányzó kötelező paraméter(ek) -m vagy -tp.");
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
                System.out.println("TODO: Egy gombász pontszámainak lekérdezése a játékban");
            } else {
                System.out.println("Hiba: Hiányzó -n paraméter.");
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
                System.out.println("TODO:  Egy gombász pontszámainak lekérdezése a játékban. Ez a gombászhoz tartozó összes gomba számát jelenti.\n");
            } else {
                System.out.println("Hiba: Hiányzó -n paraméter.");
            }
        });

        commands.put("win", (x) -> { 
            // TODO: Mycologist , Instectist class not implemented
            // A parancs hatására véget ér a játék és mindkét fajta játékosnál a legtöbb pontot szerzett lesz a nyertes. Kiírja minden játékoshoz tartozó pontszámot. 
            System.out.println("TODO: A parancs hatására véget ér a játék és mindkét fajta játékosnál a legtöbb pontot szerzett lesz a nyertes. Kiírja minden játékoshoz tartozó pontszámot.");
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
                                System.out.println("Hiba: A szintnek 1-2 között kell lennie.");
                                return;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Hiba: A megadott szint nem szám.");
                            return;
                        }
                        break;
                }
            }
        
            if (id == null || level == null) {
                System.out.println("Hiba: hiányzó kötelező paraméter(ek) (-id vagy -lvl).");
                return;
            }
        
            Object obj = Interpreter.getObject(id);
            if (obj instanceof Mushroom) {
                Mushroom mushroom = (Mushroom) obj;
                if (level == 2 && mushroom.getLevel() == 1) {
                    mushroom.evolve();
                } else if (level == 1 && mushroom.getLevel() == 2) {
                    System.out.println("Hiba: A gomba már 2-es szinten van.");
                    return;
                } else {
                    return;
                }
                System.out.println("Gomba módosítva: " + id + ", új szint: " + level);
            } else {
                System.out.println("Hiba: Nem található gomba azonosítóval: " + id);
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
                System.out.println("Hiba: hiányzó kötelező paraméter -id.");
                return;
            }
        
            Object obj = Interpreter.getObject(id);
            if (obj instanceof Insect) {
                Insect insect = (Insect) obj;
                // TODO: Nincs mit modositani documentacio szerint, cxsak valami state-et ami nem letezik.
                // igy ertelme se lenne ennek a commandnak
                return;
            } else {
                System.out.println("Hiba: Nem található rovar azonosítóval: " + id);
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
                System.out.println("Hiba: hiányzó kötelező paraméter: -id");
                return;
            }

            Object tectonObj = Interpreter.getObject(id);
            if (!(tectonObj instanceof Tecton)) {
                System.out.println("Hiba: Nem található tekton azonosítóval: " + id);
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
                } else {
                    System.out.println("Hiba: Nem található szomszéd tekton azonosítóval: " + addNeighbor);
                }
            }

            if (removeNeighbor != null) {
                Object neighborObj = Interpreter.getObject(removeNeighbor);
                if (neighborObj instanceof Tecton) {
                    Tecton neighbor = (Tecton) neighborObj;
                    tecton.removeNeighbour(neighbor);
                    neighbor.removeNeighbour(tecton); // Ensure mutual removal
                    System.out.println("Eltávolítva szomszéd: " + removeNeighbor + " a tektonból: " + id);
                } else {
                    System.out.println("Hiba: Nem található szomszéd tekton azonosítóval: " + removeNeighbor);
                }
            }

            if (addNeighbor == null && removeNeighbor == null) {
                System.out.println("Figyelem: Nem történt változtatás, mert nem adtál meg -addn vagy -remn paramétert.");
            }
        });

        commands.put("save", (x) -> {
            String name = null;
        
            for (int i = 0; i < x.length - 1; i++) {
                if ("-n".equals(x[i])) {
                    name = x[i + 1];
                }
            }
        
            if (name == null) {
                System.out.println("Hiba: hiányzó kötelező paraméter: -n");
                return;
            }
        
            try {
                // TODO: Implement the saving logic here.
                System.out.println("Játék sikeresen elmentve: " + name);
            } catch (Exception e) {
                System.out.println("Hiba a mentés során: " + e.getMessage());
            }
        });

        /*
        commands.put("load", (x) -> {
            String name = null;
            for (int i = 0; i < x.length - 1; i++) {
                if ("-n".equals(x[i])) {
                    name = x[i + 1];
                    break;
                }
            }
        
            if (name == null) {
                System.out.println("Hiba: hiányzik a fájlnév (-n).");
                return;
            }
        
            try {
                // TODO: Implement the loading logic here.
                System.out.println("Allapot betöltve: " + name);
            } catch (Exception e) {
                System.out.println("Hiba a betöltés közben: " + e.getMessage());
            }
        });*/

        /*
                commands.put("load", (x) -> {
            String name = null;
            for (int i = 0; i < x.length - 1; i++) {
                if ("-n".equals(x[i])) {
                    name = x[i + 1];
                    break;
                }
            }

            if (name == null) {
                System.out.println("Hiba: hiányzik a fájlnév (-n).");
                return;
            }

            try {
                String filepath = "tests/" + name + "/input.txt";
                BufferedReader reader = new BufferedReader(new FileReader(filepath));
                String line;
                while ((line = reader.readLine()) != null) {
                    processCommand(line); // <- EZ MEGY
                }
                reader.close();
                System.out.println("Allapot betöltve: test" + name);
            } catch (Exception e) {
                System.out.println("Hiba a betöltés közben: " + e.getMessage());
            }
        });
         */

        commands.put("load", (x) -> {
            String name = null;
            for (int i = 0; i < x.length - 1; i++) {
                if ("-n".equals(x[i])) {
                    name = x[i + 1];
                    break;
                }
            }

            if (name == null) {
                System.out.println("Hiba: hiányzik a fájlnév (-n).");
                return;
            }

            try {
                // TODO: Implement the loading logic here.
                System.out.println("Allapot betöltve: " + name);
            } catch (Exception e) {
                System.out.println("Hiba a betöltés közben: " + e.getMessage());
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
                System.out.println("Hiba: hiányzik a fájlnév (-n).");
                return;
            }

            try {
                String filepath = "tests/" + name + "/input.txt";
                BufferedReader reader = new BufferedReader(new FileReader(filepath));
                String line;
                while ((line = reader.readLine()) != null) {
                    processCommand(line); // <- EZ MEGY
                }
                reader.close();
                System.out.println("Allapot betöltve: test" + name);
            } catch (Exception e) {
                System.out.println("Hiba a betöltés közben: " + e.getMessage());
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
                System.out.println("Hiba: hiányzó kötelező paraméter: -n");
                return;
            }
        
            try {
                // TODO: Implement the logic to save the log to a file.
                System.out.println("Log fájlba mentve: " + name);
            } catch (Exception e) {
                System.out.println("Hiba a log mentésekor: " + e.getMessage());
            }
        });

        commands.put("lstm", (x) -> {
            System.out.println("Gombák listázása:");
            for (Map.Entry<String, Object> entry : Interpreter.objectNames.entrySet()) {
                if (entry.getValue() instanceof Mushroom) {
                    Mushroom mushroom = (Mushroom) entry.getValue();
                    System.out.println(entry.getKey() + " - Szint: " + mushroom.getLevel());
                }
            }
        });

        commands.put("lsts", (x) -> {
            System.out.println("Spórák és a hozzájuk tartozó tektonok listája:");
            for (Map.Entry<String, Object> entry : Interpreter.objectNames.entrySet()) {
                if (entry.getValue() instanceof Tecton) {
                    Tecton tecton = (Tecton) entry.getValue();
                    List<Spore> spores = tecton.getSpores(); // Feltételezve, hogy van egy getSpores() metódus a Tecton osztályban
                    if (!spores.isEmpty()) {
                        System.out.println("Tecton: " + entry.getKey());
                        for (Spore spore : spores) {
                            System.out.println("  - Spóra: " + spore.getClass().getSimpleName());
                        }
                    }
                }
            }
        });

        commands.put("lsti", (x) -> {
            System.out.println("Rovarok listázása:");
            for (Map.Entry<String, Object> entry : Interpreter.objectNames.entrySet()) {
                if (entry.getValue() instanceof Insect) {
                    Insect insect = (Insect) entry.getValue();
                    System.out.println(entry.getKey());
                }
            }
        });

        commands.put("lstth", (x) -> {
            System.out.println("Fonalak listázása:");
            for (Map.Entry<String, Object> entry : Interpreter.objectNames.entrySet()) {
                if (entry.getValue() instanceof Thread) {
                    Thread thread = (Thread) entry.getValue();
                    System.out.println(entry.getKey() + " - Size: " + thread.getSize());
                }
            }
        });

        commands.put("lstt", (x) -> {
            System.out.println("Tektonok listázása:");
            for (Map.Entry<String, Object> entry : Interpreter.objectNames.entrySet()) {
                if (entry.getValue() instanceof Tecton) {
                    Tecton tecton = (Tecton) entry.getValue();
                    System.out.println(entry.getKey() + " - Szomszédok száma: " + tecton.getNeighbors().size());
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
                System.out.println("Hiba: meg kell adni egy rovart az -id kapcsolóval.");
                return;
            }
        
            Object insectObj = Interpreter.getObject(id);
            if (insectObj instanceof Insect) {
                Insect original = (Insect) insectObj;
                Insect clone = original.clone();
                System.out.println("Rovar klónozva: " + id);
            } else {
                System.out.println("Hiba: Nem található ilyen rovar.");
            }
        });

        commands.put("time", (x) -> {
            String insectId = null;
            String objectId = null;
        
            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-id":
                        insectId = x[i + 1];
                        break;
                    case "-o":
                        objectId = x[i + 1];
                        break;
                }
            }
        
            // TODO: Implement the logic to check the time of the insect and object.
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
                        System.out.println("Rovar mozgatva: " + insectId + " -> " + tectonId);
                        return;
                    }
                }
            }

            System.out.println("Hiba: A rovar nem tartozik egyetlen rovarászhoz sem.");
        } else {
            System.out.println("Hiba: Hibás rovar vagy tekton azonosító.");
        }
        } else {
            System.out.println("Hiba: Hiányzó kötelező paraméter(ek) (-i vagy -t).");
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
            Object mycologistObj = Interpreter.getObject(mycologistId);
    
            if (tectonObj instanceof Tecton && mycologistObj instanceof Mycologist) {
                Tecton tecton = (Tecton) tectonObj;
                Mycologist mycologist = (Mycologist) mycologistObj;
    
                mycologist.addMushroom(tecton); // Növesztés a Mycologist által
                System.out.println("Gomba növesztve a tektonon: " + tectonId + " a gombász által: " + mycologistId);
            } else {
                System.out.println("Hiba: Hibás tekton vagy gombász azonosító.");
            }
        } else {
            System.out.println("Hiba: Hiányzó kötelező paraméter(ek) (-t vagy -my).");
        }
    });

    commands.put("growt", (x) -> {
        String mushroomIndexStr = null;
        String targetTectonId = null;
        String mycologistId = null;
    
        for (int i = 0; i < x.length - 1; i++) {
            switch (x[i]) {
                case "-m":
                    mushroomIndexStr = x[i + 1];
                    break;
                case "-tt":
                    targetTectonId = x[i + 1];
                    break;
                case "-my":
                    mycologistId = x[i + 1];
                    break;
            }
        }
        if (mushroomIndexStr != null && targetTectonId != null && mycologistId != null) {
            Object mycologistObj = Interpreter.getObject(mycologistId);
            Object targetTectonObj = Interpreter.getObject(targetTectonId);
    
            if (mycologistObj instanceof Mycologist && targetTectonObj instanceof Tecton) {
                Mycologist mycologist = (Mycologist) mycologistObj;
                Tecton targetTecton = (Tecton) targetTectonObj;
    
                try {
                    int mushroomIndex = Integer.parseInt(mushroomIndexStr);
                    mycologist.addThread(mushroomIndex, targetTecton);
                } catch (NumberFormatException e) {
                    System.out.println("Hiba: A megadott gomba index nem szám.");
                }
            } else {
                System.out.println("Hiba: Hibás gombász vagy céltekton azonosító.");
            }
            } else {
                System.out.println("Hiba: Hiányzó kötelező paraméter(ek) (-m, -tt vagy -my).");
            }
        });

        commands.put("shoot", (x) -> {
            String mushroomId = null;
            String tectonId = null;
        
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
        
            if (mushroomId != null && tectonId != null) {
                Object mushroomObj = Interpreter.getObject(mushroomId);
                Object tectonObj = Interpreter.getObject(tectonId);
        
                if (mushroomObj instanceof Mushroom && tectonObj instanceof Tecton) {
                    Mushroom mushroom = (Mushroom) mushroomObj;
                    Tecton tecton = (Tecton) tectonObj;
        
                    // Keressük meg a gombához tartozó Mycologist-et
                    for (Object obj : Interpreter.objectNames.values()) {
                        if (obj instanceof Mycologist) {
                            Mycologist mycologist = (Mycologist) obj;
                            if (mycologist.getMushrooms().contains(mushroom)) {
                                mycologist.shootSpores(mycologist.getMushrooms().indexOf(mushroom), tecton);
                                System.out.println("Spore shot from " + mushroomId + " to " + tectonId);
                                return;
                            }
                        }
                    }
        
                    System.out.println("Hiba: A gomba nem tartozik egyetlen gombászhoz sem.");
                } else {
                    System.out.println("Hiba: Hibás gomba vagy tekton azonosító.");
                }
            } else {
                System.out.println("Hiba: Hiányzó kötelező paraméter(ek) (-m vagy -t).");
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
                        System.out.println(insectId + " megette a spórát.");
                    } else {
                        System.out.println("Hiba: Nincs spóra a helyszínen.");
                    }
                } else {
                    System.out.println("Hiba: Nem található vagy nem rovar az adott azonosító: " + insectId);
                }
            } else {
                System.out.println("Hiba: Hiányzik az -i paraméter.");
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
                    System.out.println("Hiba: Nem található vagy hibás típus az adott rovar, fonal vagy gombász azonosító.");
                }
            } else {
                System.out.println("Hiba: Hiányzó paraméter(ek) (-id, -th vagy -my).");
            }
        });

        commands.put("cut", (x) -> {
            String insectId = null;
            String threadId = null;
            String insectistId = null;
        
            for (int i = 0; i < x.length - 1; i++) {
                switch (x[i]) {
                    case "-i":
                        insectId = x[i + 1];
                        break;
                    case "-th":
                        threadId = x[i + 1];
                        break;
                    case "-in":
                        insectistId = x[i + 1];
                        break;
                }
            }
        
            if (insectId != null && threadId != null && insectistId != null) {
                Object insectObj = Interpreter.getObject(insectId);
                Object threadObj = Interpreter.getObject(threadId);
                Object insectistObj = Interpreter.getObject(insectistId);
        
                if (insectObj instanceof Insect && threadObj instanceof Thread && insectistObj instanceof Insectist) {
                    Insect insect = (Insect) insectObj;
                    Thread thread = (Thread) threadObj;
                    Insectist insectist = (Insectist) insectistObj;
        
                    // Find the index of the insect in the Insectist's list
                    int index = insectist.getInsects().indexOf(insect);
                    if (index != -1) {
                        insectist.cutThread(index, thread);
                        System.out.println("Fonal elvágva: " + threadId + " rovar által: " + insectId);
                    } else {
                        System.out.println("Hiba: A rovar nem tartozik a megadott rovarászhoz.");
                    }
                } else {
                    System.out.println("Hiba: Nem található vagy hibás típus az adott rovar, fonal vagy rovarász azonosító.");
                }
            } else {
                System.out.println("Hiba: Hiányzó paraméter(ek) (-i, -th vagy -in).");
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
                        System.out.println("Tekton törve: " + id + " -> új tekton: " + newId);
                    } else {
                        System.out.println("Hiba: A törés nem sikerült.");
                    }
                } else {
                    System.out.println("Hiba: Nem található vagy nem tekton típusú az adott azonosító.");
                }
            } else {
                System.out.println("Hiba: Hiányzó paraméter(ek) (-id vagy -newid).");
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
            System.out.println("Ismeretlen parancs: " + command);
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
