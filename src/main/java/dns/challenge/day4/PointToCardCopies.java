package dns.challenge.day4;

import java.util.List;

public record PointToCardCopies(Card card, int points, List<CardCopies> cardCopies) {
}
