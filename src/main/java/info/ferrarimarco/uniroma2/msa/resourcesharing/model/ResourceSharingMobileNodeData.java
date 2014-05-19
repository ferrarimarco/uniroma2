package info.ferrarimarco.uniroma2.msa.resourcesharing.model;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ResourceSharingMobileNodeData {
	
	@Id
	private String mobileNodeId;
	
	private String location;
	private DateTime lastUpdate;
	
	public ResourceSharingMobileNodeData(String mobileNodeId, String location, DateTime lastUpdate) {
		this.mobileNodeId = mobileNodeId;
		this.location = location;
		this.lastUpdate = lastUpdate;
	}

	@Override
	public String toString() {
		return "ResourceSharingMobileNodeData [mobileNodeId=" + mobileNodeId + ", location=" + location + ", lastUpdate=" + lastUpdate + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((mobileNodeId == null) ? 0 : mobileNodeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ResourceSharingMobileNodeData)) {
			return false;
		}
		ResourceSharingMobileNodeData other = (ResourceSharingMobileNodeData) obj;
		if (lastUpdate == null) {
			if (other.lastUpdate != null) {
				return false;
			}
		} else if (!lastUpdate.equals(other.lastUpdate)) {
			return false;
		}
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (mobileNodeId == null) {
			if (other.mobileNodeId != null) {
				return false;
			}
		} else if (!mobileNodeId.equals(other.mobileNodeId)) {
			return false;
		}
		return true;
	}

	
	public String getMobileNodeId() {
		return mobileNodeId;
	}

	
	public void setMobileNodeId(String mobileNodeId) {
		this.mobileNodeId = mobileNodeId;
	}

	
	public String getLocation() {
		return location;
	}

	
	public void setLocation(String location) {
		this.location = location;
	}

	
	public DateTime getLastUpdate() {
		return lastUpdate;
	}

	
	public void setLastUpdate(DateTime lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	
}
