import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class EDFTrimmerGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private Color[] colors = {Color.white, Color.red, Color.blue, Color.orange, Color.cyan, Color.green, Color.magenta, Color.pink};
	private JPanel contentPane, centerPanel, xPanel, valueRanges, panel, panel_1, panel_2;
	private JTextField leftTrimSample, leftTrimTime, rightTrimSample, rightTrimTime;
	private JLabel lblScaleX, lblMaxY, lblMinY, lblScaleY, lblHz;
	private JToggleButton tglbtnLeftTrim, tglbtnRightTrim;
	private JSpinner scaleX, scaleY, freqSpinner;
	private JButton btnScaleToFit, btnSaveData;
	private DraggableLine leftLine, rightLine;
	private JTextField maxVal, minVal;
	private JScrollPane scrollPane;
	private double yScale=1, xScale=1;
	private boolean dataLoaded = false;
	private Data data = new Data();
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EDFTrimmerGUI frame = new EDFTrimmerGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public EDFTrimmerGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1049, 700);
		setMinimumSize(new Dimension(890, 500));
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
					g.drawString("No Data Loaded", 200, 200);
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
					
					g.setColor(Color.blue);
					leftLine.draw(g, getHeight(), getWidth());
					rightLine.draw(g, getHeight(), getWidth());
				}
				
			}
		};
		centerPanel.setBackground(Color.white);
		centerPanel.setPreferredSize(new Dimension(500, 500));
		setLocationRelativeTo(null);
		centerPanel.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (dataLoaded) {
					if (leftLine.dragging) {
						leftLine.setXDrawn(e.getX(), xScale);
						leftTrimSample.setText(leftLine.getXReal() + "");
						leftTrimTime.setText(String.format("%.3f sec", leftLine.getXReal()/(float)(int)freqSpinner.getValue()));
						repaint();
					}
					else if (rightLine.dragging) {
						rightLine.setXDrawn(e.getX(), xScale);
						rightTrimSample.setText(rightLine.getXReal() + "");
						rightTrimTime.setText(String.format("%.3f sec", rightLine.getXReal()/(float)(int)freqSpinner.getValue()));
						repaint();
					}
				}
			}
		});
		centerPanel.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (dataLoaded) {
					if (leftLine.dragging) {
						leftLine.dragging = false;
						if (leftLine.getXReal() < 0) {
							leftLine.setXReal(0, xScale);
							leftTrimSample.setText(0 + "");
							leftTrimTime.setText(String.format("%.3f sec", 0.0));
							repaint();
						}
					}
					if (rightLine.dragging) {
						rightLine.dragging = false;
						if (rightLine.getXReal() > data.dataPoints) {
							rightLine.setXReal(data.dataPoints, xScale);
							rightTrimSample.setText(data.dataPoints + "");
							rightTrimTime.setText(String.format("%.3f sec", data.dataPoints/(float)(int)freqSpinner.getValue()));
							repaint();
						}
					}
				}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if (dataLoaded) {
					int x = e.getX();
					leftLine.checkMouse(x);
					rightLine.checkMouse(x);
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setViewportView(centerPanel);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		contentPane.add(bottomPanel, BorderLayout.SOUTH);
		
		panel_1 = new JPanel();
		bottomPanel.add(panel_1);
		panel_1.setLayout(new GridLayout(0, 2, 0, 0));
		
		JButton btnLoadData = new JButton("Load Data");
		panel_1.add(btnLoadData);
		
		btnScaleToFit = new JButton("Scale to fit");
		panel_1.add(btnScaleToFit);
		btnScaleToFit.setEnabled(false);
		btnScaleToFit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				scaleToFitWindow();
				scrollPane.getViewport().revalidate();
				repaint();
			}
		});
		
		btnSaveData = new JButton("Save Data");
		btnSaveData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				int leftTrim = (leftLine.enabled? leftLine.getXReal():0);
				int rightTrim = (rightLine.enabled? rightLine.getXReal():data.dataPoints);
				int freq = data.result.getHeader().getNumberOfSamples()[0];
				if ((rightTrim-leftTrim) % freq != 0) {
					RoundingDialog rd = new RoundingDialog(EDFTrimmerGUI.this, data, leftLine, rightLine, freq);
					rd.setVisible(true);
					
					//update graphs with new line values
					leftTrimSample.setText(leftLine.getXReal() + "");
					leftTrimTime.setText(String.format("%.3f sec", leftLine.getXReal()/(float)(int)freqSpinner.getValue()));
					rightTrimSample.setText(rightLine.getXReal() + "");
					rightTrimTime.setText(String.format("%.3f sec", rightLine.getXReal()/(float)(int)freqSpinner.getValue()));
					repaint();
				}
				
				
				SaveFileDialog dialog = new SaveFileDialog(EDFTrimmerGUI.this, data, leftLine, rightLine);
				dialog.setVisible(true);
			}
		});
		btnSaveData.setEnabled(false);
		panel_1.add(btnSaveData);
		
		panel_2 = new JPanel();
		panel_1.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		lblHz = new JLabel(" Hz:");
		panel_2.add(lblHz, BorderLayout.WEST);
		
		freqSpinner = new JSpinner();
		freqSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (leftLine != null) 
					leftTrimTime.setText(String.format("%.3f sec", leftLine.getXReal()/(float)(int)freqSpinner.getValue()));
				if (rightLine != null) 
					rightTrimTime.setText(String.format("%.3f sec", rightLine.getXReal()/(float)(int)freqSpinner.getValue()));
			}
		});
		panel_2.add(freqSpinner, BorderLayout.CENTER);
		freqSpinner.setModel(new SpinnerNumberModel(new Integer(128), new Integer(1), null, new Integer(1)));
		btnLoadData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FileConfirm confirm = new FileConfirm(EDFTrimmerGUI.this, data);
					confirm.setVisible(true);
					if (confirm.selection.equals("OK")) {
						maxVal.setText(data.dataMax + "");
						minVal.setText(data.dataMin + "");
						leftLine = new DraggableLine(0, xScale, true);
						rightLine = new DraggableLine(centerPanel.getWidth(), xScale, false);
						scaleToFitWindow();
						rightLine.setXReal(data.dataPoints, xScale);
						btnScaleToFit.setEnabled(true);
						scaleX.setEnabled(true);
						scaleY.setEnabled(true);
						tglbtnLeftTrim.setEnabled(true);
						tglbtnRightTrim.setEnabled(true);
						btnSaveData.setEnabled(true);
						dataLoaded = true;
					}
					else if (confirm.selection.equals("Cancel")) {
						btnScaleToFit.setEnabled(false);
						scaleX.setEnabled(false);
						scaleY.setEnabled(false);
						tglbtnLeftTrim.setEnabled(false);
						tglbtnRightTrim.setEnabled(false);
						btnSaveData.setEnabled(false);
						dataLoaded = false;
					}
					tglbtnLeftTrim.setSelected(false);
					tglbtnRightTrim.setSelected(false);
					leftTrimSample.setText("");
					leftTrimTime.setText("");
					rightTrimSample.setText("");
					rightTrimTime.setText("");
				} catch (EarlyCloseException e1) {
					//JOptionPane.showMessageDialog(DataTrimmerGUI.this, "Did not load a file");
				}
				
				repaint();
			}
		});
		
		xPanel = new JPanel();
		bottomPanel.add(xPanel);
		xPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		lblScaleX = new JLabel("Scale 1/X:");
		xPanel.add(lblScaleX);
		
		
		SpinnerModel modelX = new SpinnerNumberModel(1, .01, 100, .5);
		scaleX = new JSpinner(modelX);
		scaleX.setEnabled(false);
		scaleX.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				xScale = (double)scaleX.getValue();
				leftLine.changeScale(xScale);
				rightLine.changeScale(xScale);
				centerPanel.setPreferredSize(new Dimension((int)(data.dataPoints/xScale), (int)((data.dataMax-data.dataMin)/yScale)));
				scrollPane.getViewport().revalidate();
				repaint();
			}
		});
		xPanel.add(scaleX);
		
		lblScaleY = new JLabel("Scale 1/Y:");
		xPanel.add(lblScaleY);
		
		SpinnerModel modelY = new SpinnerNumberModel(1, .01, 100, .5);
		scaleY = new JSpinner(modelY);
		scaleY.setEnabled(false);
		scaleY.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				yScale = (double)scaleY.getValue();
				centerPanel.setPreferredSize(new Dimension((int)(data.dataPoints/xScale), (int)((data.dataMax-data.dataMin)/yScale)));
				scrollPane.getViewport().revalidate();
				repaint();
			}
		});
		xPanel.add(scaleY);
		
		valueRanges = new JPanel();
		bottomPanel.add(valueRanges);
		valueRanges.setLayout(new GridLayout(0, 2, 0, 0));
		
		lblMaxY = new JLabel("Greatest Value:");
		valueRanges.add(lblMaxY);
		
		maxVal = new JTextField();
		maxVal.setHorizontalAlignment(SwingConstants.RIGHT);
		maxVal.setEditable(false);
		valueRanges.add(maxVal);
		maxVal.setColumns(6);
		
		lblMinY = new JLabel("Least Value:");
		valueRanges.add(lblMinY);
		
		minVal = new JTextField();
		minVal.setHorizontalAlignment(SwingConstants.RIGHT);
		minVal.setEditable(false);
		valueRanges.add(minVal);
		minVal.setColumns(6);
		
		panel = new JPanel();
		bottomPanel.add(panel);
		panel.setLayout(new GridLayout(0, 3, 0, 0));
		
		tglbtnLeftTrim = new JToggleButton("Left Trim");
		tglbtnLeftTrim.setEnabled(false);
		tglbtnLeftTrim.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				leftLine.enabled = tglbtnLeftTrim.isSelected();
				leftTrimSample.setText(leftLine.getXReal() + "");
				leftTrimTime.setText(String.format("%.3f sec", leftLine.getXReal()/(float)(int)freqSpinner.getValue()));
				repaint();
			}
		});
		panel.add(tglbtnLeftTrim);
		
		tglbtnRightTrim = new JToggleButton("Right Trim");
		tglbtnRightTrim.setEnabled(false);
		tglbtnRightTrim.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rightLine.enabled = tglbtnRightTrim.isSelected();
				rightTrimSample.setText(rightLine.getXReal() + "");
				rightTrimTime.setText(String.format("%.3f sec", rightLine.getXReal()/(float)(int)freqSpinner.getValue()));
				repaint();
			}
		});
		
		leftTrimSample = new JTextField();
		panel.add(leftTrimSample);
		leftTrimSample.setEditable(false);
		leftTrimSample.setColumns(10);
		
		leftTrimTime = new JTextField();
		panel.add(leftTrimTime);
		leftTrimTime.setEditable(false);
		leftTrimTime.setColumns(10);
		panel.add(tglbtnRightTrim);
		
		rightTrimSample = new JTextField();
		panel.add(rightTrimSample);
		rightTrimSample.setEditable(false);
		rightTrimSample.setColumns(10);
		
		rightTrimTime = new JTextField();
		panel.add(rightTrimTime);
		rightTrimTime.setEditable(false);
		rightTrimTime.setColumns(10);
	}
	
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
	}
	
}
