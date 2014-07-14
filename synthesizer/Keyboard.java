package synthesizer;

import java.util.List;

public interface Keyboard {
	enum SostenutoState {
		ONE, TWO, THREE;
	}

	enum Mode {
		SINGLE_NOTE("Single note"), CHORD("Chord");

		Mode(String name) {
			this.name = name;
		}

		public final String name;

		@Override
		public String toString() {
			return name;
		}
	}

	Note getNote(Note.Pitch p);

	Note getNote(int i);

	void startNote(Note.Pitch p);

	void startNote(int i);

	void stopNote(Note.Pitch p);

	void stopNote(int i);

	Note[] getNotes();

	void stopAll();

	List<Instrument> getInstruments();

	void setInstruments(List<Instrument> instruments);

	Instrument getInstrument();

	void setInstrument(Instrument instrument);

	void setGain(float gain);

	float getGain();

	void setRoot(Note.Root r);

	Note.Root getRoot();

	void setMode(Mode mode);

	Keyboard.Mode getMode();

	void setIsSustaining(boolean isSustaining);

	boolean isSustaining();

	void setSostenutoState(SostenutoState sostenutoState);

	SostenutoState getSostenutoState();

}