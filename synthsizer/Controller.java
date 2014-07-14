package synthsizer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Controller implements PropertyChangeListener {
	private View view;
	private Model model;

	public Controller(View view, Model model) {
		this.view = view;
		this.model = model;
		model.addPropertyChangeListener(this);
		view.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof View) {
			// handle view event
			switch (evt.getPropertyName()) {
			case "start":
				model.startNote((Note.Pitch) evt.getNewValue());
				break;
			case "stop":
				model.stopNote((Note.Pitch) evt.getOldValue());
				break;
			case "stopAll":
				model.setIsSustaining(false);
				model.setSostenutoState(Keyboard.SostenutoState.ONE);
				break;
			case "root":
				model.setRoot((Note.Root) evt.getNewValue());
				break;
			case "instrument":
				model.setInstrument((Instrument) evt.getNewValue());
				break;
			case "sustain":
				model.setIsSustaining((boolean) evt.getNewValue());
				break;
			case "gain":
				model.setGain((float) evt.getNewValue());
				break;
			case "sostenutoState":
				model.setSostenutoState((Keyboard.SostenutoState) evt
						.getNewValue());
				break;
			case "chordType":
				model.setChordType((Chord.Type) evt.getNewValue());
				break;
			default:
				System.out.printf("%10.10s: %8.8s changed from %8.8s to %8.8s and was not handled.\n", evt
						.getSource().toString(), evt.getPropertyName(), evt
						.getOldValue(), evt.getNewValue());
				break;
			}
		} else if (evt.getSource() instanceof Model) {
			switch (evt.getPropertyName()) {
			case "start":
				view.pressKey((int) evt.getNewValue());
				break;
			case "stop":
				view.releaseKey((int) evt.getNewValue());
				break;
			case "stopAll":
				view.setSustainEnabled(false);
				view.setSostenutoState(Keyboard.SostenutoState.ONE);
				break;
			case "root":
				view.setRoot((Note.Root) evt.getNewValue());
				break;
			case "instrument":
				// view.setInstrument((Instrument) evt.getNewValue());
				break;
			case "sustain":
				view.setSustainEnabled((boolean) evt.getNewValue());
				break;
			case "gain":
				// view.setGain((float) evt.getNewValue());
				break;
			case "sostenuto":
				view.setSostenutoState((Keyboard.SostenutoState) evt
						.getNewValue());
				break;
			case "chordType":
				view.setChordType((Chord.Type) evt.getNewValue());
				break;
			default:
				System.out.printf("%10.10s: %8.8s changed from %8.8s to %8.8s and was not handled.\n", evt
						.getSource().toString(), evt.getPropertyName(), evt
						.getOldValue(), evt.getNewValue());
				break;
			}
		}

	}

}
