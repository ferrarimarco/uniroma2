package it.uniroma2.disp.mpsr.model;

public class Node {
	
	private boolean instantService;
	private int id;
	private String description;

	public Node(boolean instantService, int id, String description) {
		super();
		this.setInstantService(instantService);
		this.setId(id);
		this.setDescription(description);
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
	
	
	public String getDescription() {
		return description;
	}

	
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString(){
		return "[" + getId() + "]: " + getDescription();
	}
}
