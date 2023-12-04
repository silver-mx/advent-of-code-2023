package dns.challenge.day4;

import dns.challenge.utils.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scratchcards {

    private static final Pattern PATTERN = Pattern.compile("Card\\s+(\\d+):(.*)\\|(.*)");

    public static int execute(String inputPath) throws IOException {
        int sum = processInput(Util.loadInput(inputPath));
        System.out.println("==========> result=" + sum);

        return 0;
    }

    private static int processInput(List<String> allLines) {
        return allLines.stream()
                .map(Scratchcards::parseLine)
                .map(Scratchcards::countPoints)
                .mapToInt(Integer::intValue)
                .sum();
    }

    private static int countPoints(Card card) {
        long matchingNumbers = card.cardNumbers().stream().filter(n -> card.winNumbers().contains(n)).count();

        if (matchingNumbers == 1) {
            return 1;
        } else if (matchingNumbers > 1) {
            return (int) Math.pow(2, matchingNumbers - 1);
        }

        return 0;
    }

    private static Card parseLine(String line) {
        Matcher matcher = PATTERN.matcher(line);

        if (matcher.matches()) {
            int id = Integer.parseInt(matcher.group(1));
            List<Integer> winNumbers = parseNumbersPart(matcher.group(2));
            List<Integer> cardNumbers = parseNumbersPart(matcher.group(3));

            return new Card(id, winNumbers, cardNumbers);
        }

        throw new IllegalArgumentException("Unparseable line=" + line);
    }

    private static List<Integer> parseNumbersPart(String numbersPart) {
        return Arrays.stream(numbersPart.trim().split("\\s+")).map(Integer::parseInt).toList();
    }
}
