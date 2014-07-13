package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;


@DatabaseTable
public class Resource implements GenericEntity{

    @DatabaseField(generatedId = true, canBeNull = false, allowGeneratedIdInsert = true)
    private Long id;

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

    public Resource(){
    }

    public Resource(String title, String description, String location, DateTime creationTime, String acquisitionMode, String creatorId){
        this.title = title;
        this.description = description;
        this.location = location;
        this.creationTime = creationTime;
        this.acquisitionMode = acquisitionMode;
        this.creatorId = creatorId;
    }

    @Override
    public String toString(){
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
        if(id != null ? !id.equals(resource.id) : resource.id != null)
            return false;
        if(location != null ? !location.equals(resource.location) : resource.location != null)
            return false;
        if(title != null ? !title.equals(resource.title) : resource.title != null)
            return false;

        return true;
    }

    @Override
    public int hashCode(){
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (creationTime != null ? creationTime.hashCode() : 0);
        result = 31 * result + (acquisitionMode != null ? acquisitionMode.hashCode() : 0);
        result = 31 * result + (creatorId != null ? creatorId.hashCode() : 0);
        return result;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
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
}
