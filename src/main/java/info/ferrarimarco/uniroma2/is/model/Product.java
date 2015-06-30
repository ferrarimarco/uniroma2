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
    
    @DBRef
    private Category category;
    
    @DBRef
    private Clazz clazz;
    private String barCode;
    
    @NonNull
    private String brand;
    
    @Transient
    private Long amount = 0L;
}
