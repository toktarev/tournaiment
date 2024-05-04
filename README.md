# This is tournament multiplayer game server.

REQUIREMENTS:

Java 21 version or later https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html

Maven: https://maven.apache.org/download.cgi

1) To install : ./build.sh
2) To run default game server use: ./run.sh it will start server on 8080 port

To play:

telnet <GAME_SERVER_HOST> <GAME_SERVER_PORT>

GAME_SERVER_PORT is 8080 by default

After follow messages received from server

3) If you want to customize server copy ./src/main/resources/application.properties to your
location and modify there server port and other server messages.
After that run:

java -jar ./target/tournament-release.jar -spring.config.name=<NEW_PROPERTY_FILE_LOCATION>


