package testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JPanel;

import testing.Note.Pitch;

public class View {
	JFrame f;
	KeyboardPanel k;

	public View() {
		this.f = new JFrame("Just Intonation");
		init();
		f.setMinimumSize(new Dimension(600, 270));
		f.setMaximumSize(new Dimension(600, 270));
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

	private void init() {
		// JScrollPane sp = new JScrollPane(k = new KeyboardPanel(),
		// JScrollPane.VERTICAL_SCROLLBAR_NEVER,
		// JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		// f.add(sp);
		f.setBackground(Color.RED);
		f.add(new DrawableKey(Note.Pitch.A, 10, 0));
		f.add(new DrawableKey(Note.Pitch.Bb, 30, 0));
		// f.add(new DrawableKey(Note.Pitch.C, 00, 0));

	}

	public void pressKey(int keyNumber) {
		k.pressKey(keyNumber);
	}

	public void releaseKey(int keyNumber) {
		k.releaseKey(keyNumber);
	}
}

class KeyboardPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = -7261532115653344378L;
	private final int numNotes;
	private final Keyboard k;

	KeyboardPanel() {
		this(88);
	}

	KeyboardPanel(int numNotes) {
		super();
		this.numNotes = numNotes;
		this.k = new Keyboard(Pitch.A, numNotes);
		this.setBackground(Color.white);
		this.addMouseListener(this);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(k.get(numNotes).getX() + 50,
				DrawableKey.WHITE_KEY_HEIGHT);
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		DrawableKey dk = new DrawableKey(Pitch.C, 10, 10);
		this.add(dk);

		// for (Iterator<DrawableKey> iterator = k.iterator();
		// iterator.hasNext();) {
		// DrawableKey drawableKey = (DrawableKey) iterator.next();
		// this.add(drawableKey);
		// }
	}

	public void pressKey(int keyNumber) {
		// PRE: keyNumber is less than numNotes
		repaint();
	}

	public void releaseKey(int keyNumber) {
		// PRE: keyNumber is less than numNotes
		this.revalidate();
		this.repaint();
	}

	private int getKeyNumber(Point point) {
		int previousSize = 0, nextSize = 0, keyNumber = 0;
		for (Iterator<DrawableKey> iterator = k.iterator(); iterator.hasNext();) {
			DrawableKey drawableKey = iterator.next();
			switch (drawableKey.getShape()) {
			case B:
				nextSize += DrawableKey.BLACK_KEY_WIDTH;
				break;
			case F:
				nextSize += DrawableKey.WHITE_KEY_WIDTH;
				break;
			case L:
				nextSize += DrawableKey.WHITE_KEY_WIDTH
						- DrawableKey.BLACK_KEY_WIDTH / 2;
				break;
			case R:
				nextSize += DrawableKey.WHITE_KEY_WIDTH
						- DrawableKey.BLACK_KEY_WIDTH / 2;
				break;
			case T:
				nextSize += DrawableKey.WHITE_KEY_WIDTH
						- DrawableKey.BLACK_KEY_WIDTH;
				break;
			default:
				break;
			}
			if (previousSize < point.x && nextSize > point.x) {
				if (point.y < DrawableKey.BLACK_KEY_HEIGHT
						&& k.get(keyNumber).getShape() == DrawableKey.Shape.B)
					return keyNumber;
				else if (point.y > DrawableKey.BLACK_KEY_HEIGHT
						&& k.get(keyNumber).getShape() == DrawableKey.Shape.B)
					return keyNumber + 1;
				return keyNumber;
			}

			keyNumber++;
			previousSize = nextSize;
		}
		return -1;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println(e.getComponent());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
