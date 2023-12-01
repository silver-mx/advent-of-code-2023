package dns.challenge;

import dns.challenge.day1.Trebuchet;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        int day = Integer.parseInt(args[0]);
        System.out.println("Executing challenge for day=" + day);
        if (day == 1) {
            Trebuchet.execute("input/day1-input.txt");
        }
    }
}