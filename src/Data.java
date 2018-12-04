import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.mipt.edf.EDFParser;
import ru.mipt.edf.EDFParserResult;

public class Data{
		protected List<double[]> matrix;
		protected int dataMax, dataMin, channels, dataPoints;
		protected EDFParserResult result;
		
		protected void loadData(File f) {
			
			try {
				FileInputStream fis = new FileInputStream(f);
				result = EDFParser.parseEDF(fis);
				fis.close();
				matrix = new ArrayList<>();
				
				short[][] signal = result.getSignal().getDigitalValues();

				//ALL = COUNTER,INTERPOLATED,AF3,F7,F3,FC5,T7,P7,O1,O2,P8,T8,FC6,F4,F8,AF4,RAW_CQ,GYROX,GYROY,MARKER,SYNC,TIME_STAMP_s,TIME_STAMP_ms,CQ_AF3,CQ_F7,CQ_F3,CQ_FC5,CQ_T7,CQ_P7,CQ_O1,CQ_O2,CQ_P8,CQ_T8,CQ_FC6,CQ_F4,CQ_F8,CQ_AF4,CQ_CMS          CQ_DRL
				//Selected = AF3,F7,F3,FC5,T7,P7,O1,O2,P8,T8,FC6,F4,F8,AF4
				int[] is = {2,3,4,5,6,7,8,9,10,11,12,13,14,15};
				
				for (int i : is)
					matrix.add(shortToDouble(signal[i]));
				
				dataMax = getMaxValue(matrix);
				dataMin = getMinValue(matrix);
				channels = matrix.size();
				dataPoints = matrix.get(0).length;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
				
		}
		
		private double[] shortToDouble(short[] s) {
			double d[] = new double[s.length];
			for (int i = 0; i < s.length; i++) 
				d[i] = s[i];
			return d;
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
			channels = matrix.size();
			dataPoints = matrix.get(0).length;
			dataMax = getMaxValue(matrix);
			dataMin = getMinValue(matrix);
		}
	}