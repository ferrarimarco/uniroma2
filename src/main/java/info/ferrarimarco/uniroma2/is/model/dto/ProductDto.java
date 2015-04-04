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
    private String clazzId;
    private int previousAmount;
    
    public Product asProductClone(){
        Product p = Product.builder().clazz(this.getClazz()).barCode(this.getBarCode()).brand(this.getBrand()).amount(this.getAmount()).build();
        p.setName(this.getName());
        p.setId(this.getId());
        return p;
    }
}
