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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

public class KeyboardView {
	private PropertyChangeSupport changes;
	private JFrame f;
	private KeyboardScrollPane sp;

	public KeyboardView() {
		changes = new PropertyChangeSupport(this);
		f = new JFrame("Keyboard");
		init();
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

	private void init() {
		f.setLayout(new BorderLayout());
		sp = new KeyboardScrollPane(changes);
		f.add(sp, BorderLayout.CENTER);
		f.add(new ControlPanel(changes), BorderLayout.NORTH);
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

	public void stopAll() {
		sp.getKeyboardPanel().stopAll();
	}

}

class ControlPanel extends JPanel implements ActionListener {
	private PropertyChangeSupport changes;
	private static final long serialVersionUID = 1951726034463130455L;
	private JComboBox<String> instruments;
	private JComboBox<Note.Key> keys;
	private JButton changeInstrument, go, stop;

	public ControlPanel(PropertyChangeSupport changes) {
		super();
		this.changes = changes;
		add(new JLabel("Choose an instrument"));
		instruments = new JComboBox<String>(FileReader.getInstruments());
		add(instruments);
		changeInstrument = new JButton("Change instrument");
		changeInstrument.setActionCommand("changeInstrument");
		changeInstrument.addActionListener(this);
		add(changeInstrument);
		add(new JLabel("Choose a key:"));
		keys = new JComboBox<Note.Key>(Note.Key.values());
		add(keys);
		go = new JButton("go");
		go.setActionCommand("go");
		go.addActionListener(this);
		add(go);
		stop = new JButton("stop");
		stop.setActionCommand("stop");
		stop.addActionListener(this);
		add(stop);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "changeInstrument") {
			changes.firePropertyChange("instrumentChanged", null,
					instruments.getItemAt(instruments.getSelectedIndex()));
		} else if (e.getActionCommand() == "go") {
			changes.firePropertyChange("keyChanged",
					keys.getItemAt(keys.getSelectedIndex()), null);
		} else if (e.getActionCommand() == "stop") {
			changes.firePropertyChange("stop", null, null);
		}
	}
}

class KeyboardScrollPane extends JScrollPane {
	private static final long serialVersionUID = 2594826271989475281L;
	private static KeyboardPanel keyboardPanel;

	public KeyboardScrollPane(PropertyChangeSupport changes) {
		super(keyboardPanel = new KeyboardPanel(changes),
				ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		getViewport().setViewPosition(new Point(1000, 0));
	}

	public KeyboardPanel getKeyboardPanel() {
		return keyboardPanel;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1000, 240);
	}
}

class KeyboardPanel extends JLayeredPane {
	private PropertyChangeSupport changes;
	private static final long serialVersionUID = -1500503807487396563L;
	private Key[] keys;

	public KeyboardPanel(PropertyChangeSupport changes) {
		this.changes = changes;
		keys = new Key[Note.Pitch.values().length];
		init();
	}

	public void stopAll() {
		for (Key k : keys) {
			if (k.isPressed()) {
				k.stop();
			}
		}
	}

	private void init() {
		int i = 0;
		for (Note.Pitch p : Note.Pitch.values()) {
			keys[i] = new Key(p, changes);
			if (p.isAccidental()) {
				add(keys[i], 1, -1);
			} else {
				add(keys[i], 0, -1);
			}
			i++;
		}

	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(50 * 63, 200);
	}
}

class Key extends JButton implements MouseListener {

	private static final long serialVersionUID = -607879188699121465L;
	private PropertyChangeSupport changes;
	private Note.Pitch p;
	private boolean isPressed;
	private boolean isBlack;

	public Key(Note.Pitch p, PropertyChangeSupport changes) {
		super(p.toString());
		this.changes = changes;
		setBorder(BorderFactory.createRaisedBevelBorder());
		this.p = p;
		isPressed = false;
		if (p.isAccidental()) {
			isBlack = true;
			setBackground(Color.BLACK);
			setForeground(Color.WHITE);
			setSize(30, 150);
			setLocation(-15 + p.getLocationOnKeyboard() * 50, 0);
		} else {
			isBlack = false;
			setBackground(Color.WHITE);
			setSize(50, 200);
			setLocation(p.getLocationOnKeyboard() * 50, 0);
		}
		addMouseListener(this);
	}

	protected void start() {
		if (!isPressed) {
			if (isBlack) {
				setBackground(Color.DARK_GRAY);
			} else {
				setBackground(Color.LIGHT_GRAY);
			}
			isPressed = true;
			changes.firePropertyChange("KeyPressed", null, p);
		}
	}

	protected void stop() {
		if (isPressed) {
			if (isBlack) {
				setBackground(Color.BLACK);
			} else {
				setBackground(Color.WHITE);
			}
			isPressed = false;
			changes.firePropertyChange("KeyReleased", p, null);
		}
	}

	protected boolean isPressed() {
		return isPressed;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		start();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		stop();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (!isPressed) {
				start();
			} else {
				stop();
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}
