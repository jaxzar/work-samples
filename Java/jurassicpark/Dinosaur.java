package coffeespills.jurassicpark;

import java.util.Collection;

public interface Dinosaur {
	
	public boolean isSafeWith(Dinosaur d);
	
	public boolean isSafeInside(Collection<Dinosaur> enclosure);
	
	public static DinoInfo getInfo(Dinosaur d) {
		return d.getClass().getAnnotation(DinoInfo.class);
	}
	
	public static DinoNature getNature(Dinosaur d) {
		return d.getClass().getAnnotation(DinoInfo.class).nature();
	}
	
	public static DinoGroup getGroup(Dinosaur d) {
		return d.getClass().getAnnotation(DinoInfo.class).group();
	}
	
	public static String dinoName(Dinosaur d) {
		return d.getClass().getSimpleName();
	}
	
	public static boolean isSafeWith(Dinosaur d1, Dinosaur d2) {
		boolean safe1 = false;
		boolean safe2 = false;
		DinoInfo di1 = Dinosaur.getInfo(d1);
		DinoInfo di2 = Dinosaur.getInfo(d2);
		if (d2.getClass() == d1.getClass() && di1.nature() != DinoNature.SOLO) {
			return true;
		}
		switch (di1.nature()) {
			case SOLO:
				safe1 = (d1.getClass() != d2.getClass());
				break;
			case AGGRESSIVE:
				safe1 = (di2.size().compareTo(di1.size()) >= 0);
				break;
			case FEARLESS:
				safe1 = (di2.size().compareTo(di1.size()) <= 0);
				break;
			case RAVENOUS:
				safe1 = (di2.diet() == DinoType.CARNIVORE);
				break;
			default:
				safe1 = true;
		}
		switch (di1.group()) {
			case FIERCE:
				safe2 = (di2.group() == DinoGroup.FIERCE || di2.group() == DinoGroup.ARMORED);
				break;
			case FEEBLE:
				safe2 = (di2.diet() == DinoType.HERBIVORE);
				break;
			case DANGEROUS:
				safe2 = (d1.getClass() == d2.getClass());
				break;
			default:
				safe2 = true;
		}
		return safe1 && safe2;
	}
}
