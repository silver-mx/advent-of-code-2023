package dns.challenge.day4;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScratchcardsTest {

    @Test
    void execute() throws IOException {
        ScratchcardsResult result = Scratchcards.execute("input/day4-input.txt");
        ScratchcardsResult expected = new ScratchcardsResult(28538, 9425061);
        assertEquals(expected, result);
    }

}