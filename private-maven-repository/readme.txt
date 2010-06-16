Your Very Own Google Code Maven Repo:
http://www.thewebsemantic.com/2009/04/11/your-very-own-google-code-maven-repo/

mvn install:install-file  -Dfile=path-to-your-artifact-jar \
                          -DgroupId=your.groupId \
                          -DartifactId=your-artifactId \
                          -Dversion=version \
                          -Dpackaging=jar \
                          -DcreateChecksum=true

Note: the checksum part is important, maven will need that. 
your.groupId is typically a reverse domain, like we use in java package names.