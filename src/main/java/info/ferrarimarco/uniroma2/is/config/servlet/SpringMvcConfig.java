package info.ferrarimarco.uniroma2.is.config.servlet;

import info.ferrarimarco.uniroma2.is.controller.application.AddRemoveProductInstanceController;
import info.ferrarimarco.uniroma2.is.controller.application.CreateUpdateProductApplicationController;
import info.ferrarimarco.uniroma2.is.controller.application.LoadEntityApplicationController;
import info.ferrarimarco.uniroma2.is.controller.application.StatsApplicationController;
import info.ferrarimarco.uniroma2.is.service.StatService;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebMvc
@ComponentScan("info.ferrarimarco.uniroma2.is.controller.spring")
public class SpringMvcConfig extends WebMvcConfigurerAdapter {

    // Dispatcher configuration for serving static resources
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**").addResourceLocations("/WEB-INF/images/");
        registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/css/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/");
        registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/js/");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver = new PageableHandlerMethodArgumentResolver();
        pageableHandlerMethodArgumentResolver.setOneIndexedParameters(true);
        pageableHandlerMethodArgumentResolver.setFallbackPageable(new PageRequest(0, 10));

        argumentResolvers.add(pageableHandlerMethodArgumentResolver);
        super.addArgumentResolvers(argumentResolvers);
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jacksonMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        jacksonMessageConverter.setObjectMapper(mapper);
        jacksonMessageConverter.setPrettyPrint(true);
        converters.add(jacksonMessageConverter);
        super.configureMessageConverters(converters);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.TEXT_HTML);
        configurer.favorPathExtension(true);
        configurer.favorParameter(false);
        configurer.useJaf(false).
        mediaType("json", MediaType.APPLICATION_JSON);
    }
    
    @Bean
    @Autowired
    public StatsApplicationController statsApplicationController(StatService statService, ProductPersistenceService productPersistenceService, ProductInstancePersistenceService productInstancePersistenceService, ClazzPersistenceService clazzPersistenceService, CategoryPersistenceService categoryPersistenceService){
        return new StatsApplicationController(productPersistenceService, productInstancePersistenceService, clazzPersistenceService, categoryPersistenceService, statService);
    }
    
    @Bean
    @Autowired
    public LoadEntityApplicationController loadEntityApplicationController(ProductPersistenceService productPersistenceService, ProductInstancePersistenceService productInstancePersistenceService, ClazzPersistenceService clazzPersistenceService, CategoryPersistenceService categoryPersistenceService){
        return new LoadEntityApplicationController(productPersistenceService, productInstancePersistenceService, clazzPersistenceService, categoryPersistenceService);
    }
    
    @Bean
    @Autowired
    public CreateUpdateProductApplicationController createUpdateProductApplicationController(ProductPersistenceService productPersistenceService, ProductInstancePersistenceService productInstancePersistenceService, ClazzPersistenceService clazzPersistenceService, CategoryPersistenceService categoryPersistenceService){
        return new CreateUpdateProductApplicationController(productPersistenceService, productInstancePersistenceService, clazzPersistenceService, categoryPersistenceService);
    }
    
    @Bean
    @Autowired
    public AddRemoveProductInstanceController addRemoveProductInstanceController(ProductPersistenceService productPersistenceService, ProductInstancePersistenceService productInstancePersistenceService, ClazzPersistenceService clazzPersistenceService, CategoryPersistenceService categoryPersistenceService){
        return new AddRemoveProductInstanceController(productPersistenceService, productInstancePersistenceService, clazzPersistenceService, categoryPersistenceService);
    }
}
