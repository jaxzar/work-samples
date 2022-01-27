package coffeespills.jurassicpark;

import java.util.Collection;

@DinoInfo(
	eon = DinoAge.JURASSIC,
	nature = DinoNature.RAVENOUS,
	group = DinoGroup.TALL,
	diet = DinoType.HERBIVORE,
	size = DinoSize.LEVIATHAN
)
public class Diplodocus implements Dinosaur {

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
		boolean safe = Dinosaur.getGroup(this).isSafeInside(enclosure);
		if (!safe) {
			System.out.println(this + Dinosaur.getGroup(this).enclosureSafetyMsg());
		}
		return safe;
	}

	@Override
	public String toString() {
		return Dinosaur.dinoName(this);
	}
}
