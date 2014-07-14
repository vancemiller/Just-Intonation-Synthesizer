package testing;

import java.util.Arrays;

public class KeyboardImpl implements Keyboard {
	private final RetunableNote[] notes;

	public KeyboardImpl() {
		Note.Pitch[] pitches = Note.Pitch.values();
		this.notes = new RetunableNote[pitches.length];
		for (int i = 0; i < pitches.length; i++) {
			notes[i] = new RetunableNote(pitches[i]);
		}
	}

	@Override
	public Note getNote(Note.Pitch p) {
		return notes[p.ordinal()];
	}

	@Override
	public Note getNote(int i) {
		return notes[i];
	}

	@Override
	public void startNote(Note.Pitch p) {
		notes[p.ordinal()].start();
	}

	@Override
	public void startNote(int i) {
		notes[i].start();
	}

	@Override
	public void stopNote(Note.Pitch p) {
		notes[p.ordinal()].stop();
	}

	@Override
	public void stopNote(int i) {
		notes[i].stop();
	}

	@Override
	public Note[] getNotes() {
		return Arrays.copyOf(notes, notes.length);
	}

	@Override
	public void setKey(Note.Key k) {
		for (RetunableNote n : notes) {
			n.tuneNote(k);
		}
	}
}
