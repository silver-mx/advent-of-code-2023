package dns.challenge.day1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Trebuchet {

    public static final Pattern REGEX_PATTERN = Pattern.compile(".*?(one|two|three|four|five|six|seven|eight|nine){1}.*?");

    public static void execute(String inputPath) throws IOException {
        Path path = Path.of(new File(Trebuchet.class.getClassLoader().getResource(inputPath).getFile()).getAbsolutePath());
        List<String> allLines = Files.readAllLines(path);
        int sum = processLines(allLines);
        System.out.println("result=" + sum);
    }

    private static int processLines(List<String> allLines) {
        return allLines.stream()
                .peek(line -> System.out.println("--------------------------Original line[" + line + "]"))
                .map(Trebuchet::replaceLetterDigitsWithDigits)
                .map(Trebuchet::findCalibrationValue)
                .mapToInt(Integer::parseInt)
                .sum();
    }

    private static String findCalibrationValue(String line) {
        String[] digits = line.chars().filter(Character::isDigit).mapToObj(Character::toString).collect(Collectors.joining()).split("");
        String detectedDigit = digits[0] + digits[digits.length - 1];
        System.out.println("line[" + line + "], digits" + Arrays.toString(digits) + ", detectedDigit=" + detectedDigit);

        return detectedDigit;
    }

    private static String replaceLetterDigitsWithDigits(String line) {
        Matcher matcher = REGEX_PATTERN.matcher(line);

        if (matcher.matches()) {

            String match = matcher.group(1);
            System.out.println("matches = " + match);

            String replacedLine = switch (match) {
                case "one" -> line.replace("one", "o1ne");
                case "two" -> line.replace("two", "t2wo");
                case "three" -> line.replace("three", "th3ree");
                case "four" -> line.replace("four", "f4our");
                case "five" -> line.replace("five", "f5ive");
                case "six" -> line.replace("six", "s6ix");
                case "seven" -> line.replace("seven", "s7even");
                case "eight" -> line.replace("eight", "e8ight");
                case "nine" -> line.replace("nine", "n9ine");
                default -> throw new IllegalArgumentException("Unable to handle " + match);
            };

            System.out.println("line[" + line + "], match[" + line + " => " + replacedLine + "]");

            return replaceLetterDigitsWithDigits(replacedLine);
        }

        return line;
    }

    public static void main(String[] args) {
        System.out.println("result = " + processLines(List.of("fourknflljrbrq63five")));
    }
}
