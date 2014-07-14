package synthsizer;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class NoteImpl implements Note {

	private static class Player {
		private static class ThreadedPlayer implements Runnable {
			public volatile boolean isFinished;

			final static int SAMPLE_RATE = 16000; // Hz
			final static AudioFormat format = new AudioFormat(SAMPLE_RATE, 16,
					1, true, true);
			final static public int SAMPLE_SIZE = 2;
			final static public double BUFFER_DURATION = 0.100;
			final static public int SINE_PACKET_SIZE = (int) (BUFFER_DURATION
					* SAMPLE_RATE * SAMPLE_SIZE);

			private int[] amplitudes;
			private double[] frequencies;
			private float gain;

			public ThreadedPlayer() {
				isFinished = true;
				gain = 0;
			}

			@Override
			public void run() {
				isFinished = false;
				try {
					SourceDataLine l;
					try {
						DataLine.Info info = new DataLine.Info(
								SourceDataLine.class, format,
								SINE_PACKET_SIZE * 2);
						if (!AudioSystem.isLineSupported(info)) {
							throw new LineUnavailableException();
						}
						l = (SourceDataLine) AudioSystem.getLine(info);
						l.open(format);
						l.start();
					} catch (LineUnavailableException e) {
						return;
					}
					FloatControl control = (FloatControl) l
							.getControl(FloatControl.Type.MASTER_GAIN);
					ByteBuffer buffer = ByteBuffer.allocate(SINE_PACKET_SIZE);
					double[] cyclePositions = new double[frequencies.length];
					double[] cycleFractions = new double[frequencies.length];
					while (!isFinished) {
						control.setValue(gain);
						buffer.clear();
						for (int i = 0; i < SINE_PACKET_SIZE / SAMPLE_SIZE; i++) {
							short tmp = 0;
							for (int j = 0; j < cycleFractions.length; j++) {
								cycleFractions[j] = frequencies[j]
										/ SAMPLE_RATE;
								tmp += (short) (amplitudes[j] * Math.sin(2
										* Math.PI * cyclePositions[j]));
								cyclePositions[j] += cycleFractions[j];
								if (cyclePositions[j] > 1) {
									cyclePositions[j] -= 1;
								}
							}
							buffer.putShort(tmp);
						}
						l.write(buffer.array(), 0, buffer.position());
						try {
							while (l.getBufferSize() - l.available() > SINE_PACKET_SIZE) {
								Thread.sleep(1);
							}
						} catch (InterruptedException e) {
							isFinished = true;
							break;
						}
					}
					buffer = ByteBuffer.allocate(SINE_PACKET_SIZE / 4);
					double dampening = 1.0;
					for (int i = 0; i < SINE_PACKET_SIZE / SAMPLE_SIZE / 4; i++, dampening *= .99) {
						short tmp = 0;
						for (int j = 0; j < cycleFractions.length; j++) {
							cycleFractions[j] = frequencies[j] / SAMPLE_RATE;
							tmp += (short) (amplitudes[j] * Math.sin(2
									* Math.PI * cyclePositions[j]));
							cyclePositions[j] += cycleFractions[j];
							if (cyclePositions[j] > 1) {
								cyclePositions[j] -= 1;
							}
						}
						buffer.putShort((short) (dampening * tmp));
					}
					l.write(buffer.array(), 0, buffer.position());
					l.drain();
					l.close();
					l = null;
				} catch (NullPointerException e) {
					// the line was closed and deleted while still running
				}
			}

			public void stop() {
				isFinished = true;
			}
		}

		public boolean started;
		private final ThreadedPlayer player;

		private Thread thread;

		public Player() {
			// make a player to play each line synchronously
			player = new ThreadedPlayer();
			// make some threads to execute the threaded players.
			thread = null;
			// initialize the threaded players.
			started = false;
		}

		public void setAmplitudes(int[] amplitudes) {
			player.amplitudes = amplitudes;
		}

		public void setFrequency(double frequency) {
			double[] frequencies = new double[Note.NUM_OVERTONES];
			for (int i = 0; i < frequencies.length; i++) {
				frequencies[i] = frequency * (i + 1);
			}
			player.frequencies = frequencies;
		}

		public void setGain(float gain) {
			player.gain = gain;
		}

		public void start() {
			if (!started) {
				started = true;
				thread = new Thread(player);
				thread.start();
			}
		}

		public void stop() {
			if (started) {
				player.stop();
				started = false;
			}
		}
	}

	private final Pitch pitch;

	private final Player p;

	public NoteImpl(Pitch pitch) {
		this.pitch = pitch;
		p = new Player();
		// requires manual call to update before playing.
	}

	public NoteImpl(Pitch pitch, Keyboard keyboard) {
		this.pitch = pitch;
		p = new Player();
		update(keyboard);
	}

	/** Getters */

	@Override
	public Pitch getPitch() {
		return pitch;
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

	@Override
	public String toString() {
		return pitch.toString();
	}

	/** Setters */

	@Override
	public void update(Keyboard keyboard) {
		p.setGain(keyboard.getGain());
		p.setFrequency(Note.Pitch.getFrequency(keyboard.getRoot(), pitch));
		p.setAmplitudes(keyboard.getInstrument().getAmplitudes());
		// p.recalculate();
	}
}
