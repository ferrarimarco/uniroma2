package it.uniroma2.disp.mpsr.model;

public class Node {
	
	private boolean instantService;
	private int id;

	public Node(boolean instantService, int id) {
		super();
		this.setInstantService(instantService);
		this.setId(id);
	}

	public boolean isInstantService() {
		return instantService;
	}

	public void setInstantService(boolean instantService) {
		this.instantService = instantService;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
