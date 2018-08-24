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
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

public class DataTrimmerGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private List<double[]> matrix = new ArrayList<>();
	private int maxVal, minVal;
	private VisibleWindow window = new VisibleWindow();
	private JPanel centerPanel;
	private JScrollPane scrollPane;
	private int dataPoints;
	private double yScale=1, xScale=1;
	
	class VisibleWindow{
		int maxX,maxY,minX,minY;
		int xScale, yScale;
		int x,y;
	}

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
		setBounds(100, 100, 650, 550);
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
				
				if (matrix.isEmpty()) {
					g.drawString("No Data Loaded", 50, 50);
				}
				else {
					for (double[] channel : matrix) {
						for (int i=0; i < channel.length-1; i++)
							g.drawLine((int)(i/xScale), (int)((maxVal-(int)channel[i])/yScale), (int)((i+1)/xScale), (int)((maxVal-(int)channel[i+1])/yScale));
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
				Scanner scan;
				try {
					scan = new Scanner(new File("eeg.csv"));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					return;
				}
				
				while(scan.hasNextLine()) {
					String line = scan.nextLine();
					double[] rowValues = Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray();
					matrix.add(rowValues);
				}
				scan.close();
				
				matrix = transpose(matrix);
//				for (double[] d : matrix) 
//					System.out.println(Arrays.toString(d));
				
				maxVal = getMaxValue(matrix);
				minVal = getMinValue(matrix);
				dataPoints = matrix.get(0).length;
				System.out.println(maxVal + " " + minVal);
				
				//scaleToFitWindow();
//				window.maxY = maxVal;
//				window.minY = minVal;
//				window.minX = 0;
//				window.maxX = matrix.get(0).length;
//				window.x = 0;
//				window.y = 0;

				repaint();
				centerPanel.setPreferredSize(new Dimension(matrix.get(0).length, maxVal));
				//Update Scroll bar UI -hacky, needs a better way
				setSize(getWidth()+1, getHeight()+1);
				
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
	}
	
	private void scaleToFitWindow() {
		int height = scrollPane.getHeight()-20;
		int width = scrollPane.getWidth()-20;
		
		yScale = ((double)maxVal)/height;
		xScale = ((double)dataPoints)/width;
		
		centerPanel.setPreferredSize(new Dimension(width, height));
	}
	
	private int getMaxValue(List<double[]> list) {
		double max = Double.MIN_VALUE;
		
		for (double[] row : list)
			for (double d : row)
				max = Double.max(d, max);
		
		return ((int)max+1);
	}
	
	private int getMinValue(List<double[]> list) {
		double min = Double.MAX_VALUE;
		
		for (double[] row : list)
			for (double d : row)
				min = Double.min(d, min);
		
		return ((int)min-1);
	}

	private List<double[]> transpose(List<double[]> original) {
		List<double[]> n = new ArrayList<>();
		int rows = original.get(0).length;
		int cols = original.size();
		
		for (int i = 0; i < rows; i++) {
			double[] row = new double[cols];
			
			//Populate row with i-th column values
			for (int j = 0; j < original.size(); j++) 
				row[j] = original.get(j)[i];
			
			n.add(row);
		}
		return n;
	}
	
}
