import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

public class DataTrimmerGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private List<double[]> matrix = new ArrayList<>();

	public static void main(String[] args) {
		
//		List<double[]> n = new ArrayList<>();
//		n.add(new double[]{1.0,2.0,3.0});
//		n.add(new double[]{4.0,5.0,6.0});
//		n.add(new double[]{7.0,8.0,9.0});
//		n.add(new double[]{10.0,11.0,12.0});
//		
//		for (double[] d : n) 
//			System.out.println(Arrays.toString(d));
//		
//		System.out.println("\n");
//		
//		for (double[] d : transpose(n)) 
//			System.out.println(Arrays.toString(d));
		
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
		
		JPanel centerPanel = new JPanel() {

			private static final long serialVersionUID = 1L;
			
			@Override
			protected void paintComponent(Graphics g1) {
				super.paintComponent(g1);
				Graphics2D g = (Graphics2D) g1;
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.setFont(new Font("Arial", Font.PLAIN, 12));
				
				if (matrix.isEmpty()) {
					g.drawString("No Data Loaded", 50, 50);
				}
				else {
					
				}
				
			}
		};
		centerPanel.setBackground(Color.white);
		contentPane.add(centerPanel, BorderLayout.CENTER);
		
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
				for (double[] d : matrix) 
					System.out.println(Arrays.toString(d));
			}
		});
		bottomPanel.add(btnLoadData);
	}
	
	private int getMaxValue(List<double[]> list) {
		double max = Double.MIN_VALUE;
		
		for (double[] row : list)
			for (double d : row)
				max = Double.max(d, max);
		
		return ((int)max+1);
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
