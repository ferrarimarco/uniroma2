package it.mp.claudianiferrari.parserjson;

//This class represents a single entry (post) in the XML feed.
// It includes the data members "title," "link," and "summary."
public class Entry {
    private String title;
    private String link;
    private String summary;

    public Entry(String title, String summary, String link) {
        this.title = title;
        this.summary = summary;
        this.link = link;
    }

    @Override
	public String toString(){
		return title + System.getProperty("line.separator") + link;
	}
    
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	
	public void setLink(String link) {
		this.link = link;
	}

	public String getSummary() {
		return summary;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
}