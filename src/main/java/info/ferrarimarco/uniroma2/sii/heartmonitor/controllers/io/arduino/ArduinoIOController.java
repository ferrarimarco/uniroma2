package info.ferrarimarco.uniroma2.sii.heartmonitor.controllers.io.arduino;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/arduino")
public class ArduinoIOController {
	
	public ArduinoIOController() {
		super();
	}

	@ResponseBody
	@RequestMapping(value="session_id", method = RequestMethod.GET)
	public String getUniqueSessionId() {
		return "RESP";
	}
}
