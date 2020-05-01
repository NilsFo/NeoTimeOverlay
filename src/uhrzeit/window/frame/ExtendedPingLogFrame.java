package uhrzeit.window.frame;

import uhrzeit.NeoUhrzeit;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ExtendedPingLogFrame extends JFrame {
	
	public static final String DELETE_MSG = "*** DELETE ALL ***";
	public static final int PREFERED_HEIGHT = 300;
	public static final int PREFERED_WIDTH = 400;
	private static final long serialVersionUID = 5882705671162542298L;
	private Window parent;
	private JTextPane textArea;
	
	public ExtendedPingLogFrame(Window parent) {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				align();
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) {
				align();
			}
		});
		this.parent = parent;
		setSize(PREFERED_WIDTH, PREFERED_HEIGHT);
		setTitle(NeoUhrzeit.TITLE + " - Erweiterter Ping-Log");
		// setResizable(false);
		
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{364, 0};
		gridBagLayout.rowHeights = new int[]{240, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		textArea = new JTextPane();
		textArea.setEditable(false);
		textArea.setFont(new Font("Courier New", Font.PLAIN, 14));
		scrollPane.setViewportView(textArea);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		getContentPane().add(scrollPane, gbc_scrollPane);
	}
	
	public void zeigen() {
		setVisible(true);
		align();
	}
	
	public void log(String s) {
		s = s.trim();
		if (s.equals("")) {
			return;
		}
		
		if (s.equals(DELETE_MSG)) {
			textArea.setText("");
			return;
		}
		
		textArea.setText(textArea.getText() + "\n" + s);
	}
	
	public void align() {
		int x = parent.getX();
		int y = parent.getY();
		setLocation(x - getWidth(), y);
		
		// int pheight = parent.getHeight();
		// int height = PREFERED_HEIGHT;
		// if (pheight >= PREFERED_HEIGHT) {
		// height = pheight;
		// }
		// setSize(getWidth(), height);
		// repaint();
	}
}
