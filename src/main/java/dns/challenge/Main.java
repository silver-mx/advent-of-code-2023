package dns.challenge;

import dns.challenge.day1.Trebuchet;
import dns.challenge.day2.CubeConundrum;
import dns.challenge.day3.GearRatios;
import dns.challenge.day4.Scratchcards;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int day = Integer.parseInt(args[0]);
        System.out.println("**************** Executing challenge for day=" + day + " ****************");

        switch (day) {
            case 1 -> Trebuchet.execute("input/day1-input.txt");
            case 2 -> CubeConundrum.execute("input/day2-input.txt");
            case 3 -> GearRatios.execute("input/day3-input.txt");
            case 4 -> Scratchcards.execute("input/day4-input.txt");
            default -> throw new IllegalArgumentException("No challenge for day=" + day);
        }
    }
}