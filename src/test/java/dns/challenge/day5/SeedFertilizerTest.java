package dns.challenge.day5;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SeedFertilizerTest {

    @Test
    void execute() throws IOException {
        SeedFertilizerResult result = SeedFertilizer.execute("input/day5-input.txt");
        //SeedFertilizerResult expected = new SeedFertilizerResult(35, 46);
        SeedFertilizerResult expected = new SeedFertilizerResult(31599214, 20358599);

        assertEquals(expected, result);
    }
}