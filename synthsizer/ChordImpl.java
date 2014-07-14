package synthsizer;

public class ChordImpl implements Chord {
	int[] notes;

	public ChordImpl(int[] notes) {
		this.notes = notes.clone();
	}

	@Override
	public int[] getNotes() {
		return notes;
	}

}
