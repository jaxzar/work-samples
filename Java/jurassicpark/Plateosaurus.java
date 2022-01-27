package coffeespills.jurassicpark;

import java.util.Collection;

@DinoInfo(
	eon = DinoAge.TRIASSIC,
	nature = DinoNature.IMPATIENT,
	group = DinoGroup.FEEBLE,
	diet = DinoType.HERBIVORE,
	size = DinoSize.ENORMOUS
)
public class Plateosaurus implements Dinosaur {

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
