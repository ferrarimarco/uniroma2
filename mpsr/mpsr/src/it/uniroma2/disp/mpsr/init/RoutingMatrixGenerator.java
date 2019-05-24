package it.uniroma2.disp.mpsr.init;

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
	
	public static SimpleMatrix<Double> generateRoutingMatrixMVA(int rows){
		SimpleMatrix<Double> routingMatrix = new SimpleMatrix<Double>(rows, rows, 0.0);
		
		// Centro 0: client1
		// Centro 1: FE server
		// Centro 2: BE server
		// Centro 3: client2
		// Centro 4: c1_r
		// Centro 5: c2_r
		
		routingMatrix.setElement(0, 0, 0.0); // client1 to client1
		routingMatrix.setElement(0, 1, 1.0); // client1 to FE server
		routingMatrix.setElement(0, 2, 0.0); // client1 to BE server
		routingMatrix.setElement(0, 3, 0.0); // client1 to client2
		routingMatrix.setElement(0, 4, 0.0); // client1 to client1 reject
		routingMatrix.setElement(0, 5, 0.0); // client1 to client2 reject
		
		routingMatrix.setElement(1, 0, 0.0); // FE server to client1
		routingMatrix.setElement(1, 1, 0.0); // FE server to FE server
		routingMatrix.setElement(1, 2, 1.0); // FE server to BE server
		routingMatrix.setElement(1, 3, 0.0); // FE server to client2
		routingMatrix.setElement(1, 4, 0.0); // FE server to client1 reject
		routingMatrix.setElement(1, 5, 0.0); // FE server to client2 reject
		
		routingMatrix.setElement(2, 0, 0.048); // BE server to client1
		routingMatrix.setElement(2, 1, 0.0); // BE server to FE server
		routingMatrix.setElement(2, 2, 0.0); // BE server to BE server
		routingMatrix.setElement(2, 3, 0.952); // BE server to client2
		routingMatrix.setElement(2, 4, 0.0); // BE server to client1 reject
		routingMatrix.setElement(2, 5, 0.0); // BE server to client2 reject
		
		routingMatrix.setElement(3, 0, 0.0); // client2 to client1
		routingMatrix.setElement(3, 1, 1.0); // client2 to FE server
		routingMatrix.setElement(3, 2, 0.0); // client2 to BE server
		routingMatrix.setElement(3, 3, 0.0); // client2 to client2
		routingMatrix.setElement(3, 4, 0.0); // client2 to client1 reject
		routingMatrix.setElement(3, 5, 0.0); // client2 to client2 reject
		
		routingMatrix.setElement(4, 0, 1.0); // client1 reject to client1
		routingMatrix.setElement(4, 1, 0.0); // client1 reject to FE server
		routingMatrix.setElement(4, 2, 0.0); // client1 reject to BE server
		routingMatrix.setElement(4, 3, 0.0); // client1 reject to client2
		routingMatrix.setElement(4, 4, 0.0); // client1 reject to client1 reject
		routingMatrix.setElement(4, 5, 0.0); // client1 reject to client2 reject
		
		routingMatrix.setElement(5, 0, 0.0); // client2 reject to client1
		routingMatrix.setElement(5, 1, 0.0); // client2 reject to FE server
		routingMatrix.setElement(5, 2, 0.0); // client2 reject to BE server
		routingMatrix.setElement(5, 3, 1.0); // client2 reject to client2
		routingMatrix.setElement(5, 4, 0.0); // client2 reject to client1 reject
		routingMatrix.setElement(5, 5, 0.0); // client2 reject to client2 reject
		
		return routingMatrix;
	}

}
