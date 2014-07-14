package testing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import testing.Note.Pitch;

public class Keyboard implements List<DrawableKey> {
	private final int numNotes;
	private final Pitch start;

	public Keyboard(Pitch start, int numNotes) {
		this.start = start;
		this.numNotes = numNotes;
	}

	@Override
	public int size() {
		return numNotes;
	}

	@Override
	public boolean isEmpty() {
		return numNotes == 0;
	}

	@Override
	public Iterator<DrawableKey> iterator() {
		final DrawableKey[] keys = toArray();
		return new Iterator<DrawableKey>() {
			private int current = 0;

			@Override
			public boolean hasNext() {
				return keys.length > current;
			}

			@Override
			public DrawableKey next() {
				return keys[current++];
			}

			@Override
			public void remove() {
				// not supported
			}

		};
	}

	@Override
	public DrawableKey[] toArray() {
		DrawableKey[] keys = new DrawableKey[size()];
		for (int i = 0; i < size(); i++)
			keys[i] = get(i);
		return keys;

	}

	@Override
	public DrawableKey get(int index) {
		return get(index, 0, 0, new DrawableKey(start, 0, 0));
	}

	private DrawableKey get(int index, int previousKeyNumber,
			int accumulatedWidth, DrawableKey previous) {
		if (index == previousKeyNumber) {
			return previous;
		}
		accumulatedWidth += previous.getWidth();
		return get(
				index,
				previousKeyNumber + 1,
				accumulatedWidth,
				new DrawableKey(previous.getPitch().next(), accumulatedWidth, 0));
	}

	@Override
	public ListIterator<DrawableKey> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<DrawableKey> listIterator(int index) {
		final List<DrawableKey> list = subList(0, numNotes - 1);
		final int theIndex = index;
		return new ListIterator<DrawableKey>() {
			private int current = theIndex;

			@Override
			public boolean hasNext() {
				return current + 1 < list.size();
			}

			@Override
			public DrawableKey next() {
				return list.get(++current);
			}

			@Override
			public boolean hasPrevious() {
				return current > 0;
			}

			@Override
			public DrawableKey previous() {
				return list.get(--current);
			}

			@Override
			public int nextIndex() {
				return current + 1;
			}

			@Override
			public int previousIndex() {
				return current - 1;
			}

			/** methods below this point not supported */
			@Override
			public void remove() {
				// not supported
			}

			@Override
			public void set(DrawableKey e) {
				// not supported
			}

			@Override
			public void add(DrawableKey e) {
				// not supported
			}
		};
	}

	@Override
	public List<DrawableKey> subList(int fromIndex, int toIndex) {
		List<DrawableKey> list = new ArrayList<DrawableKey>(toIndex - fromIndex);
		for (int i = fromIndex; i < toIndex; i++) {
			list.add(get(i));
		}
		return list;
	}

	/** operations below this point not supported */

	@Override
	public boolean contains(Object o) {
		// not supported
		return false;
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// not supported
		return null;
	}

	@Override
	public boolean add(DrawableKey e) {
		// Not supported
		return false;
	}

	@Override
	public boolean remove(Object o) {
		// Not supported
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// not supported
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends DrawableKey> c) {
		// not supported
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends DrawableKey> c) {
		// not supported
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// not supported
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// not supported
		return false;
	}

	@Override
	public void clear() {
		// not supported
	}

	@Override
	public DrawableKey set(int index, DrawableKey element) {
		// Not supported
		return null;
	}

	@Override
	public void add(int index, DrawableKey element) {
		// not supported

	}

	@Override
	public DrawableKey remove(int index) {
		// not supported
		return null;
	}
}