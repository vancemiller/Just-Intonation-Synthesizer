package synthesizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileNotFoundException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class KeyboardView implements View {
	private PropertyChangeSupport changes;
	private JFrame f;
	private KeyboardScrollPane sp;
	private Pedals pedals;

	public KeyboardView() throws FileNotFoundException {
		changes = new PropertyChangeSupport(this);
		f = new JFrame("Keyboard");
		f.setLayout(new BorderLayout());
		sp = new KeyboardScrollPane(changes);
		f.add(sp, BorderLayout.CENTER);
		f.add(new ControlPanel(changes), BorderLayout.NORTH);
		f.add(pedals = new Pedals(changes), BorderLayout.SOUTH);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

	@Override
	public void pressKey(int i) {
		sp.getKeyboardPanel().start(i);
	}

	@Override
	public void releaseKey(int i) {
		sp.getKeyboardPanel().stop(i);
	}

	@Override
	public void setSustainEnabled(boolean isSustainEnabled) {
		if (isSustainEnabled) {
			pedals.sustain.setText("release");
		} else {
			pedals.sustain.setText("sustain");
		}
		sp.getKeyboardPanel().setSustainEnabled(isSustainEnabled);
		// property change fires from KeyboardPanel method.
	}

	@Override
	public void setSostenutoState(Keyboard.SostenutoState sostenutoState) {
		switch (sostenutoState) {
		case ONE:
			pedals.sostenuto.setText("sostenuto");
			pedals.sostenuto.setActionCommand("sostenuto1");
			break;
		case TWO:
			pedals.sostenuto.setText("pick notes");
			pedals.sostenuto.setActionCommand("sostenuto2");
			break;
		case THREE:
			pedals.sostenuto.setText("  release  ");
			pedals.sostenuto.setActionCommand("sostenuto3");
			break;
		}
		sp.getKeyboardPanel().setSostenutoState(sostenutoState);
		// property change fires from KeyboardPanel method.
	}

	@Override
	public String toString() {
		return "Keyboard View";
	}
}

class ControlPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1951726034463130455L;
	private PropertyChangeSupport changes;
	private JComboBox<Instrument> instruments;
	private JComboBox<Note.Root> root;
	private JComboBox<Keyboard.Mode> modes;
	protected JButton changeMode, changeInstrument, changeRoot, stop;

	private Keyboard.Mode selectedMode;
	private Instrument selectedInstrument;
	private Note.Root selectedRoot;

	public ControlPanel(PropertyChangeSupport changes)
			throws FileNotFoundException {
		super();
		this.changes = changes;
		// mode selection
		add(new JLabel("Choose a mode"));
		modes = new JComboBox<Keyboard.Mode>(Keyboard.Mode.values());
		add(modes);
		selectedMode = modes.getItemAt(0);
		changeMode = new JButton("change mode");
		changeMode.setActionCommand("changeMode");
		changeMode.addActionListener(this);
		add(changeMode);
		// instrument selection
		add(new JLabel("Choose an instrument"));
		instruments = new JComboBox<Instrument>(FileHelper.getInstruments());
		add(instruments);
		selectedInstrument = instruments.getItemAt(0);
		changeInstrument = new JButton("change instrument");
		changeInstrument.setActionCommand("changeInstrument");
		changeInstrument.addActionListener(this);
		add(changeInstrument);
		// key selection
		add(new JLabel("Choose a root:"));
		root = new JComboBox<Note.Root>(Note.Root.values());
		add(root);
		selectedRoot = root.getItemAt(0);
		changeRoot = new JButton("change root");
		changeRoot.setActionCommand("changeRoot");
		changeRoot.addActionListener(this);
		add(changeRoot);
		// stop all
		stop = new JButton("stop");
		stop.setActionCommand("stop");
		stop.addActionListener(this);
		add(stop);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		switch (action) {
		case "changeMode":
			Keyboard.Mode oldMode = selectedMode;
			selectedMode = (Keyboard.Mode) modes.getSelectedItem();
			changes.firePropertyChange("mode", oldMode, selectedMode);
		case "changeInstrument":
			Instrument oldInstrument = selectedInstrument;
			selectedInstrument = (Instrument) instruments.getSelectedItem();
			changes.firePropertyChange("instrument", oldInstrument,
					selectedInstrument);
			break;
		case "changeRoot":
			Note.Root oldRoot = selectedRoot;
			selectedRoot = (Note.Root) root.getSelectedItem();
			changes.firePropertyChange("root", oldRoot, selectedRoot);
			break;
		case "stop":
			changes.firePropertyChange("stopAll", null, null);
			break;
		default:
			break;
		}
	}

}

class KeyboardScrollPane extends JScrollPane {

	private static final long serialVersionUID = 2594826271989475281L;
	private KeyboardPanel keyboardPanel;

	protected KeyboardScrollPane(PropertyChangeSupport changes) {
		super(new KeyboardPanel(changes),
				ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		keyboardPanel = (KeyboardPanel) super.getViewport().getView();
		super.getViewport().setViewPosition(new Point(1000, 0));
		super.setWheelScrollingEnabled(true);
		super.getHorizontalScrollBar().setUnitIncrement(16);
	}

	protected KeyboardPanel getKeyboardPanel() {
		return keyboardPanel;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1000, 220);
	}
}

class KeyboardPanel extends JLayeredPane {

	private static final long serialVersionUID = -1500503807487396563L;
	private PropertyChangeSupport changes;
	private Key[] keys;
	private boolean isSustainEnabled;
	private Keyboard.SostenutoState sostenutoState;

	protected KeyboardPanel(PropertyChangeSupport changes) {
		this.changes = changes;
		keys = new Key[Note.Pitch.values().length];
		isSustainEnabled = false;
		sostenutoState = Keyboard.SostenutoState.ONE;
		int i = 0;
		for (Note.Pitch p : Note.Pitch.values()) {
			keys[i] = new Key(p, changes, this);
			if (p.isAccidental()) {
				add(keys[i], 1, -1);
			} else {
				add(keys[i], 0, -1);
			}
			i++;
		}
	}

	protected void setSustainEnabled(boolean isSustainEnabled) {
		boolean old = this.isSustainEnabled;
		this.isSustainEnabled = isSustainEnabled;
		if (!isSustainEnabled) {
			for (Key k : keys) {
				k.setIsSustaining(false);
			}
		}
		changes.firePropertyChange("isSustainEnabled", old, isSustainEnabled);
	}

	protected boolean isSustainEnabled() {
		return isSustainEnabled;
	}

	protected void setSostenutoState(Keyboard.SostenutoState sostenutoState) {
		Keyboard.SostenutoState old = this.sostenutoState;
		this.sostenutoState = sostenutoState;
		if (sostenutoState.equals(Keyboard.SostenutoState.ONE)) {
			if (isSustainEnabled) {
				for (Key k : keys) {
					k.setIsSustaining(true);
				}
			}
			for (Key k : keys) {
				k.setIsSostenuto(false);
			}
		}
		changes.firePropertyChange("sostenutoState", old, sostenutoState);
	}

	protected Keyboard.SostenutoState getSostenutoState() {
		return sostenutoState;
	}

	protected void start(int i) {
		keys[i].start();
	}

	protected void stop(int i) {
		keys[i].stop();
	}

	protected void stopAll() {
		for (Key k : keys) {
			if (k.isPressed()) {
				k.setIsSostenuto(false);
				k.setIsSustaining(false);
				k.stop();
			}
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(50 * 63, 200);
	}

	@Override
	public String toString() {
		return "KeyboardPanel";
	}
}

class Pedals extends JPanel implements ActionListener, ChangeListener {

	private static final long serialVersionUID = -913207215974354521L;
	private PropertyChangeSupport changes;
	private JSlider gain;
	protected JButton sostenuto, sustain;

	private float selectedGain;

	public Pedals(PropertyChangeSupport changes) {
		super();
		this.changes = changes;
		// gain slider (soft pedal)
		add(new JLabel("Set the gain:"));
		gain = new JSlider(-40, 6, 0);
		gain.addChangeListener(this);
		add(gain);
		selectedGain = gain.getValue();
		// sostenuto
		sostenuto = new JButton("sostenuto");
		sostenuto.setActionCommand("sostenuto1");
		sostenuto.addActionListener(this);
		add(sostenuto);
		// sustain or release all
		sustain = new JButton("sustain");
		sustain.setActionCommand("sustain");
		sustain.addActionListener(this);
		add(sustain);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();
		switch (action) {
		case "sostenuto1":
			changes.firePropertyChange("sostenutoState",
					Keyboard.SostenutoState.ONE, Keyboard.SostenutoState.TWO);
			break;
		case "sostenuto2":
			changes.firePropertyChange("sostenutoState",
					Keyboard.SostenutoState.TWO, Keyboard.SostenutoState.THREE);
			break;
		case "sostenuto3":
			changes.firePropertyChange("sostenutoState",
					Keyboard.SostenutoState.THREE, Keyboard.SostenutoState.ONE);
			break;
		case "sustain":
			changes.firePropertyChange("sustain",
					!sustain.getText().equals("sustain"), sustain.getText()
							.equals("sustain"));
			break;
		default:
			break;
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource().equals(gain)) {
			if (!gain.getValueIsAdjusting()) {
				float oldGain = selectedGain;
				selectedGain = gain.getValue();
				changes.firePropertyChange("gain", oldGain, selectedGain);
			}
		}
	}
}

class Key extends JButton implements MouseListener {

	private static final long serialVersionUID = -607879188699121465L;
	// for the gross key listener thing
	private static Key lastPressed;
	private static boolean wasPreviouslyClicked;

	private PropertyChangeSupport changes;
	private Note.Pitch p;
	private boolean isPressed;
	private boolean isSustaining, isSostenuto;
	private KeyboardPanel keyboard;

	public Key(Note.Pitch p, PropertyChangeSupport changes,
			KeyboardPanel keyboard) {
		super(p.toString());
		// set up fields
		this.p = p;
		this.changes = changes;
		this.keyboard = keyboard;
		isPressed = false;
		isSostenuto = false;
		isSustaining = false;
		// button-specific setup
		setBorder(BorderFactory.createRaisedBevelBorder());
		if (p.isAccidental()) {
			setBackground(Color.BLACK);
			setForeground(Color.WHITE);
			setSize(30, 150);
			setLocation(-15 + p.getLocationOnKeyboard() * 50, 0);
		} else {
			setBackground(Color.WHITE);
			setSize(50, 200);
			setLocation(p.getLocationOnKeyboard() * 50, 0);
		}
		setContentAreaFilled(false);
		setOpaque(true);
		setFocusable(false);
		addMouseListener(this);
	}

	public void setIsSustaining(boolean isSustaining) {
		boolean old = this.isSustaining;
		this.isSustaining = isSustaining;
		if (isPressed && !isSustaining && !isSostenuto) {
			stop();
		}
		changes.firePropertyChange("isSustaining", old, isSustaining);
	}

	public void setIsSostenuto(boolean isSostenuto) {
		boolean old = this.isSostenuto;
		this.isSostenuto = isSostenuto;
		if (isPressed && !isSustaining && !isSostenuto) {
			stop();
		}
		changes.firePropertyChange("isSostenuto", old, isSostenuto);
	}

	protected void start() {
		// PRE: isPressed == false
		if (p.isAccidental()) {
			setBackground(Color.DARK_GRAY);
		} else {
			setBackground(Color.LIGHT_GRAY);
		}
		isPressed = true;
	}

	protected void stop() {
		// PRE: isPressed == true
		if (p.isAccidental()) {
			setBackground(Color.BLACK);
		} else {
			setBackground(Color.WHITE);
		}
		isPressed = false;
	}

	protected boolean isPressed() {
		return isPressed;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		boolean isSustainEnabled = keyboard.isSustainEnabled();
		Keyboard.SostenutoState sostenutoState = keyboard.getSostenutoState();
		if (isSustainEnabled
				&& sostenutoState.equals(Keyboard.SostenutoState.TWO)) {
			if (!isPressed) {
				setIsSustaining(true);
				setIsSostenuto(true);
				start();
				changes.firePropertyChange("start", null, p);
			} else {
				setIsSustaining(false);
				setIsSostenuto(false);
				stop();
				changes.firePropertyChange("stop", p, null);
			}
		} else if (isSustainEnabled) {
			if (!isPressed) {
				setIsSustaining(true);
				start();
				changes.firePropertyChange("start", null, p);
			} else {
				setIsSustaining(false);
				if (!isSostenuto) {
					stop();
					changes.firePropertyChange("stop", p, null);
				}
			}
		} else if (sostenutoState.equals(Keyboard.SostenutoState.TWO)) {
			if (!isPressed) {
				setIsSostenuto(true);
				start();
				changes.firePropertyChange("start", null, p);
			} else {
				setIsSostenuto(false);
				stop();
				changes.firePropertyChange("stop", p, null);
			}
		} else {
			if (!isPressed) {
				start();
				changes.firePropertyChange("start", null, p);
			} else if (isSostenuto) {
				stop();
				changes.firePropertyChange("stop", p, null);
				try {
					Thread.sleep(30);
				} catch (InterruptedException e1) {

				}
				start();
				changes.firePropertyChange("start", null, p);
			}
		}
		lastPressed = this;
		wasPreviouslyClicked = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		boolean isSustainEnabled = keyboard.isSustainEnabled();
		Keyboard.SostenutoState sostenutoState = keyboard.getSostenutoState();
		if (this != lastPressed) {
			if (!lastPressed.isSostenuto && !lastPressed.isSustaining) {
				lastPressed.stop();
				changes.firePropertyChange("stop", lastPressed.p, null);
			}
		} else if (isSustainEnabled) {

		} else if (sostenutoState.equals(Keyboard.SostenutoState.TWO)) {

		} else if (isSostenuto
				&& sostenutoState.equals(Keyboard.SostenutoState.THREE)) {

		} else {
			stop();
			changes.firePropertyChange("stop", p, null);
		}
		wasPreviouslyClicked = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e) && wasPreviouslyClicked) {
			boolean isSustainEnabled = keyboard.isSustainEnabled();
			Keyboard.SostenutoState sostenutoState = keyboard
					.getSostenutoState();
			if (isSustainEnabled
					&& sostenutoState.equals(Keyboard.SostenutoState.TWO)) {
				if (!isPressed) {
					setIsSustaining(true);
					setIsSostenuto(true);
					start();
					changes.firePropertyChange("start", null, p);
					lastPressed = this;
				} else {
					setIsSustaining(false);
					setIsSostenuto(false);
					stop();
					changes.firePropertyChange("stop", p, null);
				}
			} else if (isSustainEnabled) {
				if (!isPressed) {
					setIsSustaining(true);
					start();
					changes.firePropertyChange("start", null, p);
					lastPressed = this;
				} else {
					setIsSustaining(false);
					if (!isSostenuto) {
						stop();
						changes.firePropertyChange("stop", p, null);
					}
				}
			} else if (sostenutoState.equals(Keyboard.SostenutoState.TWO)) {
				if (!isPressed) {
					setIsSostenuto(true);
					start();
					changes.firePropertyChange("start", null, p);
					lastPressed = this;
				} else {
					setIsSostenuto(false);
					stop();
					changes.firePropertyChange("stop", p, null);
				}
			} else {
				if (!isPressed) {
					start();
					changes.firePropertyChange("start", null, p);
					lastPressed = this;
				} else if (isSostenuto) {
					stop();
					changes.firePropertyChange("stop", p, null);
					start();
					changes.firePropertyChange("start", null, p);
				}
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (!isSustaining && !isSostenuto
				&& SwingUtilities.isLeftMouseButton(e) && wasPreviouslyClicked) {
			stop();
			changes.firePropertyChange("stop", p, null);
		}
	}

	@Override
	public String toString() {
		return "Key " + p.toString();
	}
}
