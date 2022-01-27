package coffeespills.jurassicpark;

import java.util.Collection;

@DinoInfo(
	eon = DinoAge.TRIASSIC,
	nature = DinoNature.IMPATIENT,
	group = DinoGroup.SMART,
	diet = DinoType.CARNIVORE,
	size = DinoSize.TINY
)
public class Eoraptor implements Dinosaur {

	@Override
	public boolean isSafeWith(Dinosaur d) {
		return true;
	}

	@Override
	public boolean isSafeInside(Collection<Dinosaur> enclosure) {
		boolean safe1 = Dinosaur.getNature(this).isSafeInside(enclosure);
		if (!safe1) {
			System.out.println(this + Dinosaur.getNature(this).enclosureSafetyMsg());
		}
		boolean safe2 = Dinosaur.getGroup(this).isSafeInside(enclosure);
		if (!safe2) {
			System.out.println(this + Dinosaur.getGroup(this).enclosureSafetyMsg());
		}
		return safe1 && safe2;
	}

	@Override
	public String toString() {
		return Dinosaur.dinoName(this);
	}
}
