
/*--------------------------------------------------------

1.Mohammed Abdulkadir / 04/20/2019

2. Java 1.8

3. Javac JokeClientAdmin.java

4.In separate shell windows run:
> java JokeClientAdmin

type quit to end or Command+C to terminate program.

5. files needed for running the program.

 a. checklist.html
 b. JokeServer.java
 c. JokeClient.java
 d. JokeClientAdmin.java

5. Notes:
I get have a resource leak warning, and everytime I close the socket program crashes. 
----------------------------------------------------------*/

import java.io.*;  //import all necessary input/out libraries
import java.net.*; //import all necessary network libraries

public class JokeClientAdmin {

    public static void main(String[] args) {
        int serverPort = 5050; //port to listen to
        String serverName;

        if (args.length < 1) serverName = "localhost"; //default localhost 
        else
        serverName = args[0];
        System.out.println("Mohammed's Joke's Client Admin, 1.8 .\n");
        System.out.printf("using server: " + serverName + ", port: 5050 \n");

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));  //BufferedReader will read input from Stdin 

        try {
            String name;

            do{
                System.out.print("Hit Enter to switch between Proverb and Joke Mode - or type 'quit' to end session: ");
                System.out.flush(); 
                name = in.readLine();

                if (name.equals("")) { //toggle switch 
                    toggleMode(serverName, serverPort);
                
            }

        }  while (!name.equals("quit")); //will stay in the loop but exits session if name == quit 
        System.out.println("User cancelled ... Good Bye!");
    } catch(IOException x){
        x.printStackTrace();
        }
    }

    static void toggleMode (String serverName, int servPort) {
         Socket sock;
         BufferedReader fromServer;
         PrintStream toServer;
         String textFromServer;

         try {
            sock = new Socket(serverName, 5050);
            /** 
             * 
             * sock creates a new socket connection, and 'toServer' gets inputs and send it to the server
             * while 'fromServer' outputs it as IP from the server. 
             */
            fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            toServer = new PrintStream(sock.getOutputStream());

            toServer.println("ToggleMode");
            toServer.flush();
            sock.close();

    }  catch (IOException x) {
        System.out.println ("Socket error.");
        x.printStackTrace();
        }
    }
}
