import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JSpinner;

public class DataTrimmerGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane, centerPanel;
	private JScrollPane scrollPane;
	private double yScale=1, xScale=1;
	private JPanel xPanel, yPanel;
	private JLabel lblMaxX, lblMinX, lblScaleX, lblMaxY, lblMinY, lblScaleY;
	private JSpinner maxX, minX, scaleX, maxY, minY, scaleY;
	Data data = new Data();
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DataTrimmerGUI frame = new DataTrimmerGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public DataTrimmerGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 625);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		centerPanel = new JPanel() {

			private static final long serialVersionUID = 1L;
			
			@Override
			protected void paintComponent(Graphics g1) {
				super.paintComponent(g1);
				Graphics2D g = (Graphics2D) g1;
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.setFont(new Font("Arial", Font.PLAIN, 12));
				g.setColor(Color.black);
				
				if (data.matrix == null || data.matrix.isEmpty()) {
					g.drawString("No Data Loaded", 50, 50);
				}
				else {
					for (double[] channel : data.matrix) {
						for (int i=0; i < channel.length-1; i++) {
							g.drawLine((int)(i/xScale),     (int)((data.dataMax-channel[i])/yScale),
									   (int)((i+1)/xScale), (int)((data.dataMax-channel[i+1])/yScale));
						}
					}
				}
				
			}
		};
		centerPanel.setBackground(Color.white);
		centerPanel.setPreferredSize(new Dimension(1000, 1000));
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setViewportView(centerPanel);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		
		JButton btnLoadData = new JButton("Load Data");
		btnLoadData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				data.loadData(new File("eeg.csv"));
				scaleToFitWindow();
				repaint();
				//centerPanel.setPreferredSize(new Dimension(data.matrix.get(0).length, data.dataMax));
				//Update Scroll bar UI -hacky, needs a better way
				//setSize(getWidth()+1, getHeight()+1);
				
			}
		});
		bottomPanel.add(btnLoadData);
		
		JButton btnScaleToFit = new JButton("Scale to fit");
		btnScaleToFit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				scaleToFitWindow();
				repaint();
				//Update Scroll bar UI -hacky, needs a better way
				setSize(getWidth()+1, getHeight()+1);
			}
		});
		bottomPanel.add(btnScaleToFit);
		
		xPanel = new JPanel();
		bottomPanel.add(xPanel);
		xPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		lblMaxX = new JLabel("Max X:");
		xPanel.add(lblMaxX);
		
		maxX = new JSpinner();
		xPanel.add(maxX);
		
		lblMinX = new JLabel("Min X:");
		xPanel.add(lblMinX);
		
		minX = new JSpinner();
		xPanel.add(minX);
		
		lblScaleX = new JLabel("Scale X:");
		xPanel.add(lblScaleX);
		
		scaleX = new JSpinner();
		xPanel.add(scaleX);
		
		yPanel = new JPanel();
		bottomPanel.add(yPanel);
		yPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		lblMaxY = new JLabel("Max Y:");
		yPanel.add(lblMaxY);
		
		maxY = new JSpinner();
		yPanel.add(maxY);
		
		lblMinY = new JLabel("Min Y:");
		yPanel.add(lblMinY);
		
		minY = new JSpinner();
		yPanel.add(minY);
		
		lblScaleY = new JLabel("Scale Y:");
		yPanel.add(lblScaleY);
		
		scaleY = new JSpinner();
		yPanel.add(scaleY);
	}
	
	private void updateSpinners(int xMax, int xMin, int xScale, int yMax, int yMin, int yScale ) {
		maxX.setValue(xMax);
		minX.setValue(xMin);
		scaleX.setValue(xScale);
		maxY.setValue(yMax);
		minY.setValue(yMin);
		scaleY.setValue(yScale);
	}
	
	private void scaleToFitWindow() {
		int height = scrollPane.getHeight()-20;
		int width = scrollPane.getWidth()-20;
		
		yScale = ((double)(data.dataMax-data.dataMin))/height;
		xScale = ((double)data.dataPoints)/width;
		
		updateSpinners(data.dataPoints, 0, (int)Math.ceil(xScale), data.dataMax, data.dataMin, (int)Math.ceil(yScale));
		centerPanel.setPreferredSize(new Dimension(width, height));
	}
	
}
