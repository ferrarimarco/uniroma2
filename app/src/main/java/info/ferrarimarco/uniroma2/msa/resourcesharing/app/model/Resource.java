package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;


@DatabaseTable
public class Resource implements GenericEntity {

    public enum ResourceType {
        NEW,
        CREATED_BY_ME
    }

    @DatabaseField(generatedId = true, canBeNull = false, allowGeneratedIdInsert = true)
    private Long androidId;

    @DatabaseField
    private String backendId;

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

    @DatabaseField
    private ResourceType type;

    @DatabaseField
    private Boolean expired;

    @DatabaseField
    private Boolean sentToBackend;

    public Resource() {
        type = ResourceType.NEW;
        expired = false;
        sentToBackend = false;
    }

    public Resource(String title, String description, String location, DateTime creationTime, String acquisitionMode, String creatorId, ResourceType type, Boolean expired, Boolean sentToBackend) {
        this();
        this.title = title;
        this.description = description;
        this.location = location;
        this.creationTime = creationTime;
        this.acquisitionMode = acquisitionMode;
        this.creatorId = creatorId;
        this.type = type;
        this.expired = expired;
        this.sentToBackend = sentToBackend;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "androidId='" + androidId + '\'' +
                ", backendId='" + backendId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", creationTime=" + creationTime +
                ", acquisitionMode='" + acquisitionMode + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", type='" + type.toString() + '\'' +
                ", IsExpired='" + expired.toString() + '\'' +
                ", IsSentToBackend='" + sentToBackend.toString() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Resource))
            return false;

        Resource resource = (Resource) o;

        if (acquisitionMode != null ? !acquisitionMode.equals(resource.acquisitionMode) : resource.acquisitionMode != null)
            return false;
        if (creationTime != null ? !creationTime.equals(resource.creationTime) : resource.creationTime != null)
            return false;
        if (creatorId != null ? !creatorId.equals(resource.creatorId) : resource.creatorId != null)
            return false;
        if (description != null ? !description.equals(resource.description) : resource.description != null)
            return false;
        if (androidId != null ? !androidId.equals(resource.androidId) : resource.androidId != null)
            return false;
        if (location != null ? !location.equals(resource.location) : resource.location != null)
            return false;
        if (title != null ? !title.equals(resource.title) : resource.title != null)
            return false;
        if (type != null ? !type.equals(resource.type) : resource.type != null)
            return false;
        if (expired != null ? !expired.equals(resource.expired) : resource.expired != null)
            return false;
        if (sentToBackend != null ? !sentToBackend.equals(resource.sentToBackend) : resource.sentToBackend != null)
            return false;
        return !(backendId != null ? !backendId.equals(resource.backendId) : resource.backendId != null);

    }

    @Override
    public int hashCode() {
        int result = androidId != null ? androidId.hashCode() : 0;
        result = 31 * result + (backendId != null ? backendId.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (creationTime != null ? creationTime.hashCode() : 0);
        result = 31 * result + (acquisitionMode != null ? acquisitionMode.hashCode() : 0);
        result = 31 * result + (creatorId != null ? creatorId.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (expired != null ? expired.hashCode() : 0);
        return result;
    }

    public Long getAndroidId() {
        return androidId;
    }

    public void setAndroidId(Long androidId) {
        this.androidId = androidId;
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

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public Boolean isExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public String getBackendId() {
        return backendId;
    }

    public void setBackendId(String backendId) {
        this.backendId = backendId;
    }

    public Boolean isSentToBackend() {
        return sentToBackend;
    }

    public void setSentToBackend(Boolean sentToBackend) {
        this.sentToBackend = sentToBackend;
    }
}
