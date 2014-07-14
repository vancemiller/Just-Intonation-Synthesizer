package testing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class View extends Observable implements ViewObserver {
	JFrame f;
	NoteStack s;

	public View() {
		this.f = new JFrame("Just Intonation");
		init();

		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}

	private void init() {
		f.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		f.add(new ControlPanel(this));
		f.add(s = new NoteStack(this));
	}

	@Override
	public void update(JPanel p, Event e) {
		setChanged();
		e.setNoteStack(s);
		notifyObservers(e);
	}
}

class NoteStack extends JPanel implements ViewObservable {

	private static final long serialVersionUID = 346897567898468463L;
	private List<NoteBox> notes;
	private JPanel p;
	private ViewObserver v;

	public NoteStack(ViewObserver o) {
		super();
		this.v = o;

		setBorder(BorderFactory.createTitledBorder("NoteStack"));
		setLayout(new BorderLayout());

		notes = new LinkedList<NoteBox>();

		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
		add(new JScrollPane(p), BorderLayout.CENTER);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(120, 300);
	}

	protected void addNote(Note n) {
		notes.add(new NoteBox(n));
		p.add(notes.get(notes.size() - 1));
		revalidate();
		repaint();
	}

	protected void removeLast() {
		if (notes.size() == 0)
			return;
		p.remove(notes.get(notes.size() - 1));
		notes.remove(notes.size() - 1);
		revalidate();
		repaint();
	}

	private class NoteBox extends JPanel implements MouseListener {
		private Note n;

		private static final long serialVersionUID = 393530037508215982L;

		public NoteBox(Note n) {
			super();
			this.n = n;
			setBorder(BorderFactory.createTitledBorder("Note"));
			add(new JLabel(n.toString()));
			addMouseListener(this);
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(80, 50);
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
		public void mouseClicked(MouseEvent e) {
			final Color bg = getBackground();
			setBackground(bg.darker());

			JPanel p = new JPanel();
			JTextField[] overtones = new JTextField[Note.NUM_OVERTONES];
			p.setBorder(BorderFactory.createTitledBorder(n.getRootPitch()
					.toString()
					+ n.getRootOctave()
					+ " + "
					+ n.getInterval().longName + " = " + n.toString()));
			p.setLayout(new GridBagLayout());
			GridBagConstraints gc = new GridBagConstraints();
			gc.insets = new Insets(5, 5, 5, 5);
			gc.anchor = GridBagConstraints.ABOVE_BASELINE_LEADING;
			gc.gridwidth = 3;
			gc.gridx = 0;
			gc.gridy = 0;

			for (int i = 0; i < overtones.length; i++) {
				gc.gridx = 0;
				gc.gridwidth = 1;
				gc.gridy++;
				p.add(new JLabel("Overtone " + i + ":"), gc);
				gc.gridwidth = 2;
				gc.gridx = 1;
				p.add(overtones[i] = new JTextField(
						n.getOvertoneVolume(i) + "", 5), gc);
			}

			if (JOptionPane.showConfirmDialog(this, p, "Note detail",
					JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				double[] converted = new double[overtones.length];
				for (int i = 0; i < overtones.length; i++) {
					try {
						converted[i] = Double.parseDouble(overtones[i]
								.getText());
					} catch (NumberFormatException x) {
						converted[i] = 0.0;
					}
				}
				notifyView(new OvertoneUpdateEvent(converted, n));
			}
			setBackground(bg);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

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

	@Override
	public void notifyView(Event e) {
		v.update(this, e);
	}

}

class ControlPanel extends JPanel implements ActionListener, ViewObservable {

	private static final long serialVersionUID = -4807812618627195935L;
	ViewObserver v;
	JButton push, pop, play;
	JComboBox<Note.Pitch> root;
	JComboBox<Note.Interval> interval;
	JComboBox<Integer> octave;
	JTextField duration;

	public ControlPanel(ViewObserver o) {
		super();
		v = o;

		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder("ControlPanel"));
		push = new JButton("Push");
		pop = new JButton("Pop");
		play = new JButton("Play");
		root = new JComboBox<Note.Pitch>(Note.Pitch.values());
		interval = new JComboBox<Note.Interval>(Note.Interval.values());
		octave = new JComboBox<Integer>(Note.validOctaves);
		duration = new JTextField("2000", 10);

		push.setActionCommand("push");
		pop.setActionCommand("pop");
		play.setActionCommand("play");

		push.addActionListener(this);
		pop.addActionListener(this);
		play.addActionListener(this);

		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.WEST;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(4, 4, 4, 4);
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 3;
		add(root, gc);
		gc.gridy++;
		add(octave, gc);
		gc.gridy++;
		add(interval, gc);
		gc.gridy++;
		add(push, gc);
		gc.gridy++;
		add(pop, gc);
		gc.gridy++;
		gc.gridwidth = 2;
		add(duration, gc);
		gc.gridx = 2;
		gc.gridwidth = 1;
		add(play, gc);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(200, 300);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "push":
			notifyView(Event.newAddEvent(root.getSelectedIndex(),
					interval.getSelectedIndex(), octave.getSelectedIndex()));
			break;
		case "pop":
			notifyView(Event.newRemoveEvent());
			break;
		case "play":
			try {
				notifyView(Event.newPlayEvent(Integer.parseInt(duration
						.getText())));
			} catch (NumberFormatException x) {
				duration.setText("");
			} finally {

			}
			break;
		default:
			break;
		}
	}

	@Override
	public void notifyView(Event e) {
		v.update(this, e);
	}
}
