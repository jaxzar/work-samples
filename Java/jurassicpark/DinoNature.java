package coffeespills.jurassicpark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public enum DinoNature {

	IMPATIENT, PACK, SOLO, DOCILE, AGGRESSIVE, FEARLESS, RAVENOUS, RESILIENT;
	
	public boolean isSafeInside(Collection<Dinosaur> enclosure) {
		boolean safe = false;
		switch (this) {
			case IMPATIENT:
				safe = !(enclosure instanceof Stack || enclosure instanceof Queue);
				break;
			case PACK:
				safe = !(enclosure instanceof Set);
				break;
			case SOLO:
				safe = (enclosure instanceof Set);
				break;
			case AGGRESSIVE:
			case FEARLESS:
				safe = !(enclosure instanceof TreeSet);
				break;
			case RESILIENT:
				safe = (enclosure instanceof Queue || enclosure instanceof Deque);
				break;
			case RAVENOUS:
				safe = (enclosure instanceof ArrayList || enclosure instanceof HashSet);
				break;
			default:
				safe = true;
		}
		return safe;
	}
	
	public String dinoSafetyMsg() {
		String result = "";
		switch (this) {
			case SOLO:
				result = " is a solo dinosaur. It does not do well with others of its own kind in the same enclosure.";
				break;
			case DOCILE:
				result = " is docile and probably not safe around fierce dinosaurs. Make sure no fierce dinosaurs are in the same enclosure.";
				break;
			case AGGRESSIVE:
				result = " is aggressive and will attack all dinosaurs smaller than it. Don't put one in the same enclosure as a smaller dinosaur.";
				break;
			case FEARLESS:
				result = " is fearless and will attack dinosaurs larger than itself. It is only safe with dinosaurs its size or smaller.";
				break;
			case RAVENOUS:
				result = " is ravenous and will devour all available plant food and starve other herbivores or omnivores. It is only safe with its own kind or other carnivores.";
				break;
			default:
				result = "";
		}
		return result;
	}
	
	public String enclosureSafetyMsg() {
		String result = "";
		switch (this) {
			case IMPATIENT:
				result = " is impatient. It does not respond well to walking single file or waiting in line. Try an enclosure with multiple entrances and exits.";
				break;
			case PACK:
				result = " is a pack animal and wants to be near others of its kind. It should therefore be inside an enclosure that allows duplicates.";
				break;
			case SOLO:
				result = " is a loner and will fight with others of its own kind. Try another enclosure that does not allow duplicates.";
				break;
			case AGGRESSIVE:
				result = " is aggressive and will attack any dinosaurs smaller than itself. In a sorted enclosure it will always end up near a smaller dinosaur and attack it.";
				break;
			case FEARLESS:
				result = " is fearless and will attack any dinosaurs larger than itself. In a sorted enclosure it will always end up near a larger dinosaur and attack it.";
				break;
			case RAVENOUS:
				result = " is ravenous and as a carnivore this makes it dangerous. It needs to be housed in a structure that allows it to be quickly located.";
				break;
			case RESILIENT:
				result = " is resilient and likes to roam vast distances. It needs a long enclosure that has one entrance or exit at each end.";
				break;
			default:
				result = "";
		}
		return result;
	}
}
