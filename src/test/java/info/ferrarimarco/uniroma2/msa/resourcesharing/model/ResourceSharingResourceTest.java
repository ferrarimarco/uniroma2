package info.ferrarimarco.uniroma2.msa.resourcesharing.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import info.ferrarimarco.uniroma2.msa.resourcesharing.BaseSpringTest;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration("classpath:spring-context.xml")
public class ResourceSharingResourceTest  extends BaseSpringTest{

	@Test(groups = {"modelTests"})
	public void checkResourceValidityTest() {

		String title = "title";
		String description = "description";
		String location = "location";
		String acquisitionMode = "acquisitionMode";
		String creatorId = "creatorId";

		ResourceSharingResource resource = new ResourceSharingResource(title, description, location, acquisitionMode, creatorId);

		assertThat(resource.isValid(), is(true));
	}

}
