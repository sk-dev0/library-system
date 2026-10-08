package library.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.*;

public class TsvIO {
    public static List<String> readLines(String filePath) throws IOException {
        return Files.readAllLines(Path.of(filePath), StandardCharsets.UTF_8);
    }

    public static void writeLines(String filePath, List<String> lines) throws IOException {
        Files.write(Path.of(filePath), lines, StandardCharsets.UTF_8);
    }
}
