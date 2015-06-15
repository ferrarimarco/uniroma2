package info.ferrarimarco.uniroma2.is.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import lombok.extern.slf4j.Slf4j;
import info.ferrarimarco.uniroma2.is.config.context.RootConfig;
import info.ferrarimarco.uniroma2.is.model.Category;
import info.ferrarimarco.uniroma2.is.model.Clazz;
import info.ferrarimarco.uniroma2.is.model.Product;
import info.ferrarimarco.uniroma2.is.service.persistence.CategoryPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ClazzPersistenceService;
import info.ferrarimarco.uniroma2.is.service.persistence.ProductPersistenceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(classes = {RootConfig.class})
@Slf4j
public class StatServiceImplProductIndexesIT extends AbstractTestNGSpringContextTests{
    
    @Autowired
    private CategoryPersistenceService categoryPersistenceService;
    
    @Autowired
    private ClazzPersistenceService clazzPersistenceService;
    
    @Autowired
    private ProductPersistenceService productPersistenceService;
    
    private Clazz clazz;
    private Product emptyProduct;
    private Product notEmptyProduct;
    
    @Autowired
    private StatServiceImpl statService;
    
    @BeforeClass(groups = { "integrationTests" })
    protected void setup(){
        Category cat = new Category();
        cat.setName("cat");
        cat = categoryPersistenceService.save(cat);
        clazz = new Clazz();
        clazz.setCategory(cat);
        clazz.setName("clazz");
        clazz = clazzPersistenceService.save(clazz);
    }
    
    @AfterClass(groups = { "integrationTests" })
    protected void teardown(){
        productPersistenceService.delete(emptyProduct.getId());
        productPersistenceService.delete(notEmptyProduct.getId());
        clazzPersistenceService.delete(clazz.getId());
        categoryPersistenceService.delete(clazz.getCategory().getId());
    }
    
    @BeforeMethod(groups = { "integrationTests" })
    protected void initializeProducts(){
        log.debug("Initializing Products");
        emptyProduct = new Product();
        emptyProduct.setDispensed(0L);
        emptyProduct.setRequested(0L);
        emptyProduct.setExpired(0L);
        emptyProduct.setStocked(0L);
        emptyProduct.setClazz(clazz);
        emptyProduct = productPersistenceService.save(emptyProduct);
        
        notEmptyProduct = new Product();
        notEmptyProduct.setDispensed(0L);
        notEmptyProduct.setRequested(1L);
        notEmptyProduct.setExpired(1L);
        notEmptyProduct.setStocked(1L);
        notEmptyProduct.setClazz(clazz);
        notEmptyProduct = productPersistenceService.save(notEmptyProduct);
    }
    
    @Test(groups = { "integrationTests" })
    public void successProductTest(){
        double result = statService.success(notEmptyProduct.getId(), Product.class);
        double expected = notEmptyProduct.getDispensed().doubleValue()/notEmptyProduct.getRequested().doubleValue();
        assertThat(result, equalTo(expected));
    }
    
    @Test(groups = { "integrationTests" })
    public void perishabilityProductTest(){
        double result = statService.perishability(notEmptyProduct.getId(), Product.class);
        double expected = notEmptyProduct.getExpired().doubleValue()/notEmptyProduct.getStocked().doubleValue();
        assertThat(result, equalTo(expected));
    }
    
    @Test(groups = { "integrationTests" })
    public void likingProductTest(){
        notEmptyProduct.setDispensed(1L);
        notEmptyProduct = productPersistenceService.save(notEmptyProduct);
        double result = statService.liking(notEmptyProduct.getId(), Product.class);
        double expected = notEmptyProduct.getDispensed().doubleValue()/(emptyProduct.getDispensed().doubleValue()+notEmptyProduct.getDispensed().doubleValue());
        assertThat(result, equalTo(expected));
    }
    
    @Test(groups = { "integrationTests" }, expectedExceptions = ArithmeticException.class)
    public void successProductNotRequestedTest(){
        statService.success(emptyProduct.getId(), Product.class);
    }
    
    @Test(groups = { "integrationTests" }, expectedExceptions = ArithmeticException.class)
    public void perishabilityProductNotStockedTest(){
        statService.perishability(emptyProduct.getId(), Product.class);
    }
    
    @Test(groups = { "integrationTests" }, expectedExceptions = ArithmeticException.class)
    public void likingProductNotDispensedTest(){
        statService.liking(emptyProduct.getId(), Product.class);
    }
}
