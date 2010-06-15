package interfaces;

import java.util.ArrayList;

public interface Generator {


	public Number getActualValue();
	public Number getNextValue();
	
	public ArrayList<? extends Number> getActualSequence();
	
}
