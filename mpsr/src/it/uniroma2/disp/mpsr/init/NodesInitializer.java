package it.uniroma2.disp.mpsr.init;

import it.uniroma2.disp.mpsr.model.Node;

import java.util.ArrayList;
import java.util.List;

public class NodesInitializer {
	
	public static List<Node> generateNodes(int nodesNumber){
		
		List<Node> nodes = new ArrayList<Node>(nodesNumber);
		
		nodes.add(new Node(true, 0, "Client1")); // client1
		nodes.add(new Node(false, 1, "FE server")); // FE server
		nodes.add(new Node(false, 2, "BE server")); // BE server
		nodes.add(new Node(true, 3, "Client2")); // client2
		
		return nodes;
	}
	
	public static List<Node> generatesNodesForMVA(int nodesNumber){
		
		List<Node> nodes = generateNodes(nodesNumber);
		
		nodes.add(new Node(true, 4, "Client1 Reject")); // client1 reject
		nodes.add(new Node(true, 5, "Client2 Reject")); // client2 reject
		
		return nodes;
	}
	
}
