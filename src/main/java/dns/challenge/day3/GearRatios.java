package dns.challenge.day3;

import dns.challenge.utils.Util;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class GearRatios {

    private static final Pattern PATTERN_SYMBOL = Pattern.compile("([^.\\d])");
    private static final Pattern PATTERN_DIGIT = Pattern.compile("(\\d+)");

    public static int execute(String inputPath) throws IOException {
        GearRatiosResult result = processInput(Util.loadInput(inputPath));
        System.out.println("==========> result=" + result);

        return 0;
    }

    private static GearRatiosResult processInput(List<String> allLines) {
        List<Part> parts = IntStream.range(0, allLines.size())
                .mapToObj(i -> findParts(i, allLines))
                .flatMap(List::stream)
                .toList();

        // part 1
        int partsSum = parts.stream()
                .map(Part::digit)
                .mapToInt(Integer::intValue)
                .sum();

        // part 2
        Map<AdjacentSymbol, List<Part>> symbolToPartsMap = parts.stream()
                .map(part -> part.adjacentSymbols().stream()
                        .collect(toMap(Function.identity(), symbol -> part)))
                .map(Map::entrySet).flatMap(Set::stream)
                .collect(groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toList())))
                .entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        int gearSum = symbolToPartsMap.values().stream()
                .map(partList -> partList.stream().map(Part::digit)
                        .mapToInt(Integer::intValue)
                        .reduce(1, (a, b) -> a * b))
                .mapToInt(Integer::intValue)
                .sum();

        return new GearRatiosResult(partsSum, gearSum);
    }

    private static List<Part> findParts(int index, List<String> allLines) {
        List<Part> partsInLine = new ArrayList<>();
        List<AdjacentSymbol> symbols = findSymbolPositions(index, allLines);
        String line = allLines.get(index);
        Matcher matcher = PATTERN_DIGIT.matcher(line);

        System.out.println("=================>line = " + line);

        while (matcher.find()) {
            int digit = Integer.parseInt(matcher.group(1));
            int column = matcher.start(1);
            List<AdjacentSymbol> adjacentSymbols = findAdjacentSymbol(digit, index, column, symbols);
            if (!adjacentSymbols.isEmpty()) {
                partsInLine.add(new Part(digit, index, column, adjacentSymbols));
            }
        }

        return partsInLine;
    }

    private static List<AdjacentSymbol> findAdjacentSymbol(int digit, int row, int column, List<AdjacentSymbol> symbols) {
        int digitLength = String.valueOf(digit).length();
        Set<Integer> validRows = Set.of(row - 1, row, row + 1);
        Set<Integer> validColumns = IntStream.range(column - 1, column + digitLength + 1).boxed().collect(toSet());

        return symbols.stream().filter(position -> {
            boolean isAdjacentTo = validRows.contains(position.row()) && validColumns.contains(position.column());
            if (isAdjacentTo) System.out.println("digit = " + digit + " has adjacent symbol[" + position + "]");
            return isAdjacentTo;
        }).toList();
    }

    private static List<AdjacentSymbol> findSymbolPositions(int index, List<String> allLines) {
        List<AdjacentSymbol> symbolsLineBefore = index - 1 >= 0 ? findSymbols(index - 1, allLines.get(index - 1)) : Collections.emptyList();
        List<AdjacentSymbol> symbolsLine = findSymbols(index, allLines.get(index));
        List<AdjacentSymbol> symbolsLineAfter = index + 1 < allLines.size() ? findSymbols(index + 1, allLines.get(index + 1)) : Collections.emptyList();

        return Stream.concat(Stream.concat(symbolsLineBefore.stream(), symbolsLine.stream()), symbolsLineAfter.stream()).toList();

    }

    private static List<AdjacentSymbol> findSymbols(int row, String line) {
        Matcher matcher = PATTERN_SYMBOL.matcher(line);
        List<AdjacentSymbol> symbols = new ArrayList<>();

        while (matcher.find()) {
            String symbol = matcher.group(1);
            int column = matcher.start(1);
            symbols.add(new AdjacentSymbol(symbol, row, column));
        }

        return symbols;
    }
}
