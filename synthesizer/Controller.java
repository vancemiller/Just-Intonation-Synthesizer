package synthesizer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Controller implements PropertyChangeListener {
	private KeyboardView view;
	private KeyboardImpl model;

	public Controller(KeyboardView view, KeyboardImpl model) {
		this.view = view;
		this.model = model;
		model.addPropertyChangeListener(this);
		view.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println(evt.getPropertyName());
		if (evt.getSource() instanceof KeyboardView) {
			String event = evt.getPropertyName();
			if (event == "KeyPressed") {
				model.startNote((Note.Pitch) evt.getNewValue());
			} else if (event == "KeyReleased") {
				model.stopNote((Note.Pitch) evt.getOldValue());
			} else if (event == "keyChanged") {
				model.setKey((Note.Key) evt.getOldValue());
			} else if (event == "stop") {
				view.stopAll();
				model.stopAll();
			} else if (event == "instrumentChanged") {
				model.setInstrument((String) evt.getNewValue());
			}
			// handle view event
		} else if (evt.getSource() instanceof KeyboardImpl) {
			// handle model event
		}

	}

}
