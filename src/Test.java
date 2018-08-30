
import java.util.Arrays;

import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;

import FuncTest.*;

public class Test {
	public Test() {
		try {
			//Class1.FuncTest returns the sum of the 2 inputs
			Class1 c = new Class1();
			Object[] objs = c.FuncTest(1, 5,6);
			Object o = objs[0];
			MWNumericArray nma = (MWNumericArray)o;
			System.out.println(Arrays.toString(nma.getIntData()));
		} catch (MWException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Test();
	}

}
