package info.ferrarimarco.uniroma2.is.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class Product extends Entity{
    
    @NonNull
    @DBRef
    private Category category;
    
    @NonNull
    @DBRef
    private Clazz clazz;
    private String barCode;
    
    @NonNull
    private String brand;
    
    @Transient
    private Long amount = 0L;
    
    private Long requested = 0L;
    private Long dispensed = 0L;
    private Long expired = 0L;
    private Long stocked = 0L;
}
