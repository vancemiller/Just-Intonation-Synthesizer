package synthesizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class FileReader {

	public static void loadAmplitudes(String instrument, int[] amplitudes) {
		Scanner sc;
		try {
			sc = new Scanner(new File("instruments.txt"));
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (line.contains(instrument)) {
					String[] split = line.substring(instrument.length() + 2)
							.split(", ");
					for (int i = 0; i < amplitudes.length && i < split.length; i++) {
						amplitudes[i] = Integer.parseInt(split[i]);
					}
					break;
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Vector<String> getInstruments() {
		Vector<String> instruments = new Vector<String>();
		Scanner sc;
		try {
			sc = new Scanner(new File("instruments.txt"));
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] split = line.split(", ");
				instruments.add(split[0]);
			}
			sc.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instruments;
	}
}
