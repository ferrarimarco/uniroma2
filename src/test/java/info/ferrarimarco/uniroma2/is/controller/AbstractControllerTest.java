package info.ferrarimarco.uniroma2.is.controller;

import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import info.ferrarimarco.uniroma2.is.BaseSpringMvcSingleControllerTest;
import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductInstancePersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

@Slf4j
@TestExecutionListeners(inheritListeners = false, listeners = {})
public abstract class AbstractControllerTest extends BaseSpringMvcSingleControllerTest{
    @Mock
    protected CategoryPersistenceService categoryPersistenceService;
    
    @Mock
    protected ClazzPersistenceService clazzPersistenceService;
    
    @Mock
    protected ProductPersistenceService productPersistenceService;
    
    @Mock
    protected ProductInstancePersistenceService productInstancePersistenceService;
    
    protected Category category;
    protected Clazz clazz;
    protected Product product;
    protected Page<Product> productsPage;
    
    protected abstract AbstractController getController();
    
    @BeforeClass(groups = { "unitTests" })
    protected void setup(){
        log.debug("Initializing AbstractController");
        MockitoAnnotations.initMocks(this);
        PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver = new PageableHandlerMethodArgumentResolver();
        pageableHandlerMethodArgumentResolver.setOneIndexedParameters(true);
        pageableHandlerMethodArgumentResolver.setFallbackPageable(new PageRequest(0, 10));
        super.setupMockMvc(new Object[]{getController()}, new HandlerMethodArgumentResolver[] {pageableHandlerMethodArgumentResolver});
    }
    
    @BeforeMethod(groups = { "unitTests" })
    protected void setupMocksForGenericModelAttributes(){
        log.debug("Setting up mocks for generic ModelAttributes");
        category = new Category();
        category.setId("cat-id");
        category.setName("cat-name");
        when(categoryPersistenceService.findById(category.getId())).thenReturn(category);
        
        clazz = new Clazz();
        clazz.setId("clazz-id");
        clazz.setCategory(category);
        clazz.setName("clazz-name");
        when(clazzPersistenceService.findById(clazz.getId())).thenReturn(clazz);
        
        List<Category> categories = new ArrayList<>();
        categories.add(category);
        when(categoryPersistenceService.findAll()).thenReturn(categories);
        
        List<Clazz> clazzes = new ArrayList<>();
        clazzes.add(clazz);
        when(clazzPersistenceService.findAll()).thenReturn(clazzes);
        
        product = Product.builder().category(category).clazz(clazz).brand("brand").build();
        product.setId("prod1-id");
        product.setName("product-name");
        when(productPersistenceService.findById(product.getId())).thenReturn(product);

        List<Product> products = new ArrayList<>();
        products.add(product);
        productsPage = new PageImpl<Product>(products);
        
        when(productPersistenceService.findAll(notNull(PageRequest.class))).thenReturn(productsPage);
    }
    
    @AfterMethod(groups = { "unitTests" })
    public void verifyGenericModelAttributeMocks(){
        verify(categoryPersistenceService, times(1)).findAll();
        verify(clazzPersistenceService, times(1)).findAll();
        
        Mockito.reset(categoryPersistenceService);
        Mockito.reset(clazzPersistenceService);
    }
}
