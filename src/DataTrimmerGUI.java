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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class DataTrimmerGUI extends JFrame {

	private Color[] colors = {Color.white, Color.red, Color.blue, Color.orange, Color.cyan, Color.green, Color.magenta, Color.pink};
	private static final long serialVersionUID = 1L;
	private JPanel contentPane, centerPanel;
	private JScrollPane scrollPane;
	private double yScale=1, xScale=1;
	private JPanel xPanel, valueRanges;
	private JLabel lblScaleX, lblMaxY, lblMinY, lblScaleY;
	private JSpinner scaleX, scaleY;
	private Data data = new Data();
	private JTextField maxVal;
	private JTextField minVal;
	
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
				
				
				if (data.matrix == null || data.matrix.isEmpty()) {
					g.setColor(Color.black);
					g.drawString("No Data Loaded", 50, 50);
				}
				else {
					int c = 0;
					for (double[] channel : data.matrix) {
						g.setColor(colors[c++%colors.length]);
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
				maxVal.setText(data.dataMax + "");
				minVal.setText(data.dataMin + "");
				scaleToFitWindow();
				repaint();
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
				//setSize(getWidth()+1, getHeight()+1);
			}
		});
		bottomPanel.add(btnScaleToFit);
		
		xPanel = new JPanel();
		bottomPanel.add(xPanel);
		xPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		lblScaleX = new JLabel("Scale X:");
		xPanel.add(lblScaleX);
		
		
		SpinnerModel modelX = new SpinnerNumberModel(1, .01, 100, .5);
		scaleX = new JSpinner(modelX);
		scaleX.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				System.out.println(scaleX.getValue());
				xScale = (double)scaleX.getValue();
				centerPanel.setPreferredSize(new Dimension((int)(data.dataPoints/xScale), centerPanel.getHeight()));
			}
		});
		xPanel.add(scaleX);
		
		lblScaleY = new JLabel("Scale Y:");
		xPanel.add(lblScaleY);
		
		SpinnerModel modelY = new SpinnerNumberModel(1, .01, 100, .5);
		scaleY = new JSpinner(modelY);
		scaleY.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				yScale = (double)scaleY.getValue();
				centerPanel.setPreferredSize(new Dimension(centerPanel.getWidth(), (int)((data.dataMax-data.dataMin)/yScale)));
			}
		});
		xPanel.add(scaleY);
		
		valueRanges = new JPanel();
		bottomPanel.add(valueRanges);
		valueRanges.setLayout(new GridLayout(0, 2, 0, 0));
		
		lblMaxY = new JLabel("Greatest Value:");
		valueRanges.add(lblMaxY);
		
		maxVal = new JTextField();
		maxVal.setEditable(false);
		valueRanges.add(maxVal);
		maxVal.setColumns(10);
		
		lblMinY = new JLabel("Least Value:");
		valueRanges.add(lblMinY);
		
		minVal = new JTextField();
		minVal.setEditable(false);
		valueRanges.add(minVal);
		minVal.setColumns(10);
	}
	
//	private int roundScale(double scale) {
//		return (int)Math.ceil(scale);
//	}
	
	private void updateSpinners(double xScale, double yScale ) {
		scaleX.setValue(xScale);
		scaleY.setValue(yScale);
	}
	
	private void scaleToFitWindow() {
		int height = scrollPane.getHeight()-20;
		int width = scrollPane.getWidth()-20;
		
		yScale = ((double)(data.dataMax-data.dataMin))/height;
		xScale = ((double)data.dataPoints)/width;
		
		updateSpinners(xScale, yScale);
		centerPanel.setPreferredSize(new Dimension(width, height));
	}
	
}
