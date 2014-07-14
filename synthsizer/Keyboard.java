package synthsizer;

import java.util.List;

public interface Keyboard {
	enum Mode {
		SINGLE_NOTE("Single note"), CHORD("Chord");

		public final String name;

		Mode(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	enum SostenutoState {
		ONE, TWO, THREE;
	}

	Chord.Type getChordType();

	float getGain();

	Instrument getInstrument();

	List<Instrument> getInstruments();

	Keyboard.Mode getMode();

	Note getNote(int i);

	Note getNote(Note.Pitch p);

	Note[] getNotes();

	Note.Root getRoot();

	SostenutoState getSostenutoState();

	boolean isSustaining();

	void setChordType(Chord.Type type);

	void setGain(float gain);

	void setInstrument(Instrument instrument);

	void setInstruments(List<Instrument> instruments);

	void setIsSustaining(boolean isSustaining);

	void setMode(Mode mode);

	void setRoot(Note.Root r);

	void setSostenutoState(SostenutoState sostenutoState);

	void startNote(int i);

	void startNote(Note.Pitch p);

	void stopAll();

	void stopNote(int i);

	void stopNote(Note.Pitch p);

}