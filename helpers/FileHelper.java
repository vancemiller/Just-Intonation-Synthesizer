package helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import synthsizer.Instrument;
import synthsizer.InstrumentImpl;

public class FileHelper {
	private static String fileName = "instruments.txt";

	public static String getFileName() {
		return fileName;
	}

	public static Vector<Instrument> getInstruments()
			throws FileNotFoundException {
		Vector<Instrument> instruments = new Vector<Instrument>();
		Scanner sc = new Scanner(new File(FileHelper.fileName));
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			String[] split = line.split(", ");
			if (split[0].substring(0, 12).contains("instrument: ")) {
				int[] amplitudes = new int[split.length - 1];
				for (int i = 1; i < split.length - 1; i++)
					amplitudes[i - 1] = Integer.parseInt(split[i]);
				instruments.add(new InstrumentImpl(split[0].substring(12),
						amplitudes));
			}
		}
		sc.close();
		return instruments;
	}

	public static void writeNewInstrument(Instrument instrument)
			throws IOException {
		FileWriter fw = new FileWriter(new File(FileHelper.fileName), true);
		fw.append("instrument: " + instrument.getName());
		for (int i : instrument.getAmplitudes()) {
			fw.append(", " + i);
		}
		fw.append('\n');
		fw.close();
	}

	public static void remove(Instrument instrument) throws IOException {
		File tempFile = new File("tmpfile.txt");
		File file = new File(FileHelper.fileName);

		BufferedReader reader = new BufferedReader(new FileReader(file));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

		String currentLine;
		while ((currentLine = reader.readLine()) != null) {
			if (!currentLine.contains("instrument: " + instrument.getName())) {
				writer.write(currentLine + "\n");
			}
		}
		reader.close();
		writer.close();
		file.delete();
		tempFile.renameTo(file);
	}

	public static void removeLine(int currentlySelected) throws IOException {
		File tempFile = new File("tmpfile.txt");
		File file = new File(FileHelper.fileName);

		BufferedReader reader = new BufferedReader(new FileReader(file));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

		String currentLine;
		int count = 0;
		while ((currentLine = reader.readLine()) != null) {
			if (count++ != currentlySelected) {
				writer.write(currentLine + "\n");
			}
		}
		reader.close();
		writer.close();
		file.delete();
		tempFile.renameTo(file);
	}
}
