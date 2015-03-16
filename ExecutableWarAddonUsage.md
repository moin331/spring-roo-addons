## How to use the executable war addon ##

  * Add executable war setup - configures your maven project to build executable wars
```
roo> executable war setup
```
This will add jetty to your pom dependecies, will add ExecWar.java to your top package level directory and will update your maven assembly plugin to produce executable wars.

  * Build an executable war:
```
roo> perform assembly
```
or directly from the command console:
```
 mvn assembly:assembly
```
This generates the standards XXXX.war application as well as an additional XXX-EXECWAR.war that can be executed as explained below or deployed in a web container.

  * Execute the war
From the command line run
```
 java -jar XXX-EXECWAR.war 
```
Will start the application at: http://your-host:8080. You can set different port or context path like this:
```
 java -Dport=8081 -Dpath=app -jar XXX-EXECWAR.war 
```
Now the same application will be available at: http://your-host:8081/app