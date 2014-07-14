package instrumentEditor;

import helpers.FileHelper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSlider;

import synthsizer.Instrument;
import synthsizer.InstrumentImpl;

public class Controller implements PropertyChangeListener {
	private final InstrumentView view;
	private int currentlySelected = -1;
	private boolean creatingNewInstrument;
	private String newInstrumentName;

	public Controller(InstrumentView view) {
		this.view = view;
		view.addPropertyChangeListener(this);
		try {
			view.setInstruments(FileHelper.getInstruments());
		} catch (FileNotFoundException e) {
			JOptionPane
					.showMessageDialog(
							null,
							"The file "
									+ FileHelper.getFileName()
									+ " is missing.\nPlease place the file in the same directory as this program and relaunch.",
							"Instrument file missing",
							JOptionPane.ERROR_MESSAGE);
		}
	}

	private void handleListSelectionChange(int newValue) {
		currentlySelected = newValue;
		view.selectInstrument(newValue);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof JButton) {
			switch (evt.getPropertyName()) {
			case "delete":
				if (creatingNewInstrument) {
					creatingNewInstrument = false;
					view.resetLevels();
					view.createInstrument("");
				} else if (currentlySelected == -1) {

				} else {
					try {
						FileHelper.removeLine(currentlySelected);
						view.removeInstrument(currentlySelected);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(view,
								"Error deleting instrument.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				break;
			case "new":
				newInstrumentName = JOptionPane.showInputDialog(
						"Instrument name:", "New Instrument");
				creatingNewInstrument = true;
				view.resetLevels();
				view.createInstrument(newInstrumentName);
				break;
			case "save":
				if (creatingNewInstrument) {
					Instrument i = new InstrumentImpl(newInstrumentName,
							view.getLevels());
					try {
						FileHelper.writeNewInstrument(i);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(view,
								"Error creating instrument.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
					view.addInstrument(i);
					view.selectInstrument(-1);
					creatingNewInstrument = false;
				} else {
					Instrument i = new InstrumentImpl(view
							.getCurrentInstrument().getName(), view.getLevels());
					try {
						FileHelper.remove(i);
						FileHelper.writeNewInstrument(i);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(view,
								"Error saving instrument.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
					view.removeInstrument(view.getCurrentInstrument());
					view.addInstrument(i);
					view.selectInstrument(-1);
				}
				break;
			case "raise":
				view.raiseLevels();
				break;
			case "lower":
				view.lowerLevels();
				break;
			default:
				break;
			}
		} else if (evt.getSource() instanceof JList) {
			handleListSelectionChange((int) evt.getNewValue());
		} else if (evt.getSource() instanceof JSlider) {

		}
	}
}
