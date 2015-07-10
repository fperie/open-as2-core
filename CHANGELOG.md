## 1.2
 - use the templating maven plugin to indicate the version of project
 - worker returns void value

## 1.1 (2015-07-06)
 - add loggers
 - SHA-224, SHA-256, SHA-384 and SHA-512 are supported
 
## 1.0 (2015-06-08)
 - create a maven project
 - use a modern logging api : migration to SLF4J
 - use the generics in list (java 5)
 - fix the potential null pointer exception
 - use the last version (1.46) bcmail api
 - create a worker interface to execute a custom action to process as2 message
 - give the capability to implements it's own stream instead default socket (http request for example)



