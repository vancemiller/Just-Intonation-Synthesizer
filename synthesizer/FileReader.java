package synthesizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class FileReader {
	private static String fileName = "instruments.txt";

	public static String getFileName() {
		return fileName;
	}
	
	public static Vector<Instrument> getInstruments()
			throws FileNotFoundException {
		Vector<Instrument> instruments = new Vector<Instrument>();
		Scanner sc = new Scanner(new File(FileReader.fileName));
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			String[] split = line.split(", ");
			if (split[0].substring(0, 12).contains("instrument: ")) {
				int[] amplitudes = new int[split.length - 1];
				for (int i = 1; i < split.length-1; i++)
					amplitudes[i-1] = Integer.parseInt(split[i]);
				instruments.add(new InstrumentImpl(split[0].substring(12),
						amplitudes));
			}
		}
		sc.close();
		return instruments;
	}
}
