package info.ferrarimarco.uniroma2.is.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import info.ferrarimarco.uniroma2.is.service.persistence.EntityStatPersistenceService;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StatServiceImplTest{
    
    private enum IndexType{
        SUCCESS, LIKING, PERISHABILITY, DEFECTING
    }
    
    @Mock
    private EntityStatPersistenceService entityStatPersistenceService;
    
    @InjectMocks
    private StatServiceImpl statService;
    
    private String productId = "prod";
    private String clazzId = "clazz";
    private String categoryId = "cat";
    
    private String emptyProduct = "emptyProd";
    private String emptyClazz = "emptyClazz";
    private String emptyCategory = "emptyCategory";
    
    @BeforeMethod(groups = { "unitTests" })
    protected void setupMethod(){
        MockitoAnnotations.initMocks(this);
    }
    
    @AfterMethod(groups = { "unitTests" })
    protected void cleanupMethod(){
        Mockito.reset(entityStatPersistenceService);
        statService.clearStats();
    }
    
    @Test(groups = { "unitTests" })
    public void initProductStatTest(){
        statService.initProductStat(productId, clazzId, categoryId);
        assertThat(statService.getDispensed(productId), equalTo(0L));
        assertThat(statService.getExpired(productId), equalTo(0L));
        assertThat(statService.getRequested(productId), equalTo(0L));
        assertThat(statService.getStocked(productId), equalTo(0L));
        assertThat(statService.getDefected(productId), equalTo(0L));
    }
    
    @Test(groups = { "unitTests" })
    public void addDispensedTest(){
        statService.initProductStat(productId, clazzId, categoryId);
        statService.addDispensed(productId, 1L);
        assertThat(statService.getDispensed(productId), equalTo(1L));
        assertThat(statService.getDispensed(clazzId), equalTo(1L));
        assertThat(statService.getDispensed(categoryId), equalTo(1L));
    }
    
    @Test(groups = { "unitTests" })
    public void addExpiredTest(){
        statService.initProductStat(productId, clazzId, categoryId);
        statService.addExpired(productId, 1L);
        assertThat(statService.getExpired(productId), equalTo(1L));
        assertThat(statService.getExpired(clazzId), equalTo(1L));
        assertThat(statService.getExpired(categoryId), equalTo(1L));
    }
    
    @Test(groups = { "unitTests" })
    public void addRequestedTest(){
        statService.initProductStat(productId, clazzId, categoryId);
        statService.addRequested(productId, 1L);
        assertThat(statService.getRequested(productId), equalTo(1L));
        assertThat(statService.getRequested(clazzId), equalTo(1L));
        assertThat(statService.getRequested(categoryId), equalTo(1L));
    }
    
    @Test(groups = { "unitTests" })
    public void addStockedTest(){
        statService.initProductStat(productId, clazzId, categoryId);
        statService.addStocked(productId, 1L);
        assertThat(statService.getStocked(productId), equalTo(1L));
        assertThat(statService.getStocked(clazzId), equalTo(1L));
        assertThat(statService.getStocked(categoryId), equalTo(1L));
    }
    
    @Test(groups = { "unitTests" })
    public void addDefectedTest(){
        statService.initProductStat(productId, clazzId, categoryId);
        statService.addDefected(productId, 1L);
        assertThat(statService.getDefected(productId), equalTo(1L));
        assertThat(statService.getDefected(clazzId), equalTo(1L));
        assertThat(statService.getDefected(categoryId), equalTo(1L));
    }
    
    private void computeIndex(IndexType indexType, String entityId){
        double result = -1.0;
        
        statService.initProductStat(productId, clazzId, categoryId);
        statService.addDispensed(productId, 1L);
        statService.addRequested(productId, 1L);
        statService.addExpired(productId, 1L);
        statService.addStocked(productId, 1L);
        statService.addDefected(productId, 1L);
        
        statService.initProductStat(emptyProduct, emptyClazz, emptyCategory);
        
        switch(indexType){
        case LIKING:
            result = statService.liking(entityId);
            break;
        case PERISHABILITY:
            result = statService.perishability(entityId);
            break;
        case SUCCESS:
            result = statService.success(entityId);
            break;
        case DEFECTING:
            result = statService.defecting(entityId);
            break;
        }
        
        assertThat(result, equalTo(1.0));
    }
    
    @Test(groups = { "unitTests" })
    public void successByCategory(){
        computeIndex(IndexType.SUCCESS, categoryId);
    }
    
    @Test(groups = { "unitTests" })
    public void successByClazz(){
        computeIndex(IndexType.SUCCESS, clazzId);
    }
    
    @Test(groups = { "unitTests" })
    public void perishabilityByCategory(){
        computeIndex(IndexType.PERISHABILITY, categoryId);
    }
    
    @Test(groups = { "unitTests" })
    public void perishabilityByClazz(){
        computeIndex(IndexType.PERISHABILITY, clazzId);
    }
    
    @Test(groups = { "unitTests" })
    public void likingByCategory(){
        computeIndex(IndexType.LIKING, categoryId);
    }
    
    @Test(groups = { "unitTests" })
    public void likingByClazz(){
        computeIndex(IndexType.LIKING, clazzId);
    }
    
    @Test(groups = { "unitTests" })
    public void defectingByCategory(){
        computeIndex(IndexType.DEFECTING, categoryId);
    }
    
    @Test(groups = { "unitTests" })
    public void defectingByClazz(){
        computeIndex(IndexType.DEFECTING, clazzId);
    }
    
    @Test(groups = { "unitTests" })
    public void successProductTest(){
        computeIndex(IndexType.LIKING, productId);
    }
    
    @Test(groups = { "unitTests" })
    public void perishabilityProductTest(){
        computeIndex(IndexType.LIKING, productId);
    }
    
    @Test(groups = { "unitTests" })
    public void likingProductTest(){
        computeIndex(IndexType.LIKING, productId);
    }
    
    @Test(groups = { "unitTests" })
    public void defectingProductTest(){
        computeIndex(IndexType.DEFECTING, productId);
    }
    
    @Test(groups = { "unitTests" }, expectedExceptions = ArithmeticException.class)
    public void successProductNotRequestedTest(){
        computeIndex(IndexType.SUCCESS, emptyProduct);
    }
    
    @Test(groups = { "unitTests" }, expectedExceptions = ArithmeticException.class)
    public void perishabilityProductNotStockedTest(){
        computeIndex(IndexType.PERISHABILITY, emptyProduct);
    }
    
    @Test(groups = { "unitTests" }, expectedExceptions = ArithmeticException.class)
    public void likingProductNotDispensedTest(){
        computeIndex(IndexType.LIKING, emptyProduct);
    }
    
    @Test(groups = { "unitTests" }, expectedExceptions = ArithmeticException.class)
    public void defectingProductNotDispensedTest(){
        computeIndex(IndexType.DEFECTING, emptyProduct);
    }
}