package info.ferrarimarco.uniroma2.is.config.context;

import java.io.IOException;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@PropertySource("classpath:application.properties")
@Configuration
@ComponentScan
@Slf4j
public class RootConfig {
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "applicationProperties")
    public Properties applicationProperties() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        
        String applicationPropertiesPath = "application.properties";
        propertiesFactoryBean.setLocation(new ClassPathResource(applicationPropertiesPath));

        Properties properties = null;
        
        try {
            propertiesFactoryBean.afterPropertiesSet();
            properties = propertiesFactoryBean.getObject();

        } catch (IOException e) {
            log.warn("Cannot load {} file: {}", applicationPropertiesPath, ExceptionUtils.getStackTrace(e));
        }
        return properties;
    }
}
