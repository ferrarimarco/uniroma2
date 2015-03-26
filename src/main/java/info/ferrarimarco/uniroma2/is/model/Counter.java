package info.ferrarimarco.uniroma2.is.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class Counter {
    private String name;
    private long sequence;
}
