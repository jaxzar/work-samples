public class CardsDemo {
	
	public CardsDemo() { }

	public static void main(String[] args) {
		DeckOfCards deck = new DeckOfCards();
		Card C;

		System.out.println( "Number of cards: " + deck.getTotalCards() );

		while (deck.getTotalCards() != 0) {
			C = deck.draw();
			System.out.println("Drew next card: " + C.toString());
		}
		
		// deck is ordered when first built
		DeckOfCards orderedDeck = new DeckOfCards();
        orderedDeck.evaluateUniformDistribution("Sorted deck");
		
		// now we will shuffle it and output the result
        orderedDeck.sort();
		orderedDeck.spreadShuffle();
        orderedDeck.evaluateUniformDistribution("Spread shuffle");

        orderedDeck.sort();
		orderedDeck.zipShuffle();
        orderedDeck.evaluateUniformDistribution("Zip/riffle shuffle");

        //orderedDeck.sort();
		//orderedDeck.hinduShuffle();
        //orderedDeck.evaluateUniformDistribution("Hindu shuffle");

        orderedDeck.sort();
		orderedDeck.pileShuffle(3);
        orderedDeck.evaluateUniformDistribution("Pile shuffle (3)");

        orderedDeck.sort();
		orderedDeck.pileShuffle(4);
        orderedDeck.evaluateUniformDistribution("Pile shuffle (4)");

        orderedDeck.sort();
		orderedDeck.pileShuffle(5);
        orderedDeck.evaluateUniformDistribution("Pile shuffle (5)");

        orderedDeck.sort();
		orderedDeck.splitShuffle(2);
        orderedDeck.evaluateUniformDistribution("Split shuffle (2)");

        orderedDeck.sort();
		orderedDeck.splitShuffle(4);
        orderedDeck.evaluateUniformDistribution("Split shuffle (4)");
		
		//System.out.println("Shuffled deck, result is: ");
		
		// instead of a while loop, let's make use of Java 8 streams API
		//orderedDeck.stream().forEach(card -> System.out.println(card));
	}
}
