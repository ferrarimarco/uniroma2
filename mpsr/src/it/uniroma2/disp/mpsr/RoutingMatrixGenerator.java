package it.uniroma2.disp.mpsr;

import info.ferrarimarco.java.helper.math.SimpleMatrix;

public class RoutingMatrixGenerator {
	
	
	public static SimpleMatrix<Double> generateRoutingMatrix(int rows){
		
		SimpleMatrix<Double> routingMatrix = new SimpleMatrix<Double>(rows, rows, 0.0);
		
		// Centro 0: client1
		// Centro 1: FE server
		// Centro 2: BE server
		// Centro 3: client2
		
		routingMatrix.setElement(0, 0, 0.0); // client1 to client1
		routingMatrix.setElement(0, 1, 1.0); // client1 to FE server
		routingMatrix.setElement(0, 2, 0.0); // client1 to BE server
		routingMatrix.setElement(0, 3, 0.0); // client1 to client2
		
		routingMatrix.setElement(1, 0, 0.0); // FE server to client1
		routingMatrix.setElement(1, 1, 0.0); // FE server to FE server
		routingMatrix.setElement(1, 2, 1.0); // FE server to BE server
		routingMatrix.setElement(1, 3, 0.0); // FE server to client2
		
		routingMatrix.setElement(2, 0, 0.048); // BE server to client1
		routingMatrix.setElement(2, 1, 0.0); // BE server to FE server
		routingMatrix.setElement(2, 2, 0.0); // BE server to BE server
		routingMatrix.setElement(2, 3, 0.952); // BE server to client2
		
		routingMatrix.setElement(3, 0, 0.0); // client2 to client1
		routingMatrix.setElement(3, 1, 1.0); // client2 to FE server
		routingMatrix.setElement(3, 2, 0.0); // client2 to BE server
		routingMatrix.setElement(3, 3, 0.0); // client2 to client2
		
		return routingMatrix;
	}

}
