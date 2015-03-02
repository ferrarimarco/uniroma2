package info.ferrarimarco.uniroma2.is.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

@Configuration
@Slf4j
public class TemplateConfig {

    @Value("${config.template.prefix}")
    private String prefix;
    
    @Value("${config.template.suffix}")
    private String suffix;
    
    @Value("${config.template.templateMode}")
    private String templateMode;
    
    @Value("${config.template.viewResolverOrder}")
    private int viewResolverOrder;
    
    @Value("${config.template.viewNames}")
    private String[] viewNames;
    
    @Value("${config.template.internationalization.basename}")
    private String baseName;
    
    @Bean
    public TemplateResolver templateResolver(){
        TemplateResolver templateResolver = new ServletContextTemplateResolver();
        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(suffix);
        templateResolver.setTemplateMode(templateMode);
        return templateResolver;
    }
    
    @Bean
    @Autowired
    public TemplateEngine templateEngine(TemplateResolver templateResolver){
        TemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }
    
    @Bean
    @Autowired
    public ViewResolver viewResolver(TemplateEngine templateEngine){
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        if(templateEngine instanceof SpringTemplateEngine){
            viewResolver.setTemplateEngine((SpringTemplateEngine) templateEngine);
        }else{
            log.warn("Template engine for view resolver was not set.");
        }
        viewResolver.setOrder(viewResolverOrder);
        viewResolver.setViewNames(viewNames);
        return viewResolver;
    }
    
//    <!-- **************************************************************** -->
//    <!--  MESSAGE EXTERNALIZATION/INTERNATIONALIZATION                    -->
//    <!--  Standard Spring MessageSource implementation                    -->
//    <!-- **************************************************************** -->
//    <bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
//      <property name="basename" value="Messages" />
//    </bean>
    
    public MessageSource messageSource(){
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(baseName);
        return messageSource;
    }
}
