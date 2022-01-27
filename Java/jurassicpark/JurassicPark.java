package coffeespills.jurassicpark;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class JurassicPark extends Park {
	
	List<Dinosaur> looseDinosaurs;
	
	// Choose and create your enclosures (different collection types)
	// Each specific park scenario will tell you exactly how many enclosures you can use
	Set<Dinosaur> soloDinos;
	//Set<Dinosaur> herbivoreDinos; ?
	//List<Dinosaur> packDinos; ?
	
	public JurassicPark() {
		// Choices are TRIASSIC, JURASSIC, CRETACEOUS, or ALL to combine all three for a large set of dinosaurs
		super(DinoAge.JURASSIC, 210334); // Change seed value to try a different park scenario
		
		looseDinosaurs = getDinosaurs(); // These are the dinosaurs in our park we have to process into enclosures

		soloDinos = new HashSet<Dinosaur>();
		//herbivoreDinos = new TreeSet<Dinosaur>(); // what type of collection should I use? (HashSet, TreeSet, etc)
		//packDinos = ? // what type of collection should I use? (SortedList, LinkedList, etc)
	}
	
	@Override
	protected void registerDinosaurs() {
		Iterator<Dinosaur> it = looseDinosaurs.iterator();		
		while (it.hasNext()) {
			Dinosaur d = it.next();
			
			// Now we need to check what types of dinosaurs we have
			// and decide how to place them all safely in a certain
			// number of enclosures

			Enclosure e1 = new Enclosure(soloDinos);
			// Enclosure e2 = new Enclosure(herbivoreDinos);
			
			if (d instanceof Trex) {
				// We probably should place these dangerous dinosaurs in their own enclosure
				
				register(d, e1); // Attempt to place the dinosaur inside this enclosure
			}
			// else if (d instanceof OtherDinosaur) {
			//     ...
			// }

			// Expand this as you see fit...
		}
	}

	public static void main(String[] args) {
		JurassicPark myPark = new JurassicPark();
		
		// When you're confident that your park is ready to go, uncomment the below line and run your program again. Check the output to see how you did!
		//(new Thread(myPark)).start();
	}
}
