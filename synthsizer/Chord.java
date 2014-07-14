package synthsizer;

public interface Chord {
	public enum Type {
		NO_CHORD("N.C.", new int[] { 0 }),
		/** Major chords */
		MAJOR("M", new int[] { -12, 0, 4, 7 }), //
		MAJOR_SEVENTH("M7", new int[] { -12, 0, 4, 7, 11 }), //
		MAJOR_NINTH("maj9", new int[] { -12, 0, 4, 7, 11, 14 }), //
		MAJOR_THIRTEENTH("maj13", new int[] { -12, 0, 4, 7, 11, 14, 17, 21 }), //
		SIXTH("6", new int[] { -12, 0, 4, 7, 9 }), //
		SIXTH_NINTH("69", new int[] { -12, 0, 4, 7, 9, 14 }), //
		MAJOR_SEVENTH_FLAT_SIXTH("maj7b6", new int[] { -12, 0, 4, 7, 11, 14,
				17, 21 }), //
		/** Dominant/Seventh chords */
		// Normal
		DOMINANT_SEVENTH("7", new int[] { -12, 0, 4, 7, 10 }), //
		DOMINANT_NINTH("9", new int[] { -12, 0, 4, 7, 10, 14 }), //
		DOMINANT_THIRTEENTH("13", new int[] { -12, 0, 4, 7, 10, 14, 21 }), //
		LYDIAN_DOMINANT_SEVENTH("7#11", new int[] { -12, 0, 4, 7, 10, 14, 18,
				21 }), //
		// Altered
		DOMINANT_FLAT_9("7b9", new int[] { -12, 0, 4, 7, 10, 13 }), //
		DOMINANT_SHARP_9("7#9", new int[] { -12, 0, 4, 7, 10, 15 }), //
		ALTERED("alt7", new int[] { -12, 0, 4, 6, 10, 13 }), //
		// Suspended
		SUSPENDED_FOURTH("sus4", new int[] { -12, 0, 5, 7 }), //
		SUSPENDED_SECOND("sus2", new int[] { -12, 0, 2, 7 }), //
		SUSPENDED_FOURTH_SEVENTH("sus47", new int[] { -12, 0, 5, 7, 11 }), //
		ELEVENTH("11", new int[] { -12, 0, 7, 10, 14, 17 }), //
		/** Minor chords */
		// Normal
		MINOR("m", new int[] { -12, 0, 3, 7 }), //
		MINOR_SEVENTH("m7", new int[] { -12, 0, 3, 7, 10 }), //
		MINOR_MAJOR_SEVENTH("m/M7", new int[] { -12, 0, 3, 7, 11 }), //
		MINOR_SIXTH("m6", new int[] { -12, 0, 3, 7, 9 }), //
		MINOR_NINTH("m9", new int[] { -12, 0, 3, 7, 10, 14 }), //
		MINOR_ELEVENTH("m11", new int[] { -12, 0, 3, 7, 10, 14, 17 }), //
		MINOR_THIRTEENTH("m13", new int[] { -12, 0, 3, 7, 10, 14, 21 }), //
		// Diminished
		DIMINISHED("dim", new int[] { -12, 0, 3, 6 }), //
		DIMINISHED_SEVENTH("dim7", new int[] { -12, 0, 3, 6, 9 }), //
		HALF_DIMINISHED("m7b5", new int[] { -12, 0, 3, 6, 10 }), //
		;

		public final String name;
		public final int[] halfStepsFromRoot;

		Type(String name, int[] degrees) {
			this.name = name;
			halfStepsFromRoot = degrees;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	public int[] getNotes();

}
