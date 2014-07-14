package synthsizer;

import helpers.PropertyChanger;
import synthsizer.Chord.Type;
import synthsizer.Note.Root;

public interface View extends PropertyChanger {

	void pressKey(int i);

	void releaseKey(int i);

	public void setChordType(Type newValue);

	public void setRoot(Root newValue);

	void setSostenutoState(Keyboard.SostenutoState sostenutoState);

	public void setSustainEnabled(boolean sustain);

}
