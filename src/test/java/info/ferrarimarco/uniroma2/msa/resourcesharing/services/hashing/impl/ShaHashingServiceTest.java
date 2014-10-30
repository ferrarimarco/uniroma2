package info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.impl;

import info.ferrarimarco.uniroma2.msa.resourcesharing.BaseSpringTest;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.DatatypeConversionService;
import info.ferrarimarco.uniroma2.msa.resourcesharing.services.hashing.HashingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ShaHashingServiceTest extends BaseSpringTest {

    private static Logger logger = LoggerFactory.getLogger(ShaHashingServiceTest.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private HashingService hashingService;

    @Autowired
    private DatatypeConversionService datatypeConversionService;

    @BeforeClass
    protected void setup() throws Exception {
        Assert.assertNotNull(applicationContext);
    }

    @Test(groups = { "ShaHashingServiceTestGroup", "springServicesTestGroup" })
    public void hashEqualsTest() {

        String password = "password";
        String passwordCorrectHash = "b109f3bbbc244eb82441917ed06d618b9008dd09b3befd1b5e07394c706a8bb980b1d7785e5976ec049b46df5f1326af5a2ea6d103fd07c95385ffab0cacbc86";

        byte[] hashedPasswordBytes = hashingService.hash(password);
        String hashedPassword = datatypeConversionService.bytesToHexString(hashedPasswordBytes);

        logger.info("SHA input:{} , SHA output:{} , correct output:{}", password, hashedPassword, passwordCorrectHash);

        Assert.assertEquals(hashedPassword, passwordCorrectHash);
    }
}
