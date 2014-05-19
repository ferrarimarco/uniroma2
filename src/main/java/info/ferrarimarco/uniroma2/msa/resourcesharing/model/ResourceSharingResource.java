package info.ferrarimarco.uniroma2.msa.resourcesharing.model;

import java.lang.reflect.Field;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ResourceSharingResource implements ResourceSharingSearchCriterion {

	@Id
	private String id;

	private String title;
	private String description;
	private String location;
	private DateTime creationTime;
	private String acquisitionMode;
	private String creatorId;

	public ResourceSharingResource(String title, String description, String location, String acquisitionMode, String creatorId) {
		this.title = title;
		this.description = description;
		this.location = location;
		this.creationTime = new DateTime();
		this.acquisitionMode = acquisitionMode;
		this.creatorId = creatorId;
	}

	@Override
	public String toString() {
		return "ResourceSharingResource [id=" + id + ", title=" + title
				+ ", description=" + description + ", location=" + location
				+ ", creationTime=" + creationTime + ", acquisitionMode="
				+ acquisitionMode + ", creatorId=" + creatorId + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((acquisitionMode == null) ? 0 : acquisitionMode.hashCode());
		result = prime * result
				+ ((creationTime == null) ? 0 : creationTime.hashCode());
		result = prime * result
				+ ((creatorId == null) ? 0 : creatorId.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ResourceSharingResource)) {
			return false;
		}
		ResourceSharingResource other = (ResourceSharingResource) obj;
		if (acquisitionMode == null) {
			if (other.acquisitionMode != null) {
				return false;
			}
		} else if (!acquisitionMode.equals(other.acquisitionMode)) {
			return false;
		}
		if (creationTime == null) {
			if (other.creationTime != null) {
				return false;
			}
		} else if (!creationTime.equals(other.creationTime)) {
			return false;
		}
		if (creatorId == null) {
			if (other.creatorId != null) {
				return false;
			}
		} else if (!creatorId.equals(other.creatorId)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		return true;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public DateTime getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(DateTime creationTime) {
		this.creationTime = creationTime;
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

	@Override
	public boolean isValid() {

		boolean result = false;

		// Check if there is at least one not-null field
		for (Field f : this.getClass().getDeclaredFields()) {
			f.setAccessible(true);
			
			if (!f.getType().equals(org.slf4j.Logger.class) && !f.getName().equalsIgnoreCase("__cobertura_counters")) {
				try {
					result = f.get(this) == null || result;
				} catch (IllegalArgumentException | IllegalAccessException e) {
					result = false || result;
				}
			}
		}

		return result;
	}
}
