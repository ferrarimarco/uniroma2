package info.ferrarimarco.uniroma2.is.model.util;

import lombok.Data;
import lombok.NonNull;
import info.ferrarimarco.uniroma2.is.model.Entity;

@Data
public class StatResult {
    @NonNull
    private final Entity entity;
    
    @NonNull
    private final Double stat;
    
}
