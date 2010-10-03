import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public final class Start {

	public static void main(String[] args) throws Exception {

		String contextPath = System.getProperty("contextPath", "");

		int port = Integer.parseInt(System.getProperty("port", "8080"));

		Server server = new Server(port);

		System.out.println("Start :" + port + "/" + contextPath);

		ProtectionDomain domain = Start.class.getProtectionDomain();
		
		URL location = domain.getCodeSource().getLocation();

		WebAppContext webapp = new WebAppContext();

		webapp.setContextPath("/" + contextPath);

		webapp.setDescriptor(location.toExternalForm() + "/WEB-INF/web.xml");

		webapp.setServer(server);
		webapp.setWar(location.toExternalForm());

		server.setHandler(webapp);
		server.start();

		System.out.println("Jetty running on port:" + port);

		server.join();

		System.out.println("Jetty stopped!");
	}
}
