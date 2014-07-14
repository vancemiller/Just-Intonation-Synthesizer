package instrumentEditor;

import helpers.PropertyChanger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import synthsizer.Instrument;
import synthsizer.Note;

class Buttons extends JPanel implements ActionListener {

	private static final long serialVersionUID = -6230887227067891985L;
	JButton[] buttons;
	private final PropertyChangeListener l;

	public Buttons(PropertyChangeListener l) {
		buttons = new JButton[5];
		this.l = l;
		GridBagConstraints gc = new GridBagConstraints();
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new JButton();
			add(buttons[i], gc);
			gc.gridx++;
		}
		int i = 0;
		buildButton(buttons[i++], "delete");
		buildButton(buttons[i++], "new");
		buildButton(buttons[i++], "save");
		buildButton(buttons[i++], "raise");
		buildButton(buttons[i++], "lower");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		l.propertyChange(new PropertyChangeEvent(e.getSource(), e
				.getActionCommand(), false, true));
	}

	private void buildButton(JButton button, String text) {
		button.setText(text);
		button.setActionCommand(text);
		button.addActionListener(this);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(300, 50);
	}

}

public class InstrumentView extends JFrame implements PropertyChanger,
		PropertyChangeListener, ListSelectionListener {

	private static final long serialVersionUID = -2534056121367029323L;
	private static InstrumentView singleton;

	public static InstrumentView getInstrumentView() {
		if (singleton == null) {
			singleton = new InstrumentView();
		}
		singleton.setVisible(true);
		return singleton;
	}

	private final PropertyChangeSupport changes;
	private final DefaultListModel<Instrument> model;
	private final JList<Instrument> instruments;
	private final SliderPanel sliders;
	private final Buttons buttons;

	private int oldSelection = -1;

	private InstrumentView() {
		super("Instrument Editor");
		setLayout(new BorderLayout());

		changes = new PropertyChangeSupport(this);

		model = new DefaultListModel<Instrument>();
		instruments = new JList<Instrument>(model);
		instruments.addListSelectionListener(this);
		JScrollPane scroll = new JScrollPane(instruments,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setPreferredSize(new Dimension(100, 300));
		add(scroll, BorderLayout.WEST);

		sliders = new SliderPanel(this);
		add(sliders, BorderLayout.CENTER);

		buttons = new Buttons(this);
		add(buttons, BorderLayout.SOUTH);

		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	protected void addInstrument(Instrument instrument) {
		model.addElement(instrument);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	protected void createInstrument(String s) {
		sliders.setCurrentInstrument(s);
	}

	protected Instrument getCurrentInstrument() {
		return instruments.getSelectedValue();
	}

	protected int[] getLevels() {
		return sliders.getLevels();
	}

	public void lowerLevels() {
		sliders.lowerLevels();

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		changes.firePropertyChange(evt);
	}

	public void raiseLevels() {
		sliders.raiseLevels();
	}

	protected void removeInstrument(Instrument instrument) {
		model.removeElement(instrument);
	}

	protected void removeInstrument(int currentlySelected) {
		model.remove(currentlySelected);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

	protected void resetLevels() {
		sliders.resetAll();
	}

	protected void selectInstrument(int instrumentNumber) {
		try {
			Instrument instrument;
			if (instrumentNumber == -1) {
				instrument = model.get(model.size() - 1);
				instruments.setSelectedIndex(model.size() - 1);
			} else {
				instrument = model.get(instrumentNumber);
			}
			sliders.setCurrentInstrument(instrument.getName());
			sliders.setLevels(instrument.getAmplitudes());
		} catch (ArrayIndexOutOfBoundsException e) {
			sliders.setCurrentInstrument("");
			resetLevels();
		}

	}

	protected void setInstruments(Vector<Instrument> instruments) {
		model.clear();
		for (Instrument i : instruments) {
			model.addElement(i);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		propertyChange(new PropertyChangeEvent(instruments,
				"instrumentSelection", oldSelection,
				instruments.getSelectedIndex()));
		oldSelection = instruments.getSelectedIndex();
	}

}

class SliderPanel extends JPanel {

	private class Slider extends JSlider implements PropertyChanger,
			ChangeListener {

		private static final long serialVersionUID = -5634883404490528972L;
		private int oldValue = 0;
		private final int sliderNumber;

		public Slider(int sliderNumber) {
			super(SwingConstants.VERTICAL, 0, Short.MAX_VALUE / 2, 0);
			this.sliderNumber = sliderNumber;
			addChangeListener(this);
			setPaintTicks(true);
			setMajorTickSpacing(1000);
		}

		@Override
		public void addPropertyChangeListener(PropertyChangeListener l) {
			changes.addPropertyChangeListener(l);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(35, 220);
		}

		@Override
		public void removePropertyChangeListener(PropertyChangeListener l) {
			changes.removePropertyChangeListener(l);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			changes.firePropertyChange(new PropertyChangeEvent(e.getSource(),
					"slider" + sliderNumber, oldValue, getValue()));
			oldValue = getValue();
		}
	}

	private static final long serialVersionUID = 8729322721727832671L;
	protected JSlider[] sliders;
	private JLabel currentInstrument;

	private final PropertyChangeSupport changes;

	public SliderPanel(PropertyChangeListener l) {
		changes = new PropertyChangeSupport(this);

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
			sliders[i] = new Slider(i);
			sliders[i].addPropertyChangeListener(l);
			gc.gridheight = 4;
			gc.gridy = 1;
			gc.gridx = i;
			add(sliders[i], gc);
			gc.gridheight = 1;
			gc.gridy += 4;
			add(new JLabel(" " + i + " "), gc);
		}
	}

	public int[] getLevels() {
		int[] levels = new int[sliders.length];
		for (int i = 0; i < levels.length; i++) {
			levels[i] = sliders[i].getValue();
		}
		return levels;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(sliders.length * 35, 200);
	}

	public void lowerLevels() {
		for (JSlider s : sliders) {
			s.setValue(s.getValue() - 200);
		}
	}

	public void raiseLevels() {
		for (JSlider s : sliders) {
			s.setValue(s.getValue() + 200);
		}
	}

	protected void resetAll() {
		for (JSlider slider : sliders) {
			slider.setValue(0);
		}
	}

	protected void setCurrentInstrument(String currentInstrument) {
		this.currentInstrument.setText(currentInstrument);
		revalidate();
		repaint();
	}

	protected void setLevels(int[] amplitudes) {
		for (int i = 0; i < amplitudes.length; i++) {
			sliders[i].setValue(amplitudes[i]);
		}
	}

}