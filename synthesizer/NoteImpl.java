package synthesizer;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class NoteImpl implements Note {

	private Pitch pitch;
	private Player p;

	public NoteImpl(Pitch pitch, Keyboard keyboard) {
		this.pitch = pitch;
		p = new Player();
		update(keyboard);
	}

	public NoteImpl(Pitch pitch) {
		this.pitch = pitch;
		p = new Player();
		// requires manual call to update before playing.
	}

	/** Getters */

	@Override
	public Pitch getPitch() {
		return pitch;
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

	private static class Player {
		public boolean started;
		private ThreadedPlayer[] players;
		private Thread[] threads;

		public Player() {
			// make a player to play each line synchronously
			players = new ThreadedPlayer[Note.NUM_OVERTONES];
			// make some threads to execute the threaded players.
			threads = new Thread[Note.NUM_OVERTONES];
			// initialize the threaded players.
			for (int i = 0; i < Note.NUM_OVERTONES; i++) {
				players[i] = new ThreadedPlayer();
			}
			started = false;
		}

		public void setFrequency(double frequency) {
			for (int i = 0; i < players.length; i++) {
				players[i].frequency = frequency * Math.pow(2, i);
			}
		}

		public void setGain(float gain) {
			for (ThreadedPlayer player : players) {
				player.setGain(gain);
			}
		}

		public void setAmplitudes(int[] amplitudes) {
			for (int i = 0; i < players.length; i++) {
				players[i].amplitude = amplitudes[i];
			}
		}

		public void start() {
			if (!started) {
				started = true;
				for (int i = 0; i < Note.NUM_OVERTONES; i++) {
					threads[i] = new Thread(players[i]);
				}
				for (int i = 0; i < Note.NUM_OVERTONES; i++) {
					threads[i].start();
				}
			}
		}

		public void stop() {
			if (started) {
				for (ThreadedPlayer player : players) {
					player.stop();
				}
				started = false;
			}
		}

		private static class ThreadedPlayer implements Runnable {
			public volatile boolean isFinished;

			final static int SAMPLE_RATE = 16000; // Hz
			final static AudioFormat format = new AudioFormat(SAMPLE_RATE, 16,
					1, true, true);
			final static public int SAMPLE_SIZE = 2;
			final static public double BUFFER_DURATION = 0.100;
			final static public int SINE_PACKET_SIZE = (int) (BUFFER_DURATION
					* SAMPLE_RATE * SAMPLE_SIZE);

			private int amplitude;
			private double frequency;
			private float gain;

			public ThreadedPlayer() {
				isFinished = true;
				gain = 0;
			}

			public void setGain(float gain) {
				this.gain = gain;
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
					double cyclePosition = 0;
					double cycleFraction = 0.0;
					while (!isFinished) {
						cycleFraction = frequency / SAMPLE_RATE;
						control.setValue(gain);
						buffer.clear();
						for (int i = 0; i < SINE_PACKET_SIZE / SAMPLE_SIZE; i++) {
							buffer.putShort((short) (amplitude * Math.sin(2
									* Math.PI * cyclePosition)));
							cyclePosition += cycleFraction;
							if (cyclePosition > 1) {
								cyclePosition -= 1;
							}
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
						buffer.putShort((short) (dampening * amplitude * Math
								.sin(2 * Math.PI * cyclePosition)));
						cyclePosition += cycleFraction;
						if (cyclePosition > 1) {
							cyclePosition -= 1;
						}
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
	}
}
