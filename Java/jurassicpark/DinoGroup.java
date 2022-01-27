package coffeespills.jurassicpark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

public enum DinoGroup {
	
	FLYING,	FIERCE,	FEEBLE,	ARMORED, TALL, SMART, DANGEROUS;
	
	public boolean isSafeInside(Collection<Dinosaur> enclosure) {
		boolean safe = true;
		switch (this) {
			case FLYING:
				safe = (enclosure instanceof Deque);
				break;
			case TALL:
				safe = (enclosure instanceof TreeSet || enclosure instanceof LinkedList);
				break;
			case SMART:
				safe = (enclosure instanceof HashSet || enclosure instanceof ArrayList || enclosure instanceof LinkedList);
				break;
			default:
				safe = true;
		}
		return safe;
	}
	
	public String dinoSafetyMsg() {
		String result = "";
		switch (this) {
			case FIERCE:
				result = " is fierce and will actively attack other dinosaurs that are not also fierce, and lack protective armor.";
				break;
			case FEEBLE:
				result = " is feeble and unable to defend itself from omnivores or carnivores. It is only safe with herbivores or others of its own kind.";
				break;
			case DANGEROUS:
				result = " does not get along well with other dinosaurs. It is barely tolerant of its own kind.";
				break;
			default:
				result = "";
		}
		return result;
	}
	
	public String enclosureSafetyMsg() {
		String result = "";
		switch (this) {
			case FLYING:
				result = " as winged dinosaurs require an enclosure that allows them easy access from any direction (both entrance and exit).";
				break;
			case TALL:
				result = " is tall and even baby dinos can grow rapidly in size. Its environment should allow for quick resizing.";
				break;
			case SMART:
				result = " is extremely intelligent and will eventually escape unless its enclosure allows it to be quickly inserted.";
				break;
			default:
				result = "";
		}
		return result;
	}
}
