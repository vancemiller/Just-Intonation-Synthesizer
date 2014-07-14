package testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class KeyboardView {
	JFrame f;
	JPanel k;
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
		JScrollPane sp = new KeyboardScrollPane(model);
		f.add(sp);
	}

}

class KeyboardScrollPane extends JScrollPane {
	private static final long serialVersionUID = 2594826271989475281L;

	public KeyboardScrollPane(Keyboard model) {
		super(new KeyboardPanel(model), JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
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
		gc.insets = new Insets(2, 2, 2, 2);
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
