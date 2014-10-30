package info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = { "info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.connection", "info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.message" })
@PropertySource("classpath:MsaResourceSharingCommon.properties")
public class GcmConfig {
}
