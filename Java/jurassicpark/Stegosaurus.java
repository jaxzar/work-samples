package coffeespills.jurassicpark;

import java.util.Collection;
import java.util.Set;

@DinoInfo(
	eon = DinoAge.JURASSIC,
	nature = DinoNature.SOLO,
	group = DinoGroup.ARMORED,
	diet = DinoType.OMNIVORE,
	size = DinoSize.LARGE
)
public class Stegosaurus implements Dinosaur {

	@Override
	public boolean isSafeWith(Dinosaur d) {
		boolean safe = Dinosaur.isSafeWith(this, d);
		if (!safe) {
			System.out.println(this + Dinosaur.getNature(this).dinoSafetyMsg());
		}
		return safe;
	}

	@Override
	public boolean isSafeInside(Collection<Dinosaur> enclosure) {
		boolean safe = (enclosure instanceof Set);
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
