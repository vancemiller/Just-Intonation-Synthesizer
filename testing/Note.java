package testing;

public interface Note {
	public static final double TUNING = 440.0;
	public static final Integer[] VALID_OCTAVES = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
	public static final int NUM_OVERTONES = 6;

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

	public void start();
	
	public void stop();
	
}
