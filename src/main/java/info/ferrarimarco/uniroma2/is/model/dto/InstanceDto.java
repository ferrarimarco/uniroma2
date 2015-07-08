package info.ferrarimarco.uniroma2.is.model.dto;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import info.ferrarimarco.uniroma2.is.model.ProductInstance;
import info.ferrarimarco.uniroma2.is.model.dto.ProductDto.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceDto {
    private String productId;
    private Long newAmount;
    private Operation operation;
    private String expirationDate;
    
    private static final DateTimeFormatter expirationDateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
    
    public ProductInstance asProductInstanceClone(){
        return StringUtils.isBlank(getExpirationDate()) 
                ? ProductInstance.builder().productId(getProductId()).amount(getNewAmount()).build()
                : ProductInstance.builder().productId(getProductId()).amount(getNewAmount()).expirationDate(expirationDateFormatter.parseDateTime(getExpirationDate())).build();
    }
}
