package testing;

import java.util.Observable;
import java.util.Observer;

public class Controller implements Observer {
	private Chord c;

	@Override
	public void update(Observable o, Object arg) {

		if (o instanceof View) {

			Event e = (Event) arg;
			if (e.isAddEvent()) {
				AddEvent ae = ((AddEvent) e);
				if (c == null) {
					c = new Chord(ae.getPitch(), ae.getOctave());
					Note n = new Note(ae.getPitch(), ae.getOctave(),
							ae.getInterval());
					e.getNoteStack().addNote(n);
				} else {
					Note n = new Note(ae.getPitch(), ae.getOctave(),
							ae.getInterval());
					c.addNote(n);
					e.getNoteStack().addNote(n);
				}

			} else if (e.isPlayEvent()) {
				if (c == null)
					return;
				c.play(((PlayEvent) e).getDuration(), 1);
			} else if (e.isRemoveEvent()) {
				if (c == null)
					return;
				e.getNoteStack().removeLast();
				c.removeNote(-1);
			} else if (e.isOvertoneUpdateEvent()) {
				OvertoneUpdateEvent oe = (OvertoneUpdateEvent) e;
				for (int i = 0; i < Note.NUM_OVERTONES; i++) {
					
					oe.getNote().setOvertoneVolume(i, oe.getOvertones()[i]);
				}
			}
		}

	}

}
