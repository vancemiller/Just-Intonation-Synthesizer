package synthsizer;

import synthsizer.Chord.Type;
import synthsizer.Note.Root;
import helpers.PropertyChanger;

public interface View extends PropertyChanger {
	
	public void setSustainEnabled(boolean sustain);

	void setSostenutoState(Keyboard.SostenutoState sostenutoState);

	void pressKey(int i);

	void releaseKey(int i);

	public void setRoot(Root newValue);

	public void setChordType(Type newValue);

}
