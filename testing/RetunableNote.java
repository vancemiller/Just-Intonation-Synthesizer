package testing;

public class RetunableNote implements Note {
	private Note.Pitch pitch;
	private NoteImpl n;

	public RetunableNote(Pitch pitch) {
		this.pitch = pitch;
		this.n = new NoteImpl(pitch, Interval.UNISON);
	}

	public void tuneNote(Key k) {
		Interval i = Interval.UNISON;
		if (k == Note.Key.EQUAL_TEMPERMENT)
			n = new NoteImpl(pitch, i);
		else {
			int halfStepsAway = (pitch.ordinal() - (k.ordinal() - 1)) % 12;
			for(int x = 0; x < halfStepsAway; x++) {
				i = i.next();
			}
			n = new NoteImpl(Note.Pitch.values()[pitch.ordinal()-halfStepsAway],i);
		}
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
