package net.tzolov.roo.addon.oauth2.provider;

import static org.springframework.roo.support.util.CollectionUtils.isEmpty; 

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.tzolov.roo.addon.oauth2.OAuth2Common;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.process.manager.FileManager;
import org.springframework.roo.project.Dependency;
import org.springframework.roo.project.Path;
import org.springframework.roo.project.PathResolver;
import org.springframework.roo.project.ProjectOperations;
import org.springframework.roo.project.Property;
import org.springframework.roo.project.Repository;
import org.springframework.roo.support.util.Assert;
import org.springframework.roo.support.util.DomUtils;
import org.springframework.roo.support.util.FileCopyUtils;
import org.springframework.roo.support.util.FileUtils;
import org.springframework.roo.support.util.StringUtils;
import org.springframework.roo.support.util.XmlElementBuilder;
import org.springframework.roo.support.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Provides OAuth2 Protected Resource installation services.
 *
 * @author Christian Tzolov (christian@tzolov.net)
 * @since 1.0
 */
@Component
@Service
public class Oauth2ProtectedResourceOperationsImpl implements Oauth2ProtectedResourceOperations  {

	// Fields
	@Reference private FileManager fileManager;
	@Reference private PathResolver pathResolver;
	@Reference private ProjectOperations projectOperations;

	public boolean isOAuth2ProtectedResourceInstallationPossible() {
		return projectOperations.isFocusedProjectAvailable() 
				&& fileManager.exists(pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/web.xml")) 
				&& hasDependencyExcludingVersion(OAuth2Common.SPRING_SECURITY); 
	}

	private boolean hasDependencyExcludingVersion(Dependency dependencyDef ) {
		Set<Dependency> dependecies = projectOperations.getFocusedModule().getDependenciesExcludingVersion(dependencyDef);
		return !isEmpty(dependecies);
	}
	
	public void installOAuth2ProtectedResource(String protectedResourceName, String urlPathAccess) {
		
		// Parse the configuration.xml file
		Element configuration = XmlUtils.getConfiguration(AuthorizationAndResourceServerOperationsImpl.class);

		// Add POM properties
		updatePomProperties(configuration, projectOperations.getFocusedModuleName());

		// Add POM repositories
		updateRepository(configuration,  projectOperations.getFocusedModuleName());
		
		// Add dependencies to POM
		updateDependencies(configuration, projectOperations.getFocusedModuleName());
		
		//
		// applicationContext-security.xml
		// 
		String securityAppContextPath = pathResolver.getFocusedIdentifier(Path.SRC_MAIN_RESOURCES, "META-INF/spring/applicationContext-security.xml");
		Document secAppContextDocument = XmlUtils.readXml(fileManager.getInputStream(securityAppContextPath));
		Element securityAppContext = secAppContextDocument.getDocumentElement();
		Element httpChain = DomUtils.findFirstElementByName("http", securityAppContext);
		Assert.notNull(httpChain, "Could not find http in " + securityAppContext);
		
		// Add oauth:, p: and c: namespaces to the applicationContext-security.xml
		OAuth2Common.addNamespaces(securityAppContext);
		
		boolean httpUseExpressions = (httpChain.getAttribute("use-expressions") != null && httpChain.getAttribute("use-expressions").equalsIgnoreCase("true"));

		//Add security oauth resource intercepter and filter
		String resourceUrlPathAccess = urlPathAccess;
		if (StringUtils.isBlank(resourceUrlPathAccess)) {
			resourceUrlPathAccess = httpUseExpressions? 
					"hasRole('ROLE_USER') and oauthClientHasRole('ROLE_CLIENT') and oauthHasScope('read')":
					"ROLE_USER,SCOPE_READ";
		}
		httpChain.insertBefore(new XmlElementBuilder("intercept-url", secAppContextDocument)
			.addAttribute("pattern", "/" + protectedResourceName.toLowerCase() + "/**")
			.addAttribute("access", resourceUrlPathAccess).build(), httpChain.getFirstChild());

		if (null == XmlUtils.findFirstElement("/beans/http/custom-filter[@ref = 'resourceServerFilter']", securityAppContext)) {
					
			httpChain.appendChild(new XmlElementBuilder("custom-filter", secAppContextDocument).addAttribute("ref", "resourceServerFilter").addAttribute("after", "EXCEPTION_TRANSLATION_FILTER").build());
	
			securityAppContext.appendChild(new XmlElementBuilder("oauth:resource-server", secAppContextDocument)
				.addAttribute("id", "resourceServerFilter")
				//.addAttribute("resource-id", protectedResourceName.toLowerCase())
				.addAttribute("token-services-ref", "tokenServices").build());
	
			securityAppContext.appendChild(new XmlElementBuilder("beans:bean", secAppContextDocument)
				.addAttribute("id", "tokenServices")
				.addAttribute("class", "org.springframework.security.oauth2.provider.token.RandomValueTokenServices")
				.addAttribute("p:tokenStore-ref", "tokenStore")
				.addAttribute("p:supportRefreshToken", "true").build());
	
			securityAppContext.appendChild(new XmlElementBuilder("beans:bean", secAppContextDocument)
				.addAttribute("id", "tokenStore")
				.addAttribute("class", "org.springframework.security.oauth2.provider.token.JdbcTokenStore")
				.addAttribute("c:dataSource-ref", "dataSource").build());
		}		

		boolean isAccessDecisionManagerAttributePresent = !StringUtils.isBlank(httpChain.getAttribute("access-decision-manager-ref"));
		
		if (httpUseExpressions) {
			//HTTP expression handler with support for oauth2 clients and scopes
			if (null == XmlUtils.findFirstElement("/beans/http/expression-handler[@ref = 'oauth2WebSecurityExpressionHandler']", securityAppContext)) {
				httpChain.appendChild(new XmlElementBuilder("expression-handler", secAppContextDocument).addAttribute("ref", "oauth2WebSecurityExpressionHandler").build());
				securityAppContext.appendChild(new XmlElementBuilder("beans:bean", secAppContextDocument).addAttribute("id", "oauth2WebSecurityExpressionHandler").addAttribute("class", "org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler").build());
			}			
		} else if (!isAccessDecisionManagerAttributePresent) {
			Element voterList = new XmlElementBuilder("beans:list", secAppContextDocument).build();
			voterList.appendChild(new XmlElementBuilder("beans:bean", secAppContextDocument).addAttribute("class", "org.springframework.security.oauth2.provider.vote.ScopeVoter").build());
			voterList.appendChild(new XmlElementBuilder("beans:bean", secAppContextDocument).addAttribute("class", "org.springframework.security.access.vote.RoleVoter").build());
			voterList.appendChild(new XmlElementBuilder("beans:bean", secAppContextDocument).addAttribute("class", "org.springframework.security.access.vote.AuthenticatedVoter").build());
		
			securityAppContext.appendChild(new XmlElementBuilder("beans:bean", secAppContextDocument)
				.addAttribute("id", "accessDecisionManager")
				.addAttribute("class", "org.springframework.security.access.vote.UnanimousBased").build())
					.appendChild(new XmlElementBuilder("beans:constructor-arg", secAppContextDocument).build())
						.appendChild(voterList);
			
			httpChain.setAttribute("access-decision-manager-ref", "accessDecisionManager");
		}		

		//Enable method-level expression handler
		if (null == XmlUtils.findFirstElement("/beans/expression-handler[@id = 'oauthExpressionHandler']", securityAppContext)) {
			securityAppContext.appendChild(new XmlElementBuilder("oauth:expression-handler", secAppContextDocument)
				.addAttribute("id", "oauthExpressionHandler").build());
			
			securityAppContext.appendChild(new XmlElementBuilder("global-method-security", secAppContextDocument)
				.addAttribute("pre-post-annotations", "enabled")
				.addAttribute("proxy-target-class", "true").build())
					.appendChild(new XmlElementBuilder("expression-handler", secAppContextDocument)
						.addAttribute("ref", "oauthExpressionHandler").build());
		}
		
		fileManager.createOrUpdateTextFileIfRequired(securityAppContextPath, XmlUtils.nodeToString(secAppContextDocument), false);
		String resourceControllerNamePrefix = protectedResourceName.substring(0, 1).toUpperCase() + protectedResourceName.substring(1); 
		Map<String,String> substitutionMap = new HashMap<String, String>();
		substitutionMap.put("__TOP_LEVEL_PACKAGE__", projectOperations.getFocusedTopLevelPackage().getFullyQualifiedPackageName());
		substitutionMap.put("__PROTECTED_RESOURCE_CONTROLLER__", resourceControllerNamePrefix);
		substitutionMap.put("__PROTECTED_RESOURCE_MAPPING__", protectedResourceName.toLowerCase());
		
		String rootPackageName = projectOperations.getFocusedTopLevelPackage().getFullyQualifiedPackageName();				
		copyFileTemplate(Path.SRC_MAIN_JAVA, (rootPackageName + ".web").replace(".", "/")  + "/" + protectedResourceName + "Controller.java", "TemplateProtectedResourceController-template.java", substitutionMap);

		//
		// webmvc-config.xml
		// 
		String webConfigPath = pathResolver.getFocusedIdentifier(Path.SRC_MAIN_WEBAPP, "WEB-INF/spring/webmvc-config.xml");
		Document webConfigDocument = XmlUtils.readXml(fileManager.getInputStream(webConfigPath));		
		OAuth2Common.replaceSimpleMappingExceptionResolver(webConfigDocument);				
		fileManager.createOrUpdateTextFileIfRequired(webConfigPath, XmlUtils.nodeToString(webConfigDocument), false);
	}

	private void updatePomProperties(final Element configuration, final String moduleName) {
		List<Element> databaseProperties = XmlUtils.findElements("/configuration/spring-security/properties/*", configuration);
		for (Element property : databaseProperties) {
			projectOperations.addProperty(moduleName, new Property(property));
		}
	}

	private void updateDependencies(final Element configuration, final String moduleName) {
		List<Dependency> dependencies = new ArrayList<Dependency>();
		List<Element> securityDependencies = XmlUtils.findElements("/configuration/spring-security/dependencies/dependency", configuration);
		for (Element dependencyElement : securityDependencies) {			
			dependencies.add(new Dependency(dependencyElement));
		}
		projectOperations.addDependencies(moduleName, dependencies);		
	}
	
	private void updateRepository(final Element configuration, final String moduleName) {
		List<Repository> repositories = new ArrayList<Repository>();
		List<Element> securityRepositories = XmlUtils.findElements("/configuration/spring-security/repositories/repository", configuration);
		for (Element repositoryElement : securityRepositories) {
			repositories.add(new Repository(repositoryElement));
		}
		projectOperations.addRepositories(moduleName, repositories);		
	}

	private void copyFileTemplate(Path path, String targetName, String templateName, Map<String, String> substitusionMap) {

		String targetFileDestination = pathResolver.getFocusedIdentifier(path, targetName);

		if (!fileManager.exists(targetFileDestination)) {
			try {
				String input = FileCopyUtils.copyToString(new InputStreamReader(FileUtils.getInputStream(getClass(), templateName)));

				if (!isEmpty(substitusionMap)) {
					for (Entry<String, String> substitutionEntry : substitusionMap.entrySet()) {
						input = input.replace(substitutionEntry.getKey(), substitutionEntry.getValue());
					}
				}

				FileCopyUtils.copy(input.getBytes(), fileManager.createFile(targetFileDestination).getOutputStream());
				
			} catch (IOException ioe) {
				throw new IllegalStateException(ioe);
			}
		}
	}
}