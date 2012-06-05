package __TOP_LEVEL_PACKAGE__.web;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestOperations;

import flexjson.JSONDeserializer;

@Controller
public class OAuthClientController {

	private String resourceURL = "__PROTECTED_RESOURCE_URI__";
	
	@Autowired
	private RestOperations restTemplate;
	
	@RequestMapping("/quiz")
	public String accessProtectedResource(Model model) throws Exception {
		
		// Call the protected __PROTECTED_RESOURCE_URI__ REST service
		String jsonResponse = restTemplate.getForObject(resourceURL, String.class);

		HashMap resourceResponse = new JSONDeserializer<HashMap>().deserialize(jsonResponse);
		
		model.addAttribute("question", resourceResponse.get("question"));
		model.addAttribute("answer", resourceResponse.get("answer"));		
		
		return "testResource";
	}

	public void setResourceURL(String resourceURL) {
		this.resourceURL = resourceURL;
	}
}

