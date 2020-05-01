package uhrzeit.window.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ScalingLabel extends JPanel {
	
	private static final long serialVersionUID = 5433015144746941027L;
	private JLabel label;
	private int padding;
	private boolean scalingenabled;
	
	public ScalingLabel(String text) {
		setup(text);
	}
	
	public ScalingLabel() {
		this("");
	}
	
	private void setup(String text) {
		padding = 10;
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{430, 0};
		gridBagLayout.rowHeights = new int[]{278, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		label = new JLabel(text);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.fill = GridBagConstraints.BOTH;
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		add(label, gbc_label);
		setScalingenabled(true);
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				resize();
			}
		});
	}
	
	public void resize() {
		if (!isScalingenabled())
			return;
		// Code from stackoverflow.com/questions/2715118/
		Font labelFont = label.getFont();
		String labelText = label.getText();
		
		int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
		int componentWidth = label.getWidth();
		
		// Find out how much the font can grow in width.
		double widthRatio = (double) componentWidth / (double) stringWidth;
		
		int newFontSize = (int) (labelFont.getSize() * widthRatio) - getPadding();
		int componentHeight = label.getHeight();
		
		// Pick a new font size so it will not be larger than the height of
		// label.
		int fontSizeToUse = Math.min(newFontSize, componentHeight);
		
		// Set the label's font size to the newly determined size.
		label.setFont(new Font(labelFont.getName(), Font.PLAIN, fontSizeToUse));
	}
	
	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return super.getPreferredSize();
	}
	
	@Override
	public Dimension getMinimumSize() {
		// System.out.println("MINIMUM");
		// return super.getMinimumSize();
		return new Dimension(15, 15);
	}
	
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if (label != null)
			label.setForeground(fg);
	}
	
	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if (label != null)
			label.setBackground(bg);
	}
	
	public int getPadding() {
		return padding;
	}
	
	public void setPadding(int padding) {
		this.padding = padding;
		resize();
	}
	
	public String getText() {
		return label.getText();
	}
	
	public void setText(String text) {
		label.setText(text);
		// resize();
	}
	
	public boolean isScalingenabled() {
		return scalingenabled;
	}
	
	public void setScalingenabled(boolean scalingenabled) {
		this.scalingenabled = scalingenabled;
	}
}
