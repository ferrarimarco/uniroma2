package info.ferrarimarco.uniroma2.is;

import lombok.extern.slf4j.Slf4j;

import org.testng.annotations.BeforeClass;

@Slf4j
public class AbstractIT extends BaseSpringMvcContextTest{
    
    @BeforeClass(groups = { "integrationTests" })
    protected void setup(){
        log.debug("Initializing AbstractIT");
        super.setupMockMvc();
    }
}
