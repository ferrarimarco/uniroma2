package info.ferrarimarco.uniroma2.is.controller;

import static org.mockito.Mockito.when;
import info.ferrarimarco.uniroma2.is.BaseSpringMvcSingleControllerTest;
import info.ferrarimarco.uniroma2.is.config.context.RootConfig;
import info.ferrarimarco.uniroma2.is.config.servlet.SpringMvcConfig;
import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.testng.annotations.BeforeClass;

@ContextConfiguration(classes = {RootConfig.class, SpringMvcConfig.class})
public abstract class AbstractControllerUT extends BaseSpringMvcSingleControllerTest{
    @Mock
    protected CategoryPersistenceService categoryPersistenceService;
    
    @Mock
    protected ClazzPersistenceService clazzPersistenceService;
    
    @Mock
    protected ProductPersistenceService productPersistenceService;
    
    @Mock
    protected ProductInstancePersistenceService productInstancePersistenceService;
    
    protected abstract AbstractController getController();
    
    @BeforeClass(groups = {"springUTinit"})
    protected void setup(){
        MockitoAnnotations.initMocks(this);
        setupMocksForGenericModelAttributes();
        PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver = new PageableHandlerMethodArgumentResolver();
        pageableHandlerMethodArgumentResolver.setOneIndexedParameters(true);
        pageableHandlerMethodArgumentResolver.setFallbackPageable(new PageRequest(0, 10));
        this.setupMockMvc(new Object[]{getController()}, new HandlerMethodArgumentResolver[] {pageableHandlerMethodArgumentResolver});
    }
    
    protected void setupMocksForGenericModelAttributes(){
        Category category = new Category();
        category.setId("cat-id");
        Clazz clazz = new Clazz();
        clazz.setId("clazz-id");
        clazz.setCategory(category);
        
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        List<Clazz> clazzes = new ArrayList<>();
        clazzes.add(clazz);
        
        when(categoryPersistenceService.findAll()).thenReturn(categories);
        when(clazzPersistenceService.findAll()).thenReturn(clazzes);
    }
}
