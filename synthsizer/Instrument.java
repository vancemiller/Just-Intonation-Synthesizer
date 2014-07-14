package synthsizer;

public interface Instrument {
	String getName();

	void setName(String name);

	void setAmplitudes(int[] amplitudes);

	int[] getAmplitudes();

}
