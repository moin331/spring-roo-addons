package net.tzolov.http.servletproxy.roo.addon;

import java.util.SortedSet;

/**
 */
public interface HttpServletProxyOperations {

	boolean isApplicableServletProxy();
	
	void addServletProxy(String proxyName, String proxyHost,
				int proxyPort, String urlPattern, boolean removePrefix);
	 
	SortedSet<String> listProxyServlets();
	
	void removeProxyServlet(String proxyName);
	
	void removeAllServletProxies();	
}