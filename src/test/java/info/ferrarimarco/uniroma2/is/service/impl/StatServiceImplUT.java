package info.ferrarimarco.uniroma2.is.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Entity;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
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

public class StatServiceImplUT{
    
    private enum IndexType{
        SUCCESS, LIKING, PERISHABILITY
    }
    
    @Mock
    private CategoryPersistenceService categoryPersistenceService;
    
    @Mock
    private ClazzPersistenceService clazzPersistenceService;
    
    @Mock
    private ProductPersistenceService productPersistenceService;
    
    @InjectMocks
    private StatServiceImpl statService;
    
    private Category category;
    private Clazz clazz;
    
    private List<Product> products;
    private Page<Product> productPage;
    
    @BeforeClass
    protected void setup(){
        category = new Category();
        category.setId("cat-id");
        
        clazz = new Clazz();
        clazz.setId("clazz-id");
        clazz.setCategory(category);
        
        Product product = new Product();
        product.setId("prod-id");
        product.setCategory(category);
        product.setClazz(clazz);
        product.setDispensed(0L);
        product.setRequested(0L);
        product.setStocked(1L);
        
        Product product2 = new Product();
        product2.setId("prod-id2");
        product2.setCategory(category);
        product2.setClazz(clazz);
        product2.setDispensed(1L);
        product2.setRequested(1L);
        product2.setStocked(1L);
        
        products = new ArrayList<>();
        products.add(product);
        products.add(product2);
        productPage = new PageImpl<Product>(products);
    }
    
    @BeforeMethod
    protected void setupMethod(){
        MockitoAnnotations.initMocks(this);
        when(categoryPersistenceService.findById(eq(category.getId()))).thenReturn(category);
        when(clazzPersistenceService.findById(eq(clazz.getId()))).thenReturn(clazz);
        when(productPersistenceService.findByCategory(eq(category), notNull(PageRequest.class))).thenReturn(productPage);
        when(productPersistenceService.findByClazz(eq(clazz), notNull(PageRequest.class))).thenReturn(productPage);
        when(productPersistenceService.findAll(notNull(PageRequest.class))).thenReturn(productPage);
    }
    
    @AfterMethod
    protected void cleanupMethod(){
        Mockito.reset(categoryPersistenceService);
        Mockito.reset(clazzPersistenceService);
        Mockito.reset(productPersistenceService);
    }
    
    private void computeIndex(IndexType indexType, String criteriaId, Class<? extends Entity> entityClass){
        double result = 0.0;
        double expected = 1.0;
        
        switch(indexType){
        case LIKING:
            expected = 1.0;
            result = statService.liking(criteriaId, entityClass);
            break;
        case PERISHABILITY:
            expected = (products.get(0).getExpired().doubleValue() + products.get(1).getExpired().doubleValue())
                    /(products.get(0).getStocked().doubleValue() + products.get(1).getStocked().doubleValue());
            result = statService.perishability(criteriaId, entityClass);
            break;
        case SUCCESS:
            expected = (products.get(0).getDispensed().doubleValue() + products.get(1).getDispensed().doubleValue())
            /(products.get(0).getRequested().doubleValue() + products.get(1).getRequested().doubleValue());
            result = statService.success(criteriaId, entityClass);
            break;
        }
        
        assertThat(result, equalTo(expected));
    }
    
    @Test(groups = { "unitTests", "springServicesTestGroup" })
    public void successByCategory(){
        computeIndex(IndexType.SUCCESS, category.getId(), Category.class);
        verify(categoryPersistenceService, times(2)).findById(category.getId());
        verify(productPersistenceService, times(2)).findByCategory(eq(category), isA(PageRequest.class));
    }
    
    @Test(groups = { "unitTests", "springServicesTestGroup" })
    public void successByClazz(){
        computeIndex(IndexType.SUCCESS, clazz.getId(), Clazz.class);
        verify(clazzPersistenceService, times(2)).findById(clazz.getId());
        verify(productPersistenceService, times(2)).findByClazz(eq(clazz), isA(PageRequest.class));
    }
    
    @Test(groups = { "unitTests", "springServicesTestGroup" })
    public void perishabilityByCategory(){
        computeIndex(IndexType.PERISHABILITY, category.getId(), Category.class);
        verify(categoryPersistenceService, times(2)).findById(category.getId());
        verify(productPersistenceService, times(2)).findByCategory(eq(category), isA(PageRequest.class));
    }
    
    @Test(groups = { "unitTests", "springServicesTestGroup" })
    public void perishabilityByClazz(){
        computeIndex(IndexType.PERISHABILITY, clazz.getId(), Clazz.class);
        verify(clazzPersistenceService, times(2)).findById(clazz.getId());
        verify(productPersistenceService, times(2)).findByClazz(eq(clazz), isA(PageRequest.class));
    }
    
    @Test(groups = { "unitTests", "springServicesTestGroup" })
    public void likingByCategory(){
        computeIndex(IndexType.LIKING, category.getId(), Category.class);
        verify(categoryPersistenceService, times(1)).findById(category.getId());
        verify(productPersistenceService, times(1)).findByCategory(eq(category), isA(PageRequest.class));
    }
    
    @Test(groups = { "unitTests", "springServicesTestGroup" })
    public void likingByClazz(){
        computeIndex(IndexType.LIKING, clazz.getId(), Clazz.class);
        verify(clazzPersistenceService, times(2)).findById(clazz.getId());
        verify(productPersistenceService, times(1)).findByClazz(eq(clazz), isA(PageRequest.class));
    }
}