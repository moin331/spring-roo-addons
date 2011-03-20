* Install using this command: 
  
	felix shell start file:C:\somewhere\target\net.tzolov.jetty.executable.war.roo.addon-0.1.0.BUILD-SNAPSHOT.jar

* Verify success via "osgi ps" and look for an entry at the bottom such as:
  
	[ 90] [Active] [1] net-tzolov-jetty-executable-war-roo-addon (0.1.0.BUILD-SNAPSHOT)

* You'll also have the new add-on's commands available (type 'execute' and hit TAB)

* You can uninstall via: 
	
	osgi uninstall --bundleSymbolicName net.tzolov.jetty.executable.war.roo.addon

* After uninstalling, you'll see the "executable war setup" commands have disappeared
