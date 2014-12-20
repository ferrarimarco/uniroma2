package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.connection", "info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.message" })
public class GcmConfig {
}
