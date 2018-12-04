
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import ru.mipt.edf.*;

public class test {

	public test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		FileInputStream fis;
		try {
			
			fis = new FileInputStream(new File("file.edf"));
			
			EDFParserResult result = EDFParser.parseEDF(fis);
			EDFSignal signal = result.getSignal();
			
//			int i = 0;
//			for (short[] sa : r.getSignal().getDigitalValues()) {
//				System.out.println(i++);
////				System.out.println(Arrays.toString(sa));
//			}
			
//			System.out.println(r.getHeader().getNumberOfRecords());
//			
//			System.out.println(Arrays.toString(r.getSignal().getDigitalValues()[0]));
//			System.out.println(r.getSignal().getDigitalValues()[0].length);
			
			
			
//			double[][] vals = signal.getValuesInUnits();
//			
//			for (int i = 0; i < vals.length; i++) {
//				vals[i] = Arrays.copyOfRange(vals[i], 0, 10);
//			}
//			
//			for (double[] v : vals)
//				System.out.println(Arrays.toString(v));
			FileOutputStream fos = new FileOutputStream(new File("file2.edf"));
			EDFWriter.writeIntoOutputStream(result.getHeader(), fos);
			EDFWriter.writeIntoOutputStream(signal, result.getHeader(), fos);

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (EDFParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
