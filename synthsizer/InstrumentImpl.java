package synthsizer;

public class InstrumentImpl implements Instrument {

	private String name;
	private final int[] amplitudes;

	public InstrumentImpl(String name, int[] amplitudes) {
		this.name = name;
		this.amplitudes = new int[Note.NUM_OVERTONES];
		for (int i = 0; i < Note.NUM_OVERTONES && i < amplitudes.length; i++) {
			this.amplitudes[i] = amplitudes[i];
		}
	}

	@Override
	public int[] getAmplitudes() {
		return amplitudes;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setAmplitudes(int[] amplitudes) {
		for (int i = 0; i < Note.NUM_OVERTONES && i < amplitudes.length; i++) {
			this.amplitudes[i] = amplitudes[i];
		}
		for (int i = amplitudes.length; i < Note.NUM_OVERTONES; i++) {
			this.amplitudes[i] = 0;
		}
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
