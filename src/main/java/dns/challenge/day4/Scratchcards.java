package dns.challenge.day4;

import dns.challenge.utils.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.summingInt;

public class Scratchcards {

    private static final Pattern PATTERN = Pattern.compile("Card\\s+(\\d+):(.*)\\|(.*)");

    public static ScratchcardsResult execute(String inputPath) throws IOException {
        ScratchcardsResult result = processInput(Util.loadInput(inputPath));
        System.out.println("==========> result=" + result);

        return result;
    }

    private static ScratchcardsResult processInput(List<String> allLines) {
        List<PointToCardCopies> pointToCardCopiesLst = allLines.stream()
                //.peek(x -> System.out.println("===============> line=" + x))
                .map(Scratchcards::parseLine)
                .map(Scratchcards::countPointsAndCardCopies)
                //.peek(x -> System.out.println("count=" + x))
                .toList();

        // Part 1
        int points = pointToCardCopiesLst.stream().mapToInt(PointToCardCopies::points).sum();

        // Part 2
        Map<Integer, Integer> idToCopiesMap = pointToCardCopiesLst.stream().map(e -> e.cardCopies().stream()
                        .collect(Collectors.toMap(CardCopies::id, CardCopies::numCopies))
                        .entrySet())
                .flatMap(Set::stream)
                .collect(Collectors.groupingBy(Map.Entry::getKey, summingInt(Map.Entry::getValue)));

        // We still need to adjust how upstream copies affect downstream copies
        int totalCopies = getTotalNumCopies(idToCopiesMap, pointToCardCopiesLst);

        return new ScratchcardsResult(points, totalCopies);
    }

    private static int getTotalNumCopies(Map<Integer, Integer> copies, List<PointToCardCopies> pointToCardCopiesLst) {
        // Adjust the copies
        for (var e : pointToCardCopiesLst) {
            int cardId = e.card().id();
            int numCopies = copies.get(cardId);
            int numMatchesInCard = e.cardCopies().size();

            if (numCopies > 1) {
                IntStream.range(1, numMatchesInCard).forEach(i -> {
                    int targetCard = cardId + i;
                    int currentValue = copies.get(targetCard);
                    copies.put(targetCard, currentValue + numCopies - 1);
                });
            }
        }

        return copies.values().stream().mapToInt(Integer::intValue).sum();
    }

    private static PointToCardCopies countPointsAndCardCopies(Card card) {
        int matchingNumbers = (int) card.cardNumbers().stream().filter(n -> card.winNumbers().contains(n)).count();
        List<CardCopies> cardCopies = IntStream.range(0, matchingNumbers + 1)
                .mapToObj(i -> new CardCopies(card.id() + i, 1)).toList();

        if (matchingNumbers == 1) {
            return new PointToCardCopies(card, 1, cardCopies);
        } else if (matchingNumbers > 1) {
            return new PointToCardCopies(card, (int) Math.pow(2, matchingNumbers - 1), cardCopies);
        }

        return new PointToCardCopies(card, 0, cardCopies);
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
