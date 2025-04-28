package fungorium.utils;

import java.io.*;

public class HLogPrintStream extends PrintStream {
    private static final ByteArrayOutputStream memoryOutput = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;

    public HLogPrintStream() {
        super(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                originalOut.write(b); // Konzolra kiír
                originalOut.flush();
                memoryOutput.write(b); // Memóriába is ment
            }
        }, true); // autoflush
    }

    public static void saveLog(String filename) {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(memoryOutput.toByteArray());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            System.err.println("Hlog hiba: " + e.getMessage());
        }
    }

    public static void resetLog() {
        memoryOutput.reset();
    }
}
