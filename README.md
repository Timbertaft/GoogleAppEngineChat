# GoogleAppeEnginge Chat

Once setup, acts as a simulation of both a server and related client environments using an intermediary GoogleAppEngine web service located at https://ultra-resolver-223804.appspot.com/ .

In order to run the client, you will need to enter the command
cd C:\Users\mdpoc\IdeaProjects\Computation_Engine\RMI\src\webclient\target
java -Djava.security.policy=C:\Users\mdpoc\IdeaProjects\Computation_Engine\RMI\src\webclient\security.policy -jar Computation_Engine-1.0-SNAPSHOT-jar-with-dependencies.jar

This command insures ports have necessary permissions to operate correctly!

If you haven't run the program yet: 

1. Navigate to the Server folder using cmd.
2. With Maven already installed and system variables correclty configured, type the following command: mvn appengine::update (NOTE: You should only do this if you are making changes directly to the server!
3. Navigate to the Client folder containing the pom.xml file.
4. Enter the command mvn clean compile assembly:single to insure the files are configured to work on your system.
5. Enter cd C:\Users\mdpoc\IdeaProjects\Computation_Engine\RMI\src\webclient\target
java -Djava.security.policy=C:\Users\mdpoc\IdeaProjects\Computation_Engine\RMI\src\webclient\security.policy -jar Computation_Engine-1.0-SNAPSHOT-jar-with-dependencies.jar (will need to be adjusted to correct filepath given your own personal system directories.)

Offers 4 options: 
  Option 1: Computes Pi up to a number of digits inputted by the user.
  Option 2: Computes the Prime numbers between a range of two values provided by the user.
  Option 3: Opens a chat client to communicate with other clients connected to the server.
  Option 4: terminates the client.
 
Once you choose Option 3 and choose a username, details of  your client are saved to the webservice to be referenced by other users.
 
 Thank you for looking at the program.
