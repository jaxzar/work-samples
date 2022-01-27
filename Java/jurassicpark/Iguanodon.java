package coffeespills.jurassicpark;

import java.util.Collection;

@DinoInfo(
	eon = DinoAge.CRETACEOUS,
	nature = DinoNature.FEARLESS,
	group = DinoGroup.SMART,
	diet = DinoType.HERBIVORE,
	size = DinoSize.MEDIUM
)
public class Iguanodon implements Dinosaur {

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
		boolean safe1 = Dinosaur.getGroup(this).isSafeInside(enclosure);
		if (!safe1) {
			System.out.println(this + Dinosaur.getGroup(this).enclosureSafetyMsg());
		}
		boolean safe2 = Dinosaur.getNature(this).isSafeInside(enclosure);
		if (!safe2) {
			System.out.println(this + Dinosaur.getNature(this).enclosureSafetyMsg());
		}
		return safe1 && safe2;
	}

	@Override
	public String toString() {
		return Dinosaur.dinoName(this);
	}
}
