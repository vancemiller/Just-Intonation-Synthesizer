package synthesizer;

public class InstrumentImpl implements Instrument {
	
	private String name;
	private int[] amplitudes;

	public InstrumentImpl(String name, int[] amplitudes) {
		this.name = name;
		this.amplitudes = new int[Note.NUM_OVERTONES];
		for (int i = 0; i < Note.NUM_OVERTONES && i < amplitudes.length; i++)
			this.amplitudes[i] = amplitudes[i];
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getAmplitudes() {
		return amplitudes;
	}

	public void setAmplitudes(int[] amplitudes) {
		for (int i = 0; i < Note.NUM_OVERTONES && i < amplitudes.length; i++)
			this.amplitudes[i] = amplitudes[i];
		for (int i = amplitudes.length; i < Note.NUM_OVERTONES; i++)
			this.amplitudes[i] = 0;
	}

	@Override
	public String toString() {
		return name;
	}
}
