package testing;

public class UntunedNote implements Note {
	private NoteImpl n;

	public UntunedNote(Pitch pitch) {
		this.n = new NoteImpl(pitch, Interval.UNISON);
	}

	public Note tuneNote(Pitch root) {
		Pitch p = n.getRoot();
		Interval i = Interval.UNISON;
		for (int x = 0; x < p.ordinal() - root.ordinal(); x++)
			i = i.next();
		return new NoteImpl(root, i);
	}

	@Override
	public void start() {
		n.start();
	}

	@Override
	public void stop() {
		n.stop();
	}

	@Override
	public Pitch getPitch() {
		return n.getPitch();
	}
	@Override
	public String toString() {
		return n.toString();
	}
}
