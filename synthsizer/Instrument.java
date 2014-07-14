package synthsizer;

public interface Instrument {
	int[] getAmplitudes();

	String getName();

	void setAmplitudes(int[] amplitudes);

	void setName(String name);

}
