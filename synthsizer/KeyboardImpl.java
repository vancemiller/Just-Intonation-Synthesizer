package synthsizer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class KeyboardImpl implements Model {
	private final PropertyChangeSupport changes;
	private final Note[] notes;
	private boolean isSustaining;
	private Keyboard.SostenutoState sostenutoState;
	private final List<Note> sustainedNotes, sostenutoedNotes;
	private List<Instrument> instruments;
	private Note.Root r;
	private Instrument instrument;
	private float gain;
	private Keyboard.Mode mode;
	private Chord.Type chordType;
	private final HashMap<Integer, Chord> playingChords;

	public KeyboardImpl(Instrument instrument, List<Instrument> instruments,
			Note.Root root, float gain, Keyboard.Mode mode) {
		// Property changes
		changes = new PropertyChangeSupport(this);
		// initialize the sustained notes list; nothing is sustained now
		sustainedNotes = new ArrayList<Note>();
		isSustaining = false;
		// same for sostenuto
		sostenutoedNotes = new ArrayList<Note>();
		sostenutoState = Keyboard.SostenutoState.ONE;
		// same for chord type
		chordType = Chord.Type.NO_CHORD;
		playingChords = new HashMap<Integer, Chord>(11);
		// build the notes array
		Note.Pitch[] pitches = Note.Pitch.values();
		notes = new Note[pitches.length];
		for (int i = 0; i < pitches.length; i++) {
			notes[i] = new NoteImpl(pitches[i]);
		}
		// set the instrument
		this.instrument = instrument;
		// set the list of instruments
		this.instruments = instruments.subList(0, instruments.size());
		// set the root (for just intonation)
		r = root;
		// set the gain
		this.gain = gain;
		// set the mode
		this.mode = mode;
		// update all keys
		updateKeys();
	}

	public KeyboardImpl(List<Instrument> instruments) {
		this(instruments.get(0), instruments, Note.Root.EQUAL_TEMPERMENT, 0f,
				Keyboard.Mode.SINGLE_NOTE);
	}

	/** Property Change support */

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	@Override
	public Chord.Type getChordType() {
		return chordType;
	}

	@Override
	public float getGain() {
		return gain;
	}

	@Override
	public Instrument getInstrument() {
		return instrument;
	}

	@Override
	public List<Instrument> getInstruments() {
		return instruments;
	}

	@Override
	public Keyboard.Mode getMode() {
		return mode;
	}

	@Override
	public Note getNote(int i) {
		return notes[i];
	}

	@Override
	public Note getNote(Note.Pitch p) {
		return getNote(p.ordinal());
	}

	@Override
	public Note[] getNotes() {
		return Arrays.copyOf(notes, notes.length);
	}

	@Override
	public Note.Root getRoot() {
		return r;
	}

	@Override
	public Keyboard.SostenutoState getSostenutoState() {
		return sostenutoState;
	}

	@Override
	public boolean isSustaining() {
		return isSustaining;
	}

	private void noteStarter(int i) {
		if (isSustaining) {
			sustainedNotes.add(getNote(i));
		}
		if (sostenutoState.equals(Keyboard.SostenutoState.TWO)) {
			sostenutoedNotes.add(getNote(i));
		}
		notes[i].start();
		changes.firePropertyChange("start", null, i);
	}

	private void noteStopper(int i) {
		notes[i].stop();
		changes.firePropertyChange("stop", null, i);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

	@Override
	public void setChordType(Chord.Type type) {
		changes.firePropertyChange("chordType", chordType, type);
		chordType = type;
	}

	@Override
	public void setGain(float gain) {
		float old = this.gain;
		this.gain = gain;
		updateKeys();
		changes.firePropertyChange("gain", old, gain);
	}

	@Override
	public void setInstrument(Instrument instrument) {
		Instrument old = this.instrument;
		this.instrument = instrument;
		updateKeys();
		changes.firePropertyChange("instrument", old, instrument);
	}

	@Override
	public void setInstruments(List<Instrument> instruments) {
		changes.firePropertyChange("instruments", this.instruments, instruments);
		this.instruments = instruments;
	}

	@Override
	public void setIsSustaining(boolean isSustaining) {
		if (!isSustaining) {
			for (Note n : sustainedNotes) {
				stopNote(n.getPitch());
			}
			sustainedNotes.removeAll(sustainedNotes);
		}
		changes.firePropertyChange("sustain", this.isSustaining, isSustaining);
		this.isSustaining = isSustaining;
	}

	@Override
	public void setMode(Mode mode) {
		changes.firePropertyChange("mode", this.mode, mode);
		this.mode = mode;
	}

	@Override
	public void setRoot(Note.Root r) {
		Note.Root old = this.r;
		this.r = r;
		updateKeys();
		changes.firePropertyChange("root", old, r);
	}

	@Override
	public void setSostenutoState(Keyboard.SostenutoState sostenutoState) {
		if (sostenutoState.equals(Keyboard.SostenutoState.ONE)) {
			if (!isSustaining) {
				for (Note n : sostenutoedNotes) {
					stopNote(n.getPitch());
				}
			} else {
				sustainedNotes.addAll(sostenutoedNotes);
			}
			sostenutoedNotes.removeAll(sostenutoedNotes);
		}
		changes.firePropertyChange("sostenuto", this.sostenutoState,
				sostenutoState);
		this.sostenutoState = sostenutoState;
	}

	@Override
	public void startNote(int i) {
		if (!chordType.equals(Chord.Type.NO_CHORD)) {
			setRoot(Note.Root.values()[1 + i % 12]);
		} else {
			setRoot(Note.Root.EQUAL_TEMPERMENT);
		}

		for (int j : chordType.halfStepsFromRoot) {
			try {
				noteStarter(i + j);
			} catch (ArrayIndexOutOfBoundsException e) {

			}
		}
		playingChords.put(i, new ChordImpl(chordType.halfStepsFromRoot));
	}

	/** Playing support */

	@Override
	public void startNote(Note.Pitch p) {
		startNote(p.ordinal());
	}

	@Override
	public void stopAll() {
		while (!sustainedNotes.isEmpty()) {
			stopNote(sustainedNotes.get(0).getPitch());
		}
	}

	@Override
	public void stopNote(int i) {
		Chord chord = playingChords.remove(i);
		int[] notes;
		if (chord == null) {
			notes = new int[] { i };
		} else {
			notes = chord.getNotes();
		}
		for (int j : notes) {
			try {
				noteStopper(i + j);
			} catch (ArrayIndexOutOfBoundsException e) {

			}
		}
	}

	@Override
	public void stopNote(Note.Pitch p) {
		stopNote(p.ordinal());
	}

	@Override
	public String toString() {
		return "Keyboard Model";
	}

	private void updateKeys() {
		for (Note n : notes) {
			n.update(this);
		}
	}
}
