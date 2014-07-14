package testing;

import testing.Note.Pitch;

public class Event {
	private NoteStack s;

	public static Event newAddEvent(int rootIndex, int intervalIndex,
			int octaveIndex) {
		return new AddEvent(rootIndex, intervalIndex, octaveIndex);

	}

	protected void setNoteStack(NoteStack s) {
		this.s = s;
	}

	public NoteStack getNoteStack() {
		return s;
	}

	public static Event newRemoveEvent() {
		return new RemoveEvent();
	}

	public static Event newPlayEvent(int duration) {
		return new PlayEvent(duration);
	}

	public static Event newOvertoneUpdateEvent(double[] overtones, Note n) {
		return new OvertoneUpdateEvent(overtones, n);
	}

	public boolean isAddEvent() {
		return false;
	}

	public boolean isRemoveEvent() {
		return false;
	}

	public boolean isPlayEvent() {
		return false;
	}

	public boolean isOvertoneUpdateEvent() {
		return false;
	}

}

class AddEvent extends Event {
	private final Pitch pitch;
	private final int octave;
	private final Note.Interval interval;

	public AddEvent(int rootIndex, int intervalIndex, int octaveIndex) {
		pitch = Note.Pitch.values()[rootIndex];
		interval = Note.Interval.values()[intervalIndex];
		octave = Note.validOctaves[octaveIndex];
	}

	@Override
	public boolean isAddEvent() {
		return true;
	}

	public Pitch getPitch() {
		return pitch;
	}

	public int getOctave() {
		return octave;
	}

	public Note.Interval getInterval() {
		return interval;
	}

}

class RemoveEvent extends Event {
	@Override
	public boolean isRemoveEvent() {
		return true;
	}
}

class PlayEvent extends Event {
	private final int duration;

	public PlayEvent(int duration) {
		this.duration = duration;
	}

	@Override
	public boolean isPlayEvent() {
		return true;
	}

	public int getDuration() {
		return duration;
	}
}

class OvertoneUpdateEvent extends Event {
	private final double[] overtones;
	private final Note n;

	public OvertoneUpdateEvent(double[] overtones, Note n) {
		this.overtones = overtones;
		this.n = n;
	}

	@Override
	public boolean isOvertoneUpdateEvent() {
		return true;
	}

	public double[] getOvertones() {
		return overtones;
	}

	public Note getNote() {
		return n;
	}

}