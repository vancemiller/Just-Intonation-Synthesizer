package synthesizer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class InstrumentView extends JFrame implements ActionListener {
	public static void main(String[] args) throws InterruptedException,
			FileNotFoundException {
		InstrumentView.showInstrumentView();
	}

	private static final long serialVersionUID = -2534056121367029323L;
	private static InstrumentView singleton;

	private final DefaultListModel<Instrument> model;
	private JList<Instrument> instruments;
	private SliderPanel sliders;
	private String newInstrumentName;
	private boolean isValidNewInstrument = false;

	public static void showInstrumentView() throws FileNotFoundException {
		if (singleton == null) {
			singleton = new InstrumentView();
		}
		singleton.setVisible(true);
	}

	private InstrumentView() throws FileNotFoundException {
		super("Instrument Editor");
		setLayout(new BorderLayout());

		model = new DefaultListModel<Instrument>();
		for (Instrument i : FileHelper.getInstruments()) {
			model.addElement(i);
		}
		add(new JScrollPane(instruments = new JList<Instrument>(model),
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				BorderLayout.WEST);
		add(sliders = new SliderPanel(), BorderLayout.CENTER);
		add(new Buttons(this), BorderLayout.SOUTH);
		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "select":
			isValidNewInstrument = false;
			int[] amplitudes = instruments.getSelectedValue().getAmplitudes();
			for (int i = 0; i < amplitudes.length; i++) {
				sliders.sliders[i].setValue(amplitudes[i]);
			}
			break;
		case "new":
			isValidNewInstrument = true;
			newInstrumentName = JOptionPane.showInputDialog(this,
					"Instrument name:");
			sliders.setCurrentInstrument(newInstrumentName);
			for (JSlider s : sliders.sliders) {
				s.setValue(0);
			}
			break;
		case "save":
			int[] newAmplitudes = new int[Note.NUM_OVERTONES];
			for (int i = 0; i < newAmplitudes.length; i++) {
				newAmplitudes[i] = sliders.sliders[i].getValue();
			}
			Instrument newInstrument;
			if (isValidNewInstrument) {
				try {
					FileHelper
							.writeNewInstrument(newInstrument = new InstrumentImpl(
									newInstrumentName, newAmplitudes));
					model.addElement(newInstrument);
					instruments.revalidate();
					instruments.repaint();
				} catch (FileNotFoundException e1) {

				} catch (IOException e2) {

				}
				isValidNewInstrument = false;
			} else {
				try {

					FileHelper.remove(instruments.getSelectedValue());
					FileHelper
							.writeNewInstrument(newInstrument = new InstrumentImpl(
									instruments.getSelectedValue().getName(),
									newAmplitudes));
				} catch (IOException e1) {

				}
			}
			break;
		case "delete":
			isValidNewInstrument = false;
			int index = instruments.getSelectedIndex();
			try {
				FileHelper.remove(model.get(index));
			} catch (IOException e1) {

			}
			model.removeElement(model.get(index));
			instruments.setSelectedIndex(index - 1);
			break;
		}
	}
}

// class InstrumentList extends JList<Instrument> {
//
// private static final long serialVersionUID = -4451518426627793271L;
//
// public InstrumentList() {
// super();
// }
//
//
//
// @Override
// public Dimension getPreferredSize() {
// return new Dimension(100, getHeight());
// }
//
// }

class SliderPanel extends JPanel {

	private static final long serialVersionUID = 8729322721727832671L;
	protected JSlider[] sliders;
	private JLabel currentInstrument;

	public SliderPanel() {
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.EAST;
		gc.fill = GridBagConstraints.VERTICAL;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.gridwidth = 4;
		add(new JLabel("Current instrument: "), gc);
		gc.gridx = 4;
		gc.anchor = GridBagConstraints.WEST;
		add(currentInstrument = new JLabel(""), gc);
		gc.anchor = GridBagConstraints.CENTER;
		gc.gridwidth = 1;
		sliders = new JSlider[Note.NUM_OVERTONES];
		for (int i = 0; i < sliders.length; i++) {
			sliders[i] = new JSlider(SwingConstants.VERTICAL, 0,
					Short.MAX_VALUE, 10000);
			sliders[i].setPaintTicks(true);
			sliders[i].setMajorTickSpacing(1000);
			gc.gridheight = 4;
			gc.gridy = 1;
			gc.gridx = i;
			add(sliders[i], gc);
			gc.gridheight = 1;
			gc.gridy += 4;
			add(new JLabel(" " + i + " "), gc);
		}
	}

	protected void setCurrentInstrument(String currentInstrument) {
		this.currentInstrument.setText(currentInstrument);
		revalidate();
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(300, 300);
	}
}

class Buttons extends JPanel {

	private static final long serialVersionUID = -6230887227067891985L;
	JButton[] buttons;

	public Buttons(InstrumentView iv) {
		buttons = new JButton[4];
		GridBagConstraints gc = new GridBagConstraints();
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new JButton();
			add(buttons[i], gc);
			gc.gridx++;
		}
		buildButton(buttons[0], "select", iv);
		buildButton(buttons[1], "delete", iv);
		buildButton(buttons[2], "new", iv);
		buildButton(buttons[3], "save", iv);
	}

	private void buildButton(JButton button, String text,
			ActionListener listener) {
		button.setText(text);
		button.setActionCommand(text);
		button.addActionListener(listener);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(300, 50);
	}

}