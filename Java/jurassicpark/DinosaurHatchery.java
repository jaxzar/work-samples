package coffeespills.jurassicpark;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DinosaurHatchery {
	
	private Random rand;
	private DinoAge eon;
	private List<Dinosaur> allDinos;
	private List<Dinosaur> jurassic;
	private List<Dinosaur> triassic;
	private List<Dinosaur> cretaceous;
	
	public DinosaurHatchery(long seed) {
		eon = null;
		init(seed);
	}
	
	public DinosaurHatchery(DinoAge age, long seed) {
		eon = age;
		init(seed);
	}
	
	private void init(long seed) {
		rand = new Random(seed);
		allDinos = new ArrayList<Dinosaur>();
		
		// PLEASE NOTE: The below dinosaurs and their attributes may not be 100% historically accurate.
		// Nevertheless, great care has been taken to ensure that they are mostly in line with their
		// actual, ancient counterparts.

		// TRIASSIC
		allDinos.add(new Coelophysis());
		allDinos.add(new Eoraptor());
		allDinos.add(new Herrerasaurus());
		allDinos.add(new Plateosaurus());
		allDinos.add(new Pteranodon());
		
		// JURASSIC
		allDinos.add(new Brontosaurus());
		allDinos.add(new Stegosaurus());
		allDinos.add(new Trex());
		allDinos.add(new Allosaurus());
		allDinos.add(new Archaeopteryx());
		allDinos.add(new Brachiosaurus());
		allDinos.add(new Diplodocus());
		
		// CRETACEOUS
		allDinos.add(new Pterodactyl());
		allDinos.add(new Ankylosaurus());
		allDinos.add(new Velociraptor());
		allDinos.add(new Iguanodon());
		allDinos.add(new Liaoxiornis());
		allDinos.add(new Triceratops());
		
		triassic = new ArrayList<Dinosaur>();
		jurassic = new ArrayList<Dinosaur>();
		cretaceous = new ArrayList<Dinosaur>();
		
		int dinos = allDinos.size();
		
		for (int i = 0; i < dinos; i++) {
			Dinosaur d = allDinos.get(i);
			DinoInfo di = Dinosaur.getInfo(d);
			switch (di.eon()) {
				case TRIASSIC:
					triassic.add(d);
					break;
				case JURASSIC:
					jurassic.add(d);
					break;
				case CRETACEOUS:
					cretaceous.add(d);
					break;
				default:
			}
		}
	}

	public Dinosaur hatchNew(DinoAge age) {
		Dinosaur newDino = null;
		Dinosaur d = null;
		switch (age) {
			case TRIASSIC:
				d = triassic.get(rand.nextInt(triassic.size()));
				break;
			case JURASSIC:
				d = jurassic.get(rand.nextInt(jurassic.size()));
				break;
			case CRETACEOUS:
				d = cretaceous.get(rand.nextInt(cretaceous.size()));
				break;
			default:
				d = allDinos.get(rand.nextInt(allDinos.size()));
		}
		
		try {
			if (d != null) {
				newDino = d.getClass().newInstance();
			}
		} catch (InstantiationException e) {
			System.out.println("DinosaurHatchery was unable to hatch (instantiate) a new Dinosaur: " + e.getMessage());
		} catch (IllegalAccessException e) {
			System.out.println("DinosaurHatchery was unable to access constructor for Dinosaur: " + e.getMessage());
		}
		
		return newDino;
	}
	
	public Dinosaur hatchNew() {
		return hatchNew(eon);
	}
	
	public List<Dinosaur> hatchMany(int numDinos, DinoAge age) {
		List<Dinosaur> babies = new ArrayList<Dinosaur>();
		for (int i = 0; i < numDinos; i++) {
			babies.add(hatchNew(age));
		}
		return babies;
	}
	
	public List<Dinosaur> hatchMany(int numDinos) {
		return hatchMany(numDinos, eon);
	}
}
