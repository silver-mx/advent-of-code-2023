package dns.challenge.day3;

import java.util.List;

public record Part(int digit, int row, int column, List<AdjacentSymbol> adjacentSymbols) {
}
