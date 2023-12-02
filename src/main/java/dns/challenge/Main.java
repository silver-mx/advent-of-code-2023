package dns.challenge;

import dns.challenge.day1.Trebuchet;
import dns.challenge.day2.CubeConundrum;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        int day = Integer.parseInt(args[0]);
        System.out.println("**************** Executing challenge for day=" + day + " ****************");

        int result = switch (day) {
            case 1 -> Trebuchet.execute("input/day1-input.txt");
            case 2 -> CubeConundrum.execute("input/day2-input.txt");
            default -> throw new IllegalArgumentException("No challenge for day=" + day);
        };

        System.exit(result);
    }
}