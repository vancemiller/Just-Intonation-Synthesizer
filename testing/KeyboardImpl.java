package testing;

import java.util.Arrays;

import testing.Note.Pitch;

public class KeyboardImpl implements Keyboard {
	private final Note[] notes;

	public KeyboardImpl() {
		Pitch[] pitches = Note.Pitch.values();
		this.notes = new Note[pitches.length];
		for (int i = 0; i < pitches.length; i++) {
			notes[i] = new UntunedNote(pitches[i]);
		}
	}

	@Override
	public Note getNote(Pitch p) {
		return notes[p.ordinal()];
	}

	@Override
	public Note getNote(int i) {
		return notes[i];
	}

	@Override
	public void startNote(Pitch p) {
		notes[p.ordinal()].start();
	}

	@Override
	public void startNote(int i) {
		notes[i].start();
	}

	@Override
	public void stopNote(Pitch p) {
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
}
