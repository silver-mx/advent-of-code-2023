package dns.challenge.day2;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static dns.challenge.day2.CubeConundrum.PATTERN_GAME;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

public record Game(int id, Map<String, Integer> colorToQuantityMap) {
    private static final Pattern PATTERN_CONTENTS = Pattern.compile("\s*(\\d+) (.*)");

    public Game(int id, String cubeData) {
        this(id, toCubeContentMap(cubeData));
    }

    private static Map<String, Integer> toCubeContentMap(String cubeData) {
        Map<String, Optional<Integer>> colorToQuantityMap = Arrays.stream(cubeData.split(";"))
                .map(t -> Arrays.stream(t.split(","))
                        .map(cubesData -> {
                            Matcher matcher = PATTERN_CONTENTS.matcher(cubesData);
                            if (!matcher.matches()) {
                                throw new IllegalArgumentException("Invalid cube data[" + cubesData + "]");
                            }
                            return new CubeContent(matcher.group(2), Integer.parseInt(matcher.group(1)));
                        }).toList())
                .flatMap(List::stream)
                .collect(groupingBy(CubeContent::color, mapping(CubeContent::quantity, maxBy(comparingInt(o -> o)))));

        return colorToQuantityMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().orElseThrow()));
    }

    public int getIdIfPossible(List<CubeContent> criteria) {
        boolean isPossible = criteria.stream().allMatch(c ->
                colorToQuantityMap.containsKey(c.color()) && colorToQuantityMap.get(c.color()) <= c.quantity()
        );

        return isPossible ? id : 0;
    }

    public int getPower() {
        return colorToQuantityMap.values().stream().reduce(1, (a, b) -> a * b);
    }

    public static void main(String[] args) {
        Matcher matcher = PATTERN_GAME.matcher("Game 1: 8 green, 4 red, 4 blue; 1 green, 6 red, 4 blue; 7 red, 4 green, 1 blue; 2 blue, 8 red, 8 green");
        System.out.println("matches=" + matcher.matches());
        System.out.println("group1=" + matcher.group(1));
        System.out.println("group2=" + matcher.group(2));

        Matcher matcherContents = PATTERN_CONTENTS.matcher(" 8 green");
        System.out.println("matches contents=" + matcherContents.matches());
        System.out.println("quantity=" + matcherContents.group(1));
        System.out.println("color=" + matcherContents.group(2));
    }
}
