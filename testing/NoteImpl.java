package testing;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class NoteImpl implements Note {

	private Pitch root;
	private Interval intervalFromRoot;
	private Player p;

	public NoteImpl(Pitch root, Interval unison) {
		this.root = root;
		this.intervalFromRoot = unison;
		int freq = (int) (getFrequencyOfRoot() * unison.ratio);
		int[] overtones = new int[Note.NUM_OVERTONES];
		int[] amplitudes = new int[Note.NUM_OVERTONES];
		for (int i = 0; i < Note.NUM_OVERTONES; i++) {
			overtones[i] = freq * (i + 1);
			amplitudes[i] = 127 - 32 * i; // should read this from a file or
											// something
		}
		this.p = new Player(overtones, amplitudes);
	}

	/** Getters */

	public Pitch getRoot() {
		return root;
	}

	public Pitch getPitch() {
		return Pitch.values()[(root.ordinal() + intervalFromRoot.halfStepsFromRoot)
				% Pitch.values().length];
	}

	public Interval getInterval() {
		return intervalFromRoot;
	}

	@Override
	public String toString() {
		return getPitch().toString();
	}

	/** Setters */

	protected void setRoot(Pitch root) {
		this.root = root;
	}

	protected void setInterval(Interval interval) {
		this.intervalFromRoot = interval;
	}

	/** Sound makers */

	@Override
	public void start() {
		p.start();
	}

	@Override
	public void stop() {
		p.stop();
	}

	/** Helpers */

	private double getFrequencyOfRoot() {
		// return tuning * (2^(1/12))^n
		return TUNING
				* Math.pow(Math.pow(2.0, 1.0 / 12.0), getHalfStepsFromA());
	}

	private int getHalfStepsFromA() {
		return root.ordinal() - Note.Pitch.A4.ordinal();
	}

	private static class Player {
		public static final int SAMPLE_RATE = 64000; // Hz
		public static final AudioFormat format = new AudioFormat(SAMPLE_RATE,
				16, 1, true, true);
		private boolean started;
		private byte[][][] sineWaves;
		private SourceDataLine[] lines;
		private ThreadedPlayer[] threads;

		public Player(int[] frequency, int[] amplitude) {
			// Make the samples
			this.sineWaves = new byte[Note.NUM_OVERTONES][3][SAMPLE_RATE];
			for (int i = 0; i < Note.NUM_OVERTONES; i++)
				makeSineWaves(i, frequency[i], amplitude[i]);
			// get a line for each overtone to play
			this.lines = new SourceDataLine[Note.NUM_OVERTONES];
			// get a thread to play each line
			this.threads = new ThreadedPlayer[Note.NUM_OVERTONES];
			for (int i = 0; i < Note.NUM_OVERTONES; i++)
				threads[i] = new ThreadedPlayer(lines[i], sineWaves[i]);
			this.started = false;
		}

		public void start() {
			if (started)
				return;
			started = true;
			for (int i = 0; i < Note.NUM_OVERTONES; i++) {
				new Thread(threads[i]).start();
			}
		}

		public void stop() {
			if (!started)
				return;
			for (ThreadedPlayer tp : threads) {
				tp.stop();
			}
			started = false;
		}

		private void makeSineWaves(int i, int frequency, int amplitude) {
			// fill all arrays
			for (int t = 0; t < SAMPLE_RATE; t++) {
				sineWaves[i][0][t] = sineWaves[i][2][t] = sineWaves[i][1][t] = (byte) (Math
						.sin(2 * frequency * Math.PI * t / SAMPLE_RATE) * amplitude);
			}
			// remove audible pop
			double dampening = 7000.0;
			for (int j = 0; j < dampening; j++) {
				sineWaves[i][0][j] *= j / dampening;
				sineWaves[i][2][sineWaves[i][2].length - 1 - j] *= j
						/ dampening;
			}
		}

		private class ThreadedPlayer implements Runnable {

			private volatile boolean isFinished = false;
			private SourceDataLine l;
			private byte[][] s;

			public ThreadedPlayer(SourceDataLine line, byte[][] sineWaves) {
				this.l = line;
				this.s = sineWaves;
			}

			@Override
			public void run() {
				try {
					l = AudioSystem.getSourceDataLine(Player.format);
					l.open();
				} catch (LineUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				isFinished = false;
				l.start();
				l.write(s[0], 0, s[0].length);
				while (!isFinished) {
					if (l.available() > (l.getBufferSize() - Player.SAMPLE_RATE)) {
						l.write(s[1], 0, s[1].length);
						try {
							Thread.sleep(Player.SAMPLE_RATE / 200);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
				l.write(s[2], 0, s[2].length);
				l.drain();
				l.close();
			}

			public void stop() {
				isFinished = true;
			}
		}
	}

}
