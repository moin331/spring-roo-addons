package net.tzolov.http.servletproxy.roo.addon;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.apache.commons.lang3.Validate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.metadata.MetadataService;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.process.manager.MutableFile;
import org.springframework.roo.project.Dependency;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.project.Repository;
import org.springframework.roo.support.util.CollectionUtils;
import org.springframework.roo.support.util.WebXmlUtils;
import org.springframework.roo.support.util.WebXmlUtils.WebXmlParam;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author Christian Tzolov (christian@tzolov.net)
 * 
 */
@Component
@Service
public class HttpServletProxyOperationsImpl implements
		HttpServletProxyOperations {

	private static Logger logger = Logger
			.getLogger(HttpServletProxyOperations.class.getName());

	@Reference
	private FileManager fileManager;
	@Reference
	private MetadataService metadataService;
	@Reference
	private PathResolver pathResolver;
	@Reference
	private ProjectOperations projectOperations;

	private ComponentContext context;

	private static class WebXmlHolder {

		private final MutableFile mutableWebXml;

		private final Document webXmlDoc;

		public WebXmlHolder(MutableFile mutableWebXml, Document webXmlDoc) {
			super();
			this.mutableWebXml = mutableWebXml;
			this.webXmlDoc = webXmlDoc;
		}

		public MutableFile getMutableWebXml() {
			return mutableWebXml;
		}

		public Document getWebXmlDoc() {
			return webXmlDoc;
		}
	}

	protected void activate(ComponentContext context) {
		this.context = context;
	}

	public boolean isApplicableServletProxy() {
		return projectOperations.isFocusedProjectAvailable() 
				&& fileManager.exists(pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "/WEB-INF/web.xml"));
	}

	public void addServletProxy(String proxyName, String proxyHost,
			int proxyPort, String urlPattern, boolean removePrefix) {

		// Install web pieces if not already installed
		Validate.isTrue(fileManager.exists(pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "/WEB-INF/web.xml")),
				"web applicatio must be settup first");

		Set<Dependency> servletProxyDependency = projectOperations.getFocusedProjectMetadata().getPom().getDependenciesExcludingVersion(new Dependency(
						"net.tzolov.http", "servletproxy", "0.0.0"));

		if (CollectionUtils.isEmpty(servletProxyDependency)) {
			// First time adding proxy -> update the POM dependencies
			updatePomDependencies();
		}

		urlPattern = (urlPattern == null) ? "/" + proxyName + "/*" : urlPattern;

		this.updateWebXml(proxyName, proxyHost, proxyPort, urlPattern,
				removePrefix);
	}

	private void updatePomDependencies() {

		//Element configuration = getConfiguration();
		Element configuration = XmlUtils.getConfiguration(getClass());

		// Add dependencies
		List<Element> dependencies = XmlUtils.findElements("/configuration/dependencies/dependency", configuration);
		for (Element dependency : dependencies) {
			projectOperations.addDependency(projectOperations.getFocusedModuleName(), new Dependency(dependency));
		}

		// Add repository
		List<Element> vegaRepositories = XmlUtils.findElements("/configuration/repositories/repository", configuration);
		for (Element repositoryElement : vegaRepositories) {
			projectOperations.addRepository(projectOperations.getFocusedModuleName(), new Repository(repositoryElement));
		}
	}

	private void updateWebXml(String proxyName, String proxyHost,
			int proxyPort, String urlPattern, boolean removePrefix) {

		Validate.isTrue(proxyPort > 0, "proxyPort must be > 0");

		WebXmlHolder webXmlHolder = getWebXmlHolder();

		WebXmlParam proxyHostParam = new WebXmlUtils.WebXmlParam("proxyHost",
				proxyHost);
		WebXmlParam proxyPortParam = new WebXmlUtils.WebXmlParam("proxyPort",
				String.valueOf(proxyPort));
		WebXmlParam removePrefixParam = new WebXmlUtils.WebXmlParam(
				"removePrefix", String.valueOf(removePrefix));

		WebXmlUtils.addServlet(proxyName, "net.tzolov.http.ProxyServlet",
				urlPattern, 0, webXmlHolder.getWebXmlDoc(),
				"Servlet proxy dependencies", proxyHostParam, proxyPortParam,
				removePrefixParam);

		XmlUtils.writeXml(webXmlHolder.getMutableWebXml().getOutputStream(),
				webXmlHolder.getWebXmlDoc());
	}

	public void removeProxyServlet(String proxyName) {

		Validate.notNull(proxyName, "Null http proxy name");

		WebXmlHolder webXmlHolder = getWebXmlHolder();

		removeSingelProxy(webXmlHolder.getWebXmlDoc(), proxyName);

		XmlUtils.writeXml(webXmlHolder.getMutableWebXml().getOutputStream(),
				webXmlHolder.getWebXmlDoc());
	}

	public void removeAllServletProxies() {

		WebXmlHolder webXmlHolder = getWebXmlHolder();

		List<Element> proxyServletNameElements = XmlUtils
				.findElements(
						"//servlet[servlet-class='net.tzolov.http.ProxyServlet']/servlet-name",
						(Element) webXmlHolder.getWebXmlDoc().getFirstChild());

		if (!CollectionUtils.isEmpty(proxyServletNameElements)) {
			for (Element servletNameElement : proxyServletNameElements) {
				if (servletNameElement != null) {
					removeSingelProxy(webXmlHolder.getWebXmlDoc(),
							servletNameElement.getTextContent());
				}
			}
		}

		XmlUtils.writeXml(webXmlHolder.getMutableWebXml().getOutputStream(),
				webXmlHolder.getWebXmlDoc());
	}

	private void removeSingelProxy(Document webXmlDoc, String proxyName) {

		Element servlet = XmlUtils.findFirstElement(
				"/web-app/servlet[servlet-name = '" + proxyName + "']",
				webXmlDoc.getDocumentElement());

		if (servlet != null) {
			Node parentNode = servlet.getParentNode();
			if (parentNode != null) {
				parentNode.removeChild(servlet);
			}
		} else {
			logger.info("The http proxy[" + proxyName + "] not found!");
		}

		// Remove servlet's mapping definition
		Element servletMapping = XmlUtils.findFirstElement(
				"/web-app/servlet-mapping[servlet-name = '" + proxyName + "']",
				webXmlDoc.getDocumentElement());

		if (servletMapping != null) {
			Node parentNode = servletMapping.getParentNode();
			if (parentNode != null) {
				parentNode.removeChild(servletMapping);
			}
		}

	}

	public SortedSet<String> listProxyServlets() {

		WebXmlHolder webXmlHolder = getWebXmlHolder();

		List<Element> proxyServletNameElements = XmlUtils
				.findElements(
						"//servlet[servlet-class='net.tzolov.http.ProxyServlet']/servlet-name",
						(Element) webXmlHolder.getWebXmlDoc().getFirstChild());

		SortedSet<String> set = new TreeSet<String>();
		if (!CollectionUtils.isEmpty(proxyServletNameElements)) {
			for (Element servletNameElement : proxyServletNameElements) {
				if (servletNameElement != null) {
					set.add(servletNameElement.getTextContent());
				}
			}
		}

		return set;
	}

	private WebXmlHolder getWebXmlHolder() {
		String webXml = pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/web.xml");
		Validate.isTrue(fileManager.exists(webXml),
				"web.xml not found; cannot continue");

		MutableFile mutableWebXml = null;
		Document webXmlDoc;
		try {
			mutableWebXml = fileManager.updateFile(webXml);
			webXmlDoc = XmlUtils.getDocumentBuilder().parse(
					mutableWebXml.getInputStream());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return new WebXmlHolder(mutableWebXml, webXmlDoc);
	}
}