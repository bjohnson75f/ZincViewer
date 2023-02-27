# Zinc Viewer
Zinc viewer is a little java swing utility that will parse and display Zinc data formats in a Table type view.  Zinc is a format specified by Project Haystack.  You can learn more about Zinc at https://project-haystack.org/doc/docHaystack/Zinc.

# Known Problems
- The CSV parsing is a simple split on "," without regard to commas embedded within a text field.  It will NOT properly parse these fields currently.

## To add to IntelliJ
    IntelliJ Preferences (Cmd-,)
    External Tools
    Click +
    Program: java
    Arguments: -jar <path-to-jar> (e.g. -jar /Users/billjohnson/Work/Experiment/ZincViewer/build/libs/ZincViewer-1.0-SNAPSHOT.jar)
    Working directory: <your home dir> (e.g. /Users/billjohnson/)

## Resolving the "no main manifest attribute" error when running a JAR

https://linuxpip.org/fix-no-main-manifest-attribute/
https://stackoverflow.com/questions/1082580/how-to-build-jars-from-intellij-properly/45303637#45303637

