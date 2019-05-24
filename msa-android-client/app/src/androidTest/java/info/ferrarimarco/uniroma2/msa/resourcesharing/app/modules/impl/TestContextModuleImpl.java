package info.ferrarimarco.uniroma2.msa.resourcesharing.app.modules.impl;

import dagger.Module;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.GenericDaoTest;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.dao.helper.DatabaseHelperManagerTest;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.services.config.SharedPreferencesServiceImplTest;
import info.ferrarimarco.uniroma2.msa.resourcesharing.app.util.ObjectGraphUtilsTest;

@Module(injects = {GenericDaoTest.class, DatabaseHelperManagerTest.class, SharedPreferencesServiceImplTest.class, ObjectGraphUtilsTest.class}, complete = false)
public class TestContextModuleImpl{
}
