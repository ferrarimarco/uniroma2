package info.ferrarimarco.uniroma2.msa.resourcesharing.app.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

@DatabaseTable
public class User implements GenericEntity{

    @DatabaseField(generatedId = true, canBeNull = false, allowGeneratedIdInsert = true)
    private Long id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String email;

    @DatabaseField
    private String hashedPassword;

    @DatabaseField
    private DateTime lastAccess;

    public User(){
    }

    public User(String name, String email, String hashedPassword, DateTime lastAccess){
        this.name = name;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.lastAccess = lastAccess;
    }

    @Override
    public String toString(){
        return "User [id=" + id + ", name=" + name + ", email=" + email + ", hashedPassword=" + hashedPassword + ", lastAccess=" + lastAccess + "]";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((hashedPassword == null) ? 0 : hashedPassword.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((lastAccess == null) ? 0 : lastAccess.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null){
            return false;
        }
        if(!(obj instanceof User)){
            return false;
        }
        User other = (User) obj;
        if(email == null){
            if(other.email != null){
                return false;
            }
        }else if(!email.equals(other.email)){
            return false;
        }
        if(hashedPassword == null){
            if(other.hashedPassword != null){
                return false;
            }
        }else if(!hashedPassword.equals(other.hashedPassword)){
            return false;
        }
        if(id == null){
            if(other.id != null){
                return false;
            }
        }else if(!id.equals(other.id)){
            return false;
        }
        if(lastAccess == null){
            if(other.lastAccess != null){
                return false;
            }
        }else if(!lastAccess.equals(other.lastAccess)){
            return false;
        }
        if(name == null){
            if(other.name != null){
                return false;
            }
        }else if(!name.equals(other.name)){
            return false;
        }
        return true;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }


    public String getEmail(){
        return email;
    }


    public void setEmail(String email){
        this.email = email;
    }

    public String getHashedPassword(){
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword){
        this.hashedPassword = hashedPassword;
    }

    public DateTime getLastAccess(){
        return lastAccess;
    }

    public void setLastAccess(DateTime lastAccess){
        this.lastAccess = lastAccess;
    }
}
