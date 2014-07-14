package synthsizer;

import helpers.FileHelper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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

import synthsizer.Chord.Type;
import synthsizer.Note.Root;

public class KeyboardView implements View {
	private class ControlPanel extends JPanel implements ActionListener {
		private static final long serialVersionUID = 1951726034463130455L;
		// Property changes
		private final PropertyChangeSupport changes;
		// selection lists
		private final JComboBox<Instrument> instruments;
		private final JComboBox<Note.Root> roots;
		private final JComboBox<Chord.Type> chordTypes;
		// confirmation buttons
		public JButton changeChord, changeInstrument, changeRoot, stop;
		// currently selected
		private Chord.Type selectedChordType;
		private Instrument selectedInstrument;
		private Note.Root selectedRoot;

		public ControlPanel(PropertyChangeSupport changes)
				throws FileNotFoundException {
			super();
			this.changes = changes;
			// mode selection
			add(new JLabel("Choose a chord"));
			chordTypes = new JComboBox<Chord.Type>(Chord.Type.values());
			add(chordTypes);
			selectedChordType = chordTypes.getItemAt(0);
			changeChord = new JButton("change chord");
			changeChord.setActionCommand("changeChord");
			changeChord.addActionListener(this);
			add(changeChord);
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
			roots = new JComboBox<Note.Root>(Note.Root.values());
			add(roots);
			selectedRoot = roots.getItemAt(0);
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
			switch (e.getActionCommand()) {
			case "changeChord":
				Chord.Type oldChordType = selectedChordType;
				selectedChordType = (Chord.Type) chordTypes.getSelectedItem();
				changes.firePropertyChange("chordType", oldChordType,
						selectedChordType);
			case "changeInstrument":
				Instrument oldInstrument = selectedInstrument;
				selectedInstrument = (Instrument) instruments.getSelectedItem();
				changes.firePropertyChange("instrument", oldInstrument,
						selectedInstrument);
				break;
			case "changeRoot":
				Note.Root oldRoot = selectedRoot;
				selectedRoot = (Note.Root) roots.getSelectedItem();
				changes.firePropertyChange("root", oldRoot, selectedRoot);
				break;
			case "stop":
				changes.firePropertyChange("stopAll", null, null);
				break;
			default:
				break;
			}
		}

		public void selectRoot(Root newValue) {
			roots.setSelectedItem(newValue);
			Note.Root oldRoot = selectedRoot;
			selectedRoot = newValue;
			changes.firePropertyChange("root", oldRoot, selectedRoot);
		}

		public void setChordType(Type newValue) {
			Chord.Type oldChordType = selectedChordType;
			selectedChordType = newValue;
			chordTypes.setSelectedItem(newValue);
			changes.firePropertyChange("chordType", oldChordType,
					selectedChordType);
		}

	}

	private static class Key extends JButton implements MouseListener {
		private static final long serialVersionUID = -607879188699121465L;
		// for the mouse listener
		private static Key lastPressed;
		private static boolean wasPreviouslyClicked;
		// Property changes
		private final PropertyChangeSupport changes;
		// Key properties
		private final Note.Pitch p;
		private boolean isPressed;
		private boolean isSostenuto, isSustaining;
		private final KeyboardPanel keyboard;

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

		@Override
		public void mouseClicked(MouseEvent e) {
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
					&& SwingUtilities.isLeftMouseButton(e)
					&& wasPreviouslyClicked) {
				stop();
				changes.firePropertyChange("stop", p, null);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
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
			Keyboard.SostenutoState sostenutoState = keyboard
					.getSostenutoState();
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

		public void setIsSostenuto(boolean isSostenuto) {
			boolean old = this.isSostenuto;
			this.isSostenuto = isSostenuto;
			if (isPressed && !isSustaining && !isSostenuto) {
				stop();
			}
			changes.firePropertyChange("isSostenuto", old, isSostenuto);
		}

		public void setIsSustaining(boolean isSustaining) {
			boolean old = this.isSustaining;
			this.isSustaining = isSustaining;
			if (isPressed && !isSustaining && !isSostenuto) {
				stop();
			}
			changes.firePropertyChange("isSustaining", old, isSustaining);
		}

		public void start() {
			// PRE: isPressed == false
			if (p.isAccidental()) {
				setBackground(Color.DARK_GRAY);
			} else {
				setBackground(Color.LIGHT_GRAY);
			}
			isPressed = true;
		}

		public void stop() {
			// PRE: isPressed == true
			if (p.isAccidental()) {
				setBackground(Color.BLACK);
			} else {
				setBackground(Color.WHITE);
			}
			isPressed = false;
		}

		@Override
		public String toString() {
			return "Key " + p.toString();
		}
	}

	private class KeyboardDispatcher implements KeyEventDispatcher {
		@Override
		public boolean dispatchKeyEvent(KeyEvent e) {
			if (e.getID() == KeyEvent.KEY_PRESSED) {
				switch (e.getKeyCode()) {
				// Major chords
				case KeyEvent.VK_1:
					setChordType(Chord.Type.MAJOR);
					break;
				case KeyEvent.VK_2:
					setChordType(Chord.Type.SIXTH);
					break;
				case KeyEvent.VK_3:
					setChordType(Chord.Type.MAJOR_SEVENTH);
					break;
				case KeyEvent.VK_4:
					setChordType(Chord.Type.MAJOR_NINTH);
					break;
				case KeyEvent.VK_5:
					setChordType(Chord.Type.SIXTH_NINTH);
					break;
				case KeyEvent.VK_6:
					setChordType(Chord.Type.MAJOR_THIRTEENTH);
					break;
				// Dominant chords
				case KeyEvent.VK_Q:
					setChordType(Chord.Type.MAJOR);
					break;
				case KeyEvent.VK_W:
					setChordType(Chord.Type.DOMINANT_SEVENTH);
					break;
				case KeyEvent.VK_E:
					setChordType(Chord.Type.DOMINANT_FLAT_9);
					break;
				case KeyEvent.VK_R:
					setChordType(Chord.Type.DOMINANT_NINTH);
					break;
				case KeyEvent.VK_T:
					setChordType(Chord.Type.DOMINANT_SHARP_9);
					break;
				case KeyEvent.VK_Y:
					setChordType(Chord.Type.DOMINANT_THIRTEENTH);
					break;
				// Minor chords
				case KeyEvent.VK_A:
					setChordType(Chord.Type.MINOR);
					break;
				case KeyEvent.VK_S:
					setChordType(Chord.Type.MINOR_SIXTH);
					break;
				case KeyEvent.VK_D:
					setChordType(Chord.Type.MINOR_SEVENTH);
					break;
				case KeyEvent.VK_F:
					setChordType(Chord.Type.MINOR_MAJOR_SEVENTH);
					break;
				case KeyEvent.VK_G:
					setChordType(Chord.Type.MINOR_NINTH);
					break;
				case KeyEvent.VK_H:
					setChordType(Chord.Type.MINOR_ELEVENTH);
					break;
				case KeyEvent.VK_J:
					setChordType(Chord.Type.MINOR_THIRTEENTH);
					break;
				// Other chords
				case KeyEvent.VK_Z:
					setChordType(Chord.Type.DIMINISHED);
					break;
				case KeyEvent.VK_X:
					setChordType(Chord.Type.DIMINISHED_SEVENTH);
					break;
				case KeyEvent.VK_C:
					setChordType(Chord.Type.HALF_DIMINISHED);
					break;
				case KeyEvent.VK_V:
					setChordType(Chord.Type.SUSPENDED_FOURTH);
					break;
				case KeyEvent.VK_B:
					setChordType(Chord.Type.SUSPENDED_FOURTH_SEVENTH);
					break;
				case KeyEvent.VK_N:
					setChordType(Chord.Type.ELEVENTH);
					break;
				case KeyEvent.VK_M:
					setChordType(Chord.Type.NO_CHORD);
					break;
				}
			}
			return false;
		}
	}

	private class KeyboardPanel extends JLayeredPane {
		private static final long serialVersionUID = -1500503807487396563L;
		// Property changes
		private final PropertyChangeSupport changes;
		// Key collection
		private final Key[] keys;
		// Keyboard states
		private boolean isSustainEnabled;
		private Keyboard.SostenutoState sostenutoState;

		public KeyboardPanel(PropertyChangeSupport changes) {
			// super();
			this.changes = changes;
			keys = new Key[Note.Pitch.values().length];
			isSustainEnabled = false;
			sostenutoState = Keyboard.SostenutoState.ONE;
			Note.Pitch[] notes = Note.Pitch.values();
			for (int i = 0; i < notes.length; i++) {
				keys[i] = new Key(notes[i], changes, this);
				if (notes[i].isAccidental()) {
					add(keys[i], 1, -1);
				} else {
					add(keys[i], 0, -1);
				}
			}
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(50 * 63, 200);
		}

		public Keyboard.SostenutoState getSostenutoState() {
			return sostenutoState;
		}

		public boolean isSustainEnabled() {
			return isSustainEnabled;
		}

		public void setSostenutoState(Keyboard.SostenutoState sostenutoState) {
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

		public void setSustainEnabled(boolean isSustainEnabled) {
			boolean old = this.isSustainEnabled;
			this.isSustainEnabled = isSustainEnabled;
			if (!isSustainEnabled) {
				for (Key k : keys) {
					k.setIsSustaining(false);
				}
			}
			changes.firePropertyChange("isSustainEnabled", old,
					isSustainEnabled);
		}

		public void start(int i) {
			keys[i].start();
		}

		public void stop(int i) {
			keys[i].stop();
		}

		@Override
		public String toString() {
			return "KeyboardPanel";
		}
	}

	private class KeyboardScrollPane extends JScrollPane {

		private static final long serialVersionUID = 2594826271989475281L;
		private final KeyboardPanel keyboardPanel;

		public KeyboardScrollPane(PropertyChangeSupport changes) {
			super(new KeyboardPanel(changes),
					ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			keyboardPanel = (KeyboardPanel) super.getViewport().getView();
			super.getViewport().setViewPosition(new Point(1000, 0));
			super.setWheelScrollingEnabled(true);
			super.getHorizontalScrollBar().setUnitIncrement(16);
		}

		public KeyboardPanel getKeyboardPanel() {
			return keyboardPanel;
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(1000, 220);
		}
	}

	private class Pedals extends JPanel implements ActionListener,
			ChangeListener {
		private static final long serialVersionUID = -913207215974354521L;
		// Property changes
		private final PropertyChangeSupport changes;
		// components
		private final JSlider gain;
		public JButton sostenuto, sustain;
		// pedal states
		private float selectedGain;

		public Pedals(PropertyChangeSupport changes) {
			super();
			this.changes = changes;
			// gain slider (una corda pedal)
			add(new JLabel("Set the gain:"));
			gain = new JSlider(-40, 6, 0);
			gain.addChangeListener(this);
			add(gain);
			selectedGain = gain.getValue();
			// sostenuto pedal
			sostenuto = new JButton("sostenuto");
			sostenuto.setActionCommand("sostenuto1");
			sostenuto.addActionListener(this);
			add(sostenuto);
			// sustain pedal
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
						Keyboard.SostenutoState.ONE,
						Keyboard.SostenutoState.TWO);
				break;
			case "sostenuto2":
				changes.firePropertyChange("sostenutoState",
						Keyboard.SostenutoState.TWO,
						Keyboard.SostenutoState.THREE);
				break;
			case "sostenuto3":
				changes.firePropertyChange("sostenutoState",
						Keyboard.SostenutoState.THREE,
						Keyboard.SostenutoState.ONE);
				break;
			case "sustain":
				changes.firePropertyChange("sustain", !sustain.getText()
						.equals("sustain"), sustain.getText().equals("sustain"));
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

	private final PropertyChangeSupport changes;

	private final JFrame f;

	private KeyboardScrollPane sp;

	private ControlPanel cp;

	private Pedals pedals;

	public KeyboardView() throws FileNotFoundException {
		// Property change support
		changes = new PropertyChangeSupport(this);
		// JFrame
		f = new JFrame("Keyboard");
		f.setLayout(new BorderLayout());
		// add components
		f.add(sp = new KeyboardScrollPane(changes), BorderLayout.CENTER);
		f.add(cp = new ControlPanel(changes), BorderLayout.NORTH);
		f.add(pedals = new Pedals(changes), BorderLayout.SOUTH);
		// finalize
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Keyboard handling
		KeyboardFocusManager manager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new KeyboardDispatcher());
		// done
		f.setVisible(true);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
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
	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

	@Override
	public void setChordType(Type newValue) {
		cp.setChordType(newValue);
	}

	@Override
	public void setRoot(Root newValue) {
		cp.selectRoot(newValue);
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
	public String toString() {
		return "Keyboard View";
	}

}
