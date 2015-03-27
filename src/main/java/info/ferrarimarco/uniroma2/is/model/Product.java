package info.ferrarimarco.uniroma2.is.model;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Product extends Entity{
    @NonNull
    @DBRef
    private Clazz clazz;
    private String barCode;
    
    @NonNull
    private String brand;
    private int amount;
    
    // This is field is used when an instance has a DTO role
    @Transient
    private String clazzId;
}
