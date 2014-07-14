package testing;
//package v7;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.LineUnavailableException;
//import javax.sound.sampled.SourceDataLine;
//
//
//public class Model {
//	
//}
//
//
//
//class Chord {
//	Pitch root;
//	int octave;
//	List<Note> notes;
//
//	public enum BasicType {
//		TRIAD, SEVENTH;
//	}
//
//	public Chord(Note.Pitch root, int octave) {
//		this.root = root;
//		this.notes = new java.util.ArrayList<Note>();
//		this.notes.add(new Note(root, octave, Note.Interval.UNISON));
//		this.octave = octave;
//	}
//
//	public void addNote(Note.Interval interval, int octave) {
//		notes.add(new Note(root, octave, interval));
//	}
//
//	public void addNote(Note.Interval interval) {
//		notes.add(new Note(this.root, interval.halfStepsFromRoot < 12 ? octave
//				: octave + interval.halfStepsFromRoot / 12, interval));
//	}
//
//	public void removeNote(int index) {
//		notes.remove(index);
//	}
//
//	public void reset() {
//		notes.removeAll(notes);
//		notes.add(new Note(root, octave, Note.Interval.UNISON));
//	}
//
//	public void invert() {
//		Note tmp = notes.remove(0);
//		notes.add(new Note(tmp.getRootPitch(), tmp.getOctave() + 1, tmp
//				.getInterval()));
//	}
//
//	public void play(int seconds, double volume) {
//		List<Runnable> notesToPlay = new ArrayList<Runnable>();
//		for (Note n : notes) {
//			notesToPlay.add(n.getRunnablePlay(seconds, volume));
//		}
//		for (Runnable r : notesToPlay)
//			new Thread(r).start();
//	}
//}
//
//class Triad extends Chord {
//	private Type type;
//
//	public enum Type {
//		MAJOR, MINOR, AUGMENTED, DIMINISHED;
//	}
//
//	public enum Inversion {
//		ROOT, FIRST, SECOND;
//	}
//
//	public Triad(Pitch pitch, int octave, Type type) {
//		super(pitch, octave);
//		this.type = type;
//		this.buildChord();
//	}
//
//	private void buildChord() {
//		switch (this.type) {
//		case MAJOR:
//			addNote(Note.Interval.MAJOR_THIRD);
//			addNote(Note.Interval.PERFECT_FIFTH);
//			break;
//		case MINOR:
//			addNote(Note.Interval.MINOR_THIRD);
//			addNote(Note.Interval.PERFECT_FIFTH);
//			break;
//		case AUGMENTED:
//			addNote(Note.Interval.MAJOR_THIRD);
//			addNote(Note.Interval.MINOR_SIXTH);
//			break;
//		case DIMINISHED:
//			addNote(Note.Interval.MINOR_THIRD);
//			addNote(Note.Interval.DIMINISHED_FIFTH);
//			break;
//		default:
//			System.out.println("fail");
//		}
//	}
//
//	void rebuildTriad() {
//		buildChord();
//	}
//
//	public void invert(Inversion inversion) {
//		reset();
//		buildChord();
//		switch (inversion) {
//		case SECOND:
//			notes.set(1, new Note(root, notes.get(0).getOctave() + 1, notes
//					.get(1).getInterval()));
//		case FIRST:
//			notes.set(0, new Note(root, octave + 1, Note.Interval.UNISON));
//		case ROOT:
//		default:
//			break;
//		}
//	}
//
//}
//
//class SeventhChord extends Triad {
//	private Type type;
//
//	public enum Type {
//		DIMINISHED(Triad.Type.DIMINISHED), HALF_DIMINISHED(
//				Triad.Type.DIMINISHED), MINOR(Triad.Type.MINOR), MINOR_MAJOR(
//				Triad.Type.MINOR), DOMINANT(Triad.Type.MAJOR), MAJOR(
//				Triad.Type.MAJOR), AUGMENTED(Triad.Type.AUGMENTED), AUGMENTED_MAJOR(
//				Triad.Type.AUGMENTED);
//
//		public final Triad.Type triad;
//
//		Type(Triad.Type triad) {
//			this.triad = triad;
//		}
//	}
//
//	public enum Inversion {
//		ROOT, FIRST, SECOND, THIRD;
//	}
//
//	public SeventhChord(Pitch pitch, int octave, Type type) {
//		super(pitch, octave, type.triad);
//		this.type = type;
//		buildChord();
//	}
//
//	private void buildChord() {
//		switch (this.type) {
//		case DIMINISHED:
//			this.addNote(Note.Interval.MAJOR_SIXTH);
//			break;
//		case AUGMENTED:
//		case DOMINANT:
//		case HALF_DIMINISHED:
//		case MINOR:
//			this.addNote(Note.Interval.GRAVE_MINOR_SEVENTH);
//			break;
//		case AUGMENTED_MAJOR:
//		case MAJOR:
//		case MINOR_MAJOR:
//			this.addNote(Note.Interval.MAJOR_SEVENTH);
//		default:
//			break;
//		}
//	}
//
//	void rebuildSeventhChord() {
//		rebuildTriad();
//		buildChord();
//	}
//
//	public void invert(Inversion inversion) {
//		reset();
//		rebuildSeventhChord();
//		buildChord();
//		switch (inversion) {
//		case THIRD:
//			notes.set(2, new Note(root, notes.get(2).getOctave() + 1, notes
//					.get(2).getInterval()));
//		case SECOND:
//			notes.set(1, new Note(root, notes.get(1).getOctave() + 1, notes
//					.get(1).getInterval()));
//		case FIRST:
//			notes.set(0, new Note(root, octave + 1, Note.Interval.UNISON));
//		case ROOT:
//		default:
//			break;
//		}
//	}
//}
//
//class ExtendedChord extends SeventhChord {
//	private Type type;
//
//	public enum Type {
//		DOMINANT_NINTH(SeventhChord.Type.DOMINANT), DOMINANT_ELEVENTH(
//				SeventhChord.Type.DOMINANT), DOMINANT_THIRTEENTH(
//				SeventhChord.Type.DOMINANT);
//
//		public final SeventhChord.Type seventhChord;
//
//		Type(SeventhChord.Type seventhChord) {
//			this.seventhChord = seventhChord;
//		}
//	}
//
//	public enum Inversion {
//		ROOT, FIRST, SECOND, THIRD, FOURTH;
//	}
//
//	public ExtendedChord(Pitch pitch, int octave, Type type) {
//		super(pitch, octave, type.seventhChord);
//		this.type = type;
//		buildChord();
//	}
//
//	private void buildChord() {
//		switch (type) {
//		case DOMINANT_ELEVENTH:
//			this.removeNote(1); // omit the third
//			this.addNote(Note.Interval.PERFECT_FOURTH, octave + 1);
//			break;
//		case DOMINANT_NINTH:
//			this.addNote(Note.Interval.MAJOR_TONE, octave + 1);
//			break;
//		case DOMINANT_THIRTEENTH:
//			this.addNote(Note.Interval.MAJOR_SIXTH, octave + 1);
//			break;
//		default:
//			break;
//
//		}
//	}
//
//	void rebuildExtendedChord() {
//		rebuildSeventhChord();
//		buildChord();
//	}
//
//	public void invert(Inversion inversion) {
//		reset();
//		rebuildExtendedChord();
//		switch (inversion) {
//		case FOURTH:
//			notes.set(3, new Note(root, notes.get(0).getOctave() + 1, notes
//					.get(0).getInterval()));
//		default:
//			super.invert();
//		}
//	}
//
//}
