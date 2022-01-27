package coffeespills.jurassicpark;

import java.util.Collection;

@DinoInfo(
	eon = DinoAge.CRETACEOUS,
	nature = DinoNature.RESILIENT,
	group = DinoGroup.ARMORED,
	diet = DinoType.OMNIVORE,
	size = DinoSize.TINY
)
public class Liaoxiornis implements Dinosaur {

	@Override
	public boolean isSafeWith(Dinosaur d) {
		return true;
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
