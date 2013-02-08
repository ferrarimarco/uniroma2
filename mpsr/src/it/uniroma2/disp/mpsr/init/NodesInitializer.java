package it.uniroma2.disp.mpsr.init;

import it.uniroma2.disp.mpsr.model.Node;

import java.util.ArrayList;
import java.util.List;

public class NodesInitializer {
	
	public static List<Node> generateNodes(int nodesNumber){
		
		List<Node> nodes = new ArrayList<Node>(nodesNumber);
		
		nodes.add(new Node(true, 0)); // client1
		nodes.add(new Node(false, 1)); // FE server
		nodes.add(new Node(false, 2)); // BE server
		nodes.add(new Node(true, 3)); // client2
		
		return nodes;
	}
	
}
