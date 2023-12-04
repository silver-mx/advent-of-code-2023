package dns.challenge.day4;

import dns.challenge.day1.Trebuchet;
import dns.challenge.utils.Util;

import java.io.IOException;
import java.util.List;

public class Scratchcards {

    public static int execute(String inputPath) throws IOException {
        int sum = processInput(Util.loadInput(inputPath));
        System.out.println("==========> result=" + sum);

        return 0;
    }

    private static int processInput(List<String> allLines) {
        return allLines.stream()
                .mapToInt(x -> 0)
                .sum();
    }
}
