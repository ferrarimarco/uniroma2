package info.ferrarimarco.uniroma2.is.model.dto;

import info.ferrarimarco.uniroma2.is.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductDto extends Product {
    
    public enum Operation{
        ADD_INSTANCES,
        REMOVE_INSTANCES
    }
    
    private String clazzId;
    
    public Product asProductClone(){
        ProductBuilder productBuilder = Product.builder().clazz(getClazz()).barCode(getBarCode()).brand(getBrand())
                .requested(getRequested()).dispensed(getDispensed()).category(getCategory());

        Product p = productBuilder.build();
        p.setName(getName());
        p.setId(getId());
        p.setSymbolicId(getSymbolicId());
        
        return p;
    }
}
