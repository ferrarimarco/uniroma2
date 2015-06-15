package info.ferrarimarco.uniroma2.is.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

import java.util.ArrayList;
import java.util.List;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StatServiceImplProductIndexesTest {
    @Mock
    private ProductPersistenceService productPersistenceService;
    
    @Mock
    private ClazzPersistenceService clazzPersistenceService;
    
    private Clazz clazz;
    private Product emptyProduct;
    private Product notEmptyProduct;
    private Page<Product> productPage;
    private Page<Product> emptyProductPage;
    
    @InjectMocks
    private StatServiceImpl statService;
    
    @BeforeClass(groups = { "unitTests" })
    protected void setup(){
        clazz = new Clazz();
        clazz.setId("clazz-id");
        
        emptyProduct = new Product();
        emptyProduct.setId("prod-id");
        emptyProduct.setDispensed(0L);
        emptyProduct.setRequested(0L);
        emptyProduct.setExpired(0L);
        emptyProduct.setStocked(0L);
        emptyProduct.setClazz(clazz);
        
        notEmptyProduct = new Product();
        notEmptyProduct.setId("empty-prod-id");
        notEmptyProduct.setDispensed(1L);
        notEmptyProduct.setRequested(1L);
        notEmptyProduct.setExpired(1L);
        notEmptyProduct.setStocked(1L);
        notEmptyProduct.setClazz(clazz);
        
        List<Product> products = new ArrayList<>();
        products.add(emptyProduct);
        products.add(notEmptyProduct);
        productPage = new PageImpl<Product>(products);
        
        List<Product> emptyProducts = new ArrayList<>();
        emptyProducts.add(emptyProduct);
        emptyProductPage = new PageImpl<Product>(emptyProducts);
    }
    
    @BeforeMethod(groups = { "unitTests" })
    protected void setupMethod(){
        MockitoAnnotations.initMocks(this);
        when(clazzPersistenceService.findById(eq(clazz.getId()))).thenReturn(clazz);
        when(productPersistenceService.findByClazz(eq(clazz), notNull(PageRequest.class))).thenReturn(productPage);
        when(productPersistenceService.findAll(notNull(PageRequest.class))).thenReturn(productPage);
        when(productPersistenceService.findById(eq(emptyProduct.getId()))).thenReturn(emptyProduct);
        when(productPersistenceService.findById(eq(notEmptyProduct.getId()))).thenReturn(notEmptyProduct);
    }
    
    @AfterMethod(groups = { "unitTests" })
    protected void cleanupMethod(){
        Mockito.reset(productPersistenceService);
        Mockito.reset(clazzPersistenceService);
    }
    
    @Test(groups = { "unitTests" })
    public void successProductTest(){
        double result = statService.success(notEmptyProduct.getId(), Product.class);
        double expected = notEmptyProduct.getDispensed().doubleValue()/notEmptyProduct.getRequested().doubleValue();
        
        assertThat(result, equalTo(expected));
        verify(productPersistenceService).findById(eq(notEmptyProduct.getId()));
    }
    
    @Test(groups = { "unitTests" })
    public void perishabilityProductTest(){
        double result = statService.perishability(notEmptyProduct.getId(), Product.class);
        double expected = notEmptyProduct.getExpired().doubleValue()/notEmptyProduct.getStocked().doubleValue();
        
        assertThat(result, equalTo(expected));
        verify(productPersistenceService).findById(eq(notEmptyProduct.getId()));
    }
    
    @Test(groups = { "unitTests" })
    public void likingProductTest(){
        double result = statService.liking(notEmptyProduct.getId(), Product.class);
        double expected = notEmptyProduct.getDispensed().doubleValue()/(emptyProduct.getDispensed().doubleValue()+notEmptyProduct.getDispensed().doubleValue());
        
        assertThat(result, equalTo(expected));
        verify(productPersistenceService).findById(eq(notEmptyProduct.getId()));
        verify(clazzPersistenceService, times(1)).findById(clazz.getId());
    }
    
    @Test(groups = { "unitTests" }, expectedExceptions = ArithmeticException.class)
    public void successProductNotRequestedTest(){
        when(productPersistenceService.findByClazz(eq(clazz), notNull(PageRequest.class))).thenReturn(emptyProductPage);
        statService.success(emptyProduct.getId(), Product.class);
        verify(productPersistenceService).findById(eq(emptyProduct.getId()));
    }
    
    @Test(groups = { "unitTests" }, expectedExceptions = ArithmeticException.class)
    public void perishabilityProductNotStockedTest(){
        when(productPersistenceService.findByClazz(eq(clazz), notNull(PageRequest.class))).thenReturn(emptyProductPage);
        statService.perishability(emptyProduct.getId(), Product.class);
        verify(productPersistenceService).findById(eq(emptyProduct.getId()));
    }
    
    @Test(groups = { "unitTests" }, expectedExceptions = ArithmeticException.class)
    public void likingProductNotDispensedTest(){
        when(productPersistenceService.findByClazz(eq(clazz), notNull(PageRequest.class))).thenReturn(emptyProductPage);
        statService.liking(emptyProduct.getId(), Product.class);
        verify(productPersistenceService).findById(eq(emptyProduct.getId()));
    }
}
