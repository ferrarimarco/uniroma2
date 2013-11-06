package info.ferrarimarco.uniroma2.sii.heartmonitor.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/arduino_io")
public class ArduinoIOController {

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String getUniqueSessionId() {
		return "RESP";
	}
}
