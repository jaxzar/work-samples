package coffeespills.jurassicpark;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Park implements Runnable {
	
	private Random rand;
	private DinosaurHatchery hatchery;
	private List<Dinosaur> dinosaurs;
	private List<Enclosure> enclosures;
	private int numDinosaurs = 0;
	private int numEnclosures = 0;
	private int dinosaursRegistered = 0;
	private int enclosuresRegistered = 0;
	private DinoAge eon;
	private boolean allowRegistration;

	/**
	 * Set up the park by generating the set of animals it will have to house.
	 * @param exhibits The list of dinosaurs.
	 * @param seed This can be used to re-attempt the same park and the same set of dinosaurs more than once.
	 */
	public Park(DinoAge age, long seed) {
		allowRegistration = false;
		rand = new Random(seed);
		eon = age;
		enclosures = new ArrayList<Enclosure>();
		hatchery = new DinosaurHatchery(eon, seed);
		dinosaurs = hatchery.hatchMany(rand.nextInt(7) + 7);
		numDinosaurs = dinosaurs.size();
		numEnclosures = rand.nextInt(3) + 4;
		System.out.println("Welcome to the dinosaur park business. Your new park is ready to begin construction.");
		System.out.println("Today " + numDinosaurs + " dinosaurs have arrived and need placed into " + numEnclosures + " different enclosures.");
		System.out.println("The dinosaurs are: " + dinosaurs);
	}
	
	public List<Dinosaur> getDinosaurs() {
		return dinosaurs;
	}
	
	public void register(Dinosaur d, Enclosure housing) {
		if (allowRegistration) {
			if (housing != null) {
				if (!enclosures.contains(housing)) {
					enclosures.add(housing);
					enclosuresRegistered++;
				}
				if (d != null) {
					if (!housing.contains(d)) {
						// check other housings for this same dinosaur
						Iterator<Enclosure> it = enclosures.iterator();
						while (it.hasNext()) {
							Enclosure e = it.next();
							if (e.contains(d)) {
								System.out.println("Unable to register " + d + ": this dinosaur is already registered in another enclosure.");
							}
						}
						if (housing.add(d)) {
							System.out.println(d + " successfully registered in " + housing + ". The ferocious creature seems relatively happy in its new abode.");
							dinosaursRegistered++;
						} else {
							System.out.println("Oh no! Something bad happened. The dinosaur did not get along with other dinosaurs in its enclosure, or it was not happy with the enclosure itself.");
						}
					} else {
						System.out.println("Unable to register " + d + ": this dinosaur has already been registered in this enclosure.");
					}
				}
			}
		} else {
			System.out.println("Unable to register dinosaurs during park initialization. You must register dinosaurs inside the registerDinosaurs() function.");
		}
	}
	
	public boolean isParkComplete() {
		return (numDinosaurs == dinosaursRegistered && numEnclosures == enclosuresRegistered);
	}
	
	protected void registerDinosaurs() {
		// This will be overridden by subclasses
		return;
	}

	@Override
	public void run() {
		
		allowRegistration = true;
		
		this.registerDinosaurs();
		
		if (isParkComplete()) {
			System.out.println("Congratulations! You have set up a successful " + eon.getClass().getName() + " park.");
		} else {
			System.out.println("Whoops! One or more dinosaurs are loose in your park, or at least one enclosure is empty. Try again.");
		}
		
		allowRegistration = false;
	}
}
