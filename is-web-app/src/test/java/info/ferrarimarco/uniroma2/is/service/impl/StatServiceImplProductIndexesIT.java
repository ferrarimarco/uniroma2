package info.ferrarimarco.uniroma2.is.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import info.ferrarimarco.uniroma2.is.config.context.RootConfig;
import lombok.extern.slf4j.Slf4j;

@ContextConfiguration(classes = {RootConfig.class})
@Slf4j
public class StatServiceImplProductIndexesIT extends AbstractTestNGSpringContextTests{
    
    private String productId = "prod";
    private String clazzId = "clazz";
    private String categoryId = "cat";
    
    private String emptyProduct = "emptyProd";
    private String emptyClazz = "emptyClazz";
    private String emptyCategory = "emptyCategory";
    
    @Autowired
    private StatServiceImpl statService;
    
    @AfterMethod(groups = { "integrationTests" })
    protected void teardown(){
        statService.clearStats();
    }
    
    @BeforeMethod(groups = { "integrationTests" })
    protected void initializeProducts(){
        log.debug("Initializing Products");
        statService.initProductStat(productId, clazzId, categoryId);
        statService.addDispensed(productId, 1L);
        statService.addRequested(productId, 1L);
        statService.addExpired(productId, 1L);
        statService.addStocked(productId, 1L);
        statService.addDefected(productId, 1L);
        
        statService.initProductStat(emptyProduct, emptyClazz, emptyCategory);
    }
    
    @Test(groups = { "integrationTests" })
    public void successProductTest(){
        assertThat(statService.success(productId), equalTo(1.0));
    }
    
    @Test(groups = { "integrationTests" })
    public void perishabilityProductTest(){
        assertThat(statService.perishability(productId), equalTo(1.0));
    }
    
    @Test(groups = { "integrationTests" })
    public void likingProductTest(){
        assertThat(statService.liking(productId), equalTo(1.0));
    }
    
    @Test(groups = { "integrationTests" })
    public void defectingProductTest(){
        assertThat(statService.defecting(productId), equalTo(1.0));
    }
    
    @Test(groups = { "integrationTests" }, expectedExceptions = ArithmeticException.class)
    public void successProductNotRequestedTest(){
        statService.success(emptyProduct);
    }
    
    @Test(groups = { "integrationTests" }, expectedExceptions = ArithmeticException.class)
    public void perishabilityProductNotStockedTest(){
        statService.perishability(emptyProduct);
    }
    
    @Test(groups = { "integrationTests" }, expectedExceptions = ArithmeticException.class)
    public void likingProductNotDispensedTest(){
        statService.liking(emptyProduct);
    }
    
    @Test(groups = { "integrationTests" }, expectedExceptions = ArithmeticException.class)
    public void defectingProductNotDefectingTest(){
        statService.defecting(emptyProduct);
    }
}
