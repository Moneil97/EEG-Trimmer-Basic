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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.ColorUIResource;

import ru.mipt.edf.EDFHeader;
import ru.mipt.edf.EDFSignal;
import ru.mipt.edf.EDFWriter;

public class TrimmerGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private Color[] colors = {Color.black, Color.red, Color.blue, Color.orange, Color.cyan, Color.green, Color.magenta, Color.pink};
	private JPanel contentPane, centerPanel, scalePanel, panel, panel_1;
	private JTextField leftTrimTime, rightTrimTime;
	private JLabel lblScaleX, lblScaleY;
	private JToggleButton tglbtnLeftTrim, tglbtnRightTrim;
	private JTextField freqBox;
	private JButton btnScaleToFit, btnSaveData;
	private DraggableLine leftLine, rightLine;
	private JScrollPane scrollPane;
	private double yScale=1, xScale=1;
	private boolean dataLoaded = false;
	private JButton plusButtonX, minusButtonX, plusButtonY, minusButtonY;
	private JButton[] scaleButtons;
	private Data data = new Data();
	private String fileName = "";
	private String lastLoadDir = ".", lastSaveDir = ".";
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TrimmerGUI frame = new TrimmerGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public TrimmerGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1049, 700);
		setMinimumSize(new Dimension(890, 500));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		UIManager.put("ToggleButton.select", new ColorUIResource( Color.GREEN ));
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
					if (leftLine.dragging) 
						leftLine.setXDrawn(e.getX(), xScale);
					else if (rightLine.dragging) 
						rightLine.setXDrawn(e.getX(), xScale);
					updateTrimData();
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
						}
					}
					if (rightLine.dragging) {
						rightLine.dragging = false;
						if (rightLine.getXReal() > data.dataPoints) {
							rightLine.setXReal(data.dataPoints, xScale);
						}
					}
					updateTrimData();
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
				int numSelected = rightTrim-leftTrim;
				int toRemove =  numSelected % freq;
				int toAdd = freq - toRemove;
				
				//ensure data samples are a multiple of the frequency
				if ((rightTrim-leftTrim) % freq != 0) {
					
					if ((leftTrim-toAdd) >= 0) {
						leftLine.enabled = true;
						leftLine.setXReal(leftTrim-toAdd);
					}
					else if ((rightTrim+toAdd) < data.dataPoints) {
						rightLine.enabled = true;
						rightLine.setXReal(rightTrim+toAdd);
					}
					else if ((leftTrim+toRemove) < rightTrim) {
						leftLine.enabled = true;
						leftLine.setXReal(leftTrim+toRemove);
					}
					else if (leftTrim < (rightTrim-toRemove)) {
						rightLine.enabled = true;
						rightLine.setXReal(rightTrim-toRemove);
					}
					else {
						JOptionPane.showMessageDialog(TrimmerGUI.this, "Failed to find rounding point", "Rounding Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					updateTrimData();
					repaint();
				}
				
				try {
					if (saveFile(data, leftLine, rightLine) == 1)
						JOptionPane.showMessageDialog(TrimmerGUI.this, "Save Succesful", "Save", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(TrimmerGUI.this, "Save Failed", "Save", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnSaveData.setEnabled(false);
		panel_1.add(btnSaveData);
		
		
		freqBox = new JTextField();
		freqBox.setEditable(false);
		panel_1.add(freqBox);

		btnLoadData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				FileNameExtensionFilter filter = new FileNameExtensionFilter("edf", "edf");
				JFileChooser fc = new JFileChooser(lastLoadDir);
				fc.setFileFilter(filter);
				fc.showOpenDialog(TrimmerGUI.this);
				File f = fc.getSelectedFile();
				
				if (f != null && f.exists()) {
					lastLoadDir = f.getParent();
					fileName = f.getName();
					fileName = fileName.substring(0, fileName.lastIndexOf("."));
					data.loadData(f);
					freqBox.setText(data.freq + " Hz");
					leftLine = new DraggableLine(0, xScale, true);
					rightLine = new DraggableLine(centerPanel.getWidth(), xScale, false);
					scaleToFitWindow();
					rightLine.setXReal(data.dataPoints, xScale);
					btnScaleToFit.setEnabled(true);
					tglbtnLeftTrim.setEnabled(true);
					tglbtnRightTrim.setEnabled(true);
					btnSaveData.setEnabled(true);
					dataLoaded = true;
					tglbtnLeftTrim.setSelected(false);
					tglbtnRightTrim.setSelected(false);
					for (JButton b : scaleButtons)
						b.setEnabled(true);
					updateTrimData();
				}
				
				scrollPane.getViewport().revalidate();
				repaint();
			}
		});
		
		scalePanel = new JPanel();
		bottomPanel.add(scalePanel);
		scalePanel.setLayout(new GridLayout(0, 3, 0, 0));
		
		ActionListener scaleListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == plusButtonX)
					xScale /= 1.25;
				else if (e.getSource() == minusButtonX)
					xScale = Math.min(xScale*1.25, getFitX()); //don't let it zoom out too far
				else if (e.getSource() == plusButtonY) 
					yScale /= 1.25;
				else 
					yScale *= 1.25;
				
//				System.out.println(xScale + " : " + yScale);
				leftLine.changeScale(xScale);
				rightLine.changeScale(xScale);
				centerPanel.setPreferredSize(new Dimension((int)(data.dataPoints/xScale), (int)((data.dataMax-data.dataMin)/yScale)));
				scrollPane.getViewport().revalidate();
				repaint();
			}
		};
		
		plusButtonX = new JButton("+");
		plusButtonX.addActionListener(scaleListener);
		scalePanel.add(plusButtonX);
		
		lblScaleX = new JLabel(" X Scale ");
		scalePanel.add(lblScaleX);
		
		minusButtonX = new JButton("-");
		minusButtonX.addActionListener(scaleListener);
		scalePanel.add(minusButtonX);
		
		plusButtonY = new JButton("+");
		plusButtonY.addActionListener(scaleListener);
		scalePanel.add(plusButtonY);
		
		lblScaleY = new JLabel(" Y Scale ");
		scalePanel.add(lblScaleY);
		
		minusButtonY = new JButton("-");
		minusButtonY.addActionListener(scaleListener);
		scalePanel.add(minusButtonY);
		
		scaleButtons = new JButton[]{plusButtonX, minusButtonX, plusButtonY, minusButtonY};
		
		for (JButton b : scaleButtons)
			b.setEnabled(false);
		
		panel = new JPanel();
		bottomPanel.add(panel);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		tglbtnLeftTrim = new JToggleButton("Left Trimmer");
		tglbtnLeftTrim.setEnabled(false);
		tglbtnLeftTrim.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				leftLine.enabled = tglbtnLeftTrim.isSelected();
				repaint();
			}
		});
		panel.add(tglbtnLeftTrim);
		
		tglbtnRightTrim = new JToggleButton("Right Trimmer");
		tglbtnRightTrim.setEnabled(false);
		tglbtnRightTrim.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rightLine.enabled = tglbtnRightTrim.isSelected();
				repaint();
			}
		});
		
		leftTrimTime = new JTextField();
		panel.add(leftTrimTime);
		leftTrimTime.setEditable(false);
		leftTrimTime.setColumns(10);
		panel.add(tglbtnRightTrim);

		rightTrimTime = new JTextField();
		panel.add(rightTrimTime);
		rightTrimTime.setEditable(false);
		rightTrimTime.setColumns(10);
		
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent componentEvent) {
				scrollPane.getViewport().revalidate();
				repaint();
		    }
		});
	}
	
	private void updateTrimData() {
		leftTrimTime.setText(String.format("%.3f sec", leftLine.getXReal()/(float)data.freq));
		rightTrimTime.setText(String.format("%.3f sec", rightLine.getXReal()/(float)data.freq));
		repaint();
	}
	
	private void scaleToFitWindow() {
		xScale = getFitX();
		yScale = getFitY();
		leftLine.changeScale(xScale);
		rightLine.changeScale(xScale);
		centerPanel.setPreferredSize(new Dimension((int)(data.dataPoints/xScale), (int)((data.dataMax-data.dataMin)/yScale)));
	}
	
	private double getFitX() {
		int width = scrollPane.getWidth()-20;
		return ((double)data.dataPoints)/width;
	}

	private double getFitY() {
		int height = scrollPane.getHeight()-20;
		return ((double)(data.dataMax-data.dataMin))/height;
	}
	
	private int saveFile(Data data, DraggableLine left, DraggableLine right) throws IOException {
		
		JFileChooser fc = new JFileChooser(lastSaveDir);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("edf", "edf");
		fc.setSelectedFile(new File(lastSaveDir + "\\" + fileName + "_trimmed.edf"));
		
		fc.setFileFilter(filter);
		if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			
			File f = fc.getSelectedFile();
			
			if (f.exists()) {
				int selection = JOptionPane.showOptionDialog(TrimmerGUI.this, "This file exists, would you like to overwrite it?", "File Overwrite", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (selection == JOptionPane.NO_OPTION)
					return 0;
			}
		
			lastSaveDir = f.getParent();
			EDFSignal signal = data.result.getSignal();
			EDFHeader header = data.result.getHeader();
			
			int leftTrim = (left.enabled? left.getXReal():0);
			int rightTrim = (right.enabled? right.getXReal():data.dataPoints);
			
			if ((rightTrim-leftTrim) % header.getNumberOfSamples()[0] != 0) {
				JOptionPane.showMessageDialog(TrimmerGUI.this, "Must be a multiple of " + header.getNumberOfSamples()[0], "Save", JOptionPane.INFORMATION_MESSAGE);
				throw new IOException();
			}
			
			//Fix header
			header.numberOfRecords = (rightTrim-leftTrim)/header.getNumberOfSamples()[0];
			
			//Trim the signal
			short[][] vals = signal.getDigitalValues();
			
			for (int i = 0; i < vals.length; i++)
				vals[i] = Arrays.copyOfRange(vals[i], leftTrim, rightTrim);
			
			if (!f.getName().endsWith(".edf"))
				f = new File(f.getPath() + ".edf");
				
			FileOutputStream fout = new FileOutputStream(f);
			EDFWriter.writeIntoOutputStream(header, fout);
			EDFWriter.writeIntoOutputStream(signal, header, fout);
			fout.close();
			return 1;
		}
		return 0;
	}
}
