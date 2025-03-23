# Fungorium

## Játék rövid leírása
A **Fungorium** egy stratégiai játék, amelyben rovarok és gombák versengenek az irányításért. A játékos célja, hogy kezelje a gombafonalak terjedését, miközben különböző rovarok és spórák hatásait kihasználja.

## Letöltés
A projekt letöltéséhez futtasd az alábbi parancsot a terminálban:

```sh
git clone https://github.com/fungorium/fungorium.git
cd fungorium
```

## Fordítás és futtatás
A forráskód fordítása és futtatása parancssorból:

```sh
javac -d out src/fungorium/**/*.java
java -cp out fungorium.Main
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

