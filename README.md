# TotemProcessBuilder
Provides pipe system and output redirection.

## Simple usage example
```java
// Shell style processing with pipes and output redirection.
String command = "ls -l | cat > ./output.txt 2> ./error.txt";

// Command will run as direct process under JVM (not as sh process).
TotemProcessBuilder processBuilder = new TotemProcessBuilder(command);

// Starts process thread.
processBuilder.start();

// processBuilder.stop(); Would instantly kill the process.

// Waits for process to finish.
processBuilder.waitFor();
```
