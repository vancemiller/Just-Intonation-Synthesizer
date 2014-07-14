package testing;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

public class Main {
	public static final int SAMPLE_RATE = 64000; // Hz
	public static final AudioFormat format = new AudioFormat(SAMPLE_RATE, 16,
			1, true, true);

	public static void main(String[] args) throws InterruptedException {
		List<Note> nl = new ArrayList<Note>();
		nl.add(new NoteImpl(Note.Pitch.A, 2, Note.Interval.UNISON));
		nl.add(new NoteImpl(Note.Pitch.A, 3, Note.Interval.UNISON));
		nl.add(new NoteImpl(Note.Pitch.A, 3, Note.Interval.MAJOR_THIRD));
		nl.add(new NoteImpl(Note.Pitch.A, 3, Note.Interval.PERFECT_FIFTH));
		nl.add(new NoteImpl(Note.Pitch.A, 3, Note.Interval.MAJOR_SEVENTH));
		nl.add(new NoteImpl(Note.Pitch.A, 4, Note.Interval.MAJOR_TONE));
		nl.add(new NoteImpl(Note.Pitch.A, 4, Note.Interval.DIMINISHED_FIFTH));
		nl.add(new NoteImpl(Note.Pitch.A, 4, Note.Interval.MAJOR_SIXTH));
		for (Note n : nl)
			n.start();
		Thread.sleep(1000);
		System.out.println("stop");
		for (Note n : nl)
			n.stop();
	}
}