Your Very Own Google Code Maven Repo:
http://www.thewebsemantic.com/2009/04/11/your-very-own-google-code-maven-repo/

-------------------------------------------------------------------------------
1. Insert the jar in your local repository
-------------------------------------------------------------------------------
   mvn install:install-file  -Dfile=path-to-your-artifact-jar \
                             -DgroupId=your.groupId \
                             -DartifactId=your-artifactId \
                             -Dversion=version \
                             -Dpackaging=jar \
                             -DcreateChecksum=true

The checksum part is important, maven will need that. The your.groupId is 
typically a reverse domain, like we use in java package names.

-------------------------------------------------------------------------------
2. Copy the tree to the googlecode svn
-------------------------------------------------------------------------------

-------------------------------------------------------------------------------
3. Add the following maven repo to your project's POM
-------------------------------------------------------------------------------
	<repository>
		<id>tzolov-private-repository</id>
		<url>http://spring-roo-addons.googlecode.com/svn/private-maven-repository/repository</url>
	</repository>
