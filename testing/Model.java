package testing;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import testing.Note.Pitch;

public class Model {
	public static final int SAMPLE_RATE = 64000;
	public static final AudioFormat af = new AudioFormat(SAMPLE_RATE, 16, 1,
			true, true);
}

class Note {
	public static final double tuning = 440.0;
	public static final Integer[] validOctaves = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
	public static final int NUM_OVERTONES = 6;
	private final double[] overtoneVolume = { 1, 1.0 / 16.0, 1.0 / 32.0,
			1.0 / 32.0, 1.0 / 64.0, 1.0 / 64.0 };

	public enum Pitch {
		A(0, false), Bb(1, true), B(2, false), C(3, false), Db(4, true), D(5,
				false), Eb(6, true), E(7, false), F(8, false), Gb(9, true), G(
				10, false), Ab(11, true);
		public final int halfStepsFromA;
		public final boolean isAccidental;
		public final static int totalPitches = 12;
		public final static Pitch start = A;

		Pitch(int halfStepsFromA, boolean isAccidental) {
			this.halfStepsFromA = halfStepsFromA;
			this.isAccidental = isAccidental;
		}

		public Pitch next() {
			return values()[(ordinal() + 1) % values().length];
		}

		public Pitch previous() {
			return values()[Math.abs((ordinal() - 1) % values().length)];
		}
	}

	public enum Interval {
		UNISON(1.0, 0, "Unison"), SEMITONE(16.0 / 15.0, 1, "Semitone"), MINOR_TONE(
				10.0 / 9.0, 1, "Minor tone"), MAJOR_TONE(9.0 / 8.0, 2,
				"Major tone"), MINOR_THIRD(6.0 / 5.0, 3, "Minor third"), MAJOR_THIRD(
				5.0 / 4.0, 4, "Major third"), PERFECT_FOURTH(4.0 / 3.0, 5,
				"Perfect fourth"), AUGMENTED_FOURTH(45.0 / 32.0, 6,
				"Augmented fourth"), DIMINISHED_FIFTH(64.0 / 45.0, 6,
				"Diminished fifth"), PERFECT_FIFTH(3.0 / 2.0, 7,
				"Perfect fifth"), MINOR_SIXTH(8.0 / 5.0, 8, "Minor sixth"), MAJOR_SIXTH(
				5.0 / 3.0, 9, "Major sixth"), HARMONIC_MINOR_SEVENTH(7.0 / 4.0,
				10, "Harmonic minor seventh"), GRAVE_MINOR_SEVENTH(16.0 / 9.0,
				10, "Grave minor seventh"), MINOR_SEVENTH(9.0 / 5.0, 10,
				"Minor seventh"), MAJOR_SEVENTH(15.0 / 8.0, 11, "Major seventh"), OCTAVE(
				2.0, 12, "Octave");

		public final double ratio;
		public final int halfStepsFromRoot;
		public final String longName;

		Interval(double ratio, int halfStepsFromRoot, String string) {
			this.ratio = ratio;
			this.halfStepsFromRoot = halfStepsFromRoot;
			this.longName = string;
		}

		public Interval next() {
			return values()[ordinal() + 1 % values().length];
		}

		@Override
		public String toString() {
			return longName;
		}
	}

	private final Pitch root;
	private final int octave;
	private final Interval intervalFromRoot;

	public Note(Pitch root, int octave, Interval intervalFromRoot) {
		this.root = root;
		this.octave = octave;
		this.intervalFromRoot = intervalFromRoot;
	}

	private double getFrequencyOfRoot() {
		return tuning
				* Math.pow(Math.pow(2.0, 1.0 / 12.0), getHalfStepsFromA());
	}

	private int getHalfStepsFromA() {
		return 12 * (octave - 4) + root.halfStepsFromA;
	}

	public Runnable getRunnablePlay(int milliseconds, double volume) {
		final Runnable harmonicsToPlay[] = new Runnable[6];
		Interval[] intervals = { intervalFromRoot, Interval.OCTAVE,
				Interval.PERFECT_FIFTH, Interval.PERFECT_FOURTH,
				Interval.MAJOR_THIRD, Interval.MINOR_THIRD };
		double intervalMultiplyer = 1;
		for (int i = 0; i < NUM_OVERTONES; i++) {
			intervalMultiplyer *= intervals[i].ratio;
			harmonicsToPlay[i] = runnableFrequency(generateSineWavefreq(volume
					* getOvertoneVolume(i), getFrequencyOfRoot()
					* intervalMultiplyer, milliseconds));
		}
		return new Runnable() {
			Runnable someHarmonicsToPlay[] = harmonicsToPlay;

			@Override
			public void run() {
				for (Runnable r : someHarmonicsToPlay) {
					new Thread(r).start();
				}
			}
		};
	}

	private Runnable runnableFrequency(byte[] samples) {
		final byte[] array = samples;
		return new Thread(new Runnable() {
			@Override
			public void run() {
				SourceDataLine line;
				try {
					line = AudioSystem.getSourceDataLine(Model.af);
					line.open(Model.af);
					line.start();
					line.write(array, 0, array.length);
					line.drain();
					line.close();
				} catch (LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public static void playFrequency(double amplitude, double frequency,
			int milliseconds) {
		final byte[] array = generateSineWavefreq(amplitude, frequency,
				milliseconds);
		new Thread(new Runnable() {
			@Override
			public void run() {
				SourceDataLine line;
				try {
					line = AudioSystem.getSourceDataLine(Model.af);
					line.open(Model.af);
					line.start();
					line.write(array, 0, array.length);
					line.drain();
					line.close();
				} catch (LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	private static byte[] generateSineWavefreq(double amplitude,
			double frequencyOfSignal, int milliseconds) {
		byte[] sin = new byte[(int) (milliseconds / 1000.0 * Model.SAMPLE_RATE)];
		double samplingInterval = Model.SAMPLE_RATE / frequencyOfSignal;
		for (int i = 0; i < sin.length; i++) {
			double angle = (2.0 * Math.PI * i) / samplingInterval;
			sin[i] = (byte) (Math.sin(angle) * amplitude * 127.0);
		}
		int dampening = 7000;
		if (sin.length < 2 * dampening)
			dampening = sin.length / 2;
		final int sNum = dampening;
		for (int i = 0; i < sNum; i++) {
			sin[sNum - i] = (byte) ((sin[sNum - i] * dampening / sNum));
			sin[sin.length - sNum + i] = (byte) ((sin[sin.length - sNum + i] * dampening) / sNum);
			dampening = dampening - 1;
		}
		return sin;
	}

	public Pitch getRootPitch() {
		return root;
	}

	public Pitch getPitch() {
		return Pitch.values()[(root.ordinal() + intervalFromRoot.halfStepsFromRoot)
				% Pitch.values().length];
	}

	public int getRootOctave() {
		return octave;
	}

	public int getOctave() {
		int o = octave;
		Pitch p = root;
		for (int i = 0; i < intervalFromRoot.halfStepsFromRoot; i++) {
			p = p.next();
			if (p == Note.Pitch.A) {
				o++;
			}
		}

		return o;
	}

	public Interval getInterval() {
		return intervalFromRoot;
	}

	@Override
	public Note clone() {
		return new Note(root, octave, intervalFromRoot);
	}

	@Override
	public String toString() {
		return getPitch().toString() + getOctave();
	}

	public double getOvertoneVolume(int i) {
		return overtoneVolume[i];
	}

	public void setOvertoneVolume(int index, double overtoneVolume) {
		this.overtoneVolume[index] = overtoneVolume;
	}
}

class Chord {
	Pitch root;
	int octave;
	List<Note> notes;

	public enum BasicType {
		TRIAD, SEVENTH;
	}

	public Chord(Note.Pitch root, int octave) {
		this.root = root;
		this.notes = new java.util.ArrayList<Note>();
		this.notes.add(new Note(root, octave, Note.Interval.UNISON));
		this.octave = octave;
	}

	public void addNote(Note.Interval interval, int octave) {
		notes.add(new Note(root, octave, interval));
	}

	public void addNote(Note.Interval interval) {
		notes.add(new Note(this.root, interval.halfStepsFromRoot < 12 ? octave
				: octave + interval.halfStepsFromRoot / 12, interval));
	}

	public void addNote(Note n) {
		notes.add(n);
	}

	public void removeNote(int index) {
		if (notes.size() == 0)
			return;
		if (index == -1) {
			notes.remove(notes.size() - 1);
		} else {
			notes.remove(index);
		}
	}

	public void reset() {
		notes.removeAll(notes);
		notes.add(new Note(root, octave, Note.Interval.UNISON));
	}

	public void invert() {
		Note tmp = notes.remove(0);
		notes.add(new Note(tmp.getRootPitch(), tmp.getOctave() + 1, tmp
				.getInterval()));
	}

	public void play(int seconds, double volume) {
		List<Runnable> notesToPlay = new ArrayList<Runnable>();
		for (Note n : notes) {
			notesToPlay.add(n.getRunnablePlay(seconds, volume));
		}
		for (Runnable r : notesToPlay)
			new Thread(r).start();
	}

}

class Triad extends Chord {
	private final Type type;

	public enum Type {
		MAJOR, MINOR, AUGMENTED, DIMINISHED;
	}

	public enum Inversion {
		ROOT, FIRST, SECOND;
	}

	public Triad(Pitch pitch, int octave, Type type) {
		super(pitch, octave);
		this.type = type;
		this.buildChord();
	}

	private void buildChord() {
		switch (this.type) {
		case MAJOR:
			addNote(Note.Interval.MAJOR_THIRD);
			addNote(Note.Interval.PERFECT_FIFTH);
			break;
		case MINOR:
			addNote(Note.Interval.MINOR_THIRD);
			addNote(Note.Interval.PERFECT_FIFTH);
			break;
		case AUGMENTED:
			addNote(Note.Interval.MAJOR_THIRD);
			addNote(Note.Interval.MINOR_SIXTH);
			break;
		case DIMINISHED:
			addNote(Note.Interval.MINOR_THIRD);
			addNote(Note.Interval.DIMINISHED_FIFTH);
			break;
		default:
			System.out.println("fail");
		}
	}

	void rebuildTriad() {
		buildChord();
	}

	public void invert(Inversion inversion) {
		reset();
		buildChord();
		switch (inversion) {
		case SECOND:
			notes.set(1, new Note(root, notes.get(0).getOctave() + 1, notes
					.get(1).getInterval()));
		case FIRST:
			notes.set(0, new Note(root, octave + 1, Note.Interval.UNISON));
		case ROOT:
		default:
			break;
		}
	}

}

class SeventhChord extends Triad {
	private final Type type;

	public enum Type {
		DIMINISHED(Triad.Type.DIMINISHED), HALF_DIMINISHED(
				Triad.Type.DIMINISHED), MINOR(Triad.Type.MINOR), MINOR_MAJOR(
				Triad.Type.MINOR), DOMINANT(Triad.Type.MAJOR), MAJOR(
				Triad.Type.MAJOR), AUGMENTED(Triad.Type.AUGMENTED), AUGMENTED_MAJOR(
				Triad.Type.AUGMENTED);

		public final Triad.Type triad;

		Type(Triad.Type triad) {
			this.triad = triad;
		}
	}

	public enum Inversion {
		ROOT, FIRST, SECOND, THIRD;
	}

	public SeventhChord(Pitch pitch, int octave, Type type) {
		super(pitch, octave, type.triad);
		this.type = type;
		buildChord();
	}

	private void buildChord() {
		switch (this.type) {
		case DIMINISHED:
			this.addNote(Note.Interval.MAJOR_SIXTH);
			break;
		case AUGMENTED:
		case DOMINANT:
		case HALF_DIMINISHED:
		case MINOR:
			this.addNote(Note.Interval.GRAVE_MINOR_SEVENTH);
			break;
		case AUGMENTED_MAJOR:
		case MAJOR:
		case MINOR_MAJOR:
			this.addNote(Note.Interval.MAJOR_SEVENTH);
		default:
			break;
		}
	}

	void rebuildSeventhChord() {
		rebuildTriad();
		buildChord();
	}

	public void invert(Inversion inversion) {
		reset();
		rebuildSeventhChord();
		buildChord();
		switch (inversion) {
		case THIRD:
			notes.set(2, new Note(root, notes.get(2).getOctave() + 1, notes
					.get(2).getInterval()));
		case SECOND:
			notes.set(1, new Note(root, notes.get(1).getOctave() + 1, notes
					.get(1).getInterval()));
		case FIRST:
			notes.set(0, new Note(root, octave + 1, Note.Interval.UNISON));
		case ROOT:
		default:
			break;
		}
	}
}

class ExtendedChord extends SeventhChord {
	private final Type type;

	public enum Type {
		DOMINANT_NINTH(SeventhChord.Type.DOMINANT), DOMINANT_ELEVENTH(
				SeventhChord.Type.DOMINANT), DOMINANT_THIRTEENTH(
				SeventhChord.Type.DOMINANT);

		public final SeventhChord.Type seventhChord;

		Type(SeventhChord.Type seventhChord) {
			this.seventhChord = seventhChord;
		}
	}

	public enum Inversion {
		ROOT, FIRST, SECOND, THIRD, FOURTH;
	}

	public ExtendedChord(Pitch pitch, int octave, Type type) {
		super(pitch, octave, type.seventhChord);
		this.type = type;
		buildChord();
	}

	private void buildChord() {
		switch (type) {
		case DOMINANT_ELEVENTH:
			this.removeNote(1); // omit the third
			this.addNote(Note.Interval.PERFECT_FOURTH, octave + 1);
			break;
		case DOMINANT_NINTH:
			this.addNote(Note.Interval.MAJOR_TONE, octave + 1);
			break;
		case DOMINANT_THIRTEENTH:
			this.addNote(Note.Interval.MAJOR_SIXTH, octave + 1);
			break;
		default:
			break;

		}
	}

	void rebuildExtendedChord() {
		rebuildSeventhChord();
		buildChord();
	}

	public void invert(Inversion inversion) {
		reset();
		rebuildExtendedChord();
		switch (inversion) {
		case FOURTH:
			notes.set(3, new Note(root, notes.get(0).getOctave() + 1, notes
					.get(0).getInterval()));
		default:
			super.invert();
		}
	}

}
