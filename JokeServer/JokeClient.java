/*--------------------------------------------------------
1.Mohammed Abdulkadir / 04/20/2019 

2. Java 1.8

3. Javac JokeClient.java

4.In separate shell windows run:
> java JokeClient

type Command+C to terminate program.

5. files needed for running the program.

 a. checklist.html
 b. JokeServer.java
 c. JokeClient.java
 d. JokeClientAdmin.java

5. Notes:
I have reused some code from the InetClient and also from Joke-thread.html example. I also wasn't much concerned on efficieny,
rather getting the job done and working!
----------------------------------------------------------*/

import java.io.*; //import all input output java libraries 
import java.net.*; //input all from network library
import java.util.*;

class Client {

    int Client_id;
    Socket socket;

    public Client(){
        Random rand = new Random();
        //obtain random from [0-99999], should scale up to 100,000 jokes.
        int random = rand.nextInt(100000);
        this.Client_id = random;
    }
    void ServerRequest(String serverName, int serverPort) throws IOException {
         this.socket = new Socket(serverName, serverPort);
        }

    void nameToServer(String name) throws IOException{ 
        PrintStream toServer;
        toServer = new PrintStream(this.socket.getOutputStream());
        String output = "NAME:" + name + " " + Client_id;
        toServer.println(output);
        toServer.flush();
    }

    //Modified code from InetClient
    void getRemoteAddress () {
        //Socket sock;
        BufferedReader fromServer;
        PrintStream toServer;
        String textFromServer;

        try {
            //sock = new Socket(serverName, 4545);
            /** 
             * 
             * sock creates a new socket connection, and toServer gets inputs and send it to the server
             * while fromServer outputs it as IP from the server. 
             */
            fromServer = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            toServer = new PrintStream(this.socket.getOutputStream());
            toServer.println(Integer.toString(this.Client_id));
            toServer.flush();

           for (int i = 1; i <= 3; i++){
                textFromServer = fromServer.readLine();
                if (textFromServer != null) 
                System.out.println(textFromServer);
            } 
           //sock.close();
        } catch (IOException x) {
            System.out.println ("Socket error.");
            x.printStackTrace();
        }
    }


}
public class JokeClient {
    public static void main(String[] args){
        Client jokeClient = new Client();
        String serverName;
        String clientName;
        int serverPort = 4545;
        /** client starts and asks for user's input then send it to the server
         *  and to quit, user types "quit" then get a nice message,
         * else client will keep waiting for a request.
         */
        if (args.length < 1) serverName = "localhost"; //this will be using the local machine as server depending on your machine. 
        else
            serverName = args[0];
        System.out.println("Mohammed's Joke's Client, 1.8 .\n");
        System.out.println("using server: " + serverName + ", port: 4545");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        try{ // User is requested to enter his name so it can be used as a response for parsing joke or proverb,
            //before we enter loop. 
            System.out.println("Please enter your name with no spaces: ");
            clientName = in.readLine();
            jokeClient.ServerRequest(serverName, serverPort);
            jokeClient.nameToServer(clientName);
            //sock.close();
    

            if (serverName != null){
                jokeClient.ServerRequest(serverName, serverPort);
                jokeClient.nameToServer(clientName);
                //servSock.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }

    try {
        String name;
        do {
            //hit enter to request for a new joke or proverb 
            // and will end if user types quit. 
            System.out.print("Hit 'Enter' to get a new joke or proverb or type 'quit' to end: ");
            System.out.flush();
            name = in.readLine();
            
            if (name.equals("")) {
                jokeClient.ServerRequest(serverName, serverPort);
                jokeClient.getRemoteAddress();
                //sock.close();
            } 

        }  while (!name.equals("quit")); //will stay in loop if its not equal to quit.
        System.out.println("User cancelled ... Good Bye!");
    } catch(IOException x){
        x.printStackTrace();
    }

    }
}

