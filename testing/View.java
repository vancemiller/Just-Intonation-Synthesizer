package testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import testing.Note.Pitch;

public class View {
	Model m;
	JFrame f;

	public View() {
		this.m = new Model();
		this.f = new JFrame("Just Intonation");
		init();
		f.setMinimumSize(new Dimension(600, 270));
		f.setMaximumSize(new Dimension(600, 270));
		// f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

	private void init() {
		JScrollPane sp = new JScrollPane(new KeyboardPanel(),
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		f.add(sp);
	}
}

class KeyboardPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = -7261532115653344378L;

	private final Keys k;

	private static class Keys {

		private final Key[] keys;

		public Keys(Note.Pitch start, Note.Pitch end, int numOctaves) {
			keys = new Key[Note.Pitch.totalPitches - start.ordinal()
					+ Note.Pitch.totalPitches * numOctaves + end.ordinal()];
			int count = 0;
			if (start.next().isAccidental)
				keys[count] = new Key(start, Key.Shape.L);
			else if (start.isAccidental)
				keys[count] = new Key(start, Key.Shape.BLACK);
			else
				keys[count] = new Key(start, Key.Shape.FULL);
			count++;
			for (Note.Pitch p = start.next(); p.ordinal() < Note.Pitch.totalPitches; p = p
					.next()) {
				addKey(p, count);
				count++;
			}
			for (int octave = 0; octave < numOctaves; octave++)
				for (Note.Pitch p : Note.Pitch.values()) {
					addKey(p, count);
					count++;
				}
			for (Note.Pitch p = Note.Pitch.start; p.next().ordinal() < end
					.ordinal(); p = p.next()) {
				addKey(p, count);
				count++;
			}
			if (end.previous().isAccidental)
				keys[count] = new Key(end, Key.Shape.R);
			else if (end.isAccidental)
				keys[count] = new Key(end, Key.Shape.BLACK);
			else
				keys[count] = new Key(end, Key.Shape.FULL);
		}

		private void addKey(Pitch p, int count) {
			if (p.isAccidental)
				keys[count] = new Key(p, Key.Shape.BLACK);
			else if (p.next().isAccidental && p.previous().isAccidental)
				keys[count] = new Key(p, Key.Shape.T);
			else if (p.next().isAccidental)
				keys[count] = new Key(p, Key.Shape.L);
			else if (p.previous().isAccidental)
				keys[count] = new Key(p, Key.Shape.R);
			else
				keys[count] = new Key(p, Key.Shape.FULL);
		}

		public void drawKeys(Graphics g) {
			for (Key k : keys)
				k.drawKey(g, 6, 0);
		}

		public static int getNumKeys() {
			// TODO Auto-generated method stub
			return 0;
		}

		private static class Key {
			public static final int BLACK_KEY_WIDTH = 30,
					BLACK_KEY_HEIGHT = 140, WHITE_KEY_WIDTH = 50,
					WHITE_KEY_HEIGHT = 200;

			public static enum Shape {
				L, R, FULL, T, BLACK
			};

			private final Note.Pitch pitch;
			private final Shape shape;

			public Key(Note.Pitch pitch, Shape shape) {
				this.pitch = pitch;
				this.shape = shape;
			}

			public void drawKey(Graphics g, int x, int y) {
				g.setColor(Color.BLACK);
				if (pitch.isAccidental) {
					g.fillRect(x, y, BLACK_KEY_WIDTH, BLACK_KEY_HEIGHT);
				} else {
					Polygon p = new Polygon();
					p.addPoint(x, y);
					switch (shape) {
					case L:
						p.addPoint(x, y + WHITE_KEY_HEIGHT);
						break;
					case BLACK:
						break;
					case FULL:
						break;
					case R:
						break;
					case T:
						break;
					default:
						break;
					}
					g.drawPolygon(p);
				}
			}

			public void press() {
				System.out.println(pitch);
			}

		}
	}

	KeyboardPanel() {
		super();
		Dimension size = getPreferredSize();
		this.setMinimumSize(size);
		this.setMaximumSize(size);
		this.setBackground(Color.white);
		this.addMouseListener(this);
		k = new Keys(Note.Pitch.A, Note.Pitch.C, 7);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Keys.getNumKeys() * Keys.Key.WHITE_KEY_WIDTH,
				Keys.Key.WHITE_KEY_HEIGHT);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		k.drawKeys(g);
	}

	private void pressKey(MouseEvent e) {
		// TODO
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		pressKey(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO
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
