======================================================================
Spring Roo 1.10.M1 - JAXB2 XML Schema compiler Add-on
======================================================================
This addon adds the maven-jaxb2-plugin to your pom.xml alnog some 
additional maven dependencies.
 
JAXB2 Maven2 (https://maven-jaxb2-plugin.dev.java.net/) allows you to 
generate code with JAXB RI in your Maven builds. The plugin participates 
in the generate-code phase and produces code of the schema-derived 
classes out of the XML Schemas, DTDs and so on.

======================================================================
1. Get the source code and build the addon 
======================================================================
1. Checkout the code 

   svn co http://spring-roo-addons.googlecode.com/svn/trunk/net.tzolov.jaxb.roo.addon addon-jaxb 

2. build the addon-jaxb:
   
   > mvn clean install

Result is a net.tzolov.jaxb.roo.addon-2.1.0-SNAPSHOT.jar created in  
your target directory. 

======================================================================
2. Installation
======================================================================
   
Open the roo shell

  roo>osgi start --url file:/<absolute path to the target directory>/net.tzolov.jaxb.roo.addon-2.1.0-SNAPSHOT.jar

If successful this operation will restart your roo shell.

To verify the installation type:

  roo>osgi ps 

Amongst other addons you should see:

[  76] [Active     ] [    1] Spring Roo - JAXB2 Schema complier (2.1.0.SNAPSHOT)

======================================================================
3. Uninstall the addon
======================================================================
From the roo shell call:

  roo>osgi uninstall --bundleSymbolicName net.tzolov.jaxb.roo.addon
  
addon would disappear from the osgi process list:

  roo>osgi ps

Also the "jaxb xsd compiler setup" would disappear from the help list:

  roo>help

======================================================================
4. Using the addon
======================================================================
from the Roo shell run:

	roo>jaxb xsd compiler setup
	
    Optional parameters:
	   generateDirectory -  Target directory for the generated code, 
	                        target/generated-sources/xjc by default 
	   schemaDirectory   -  Specifies the schema directory, 
	                        src/main/resources by default

Now drop your XSD files in the schema directory (src/main/resources 
by default). After rebuilding the project you should see the generated
java classes in your generate directory (by default target/generated-sources/xjc)
 