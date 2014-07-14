package synthesizer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import synthesizer.Note.Key;

public class KeyboardImpl implements Keyboard {
	private final PropertyChangeSupport changes;
	private Key k;
	private final Note[] notes;
	private final List<Note> playingNotes;

	public KeyboardImpl() {
		changes = new PropertyChangeSupport(this);
		playingNotes = new ArrayList<Note>();
		k = Note.Key.EQUAL_TEMPERMENT;
		Note.Pitch[] pitches = Note.Pitch.values();
		notes = new Note[pitches.length];
		for (int i = 0; i < pitches.length; i++) {
			notes[i] = new NoteImpl(pitches[i]);
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
		playingNotes.add(notes[p.ordinal()]);
		notes[p.ordinal()].start();
		changes.firePropertyChange("NoteStarted", notes[p.ordinal()], null);
	}

	@Override
	public void startNote(int i) {
		playingNotes.add(notes[i]);
		notes[i].start();
		changes.firePropertyChange("NoteStarted", notes[i], null);
	}

	@Override
	public void stopNote(Note.Pitch p) {
		playingNotes.remove(notes[p.ordinal()]);
		notes[p.ordinal()].stop();
		changes.firePropertyChange("NoteStopped", notes[p.ordinal()], null);
	}

	@Override
	public void stopNote(int i) {
		playingNotes.remove(notes[i]);
		notes[i].stop();
		changes.firePropertyChange("NoteStopped", notes[i], null);
	}

	@Override
	public Note[] getNotes() {
		return Arrays.copyOf(notes, notes.length);
	}

	@Override
	public void setKey(Key k) {
		changes.firePropertyChange("KeyChanged", this.k, k);
		this.k = k;
		for (Note n : notes) {
			n.setKey(k);
		}

	}

	@Override
	public void stopAll() {
		while (!playingNotes.isEmpty()) {
			stopNote(playingNotes.get(0).getPitch());
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

	@Override
	public void setInstrument(String instrument) {
		for (Note n : notes) {
			n.setInstrument(instrument);
		}
	}
}
