# Fungorium

## Játék rövid leírása
A **Fungorium** egy stratégiai játék, amelyben rovarok és gombák versengenek az irányításért. A játékos célja, hogy kezelje a gombafonalak terjedését, miközben különböző rovarok és spórák hatásait kihasználja.

## Letöltés
A projekt letöltéséhez futtasd az alábbi parancsot a terminálban és cd-zz be oda ahol a Main.java fájl van:

```sh
git clone https://github.com/fungorium/fungorium.git
cd fungorium
```

## Fordítás és futtatás
A forráskód fordítása és futtatása parancssorból:

```sh
javac -d ./out/ Main.java utils/Utils.java utils/Logger.java utils/Initialize.java tectons/NoMushTecton.java tectons/OneThreadTecton.java tectons/Tecton.java tectons/ThreadAbsorberTecton.java spores/CannotCutSpore.java spores/ParalyzeSpore.java spores/SlowlySpore.java spores/SpeedySpore.java spores/Spore.java model/Insect.java model/Mushroom.java model/Thread.java
jar cfe fungorium.jar fungorium.Main -C PONTOS_ELERESI_UTVONAL\main\java\fungorium\out .
```

Ha Maven-t használsz:

```sh
mvn compile
mvn exec:java -Dexec.mainClass="fungorium.Main"
```

Ha Gradle-t használsz:

```sh
./gradlew build
java -cp build/classes/java/main fungorium.Main
```
## Gradle letöltése
A Gradle letöltéséhez látogass el a [Gradle hivatalos oldalára](https://gradle.org/install/).

## Rendszerkövetelmények
- Java 17 vagy újabb
- Maven vagy Gradle (opcionális, ha nem manuálisan fordítasz)