package dns.challenge.day3;

import dns.challenge.utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class GearRatios {

    private static final Pattern PATTERN_SYMBOL = Pattern.compile("([^.\\d])");
    private static final Pattern PATTERN_DIGIT = Pattern.compile("(\\d+)");

    public static int execute(String inputPath) throws IOException {
        int score = processInput(Util.loadInput(inputPath));
        System.out.println("==========> result=" + score);

        return 0;
    }

    private static int processInput(List<String> allLines) {
        return IntStream.range(0, allLines.size())
                .map(i -> sumValidValues(i, allLines))
                .sum();
    }

    private static int sumValidValues(int index, List<String> allLines) {
        List<Integer> digitsInLine = new ArrayList<>();
        List<SymbolPosition> symbols = findSymbolPositions(index, allLines);
        String line = allLines.get(index);
        Matcher matcher = PATTERN_DIGIT.matcher(line);

        System.out.println("=================>line = " + line);

        while (matcher.find()) {
            int digit = Integer.parseInt(matcher.group(1));
            int column = matcher.start(1);
            if (hasAdjacentSymbol(digit, index, column, symbols)) {
                digitsInLine.add(digit);
            }
        }

        int digitsInLineSum = digitsInLine.stream().mapToInt(Integer::intValue).sum();
        System.out.println("+++++++++ digitsInLineSum=" + digitsInLineSum);

        return digitsInLineSum;
    }

    private static boolean hasAdjacentSymbol(int digit, int row, int column, List<SymbolPosition> symbols) {
        int digitLength = String.valueOf(digit).length();
        Set<Integer> validRows = Set.of(row - 1, row, row + 1);
        Set<Integer> validColumns = IntStream.range(column - 1, column + digitLength + 1).boxed().collect(toSet());

        return symbols.stream().anyMatch(position -> {
            boolean isAdjacentTo = validRows.contains(position.row()) && validColumns.contains(position.column());
            if (isAdjacentTo) System.out.println("digit = " + digit + " has adjacent symbol[" + position + "]");
            return isAdjacentTo;
        });
    }

    private static List<SymbolPosition> findSymbolPositions(int index, List<String> allLines) {
        List<SymbolPosition> symbolsLineBefore = index - 1 >= 0 ? findSymbols(index - 1, allLines.get(index - 1)) : Collections.emptyList();
        List<SymbolPosition> symbolsLine = findSymbols(index, allLines.get(index));
        List<SymbolPosition> symbolsLineAfter = index + 1 < allLines.size() ? findSymbols(index + 1, allLines.get(index + 1)) : Collections.emptyList();

        return Stream.concat(Stream.concat(symbolsLineBefore.stream(), symbolsLine.stream()), symbolsLineAfter.stream()).toList();

    }

    private static List<SymbolPosition> findSymbols(int row, String line) {
        Matcher matcher = PATTERN_SYMBOL.matcher(line);
        List<SymbolPosition> symbols = new ArrayList<>();

        while (matcher.find()) {
            String symbol = matcher.group(1);
            int column = matcher.start(1);
            symbols.add(new SymbolPosition(symbol, row, column));
        }

        return symbols;
    }
}
