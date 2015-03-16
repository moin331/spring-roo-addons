_![http://lanc.webs.upv.es/lanc2009/imagenes/new_en.gif](http://lanc.webs.upv.es/lanc2009/imagenes/new_en.gif)
All addons are registered with the central OBR repository (RooBot) and can be [installed directly from your roo shell](http://code.google.com/p/spring-roo-addons/wiki/AddonsInstallationViaRooBot)_
## jaxb-addon ##
> JAXB2 Schema complier addon. It adds the [maven-jaxb2-plugin](https://maven-jaxb2-plugin.dev.java.net/) to your pom.xml. The plugin participates in the generate-code phase and produces code of the schema-derived classes out of the XML Schemas, DTDs and so on.

> [Installation](JaxbAddonInstallation.md) | [Usage](JaxbAddonUsage.md) | Tutorial: [Spring Roo Addons : JAXB2 binding and Content Negotiation Support Tutorial](http://tzolov.blogspot.com/2010/05/spring-roo-addons-jaxb2-binding-and.html) with [annotated roo.log](JaxbHttpContentNegotiationTutorialRooLog.md)

## http-representation addon ##
> Addon that adds Content Negotiating View Resolver configuration to your application context: [MVC multiple representations](http://static.springsource.org/spring/docs/3.1.x/spring-framework-reference/html/mvc.html#mvc-multiple-representations) By default the addon is configured to resolve the HTML(JSP), XML and JSON representations. It favors path extension (e.g. .xml, .json, .jsonp ..) over AcceptHeader(s). This configuration is JAXB2 centric!
> This addon also provide CORS (Cross Origin Resource Sharing) support as well.

> [Installation](HttpRepresentationAddonInstallation.md) | [Usage](HttpRepresentationAddonUsage.md) | Tutorial: [Spring Roo Addons : JAXB2 binding and Content Negotiation Support Tutorial](http://tzolov.blogspot.com/2010/05/spring-roo-addons-jaxb2-binding-and.html) with [annotated roo.log](JaxbHttpContentNegotiationTutorialRooLog.md)

## executable-war addon ##
> The Executable WAR Addon configures the Maven project to makes it possible to create web applications that can be run from the command line (java -jar your-application.war) or deployed in a container. When run from the command line the executable-war starts an embedded Jetty webserver that in turn hosts the webapp contained in the war. For more information visit http://tzolov.blogspot.com/2010/10/create-executable-wars-with-spring-roo.html

> [Installation](ExecutableWarAddonInstallation.md) | [Usage](ExecutableWarAddonUsage.md)

## http-proxy addon ##
> The Http-Proxy Addon allows you to configure your web application to get around the Same Origin Policy restriction (e.g. cross-domain). You can use it to install new servlet proxies (http-proxy add), list them (http-proxy list), remove those not needed (http-proxy remove) and eventually remove all proxies with a single command (http-proxy cleanAll).


> [Installation](HttpProxyAddonInstallation.md) | [Usage](HttpProxyAddonUsage.md)


---

_**Note**: All addons (except HttpProxy) here are compatible with [SpringRoo 1.2.0.RELEASE](http://www.springsource.org/roo). Information about the previous [SpringRoo 1.1.x.RELEASE](http://www.springsource.org/roo) compatible version can be find in http://code.google.com/p/spring-roo-addons/w/list_



---

_YourKit is kindly supporting open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of innovative and intelligent tools for profiling
Java and .NET applications. Take a look at YourKit's leading software products:
[YourKit Java Profiler](http://www.yourkit.com/java/profiler/index.jsp) and
[YourKit .NET Profiler](http://www.yourkit.com/.net/profiler/index.jsp)_


---

We acknowledge [Headway Software](http://www.headwaysoftware.com) for giving us free licenses of ![http://structure101.com/images/s101_140.png](http://structure101.com/images/s101_140.png)