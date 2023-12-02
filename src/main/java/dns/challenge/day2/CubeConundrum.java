package dns.challenge.day2;

import dns.challenge.utils.Util;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CubeConundrum {

    static final Pattern PATTERN_GAME = Pattern.compile("Game (\\d+):(.*)?");

    private static final List<CubeContent> CRITERIA = List.of(
            new CubeContent("red", 12),
            new CubeContent("green", 13),
            new CubeContent("blue", 14));

    public static int execute(String inputPath) throws IOException {
        ScorePower totalScorePower = processInput(Util.loadInput(inputPath));
        System.out.println("==========> result=" + totalScorePower);

        return 0;
    }

    private static ScorePower processInput(List<String> allLines) {
        return allLines.stream()
                //.peek(gameLine -> System.out.println("*****>line = " + gameLine))
                .map(gameLine -> {
                    Matcher matcher = PATTERN_GAME.matcher(gameLine);
                    if (!matcher.matches()) {
                        throw new IllegalStateException("Invalid game data [" + gameLine + "]");
                    }
                    return new Game(Integer.parseInt(matcher.group(1)), matcher.group(2));
                })
                //.peek(game -> System.out.println("=====>game = " + game))
                .map(game -> new ScorePower(game.getIdIfPossible(CRITERIA), game.getPower()))
                //.peek(scorePower -> System.out.println("-------> scorePower = " + scorePower))
                .reduce((a, b) -> new ScorePower(a.score() + b.score(), a.power() + b.power())).orElseThrow();
    }
}
