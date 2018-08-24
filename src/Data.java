import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Data{
		protected List<double[]> matrix;
		protected int dataMax, dataMin, channels, dataPoints;
		
		protected void loadData(File f) {
			Scanner scan;
			try {
				scan = new Scanner(f);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
				return;
			}
			
			matrix = new ArrayList<>();
			while(scan.hasNextLine()) {
				String line = scan.nextLine();
				double[] rowValues = Arrays.stream(line.split(",")).mapToDouble(Double::parseDouble).toArray();
				matrix.add(rowValues);
			}
			scan.close();
			transpose();
			dataMax = getMaxValue(matrix);
			dataMin = getMinValue(matrix);
			channels = matrix.size();
			dataPoints = matrix.get(0).length;
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
		
		protected void transpose() {
			List<double[]> n = new ArrayList<>();
			int rows = matrix.get(0).length;
			int cols = matrix.size();
			
			for (int i = 0; i < rows; i++) {
				double[] row = new double[cols];
				
				//Populate row with i-th column values
				for (int j = 0; j < matrix.size(); j++) 
					row[j] = matrix.get(j)[i];
				
				n.add(row);
			}
			matrix = n;
		}
	}