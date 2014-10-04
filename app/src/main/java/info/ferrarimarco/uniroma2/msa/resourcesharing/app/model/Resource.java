package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;


@DatabaseTable
public class Resource implements GenericEntity, Parcelable{

    public enum ResourceType{
        CREATED_BY_OTHERS,
        CREATED_BY_ME,
        BOOKED_BY_ME
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
    private String title;

    @DatabaseField
    private String description;

    @DatabaseField
    private Double latitude;

    @DatabaseField
    private Double longitude;

    @DatabaseField
    private String locality;

    @DatabaseField
    private String country;

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
    private String bookerId;

    private Long timeToLive;

    public Resource(){
        this(ResourceType.CREATED_BY_OTHERS);
    }

    public Resource(ResourceType resourceType){
        type = resourceType;
        expired = false;
    }

    public Resource(String title, String description, Double latitude, Double longitude, String locality, String country, DateTime creationTime, String acquisitionMode, String creatorId, ResourceType type, Boolean expired, Long timeToLive, String bookerId) {
        this();
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locality = locality;
        this.country = country;
        this.creationTime = creationTime;
        this.acquisitionMode = acquisitionMode;
        this.creatorId = creatorId;
        this.type = type;
        this.expired = expired;
        this.timeToLive = timeToLive;
        this.bookerId = bookerId;
    }

    public Resource(Parcel in){
        this.androidId = in.readLong();
        this.title = in.readString();
        this.description = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.locality = in.readString();
        this.country = in.readString();
        this.creationTime = (DateTime) in.readSerializable();
        this.acquisitionMode = in.readString();
        this.creatorId = in.readString();
        this.type = (ResourceType) in.readSerializable();
        this.expired = in.readByte() != 0;
        this.timeToLive = in.readLong();
        this.bookerId = in.readString();
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        if(androidId != null){
            dest.writeLong(androidId);
        }else{
            dest.writeLong(-1L);
        }
        dest.writeString(title);
        dest.writeString(description);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(locality);
        dest.writeString(country);
        dest.writeSerializable(creationTime);
        dest.writeString(acquisitionMode);
        dest.writeString(creatorId);
        dest.writeSerializable(type);
        dest.writeByte((byte) (expired ? 1 : 0));
        if(timeToLive != null){
            dest.writeLong(timeToLive);
        }else{
            dest.writeLong(-1L);
        }

        dest.writeString(bookerId);
    }

    @Override
    public String toString(){
        return "Resource{" +
                "androidId='" + androidId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", locality='" + locality + '\'' +
                ", country='" + country + '\'' +
                ", creationTime=" + creationTime +
                ", acquisitionMode='" + acquisitionMode + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", type='" + type.toString() + '\'' +
                ", IsExpired='" + expired.toString() + '\'' +
                ", TimeToLive='" + timeToLive.toString() + '\'' +
                ", BookerId='" + bookerId + '\'' +
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
        if(latitude != null ? !latitude.equals(resource.latitude) : resource.latitude != null)
            return false;
        if(longitude != null ? !longitude.equals(resource.longitude) : resource.longitude != null)
            return false;
        if (locality != null ? !locality.equals(resource.locality) : resource.locality != null)
            return false;
        if (country != null ? !country.equals(resource.country) : resource.country != null)
            return false;
        if(title != null ? !title.equals(resource.title) : resource.title != null)
            return false;
        if(type != null ? !type.equals(resource.type) : resource.type != null)
            return false;
        if(timeToLive != null ? timeToLive.equals(resource.timeToLive) : resource.timeToLive != null)
            return false;
        if(bookerId != null ? bookerId.equals(resource.bookerId) : resource.bookerId != null)
            return false;
        return !(expired != null ? !expired.equals(resource.expired) : resource.expired != null);

    }

    @Override
    public int hashCode(){
        int result = androidId != null ? androidId.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (locality != null ? locality.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (creationTime != null ? creationTime.hashCode() : 0);
        result = 31 * result + (acquisitionMode != null ? acquisitionMode.hashCode() : 0);
        result = 31 * result + (creatorId != null ? creatorId.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (expired != null ? expired.hashCode() : 0);
        result = 31 * result + (bookerId != null ? bookerId.hashCode() : 0);
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

    public Double getLatitude(){
        return latitude;
    }

    public void setLatitude(Double latitude){
        this.latitude = latitude;
    }

    public Double getLocation(){
        return longitude;
    }

    public void setLongitude(Double longitude){
        this.longitude = longitude;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public Long getTimeToLive(){
        return timeToLive;
    }

    public String getBookerId(){
        return bookerId;
    }

    public void setBookerId(String bookerId){
        this.bookerId = bookerId;
    }
}
