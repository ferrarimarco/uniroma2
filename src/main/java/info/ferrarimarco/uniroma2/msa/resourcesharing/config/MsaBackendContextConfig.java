package info.ferrarimarco.uniroma2.msa.resourcesharing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource("classpath:MsaResourceSharingCommon.properties")
@ComponentScan(basePackages = { "info.ferrarimarco.uniroma2.msa.resourcesharing.dao.config", "info.ferrarimarco.uniroma2.msa.resourcesharing.io.gcm.config" })
@EnableTransactionManagement
public class MsaBackendContextConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}