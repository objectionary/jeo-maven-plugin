# Why do we need these compiled classes?

These classes were compiled for "generics" integration test.
You can read more about the test here:

jeo-maven-plugin/src/it/generics

Here we try to assemble the `Application.xmir` file. This
assembling requires all the classes to be compiled and loaded
to the classpath.