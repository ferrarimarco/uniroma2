package info.ferrarimarco.uniroma2.is.config.context;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan({"info.ferrarimarco.uniroma2.is.service", "info.ferrarimarco.uniroma2.is.config.initializer.persistence"})
public class ContextConfig {
}
