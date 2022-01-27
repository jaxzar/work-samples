import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Stream;
 
public class DeckOfCards
{
    private static final int SHUFFLE_COUNT = 3;
    private List<Card> cards;
    private Random rand;

    DeckOfCards()
    {
        rand = new Random();
        cards = new ArrayList<Card>();
        for (int suitIndex = 0; suitIndex < 4; suitIndex++)
        {
            for (int rankIndex = 0; rankIndex < 13; rankIndex++)
            {
               cards.add( new Card(suitIndex,rankIndex) );
            }
        }
        System.out.println("Original deck of cards "+ cards);
    }

    public Card draw()
    {
        return cards.remove(0);
    }

    public Card drawRandom()
    {
        int index = rand.nextInt( cards.size() );
        return cards.remove(index);
    }

    public int getTotalCards()
    {
        return cards.size();
    }

    public boolean contains(Card c)
    {
        return cards.contains(c);
    }

    public void sort() {
        cards.clear();
        for (int suitIndex = 0; suitIndex < 4; suitIndex++)
        {
            for (int rankIndex = 0; rankIndex < 13; rankIndex++)
            {
               cards.add( new Card(suitIndex,rankIndex) );
            }
        }
    }

    // Equivalent to 52-card pickup. Spread shuffle; most random.
    public void spreadShuffle()
    {
        List<Card> shuffledDeck = new ArrayList<Card>();
        List<Card> tempDeck = null;
        for (int i = 0; i < SHUFFLE_COUNT; i++) {
            while (cards.size() > 0)
            {
                shuffledDeck.add( drawRandom() );
            }
            tempDeck = cards;
            cards = shuffledDeck;
            tempDeck.clear();
            shuffledDeck = tempDeck;
        }
    }

    // Cut deck exactly in half; then take one card from each half. (aka riffle shuffle)
    public void zipShuffle() {
        List<Card> half = new ArrayList<Card>();
        List<Card> shuffledDeck = new ArrayList<Card>();
        List<Card> tempDeck = null;
        for (int x = 0; x < SHUFFLE_COUNT; x++) {
            int h = cards.size()/2;
            for (int i = 0; i < h; i++) {
                half.add(draw());
            }
            int j = 0;
            while ((half.size() + cards.size()) > 0) {
                if (half.size() > 0 && cards.size() > 0) {
                    if (j % 2 == 0) {
                        shuffledDeck.add(half.remove(0));
                    } else {
                        shuffledDeck.add(draw());
                    }
                } else if (half.size() > 0) {
                    shuffledDeck.add(half.remove(0));
                } else if (cards.size() > 0) {
                    shuffledDeck.add(draw());
                }
                j++;
            }
            tempDeck = cards;
            cards = shuffledDeck;
            half.clear();
            shuffledDeck = tempDeck;
            shuffledDeck.clear();
        }
    }

    // Draw one card from top, one from bottom, etc.
    public void splitShuffle(int count)
    {
        LinkedList<Card> tempDeck = new LinkedList<Card>();
        List<Card> shuffledDeck = new ArrayList<Card>();
        List<Card> tempDeck2 = null;
        tempDeck.addFirst(draw());
        int i = 0;
        for (int x = 0; x < count; x++) {
            while (cards.size() > 0) {
                if (i % 2 == 0) {
                    tempDeck.addFirst(draw());
                } else {
                    tempDeck.addLast(draw());
                }
                i++;
            }
            for (int j = 0; j < tempDeck.size(); j++) {
                shuffledDeck.add(tempDeck.removeFirst());
            }
            tempDeck2 = cards;
            cards = shuffledDeck;
            shuffledDeck = tempDeck2;
            shuffledDeck.clear();
            tempDeck.clear();
        }
    }

    // Split and cut and swap.
    public void hinduShuffle()
    {
        List<Card> cut = new ArrayList<Card>();
        List<Card> cut2 = new ArrayList<Card>();
        List<Card> shuffledDeck = new ArrayList<Card>();
        int cutPoint = 0;
        int cutPoint2 = 0;
        for (int x = 0; x < SHUFFLE_COUNT; x++) {
            cutPoint = rand.nextInt( cards.size()-1 );
            cutPoint2 = rand.nextInt( cards.size() - cutPoint ) + cutPoint;
            for (int i = 0; i < cutPoint; i++) {
                cut.add(draw());
            }
            for (int i = cutPoint2; i < cards.size(); i++) {
                cut2.add(draw());
            }
            for (int i = 0; i < cut.size(); i++) {
                shuffledDeck.add(cut.remove(0));
            }
            for (int i = 0; i < cards.size(); i++) {
                shuffledDeck.add(cards.remove(0));
            }
            for (int i = 0; i < cut2.size(); i++) {
                shuffledDeck.add(cut2.remove(0));
            }
            cut = cards;
            cut.clear();
            cut2.clear();
            cards = shuffledDeck;
        }
    }

    // Sort deck into X different piles and then combine the piles.
    public void pileShuffle(int numPiles)
    {
        List<List<Card>> piles = new ArrayList<List<Card>>(numPiles);
        List<Card> shuffledDeck = new ArrayList<Card>();
        for (int x = 0; x < SHUFFLE_COUNT; x++) {
            for (int i = 0; i < numPiles; i++) {
                piles.add( new ArrayList<Card>() );
            }
            int j = 0;
            while (cards.size() > 0) {
                piles.get(j).add(draw());
                j = (j + 1) % numPiles;
            }
            for (int i = 0; i < numPiles; i++) {
                while (piles.get(i).size() > 0) {
                    shuffledDeck.add(piles.get(i).remove(0));
                }
            }
            cards = shuffledDeck;
            for (int i = 0; i < numPiles; i++) {
                piles.get(i).clear();
            }
        }
    }

    // Evaluate the distribution of cards to determine which shuffle is most random.
    public void evaluateUniformDistribution(String label) {
        // subtract one point for each card of same suit next to a card one away in rank
        // subtract two points for each run of two
        // three points for each run of three, etc.
        // add no points for two adjacent cards of same suit but not of sequential rank
        // add one point for two adjacent cards of different suit and same rank
        // add two points for two adjacent cards of different suit and sequential rank
        // add five points for two adjacent cards of different suit and different rank
        int score = 0;
        Card c = cards.get(0);
        Card d = null;
        boolean run = false;
        int runCount = 0;
        int diff = 0;
        for (int i = 1; i < cards.size(); i++) {
            d = cards.get(i);
            diff = c.getRank() - d.getRank();
            if (c.getSuit() == d.getSuit()) {
                // same suit
                if (diff == 1) { // sequential rank
                    run = true;
                    score -= (1 + (runCount*2));
                    runCount++;
                }
            } else {
                // different suit
                if (diff == 0) { // same rank
                    score++;
                } else if (diff == 1) { // sequential rank
                    score += 2;
                } else {
                    score += 5;
                }
            }
            c = d;
        }
        System.out.println(label + " score: " + score);
    }
    
    public Stream<Card> stream()
    {
    	return cards.stream();
    }
}
