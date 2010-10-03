package __TOP_LEVEL_PACKAGE__;

import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public final class ExecWar {

	public static void main(String[] args) throws Exception {

		String contextPath = System.getProperty("path", "");

		int port = Integer.parseInt(System.getProperty("port", "8080"));

		Server server = new Server(port);

		System.out.println("Staring Jetty at: http://<host>:" + port + "/" + contextPath);

		ProtectionDomain domain = ExecWar.class.getProtectionDomain();
		
		URL location = domain.getCodeSource().getLocation();

		WebAppContext webapp = new WebAppContext();

		webapp.setContextPath("/" + contextPath);

		webapp.setDescriptor(location.toExternalForm() + "/WEB-INF/web.xml");

		webapp.setServer(server);
		webapp.setWar(location.toExternalForm());

		server.setHandler(webapp);
		server.start();

		System.out.println("Servert started at: http://<host>:" + port + "/" + contextPath);

		server.join();

	}
}
