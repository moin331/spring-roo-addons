package net.tzolov.http.servletproxy.roo.addon;

import java.util.SortedSet;
import java.util.logging.Logger;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.springframework.roo.shell.CliAvailabilityIndicator;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;

/**
 */
@Component
@Service
public class HttpServletProxyCommands implements CommandMarker {

	private static Logger logger = Logger
			.getLogger(HttpServletProxyCommands.class.getName());

	@Reference
	private HttpServletProxyOperations operations;

	@CliAvailabilityIndicator("http-proxy add")
	public boolean isApplicableAddServletProxy() {
		return operations.isApplicableServletProxy();
	}

	@CliCommand(value = "http-proxy add", help = "Add new ServletProxy configuration")
	public void addServletProxy(
			@CliOption(key = "name", mandatory = true, help = "Name of the proxy entry. Also used as urlPatter when the latter is not specified") String proxyName,
			@CliOption(key = "host", mandatory = true, help = "Name of the host proxy") String proxyHost,
			@CliOption(key = "port", mandatory = true, help = "Port of the host proxy") Integer proxyPort,
			@CliOption(key = "urlPattern", mandatory = false, help = "Optional servlet url pattern") String urlPattern,
			@CliOption(key = "removePrefix", mandatory = false, unspecifiedDefaultValue = "true", help = "Removes proxy prefix") Boolean removePrefix) {

		operations.addServletProxy(proxyName, proxyHost, proxyPort, urlPattern,
				removePrefix);
	}

	@CliAvailabilityIndicator("http-proxy remove")
	public boolean isApplicableRemoveServletProxy() {
		return operations.isApplicableServletProxy();
	}

	@CliCommand(value = "http-proxy remove", help = "Remove an existing ServletProxy configuration")
	public void removeServletProxy(
			@CliOption(key = "name", mandatory = true, help = "Name of the proxy entry being removed") String proxyName) {

		operations.removeProxyServlet(proxyName);
	}

	@CliAvailabilityIndicator("http-proxy cleanAll")
	public boolean isApplicableRemoveAllServletProxies() {
		return operations.isApplicableServletProxy();
	}


	@CliCommand(value = "http-proxy cleanAll", help = "Remove all existing ServletProxy configurations")
	public void removeAllServletProxies() {
		operations.removeAllServletProxies();
	}

	@CliAvailabilityIndicator("http-proxy list")
	public boolean isApplicableListServletProxy() {
		return operations.isApplicableServletProxy();
	}

	@CliCommand(value = "http-proxy list", help = "Shows the existing ServletProxy configurations")
	public SortedSet<String> listServletProxy() {
		return operations.listProxyServlets();
	}
}