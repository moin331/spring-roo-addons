package net.tzolov.roo.addon.oauth2;

import org.springframework.roo.project.Dependency;
import org.apache.commons.lang3.StringUtils;
import org.springframework.roo.support.util.XmlElementBuilder;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Christian Tzolov (christian@tzolov.net)
 *
 */
public class OAuth2Common {

	public static final Dependency SPRING_SECURITY = new Dependency("org.springframework.security", "spring-security-core", "3.1.0.RELEASE");
	public static final Dependency SPRING_OAUT_SECURITY = new Dependency("org.springframework.security.oauth", "spring-security-oauth", "1.0.0.M5");

	public static void addNamespaces(Element securityAppContext) {
		if (StringUtils.isBlank(securityAppContext.getAttribute("xmlns:oauth"))) {
			securityAppContext.setAttribute("xmlns:oauth", "http://www.springframework.org/schema/security/oauth2");
			securityAppContext.setAttribute("xsi:schemaLocation", securityAppContext.getAttribute("xsi:schemaLocation") + "  http://www.springframework.org/schema/security/oauth2" + "  http://www.springframework.org/schema/security/spring-security-oauth2-1.0.xsd");
		}
		if (StringUtils.isBlank(securityAppContext.getAttribute("xmlns:c"))) {
			securityAppContext.setAttribute("xmlns:c", "http://www.springframework.org/schema/c");
		}		
		if (StringUtils.isBlank(securityAppContext.getAttribute("xmlns:p"))) {
			securityAppContext.setAttribute("xmlns:p", "http://www.springframework.org/schema/p");
		}
	}
	public static void replaceSimpleMappingExceptionResolver(Document webConfigDocument) {

		Element webConfig = webConfigDocument.getDocumentElement();
		
		//Replace the simpleMappingExceptionResolver by
		Element simpleMappingExceptionResolver = XmlUtils.findFirstElement("/beans/bean[@class = 'org.springframework.web.servlet.handler.SimpleMappingExceptionResolver']", webConfig);
		if (simpleMappingExceptionResolver != null) {
			webConfig.removeChild(simpleMappingExceptionResolver);		
		}
		
		if (null == XmlUtils.findFirstElement("/beans/bean[@class = 'org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerExceptionResolver']", webConfig)) {
			webConfig.appendChild(
				new XmlElementBuilder("bean", webConfigDocument)		
					.addAttribute("class", "org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerExceptionResolver")
					.addAttribute("p:order", "1") .build());
		}

		if (null == XmlUtils.findFirstElement("/beans/bean[@class = 'org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver']", webConfig)) {
			webConfig.appendChild(
				new XmlElementBuilder("bean", webConfigDocument)		
					.addAttribute("class", "org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver")
					.addAttribute("p:order", "2") .build());
		}
	}
}
