## 1.3.2
 - add loggers on as2 sender module and the MessageSenderHelper class.
 

## 1.3.1 (2015-07-16)
 - fix major alert on storage archive module

## 1.3 (2015-07-16)
 - inject as2 session in worker
 - add logger in as2 sender module

## 1.2 (2015-07-15)
 - use the templating maven plugin to indicate the version of project
 - worker returns void value
 - create a new class: MessageSenderHelper to send easily a message in as2
 - supports AES-128, AES-192 and AES-256 algorithms
 - create an archive module to store output message
 - as2 sender module can call the archive module

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



