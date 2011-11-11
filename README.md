Jaiku Presence archiver.

## Building

This project is built with Maven. 

This code requires an installed snapshot of [Dust](https://github.com/aholstenson/dust). Checkout the repository and then run mvn install on the checked out code.

To package a WAR just run mvn package, the WAR can then be found in target/

## Testing

There is a class named JettyServer in src/test/java that can be run from an IDE for testing. It will launch a server on port 8888.
