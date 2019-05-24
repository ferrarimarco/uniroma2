package it.uniroma2.disp.mpsr.init;

import java.util.ArrayList;
import java.util.List;

import info.ferrarimarco.java.helper.math.SimpleMatrix;

public class XYCalculator {

	public static List<Double> computeX(int nodes, SimpleMatrix<Double> routingMatrix, List<Double> mu){
		
		List<Double> x = new ArrayList<Double>(nodes);
		
		x.add(1.0);
		x.add(mu.get(0) / (mu.get(2) * routingMatrix.getElement(2, 0)));
		x.add(mu.get(0) / (mu.get(1) * routingMatrix.getElement(2, 0)));
		x.add((mu.get(0) * routingMatrix.getElement(2, 3)) / (mu.get(3) * routingMatrix.getElement(2, 0)));
		
		return x;
	}
	
	public static List<Double> computeYMVA(int nodes, SimpleMatrix<Double> routingMatrix, List<Double> mu){
		
		List<Double> y = new ArrayList<Double>(nodes);
		
		for(int i = 0; i < nodes; i++){
			y.add(0.0);
		}
		
		y.set(2, 1.0);
		y.set(0, (y.get(2) * routingMatrix.getElement(2, 0)) / (1.0 - routingMatrix.getElement(0, 4)));
		y.set(1, y.get(2));	
		y.set(3, (y.get(2) * routingMatrix.getElement(2, 3)) / (1 - routingMatrix.getElement(3, 5)));		
		y.set(4, y.get(0) * routingMatrix.getElement(0, 4));		
		y.set(5, y.get(3) * routingMatrix.getElement(3, 5));
		
		return y;
	}	
}
