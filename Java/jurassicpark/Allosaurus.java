package coffeespills.jurassicpark;

import java.util.Collection;
import java.util.Set;

@DinoInfo(
	eon = DinoAge.JURASSIC,
	nature = DinoNature.PACK,
	group = DinoGroup.FEEBLE,
	diet = DinoType.OMNIVORE,
	size = DinoSize.LARGE
)
public class Allosaurus implements Dinosaur {

	@Override
	public boolean isSafeWith(Dinosaur d) {
		boolean safe = Dinosaur.isSafeWith(this, d);
		if (!safe) {
			System.out.println(this + Dinosaur.getGroup(this).dinoSafetyMsg());
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
