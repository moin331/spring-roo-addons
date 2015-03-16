### Tutorial Roo Log ###

Uninstall older addon versions:
```
osgi uninstall --bundleSymbolicName net.tzolov.jaxb.roo.addon
osgi uninstall --bundleSymbolicName net.tzolov.httprepresentations.roo.addon
```

Install the required addons
```
addon install --bundleSymbolicName net.tzolov.jaxb.roo.addon
addon install --bundleSymbolicName net.tzolov.httprepresentations.roo.addon
```


Check if the right addons are installed
```
osgi ps
```
Amongst the other you should see the following addons installed.
```
...
[  76] [Active     ] [    1] Spring Roo - JAXB2 Schema complier (3.2.0.RELEASE)
[  78] [Active     ] [    1] Spring Roo - HTTP Content Negotiating Resolver (3.2.0.RELEASE)
```

Create the project and set the persistent layer
```
project --topLevelPackage org.demo
persistence setup --database HYPERSONIC_IN_MEMORY --provider HIBERNATE 
```

Set the jaxb maven configuration
```
jaxb xsd compiler setup --generateDirectory src/main/java
```

Copy the [people.xsd](http://spring-roo-addons.googlecode.com/files/people.xsd) in your src/main/resource directory and the build the project to enforce the generation.
```
perform package
```
Check whether the org.example.people... classes have been generated in your src/main/java directory

Create the People controller
```
web mvc setup
controller class --class ~.web.PeopleController
```

Add the getPeople(...) method to the PeopleController as explained in the blog. Do not forget to add the import declaration for the People and the People.Person classes!

Extend the index.jspx file as explained in the blog.

Setup the http representation configuration
```
http resource representation setup
```

Add the People class to the oxm marshaling settings. You should be able to use autocompletion to resolve it:
```
http resource add oxm --class org.example.people.People
```

Build the project and start it in the Tomcat container
```
perform package
perform command --mavenCommand tomcat:run 
```