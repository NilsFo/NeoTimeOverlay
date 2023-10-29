package uhrzeit.window.frame;

import uhrzeit.NeoUhrzeit;
import uhrzeit.data.DisplayData;
import uhrzeit.data.persistency.Einstellungen;
import uhrzeit.window.component.ScalingLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observer;

public class VollbildUhrzeitFrame extends JFrame implements Observer {
	
	public static final Color BG_COL = Color.BLACK;
	public static final Color FG_COL = Color.WHITE;
	
	private static final long serialVersionUID = -2570191393730356114L;
	private ScalingLabel uhrzeitLB;
	
	public VollbildUhrzeitFrame() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		setTitle("Vollbild Uhrzeit");
		
		uhrzeitLB = new ScalingLabel("<Uhrzeit>");
		uhrzeitLB.setBorder(null);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		getContentPane().add(uhrzeitLB, gbc_lblNewLabel);
		
		getContentPane().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int i = 2;
				Einstellungen e = NeoUhrzeit.getEinstellungen();
				if (e.getBoolean(Einstellungen.ENABLE_FULLSCREEN_QUICK_HIDE)) {
					i = 1;
				}
				
				if (arg0.getClickCount() == i) {
					verstecken();
				}
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				verstecken();
			}
			
			@Override
			public void windowIconified(WindowEvent arg0) {
				verstecken();
			}
		});
		
		getContentPane().setBackground(BG_COL);
		uhrzeitLB.setBackground(BG_COL);
		getContentPane().setForeground(FG_COL);
		uhrzeitLB.setForeground(FG_COL);
		
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setUndecorated(true);
		setResizable(false);
	}
	
	public void zeigen() {
		Rectangle bounds;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for (GraphicsDevice curGs : gs) {
			GraphicsConfiguration[] gc = curGs.getConfigurations();
			for (int i = 0; i < gc.length; i++) {
				GraphicsConfiguration curGc = gc[i];
				bounds = curGc.getBounds();
				if (bounds.contains(NeoUhrzeit.getMainframe().getLocation())) {
					System.out.println("Found");
					setBounds(bounds);
				}
			}
		}
		
		setVisible(true);
		requestFocus();
		setState(JFrame.NORMAL);
		
		UhrzeitFrame f = NeoUhrzeit.getMainframe();
		f.setState(Frame.ICONIFIED);
		f.setVisible(false);
		
		double pad = getWidth() * 0.05;
		System.out.println("w: " + getWidth() + " -> pad: " + pad);
		
		uhrzeitLB.setPadding((int) pad);
		uhrzeitLB.resize();
	}
	
	public void verstecken() {
		setVisible(false);
		UhrzeitFrame f = NeoUhrzeit.getMainframe();
		
		f.setVisible(true);
		f.setState(Frame.NORMAL);
		f.requestFocus();
	}
	
	private void updateDisplayedData(DisplayData d) {
		uhrzeitLB.setText(d.getUhrzeitFullscreen());
	}
	
	@Override
	public void update(java.util.Observable o, Object arg) {
		if (o instanceof DisplayData) {
			DisplayData d = (DisplayData) o;
			updateDisplayedData(d);
		}
	}
}
