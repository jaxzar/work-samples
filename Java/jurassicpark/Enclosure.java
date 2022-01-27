package coffeespills.jurassicpark;

import java.util.Collection;
import java.util.Iterator;

public class Enclosure implements Collection<Dinosaur> {
	
	public Collection<Dinosaur> enclosure;

	/**
	 * Creates a new enclosure and captures the specific collection type used to validate
	 * against the dinosaur types stored inside it. The unchecked suppression is necessary
	 * because otherwise the type cannot be captured, and the parameter type ensures that
	 * the unchecked cast will always succeed.
	 * @param c The collection chosen by the client.
	 */
	public Enclosure(Collection<Dinosaur> c) {
		enclosure = c;
	}

	@Override
	public boolean add(Dinosaur e) {
		boolean result = true;
		if (e.isSafeInside(enclosure)) {
			Iterator<Dinosaur> it = enclosure.iterator();
			while (it.hasNext()) {
				Dinosaur d = it.next();
				if (d.isSafeWith(e)) {
					enclosure.add(e);
				} else {
					result = false;
				}
			}
		} else {
			result = false;
		}
		
		return result;
	}

	@Override
	public boolean addAll(Collection<? extends Dinosaur> c) {
		boolean result = true;
		Iterator<? extends Dinosaur> it = c.iterator();
		while (it.hasNext()) {
			Dinosaur d = it.next();
			result &= this.add(d);
		}
		
		return result;
	}

	@Override
	public void clear() {
		enclosure.clear();
	}

	@Override
	public boolean contains(Object o) {
		return enclosure.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return enclosure.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return enclosure.isEmpty();
	}

	@Override
	public Iterator<Dinosaur> iterator() {
		return enclosure.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return enclosure.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return enclosure.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return enclosure.retainAll(c);
	}

	@Override
	public int size() {
		return enclosure.size();
	}

	@Override
	public Object[] toArray() {
		return enclosure.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return enclosure.toArray(a);
	}
	
	@Override
	public String toString() {
		return enclosure.getClass().getName();
	}
}
