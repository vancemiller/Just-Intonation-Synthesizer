package synthesizer;


public interface View extends PropertyChanger {
	
	public void setSustainEnabled(boolean sustain);

	void setSostenutoState(Keyboard.SostenutoState sostenutoState);

	void pressKey(int i);

	void releaseKey(int i);

}
