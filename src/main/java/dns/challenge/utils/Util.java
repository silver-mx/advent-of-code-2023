package dns.challenge.utils;

import dns.challenge.day1.Trebuchet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Util {

    public static List<String> loadInput(String inputPath) throws IOException {
        Path path = Path.of(new File(Util.class.getClassLoader().getResource(inputPath).getFile()).getAbsolutePath());
        return Files.readAllLines(path);
    }
}
