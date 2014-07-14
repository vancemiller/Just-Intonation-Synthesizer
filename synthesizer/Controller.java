package synthesizer;

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
		System.out.printf("%10.10s: %8.8s changed from %8.8s to %8.8s\n", evt
				.getSource().toString(), evt.getPropertyName(), evt
				.getOldValue(), evt.getNewValue());
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
				// view.setIsSustaining(false);
				// view.setSostenutoState(Keyboard.SostenutoState.ONE);
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
				boolean sustain = (boolean) evt.getNewValue();
				// view.setIsSustaining(sustain);
				model.setIsSustaining(sustain);
				break;
			case "gain":
				model.setGain((float) evt.getNewValue());
				break;
			case "sostenutoState":
				Keyboard.SostenutoState sostenutoState = (Keyboard.SostenutoState) evt
						.getNewValue();
				// view.setSostenutoState(sostenutoState);
				model.setSostenutoState(sostenutoState);
				break;
			case "mode":
				model.setMode((Keyboard.Mode) evt.getNewValue());
				break;
			default:
				System.out.println("   ...and was not handled.");
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
				// view.setRoot((Note.Root) evt.getNewValue());
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
			case "mode":
				// view.setMode((Keyboard.Mode) evt.getNewValue());
				break;
			default:
				System.out.println("   ...and was not handled.");
				break;
			}
		}

	}

}
