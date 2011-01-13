package test;

import generators.IperEspGenerator;
import io.FileOutput;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class IperEspTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		IperEspGenerator iperGen = new IperEspGenerator(1L, 3L, 2.0, 0.6);
		ArrayList<String> distrib = new ArrayList<String>();
		ArrayList<String> tempi = new ArrayList<String>();

		Double time = 0.0;
		Double result = 0.0;

		String timeString = "";
		String resultString = "";
		DecimalFormat df = new DecimalFormat("##.#############");

		for (int i = 0; i < 100000; i++) {
			time = iperGen.generateNextValue();
			result = iperGen.getDensity(time);

			timeString = df.format(time);
			resultString = df.format(result);

			distrib.add(resultString);
			tempi.add(timeString);
		}

		FileOutput.textFileWriter(distrib, "C:\\distribIperGen.txt", true, false,
				true);
		FileOutput.textFileWriter(tempi, "C:\\tempiIperGen.txt", true, false, true);

	}

}
