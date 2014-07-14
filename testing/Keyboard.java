package testing;

public interface Keyboard {
	Note getNote(Note.Pitch p);
	
	Note getNote(int i);

	void startNote(Note.Pitch p);
	
	void startNote(int i);

	void stopNote(Note.Pitch p);
	
	void stopNote(int i);

	Note[] getNotes();
}