package synthesizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class NoteImpl implements Note {

	private Pitch pitch;
	private int[] amplitudes;
	private Key key;
	private Player p;

	public NoteImpl(Pitch pitch) {
		this(pitch, Note.Key.values()[pitch.ordinal() % 12 + 1]);
	}

	public NoteImpl(Pitch pitch, Key key) {
		this.pitch = pitch;
		this.key = key;
		p = new Player();
		// set the amplitudes and overtones
		setInstrument("Pure Tone");
	}

	/** Getters */

	public Key getKey() {
		return key;
	}

	@Override
	public Pitch getPitch() {
		return pitch;
	}

	@Override
	public String toString() {
		return getPitch().toString();
	}

	/** Setters */

	@Override
	public void setKey(Key key) {
		if (key == Note.Key.EQUAL_TEMPERMENT) {
			this.key = Note.Key.values()[pitch.ordinal() % 12 + 1];
		} else {
			this.key = key;
		}
		// re-make the sine waves for the new key;
		double tuningRatio = 1;
		if (key != Note.Key.EQUAL_TEMPERMENT) {
			tuningRatio = Note.Interval.getInterval(key, pitch).ratio;
		}
		double freq = getFrequencyOfRoot() * tuningRatio;
		int[] overtones = new int[Note.NUM_OVERTONES];
		for (int i = 0; i < Note.NUM_OVERTONES; i++) {
			overtones[i] = (int) freq * (i + 1);
		}
		p.setOvertones(overtones, amplitudes);
	}

	@Override
	public void setInstrument(String instrument) {
		if (amplitudes == null) {
			amplitudes = new int[Note.NUM_OVERTONES];
		}
		FileReader.loadAmplitudes(instrument, amplitudes);
		// recalculate sine waves
		setKey(key);
	}

	/** Sound makers */

	@Override
	public void start() {
		if (!p.started) {
			p.start();
		}
	}

	@Override
	public void stop() {
		if (p.started) {
			p.stop();
		}
	}

	/** Helpers */

	private double getFrequencyOfRoot() {
		// return tuning * (2^(n/12))
		return TUNING * Math.pow(2.0, getHalfStepsFromA() / 12.0);
	}

	private int getHalfStepsFromA() {
		return (pitch.ordinal() - Note.Interval.getInterval(key, pitch)
				.ordinal()) - Note.Pitch.A4.ordinal();
	}

	private static class Player {
		public static final int SAMPLE_RATE = 64000; // Hz
		public static final AudioFormat format = new AudioFormat(SAMPLE_RATE,
				16, 1, true, true);
		public boolean started;
		private byte[][][] sineWaves;
		private SourceDataLine[] lines;
		private ThreadedPlayer[] threads;

		public Player() {
			sineWaves = new byte[Note.NUM_OVERTONES][3][SAMPLE_RATE];
			lines = new SourceDataLine[Note.NUM_OVERTONES];
			threads = new ThreadedPlayer[Note.NUM_OVERTONES];
			for (int i = 0; i < Note.NUM_OVERTONES; i++) {
				threads[i] = new ThreadedPlayer(lines[i], null);
			}
			started = false;
		}

		public void setOvertones(int[] frequency, int[] amplitude) {
			for (int i = 0; i < Note.NUM_OVERTONES; i++) {
				// fill all arrays
				for (int t = 0; t < SAMPLE_RATE; t++) {
					sineWaves[i][0][t] = sineWaves[i][2][t] = sineWaves[i][1][t] = (byte) (Math
							.sin(2 * frequency[i] * Math.PI * t / SAMPLE_RATE) * amplitude[i]);
				}
				// remove audible pop
				double dampening = 7000.0;
				for (int j = 0; j < dampening; j++) {
					sineWaves[i][0][j] *= j / dampening;
					sineWaves[i][2][sineWaves[i][2].length - 1 - j] *= j
							/ dampening;
				}
			}
			for (int i = 0; i < Note.NUM_OVERTONES; i++) {
				threads[i].setSineWaves(sineWaves[i]);
			}
		}

		public void start() {
			if (threads[0].isPlaying) {
				return;
			}
			started = true;
			for (int i = 0; i < Note.NUM_OVERTONES; i++) {
				new Thread(threads[i]).start();
			}
		}

		public void stop() {
			if (!threads[0].isPlaying) {
				return;
			}
			started = false;
			for (ThreadedPlayer tp : threads) {
				tp.stop();
			}
		}

		private class ThreadedPlayer implements Runnable {
			private volatile boolean isFinished = false;
			public boolean isPlaying = false;
			private SourceDataLine l;
			private byte[][] s;

			public ThreadedPlayer(SourceDataLine line, byte[][] sineWaves) {
				l = line;
				s = sineWaves;
			}

			public void setSineWaves(byte[][] s) {
				this.s = s;
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
				isPlaying = true;
				l.start();
				l.write(s[0], 0, s[0].length);
				while (!isFinished) {
					if (l.available() > (l.getBufferSize() - Player.SAMPLE_RATE)) {
						l.write(s[1], 0, s[1].length);
						try {
							Thread.sleep(Player.SAMPLE_RATE / 190);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
				l.write(s[2], 0, s[2].length);
				l.drain();
				l.close();
				isPlaying = false;
			}

			public void stop() {
				isFinished = true;
			}
		}
	}
}
