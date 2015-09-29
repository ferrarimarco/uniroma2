package info.ferrarimarco.uniroma2.is.controller.spring;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.webjars.WebJarAssetLocator;

@RestController
@RequestMapping("/webjars")
public class WebJarsController extends AbstractController{
	
	@Autowired
	private WebJarAssetLocator assetLocator;
	
	@RequestMapping(value = "/webjarslocator/{webjar}/**")
	public ResponseEntity<ClassPathResource> locateWebjarAsset(@PathVariable String webjar, HttpServletRequest request) {
        return new ResponseEntity<>(new ClassPathResource(assetLocator.getFullPath(webjar)), HttpStatus.OK);
	}
}
