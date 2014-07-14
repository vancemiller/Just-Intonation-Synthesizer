package testing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class KeyboardView {
	JFrame f;
	KeyboardScrollPane sp;
	ControlPanel c;
	Keyboard model;

	public KeyboardView(Keyboard model) {
		this.f = new JFrame("Keyboard");
		this.model = model;
		init();
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

	private void init() {
		f.setLayout(new BorderLayout());
		JScrollPane sp = new KeyboardScrollPane(model);
		f.add(sp, BorderLayout.CENTER);
		f.add(c = new ControlPanel(model), BorderLayout.NORTH);
	}

}

class ControlPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1951726034463130455L;
	private Keyboard model;
	private JComboBox<Note.Key> keys;
	private JButton go;

	public ControlPanel(Keyboard model) {
		super();
		this.model = model;
		add(new JLabel("Choose a key:"));
		keys = new JComboBox<Note.Key>(Note.Key.values());
		add(keys);
		go = new JButton("go");
		go.setActionCommand("go");
		go.addActionListener(this);
		add(go);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == "go") {
			model.setKey(keys.getItemAt(keys.getSelectedIndex()));
		}
	}
}

class KeyboardScrollPane extends JScrollPane {
	private static final long serialVersionUID = 2594826271989475281L;

	public KeyboardScrollPane(Keyboard model) {
		super(new KeyboardPanel(model), JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		getViewport().setViewPosition(new Point(1800, 0));
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1000, 300);
	}
}

class KeyboardPanel extends JPanel {

	private static final long serialVersionUID = -1500503807487396563L;

	public KeyboardPanel(Keyboard model) {
		init(model);
	}

	private void init(Keyboard model) {
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.fill = GridBagConstraints.VERTICAL;
		gc.gridheight = 3;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.insets = new Insets(1, 0, 0, 0);
		for (Note n : model.getNotes()) {
			JButton b = new Key(model, n);
			add(b, gc);
			gc.gridx++;
		}

	}
}

class Key extends JButton implements MouseListener {

	private static final long serialVersionUID = -607879188699121465L;
	private Keyboard k;
	private Note n;
	private boolean isPlaying;
	private boolean isBlack;

	public Key(Keyboard k, Note n) {
		super(n.getPitch().toString());
		this.setBorder(BorderFactory.createRaisedBevelBorder());
		this.setMargin(new Insets(1, 1, 1, 1));
		this.k = k;
		this.n = n;
		this.isPlaying = false;
		if (n.getPitch().isAccidental()) {
			this.isBlack = true;
			setBackground(Color.BLACK);
			setForeground(Color.WHITE);
		} else {
			this.isBlack = false;
			setBackground(Color.WHITE);
		}
		addMouseListener(this);
	}

	private void start() {
		if (!isPlaying) {
			if (isBlack) {
				setBackground(Color.DARK_GRAY);
			} else {
				setBackground(getBackground().darker());
			}
			isPlaying = true;
		}
	}

	private void stop() {
		if (isPlaying) {
			if (isBlack) {
				setBackground(Color.BLACK);
			} else {
				setBackground(getBackground().brighter());
			}
			isPlaying = false;
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(50, 200);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		k.startNote(n.getPitch());
		start();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		k.stopNote(n.getPitch());
		stop();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			if (!isPlaying) {
				k.startNote(n.getPitch());
				start();
			} else {
				k.stopNote(n.getPitch());
				stop();
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}
