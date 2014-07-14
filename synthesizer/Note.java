package synthesizer;

public interface Note {
	public static final double TUNING = 440.0;
	public static final int NUM_OVERTONES = 8;
	public static final double QUALITY_TOLERANCE = 0.999999995;

	public enum Pitch {
		C0, Db0, D0, Eb0, E0, F0, Gb0, G0, Ab0, A0, Bb0, B0, C1, Db1, D1, Eb1, E1, F1, Gb1, G1, Ab1, A1, Bb1, B1, C2, Db2, D2, Eb2, E2, F2, Gb2, G2, Ab2, A2, Bb2, B2, C3, Db3, D3, Eb3, E3, F3, Gb3, G3, Ab3, A3, Bb3, B3, C4, Db4, D4, Eb4, E4, F4, Gb4, G4, Ab4, A4, Bb4, B4, C5, Db5, D5, Eb5, E5, F5, Gb5, G5, Ab5, A5, Bb5, B5, C6, Db6, D6, Eb6, E6, F6, Gb6, G6, Ab6, A6, Bb6, B6, C7, Db7, D7, Eb7, E7, F7, Gb7, G7, Ab7, A7, Bb7, B7, C8, Db8, D8, Eb8, E8, F8, Gb8, G8, Ab8, A8, Bb8, B8;

		public boolean isAccidental() {
			return toString().contains("b");
		}

		public int getLocationOnKeyboard() {
			int count = 0;
			Pitch[] pitches = values();
			for (int i = 0; i < ordinal(); i++) {
				if (!pitches[i].isAccidental()) {
					count++;
				}
			}
			return count;
		}

		public static double getFrequency(Root root, Pitch p) {
			// tuning * (2^((key number - A4)/12))
			// TODO
			Interval i = Interval.getInterval(root, p);
			return TUNING
					* Math.pow(2.0,
							((p.ordinal() - i.ordinal()) - Note.Pitch.A4
									.ordinal()) / 12.0) * i.ratio;
		}
	}

	public enum Interval {
		UNISON(1.0, "Unison"), MINOR_SECOND(25.0 / 24.0, "Minor second"), MAJOR_SECOND(
				9.0 / 8.0, "Major second"), MINOR_THIRD(6.0 / 5.0,
				"Minor third"), MAJOR_THIRD(5.0 / 4.0, "Major third"), PERFECT_FOURTH(
				4.0 / 3.0, "Perfect fourth"), DIMINISHED_FIFTH(45.0 / 32.0,
				"Diminished fifth"), PERFECT_FIFTH(3.0 / 2.0, "Perfect fifth"), MINOR_SIXTH(
				8.0 / 5.0, "Minor sixth"), MAJOR_SIXTH(5.0 / 3.0, "Major sixth"), MINOR_SEVENTH(
				9.0 / 5.0, "Minor seventh"), MAJOR_SEVENTH(15.0 / 8.0,
				"Major seventh");

		public final double ratio;
		public final String longName;

		Interval(double ratio, String string) {
			this.ratio = ratio;
			longName = string;
		}

		public Interval next() {
			return values()[(ordinal() + 1) % values().length];
		}

		public static Interval getInterval(Root root, Pitch pitch) {
			if (root.equals(Root.EQUAL_TEMPERMENT)) {
				return Interval.UNISON;
			}
			Interval[] intervals = values();
			return intervals[(intervals.length + pitch.ordinal()
					% intervals.length - root.getNumber())
					% intervals.length];
		}

		@Override
		public String toString() {
			return longName;
		}
	}

	public enum Root {
		EQUAL_TEMPERMENT, C, Db, D, Eb, E, F, Gb, G, Ab, A, Bb, B;

		@Override
		public String toString() {
			if (this == EQUAL_TEMPERMENT) {
				return "Equal temperment";
			} else {
				return super.toString();
			}
		}

		public int getNumber() {
			return ordinal() - 1;
		}
	}

	public Pitch getPitch();

	public void start();

	public void stop();

	public void update(Keyboard keyboard);
}
