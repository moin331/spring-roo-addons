* Install using this command: 
  
	osgi start --url osgi start --url file:C:\...\net.tzolov.jetty.executable.war.roo.addon-2.0.0-SNAPSHOT.jar

* Verify success via "osgi ps" and look for an entry at the bottom such as:
  
	[  80] [Active     ] [    1] Spring Roo - Executable War (2.0.0.RELEASE)

* You'll also have the new add-on's commands available (type 'execute' and hit TAB)

* You can uninstall via: 
	
	osgi uninstall --bundleSymbolicName net.tzolov.jetty.executable.war.roo.addon

* After uninstalling, you'll see the "executable war setup" commands have disappeared
