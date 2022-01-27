public class Card {

	private int rank, suit;

	private static String[] suits = { "H", "S", "D", "C" };//heart,spade,diamond,club
	private static String[] ranks  = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };


	Card(int suit, int rank)
	{
		this.rank=rank;
		this.suit=suit;
	}
	@Override
	public String toString()
	{
		  return ranks[rank]+" of "+ suits[suit];
	} 

	public int getRank() {
		 return rank;
	}

	public int getSuit() {
		return suit;
	}


}
