package coffeespills.jurassicpark;

import java.util.Collection;

@DinoInfo(
	eon = DinoAge.TRIASSIC,
	nature = DinoNature.SOLO,
	group = DinoGroup.FEEBLE,
	diet = DinoType.CARNIVORE,
	size = DinoSize.TINY
)
public class Herrerasaurus implements Dinosaur {

	@Override
	public boolean isSafeWith(Dinosaur d) {
		boolean safe = Dinosaur.isSafeWith(this, d);		
		if (!safe) {
			if (d instanceof Herrerasaurus) {
				System.out.println(this + Dinosaur.getNature(this).dinoSafetyMsg());
			} else {
				System.out.println(this + Dinosaur.getNature(this).dinoSafetyMsg());
			}
		}		
		return safe;
	}

	@Override
	public boolean isSafeInside(Collection<Dinosaur> enclosure) {
		boolean safe = Dinosaur.getNature(this).isSafeInside(enclosure);
		if (!safe) {
			System.out.println(this + Dinosaur.getNature(this).enclosureSafetyMsg());
		}
		return safe;
	}

	@Override
	public String toString() {
		return Dinosaur.dinoName(this);
	}
}
