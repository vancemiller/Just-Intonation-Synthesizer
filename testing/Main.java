package testing;

import java.util.ArrayList;
import java.util.List;

import testing.Note.Pitch;



public class Main {
	public static void main(String[] args) throws InterruptedException {

		List<Chord> progression = new ArrayList<Chord>();
		progression.add(new Triad(Pitch.D, 2, Triad.Type.MAJOR));
		progression.add(new Triad(Pitch.A, 2, Triad.Type.MAJOR));
		progression.add(new Triad(Pitch.B, 2, Triad.Type.MINOR));
		progression.add(new Triad(Pitch.Gb, 1, Triad.Type.MINOR));
		progression.add(new Triad(Pitch.G, 1, Triad.Type.MAJOR));
		progression.add(new Triad(Pitch.D, 1, Triad.Type.MAJOR));
		progression.add(new Triad(Pitch.G, 1, Triad.Type.MAJOR));
		progression.add(new Triad(Pitch.A, 2, Triad.Type.MAJOR));

		progression.add(new Triad(Pitch.D, 2, Triad.Type.MAJOR));
		progression.add(new SeventhChord(Pitch.B, 2, SeventhChord.Type.MINOR));
		progression.get(progression.size() - 1).invert();
		progression.add(new Triad(Pitch.G, 1, Triad.Type.MAJOR));
		progression.get(progression.size() - 1).invert();
		progression.get(progression.size() - 1).invert();
		progression.add(new Triad(Pitch.A, 2, Triad.Type.MAJOR));
		progression.get(progression.size() - 1).invert();
		progression.get(progression.size() - 1).invert();
		progression.add(new Triad(Pitch.D, 2, Triad.Type.MAJOR));
		progression.get(progression.size() - 1).invert();
		progression.add(new SeventhChord(Pitch.B, 2, SeventhChord.Type.MINOR));
		progression.get(progression.size() - 1).invert();
		progression.get(progression.size() - 1).invert();
		progression.get(progression.size() - 1).invert();
		progression.add(new Triad(Pitch.G, 2, Triad.Type.MAJOR));
		progression.get(progression.size() - 1).invert();
		progression.add(new Triad(Pitch.A, 3, Triad.Type.MAJOR));
		progression.get(progression.size() - 1).invert();
		// progression.add(new Triad(Pitch.D, 3, Triad.Type.MAJOR));
		// ((Triad) progression.get(progression.size() -
		// 1)).invert(Triad.Inversion.ROOT);
		progression.add(new Triad(Pitch.D, 2, Triad.Type.MAJOR));
		progression.add(new Triad(Pitch.A, 2, Triad.Type.MAJOR));
		progression.add(new Triad(Pitch.B, 2, Triad.Type.MINOR));
		progression.add(new Triad(Pitch.Gb, 1, Triad.Type.MINOR));
		progression.add(new Triad(Pitch.G, 1, Triad.Type.MAJOR));
		progression.add(new Triad(Pitch.D, 1, Triad.Type.MAJOR));
		progression.add(new Triad(Pitch.G, 1, Triad.Type.MAJOR));
		progression.add(new Triad(Pitch.A, 2, Triad.Type.MAJOR));

		for (Chord c : progression) {
			c.play(1500, 1.0);
			Thread.sleep(400);
		}		
		
		@SuppressWarnings("unused")
		View v = new View();
		
	

	}
}
