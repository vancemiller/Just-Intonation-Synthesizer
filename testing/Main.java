package testing;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Main {
	public static final int SAMPLE_RATE = 64000; // Hz
	public static final AudioFormat format = new AudioFormat(SAMPLE_RATE, 16,
			1, true, true);

	public static void main(String[] args) {

		// pre generate sine waves
		byte[][][] pitches = new byte[5][][];
		pitches[0] = makeSineWaves(400, 127);
		pitches[1] = makeSineWaves(800, 127);
		pitches[2] = makeSineWaves((int) (800 * 2.0 / 3.0), 127);
		pitches[3] = makeSineWaves((int) (800 * 5.0 / 9.0), 127);
		pitches[4] = makeSineWaves((int) (800 * 5.0 / 12.0), 127);

		// get lines to play with
		SourceDataLine[] lines = new SourceDataLine[pitches.length];
		for (int i = 0; i < pitches.length; i++) {
			try {
				lines[i] = AudioSystem.getSourceDataLine(format);
			} catch (LineUnavailableException e) {
				System.out.println("error getting line");
			}
		}

		play(lines, pitches, 100, 2);
		
		// wait some
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// stop early
		for (SourceDataLine l : lines) {
			stop(l);
		}
		
		
		// close the lines
		for (SourceDataLine l : lines) {
			l.drain();
			l.close();
		}
	}

	static byte[][] makeSineWaves(int frequency, int amplitude) {
		byte[][] waves = new byte[3][SAMPLE_RATE];
		// fill all arrays
		for (int t = 0; t < SAMPLE_RATE; t++) {
			waves[0][t] = waves[2][t] = waves[1][t] = (byte) (Math.sin(2
					* frequency * Math.PI * t / SAMPLE_RATE) * amplitude);
		}
		// remove audible pop
		double dampening = 7000.0;
		for (int i = 0; i < dampening; i++) {
			waves[0][i] *= i / dampening;
			waves[2][waves[2].length - 1 - i] *= i / dampening;
		}
		return waves;

	}

	static void play(SourceDataLine[] lines, byte[][][] sineWaves,
			int amplitude, int duration) {
		// PRE: lines.length == sineWaves.length
		// plays each element of sineWaves on a line
		try {
			for (int i = 0; i < lines.length; i++) {
				lines[i].open();
				lines[i].start();
				new Thread(write(lines[i], sineWaves[i], duration)).start();
			}
		} catch (LineUnavailableException e) {
			System.out.println("audio line error");
		}
	}

	private static Runnable write(SourceDataLine line, byte[][] sineWave, int duration) {
		final SourceDataLine l = line;
		final byte[][] s = sineWave;
		final int d = duration;

		return new Runnable() {
			@Override
			public void run() {
				// start
				l.write(s[0], 0, s[0].length);
				// loop
				for (int i = 0; i < d; i++)
					l.write(s[1], 0, s[1].length);
				// stop
				l.write(s[2], 0, s[2].length);
			}
		};
	}

	static void stop(SourceDataLine line) {
		line.stop();
		line.flush();
		line.close();
	}
}
