#Resolving the "no main manifest attribute" error when running a JAR

https://linuxpip.org/fix-no-main-manifest-attribute/
https://stackoverflow.com/questions/1082580/how-to-build-jars-from-intellij-properly/45303637#45303637

#To add to IntelliJ
Cmd-,
External Tools
Click +
Program: java
Arguments: -jar <path-to-jar> (e.g. -jar /Users/billjohnson/Work/Experiment/ZincViewer/build/libs/ZincViewer-1.0-SNAPSHOT.jar)
Working directory: <your home dir> (e.g. /Users/billjohnson/)
