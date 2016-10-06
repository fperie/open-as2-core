# open-as2-core
The open as2 core project is an free BSD open source project, hosted by sourceforge, and available to the following link: http://sourceforge.net/projects/openas2/files/latest/download

The last update has been released the 2013-04-25.

Some code refactoring has been made to the original source code :
 - create a maven project,
 - create an eclipse launcher to start open as2 server,
 - use a modern logging api : migration to SLF4J,
 - use the generics in list (java 5)
 - fix the potential null pointer exception
 - use classpath: and ${} in as2 config file
 - use the last version (1.46) bcmail api
 - create a worker interface to execute a custom action to process as2 message,
 - give the choice to store as2 files or not
 - give the capability to implements it's own stream instead default socket (http request for example)
 - etc...
 
The jar file is available in maven central repository with the following GAV:

```xml
<dependency>
   <groupId>fr.fabienperie.open-as2</groupId>
   <artifactId>open-as2-core</artifactId>
   <version>1.3.3</version>
</dependency>
```
