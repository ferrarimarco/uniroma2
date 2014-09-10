package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;


@DatabaseTable
public class Resource implements GenericEntity, Parcelable{

    public enum ResourceType{
        NEW,
        CREATED_BY_ME
    }

    public final static Parcelable.Creator<Resource> CREATOR = new Parcelable.Creator<Resource>(){
        @Override
        public Resource createFromParcel(Parcel source){
            return new Resource(source);
        }

        @Override
        public Resource[] newArray(int size){
            return new Resource[size];
        }
    };

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

    public Resource(){
        type = ResourceType.NEW;
        expired = false;
    }

    public Resource(String title, String description, String location, DateTime creationTime, String acquisitionMode, String creatorId, ResourceType type, Boolean expired){
        this();
        this.title = title;
        this.description = description;
        this.location = location;
        this.creationTime = creationTime;
        this.acquisitionMode = acquisitionMode;
        this.creatorId = creatorId;
        this.type = type;
        this.expired = expired;
    }

    public Resource(Parcel in){
        this.androidId = in.readLong();
        this.backendId = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.location = in.readString();
        this.creationTime = (DateTime) in.readSerializable();
        this.acquisitionMode = in.readString();
        this.creatorId = in.readString();
        this.type = (ResourceType) in.readSerializable();
        this.expired = in.readByte() != 0;
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeLong(androidId);
        dest.writeString(backendId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeSerializable(creationTime);
        dest.writeString(acquisitionMode);
        dest.writeString(creatorId);
        dest.writeSerializable(type);
        dest.writeByte((byte) (expired ? 1 : 0));
    }

    @Override
    public String toString(){
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
                '}';
    }

    @Override
    public boolean equals(Object o){
        if(this == o)
            return true;
        if(!(o instanceof Resource))
            return false;

        Resource resource = (Resource) o;

        if(acquisitionMode != null ? !acquisitionMode.equals(resource.acquisitionMode) : resource.acquisitionMode != null)
            return false;
        if(creationTime != null ? !creationTime.equals(resource.creationTime) : resource.creationTime != null)
            return false;
        if(creatorId != null ? !creatorId.equals(resource.creatorId) : resource.creatorId != null)
            return false;
        if(description != null ? !description.equals(resource.description) : resource.description != null)
            return false;
        if(androidId != null ? !androidId.equals(resource.androidId) : resource.androidId != null)
            return false;
        if(location != null ? !location.equals(resource.location) : resource.location != null)
            return false;
        if(title != null ? !title.equals(resource.title) : resource.title != null)
            return false;
        if(type != null ? !type.equals(resource.type) : resource.type != null)
            return false;
        if(expired != null ? !expired.equals(resource.expired) : resource.expired != null)
            return false;
        return !(backendId != null ? !backendId.equals(resource.backendId) : resource.backendId != null);

    }

    @Override
    public int hashCode(){
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

    public Long getAndroidId(){
        return androidId;
    }

    public void setAndroidId(Long androidId){
        this.androidId = androidId;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public DateTime getCreationTime(){
        return creationTime;
    }

    public void setCreationTime(DateTime creationTime){
        this.creationTime = creationTime;
    }

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String getAcquisitionMode(){
        return acquisitionMode;
    }

    public void setAcquisitionMode(String acquisitionMode){
        this.acquisitionMode = acquisitionMode;
    }

    public String getCreatorId(){
        return creatorId;
    }

    public void setCreatorId(String creatorId){
        this.creatorId = creatorId;
    }

    public ResourceType getType(){
        return type;
    }

    public void setType(ResourceType type){
        this.type = type;
    }

    public Boolean isExpired(){
        return expired;
    }

    public void setExpired(Boolean expired){
        this.expired = expired;
    }

    public String getBackendId(){
        return backendId;
    }

    public void setBackendId(String backendId){
        this.backendId = backendId;
    }
}
