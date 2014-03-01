package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

/**
 * Created by Marco on 28/02/14.
 */
@DatabaseTable
public class Resource {

	@DatabaseField(id = true, canBeNull = false, generatedId = true)
	private String id;

	@DatabaseField
	private String title;

	@DatabaseField
	private String description;

	@DatabaseField
	private String location;

	@DatabaseField
	private DateTime creationTime;

	@DatabaseField
	private String acquisitionMode;

	@DatabaseField
	private String creatorId;

	public Resource() {
	}

	public Resource(String title, String description, String location, DateTime creationTime, String acquisitionMode, String creatorId) {
		this.title = title;
		this.description = description;
		this.location = location;
		this.creationTime = creationTime;
		this.acquisitionMode = acquisitionMode;
		this.creatorId = creatorId;
	}

	@Override
	public String toString() {
		return "Resource{" +
				"id='" + id + '\'' +
				", title='" + title + '\'' +
				", description='" + description + '\'' +
				", location='" + location + '\'' +
				", creationTime=" + creationTime +
				", acquisitionMode='" + acquisitionMode + '\'' +
				", creatorId='" + creatorId + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Resource resource = (Resource) o;

		if (!id.equals(resource.id)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DateTime getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(DateTime creationTime) {
		this.creationTime = creationTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAcquisitionMode() {
		return acquisitionMode;
	}

	public void setAcquisitionMode(String acquisitionMode) {
		this.acquisitionMode = acquisitionMode;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}
}
