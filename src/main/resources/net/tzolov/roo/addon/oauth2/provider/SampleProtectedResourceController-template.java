package __TOP_LEVEL_PACKAGE__.web;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SampleProtectedResourceController {

	
	@RequestMapping(value= "/testResource", method = RequestMethod.GET)
	public void getResourceData(Model uiModel, HttpServletResponse response) throws Exception {

		response.setContentType("application/json;charset=UTF-8");
		
		PrintWriter writer = response.getWriter();
		
		writer.write("{\"question\":\"What is the Ultimate Answer to the Ultimate Question of Life, The Universe, and Everything\", " +
				"\"answer\":\"42\"}");
		writer.flush();
	}
}
