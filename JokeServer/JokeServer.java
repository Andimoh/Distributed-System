/*--------------------------------------------------------

1.Mohammed Abdulkadir - 04/20/2019

2. Java 1.8

3. Javac JokeServer.java

4.In separate shell windows run:
> java JokeServer

type Command+C to terminate program.

5. files needed for running the program.

 a. checklist.html
 b. JokeServer.java
 c. JokeClient.java
 d. JokeClientAdmin.java

5. Notes:
I have reused codes from the InetServer. I also ran into difficulty trying to get the jokes to randomize. 
----------------------------------------------------------*/ 

import java.io.*;  
import java.net.*;
import java.util.*;

public class JokeServer{
   /** JokeServer class imported from InetServer - (reused code)
    * where port is the port variable the server will be running on and client will listen to.
    *
    * q_lenght is the maximum lenght of queue
    * socket is the client socket which will be assigned as requests come in.
    */

    public static void main(String[] args) throws IOException {

        int q_len = 6; 

        int defaultClientPort = 4545; //4545 default port location for the server
        int defaultAdminPort = 5050; //primary port for client to connect to
        int serverPort = -1;
        int adminPort = -1;

        if (args.length > 0 ){
            serverPort = defaultClientPort;
            adminPort = defaultAdminPort;
        }
        else { 
            serverPort = defaultClientPort;
            adminPort = defaultAdminPort;
        } 

       /* if (args.length < 1) serverName = "localhost";
        else
            serverName = args[0]; */
            
        Socket sock; 
        ServerSocket servSock = new ServerSocket(serverPort, q_len);
        ServerSocket adminSock = new ServerSocket(adminPort, q_len);

    
        ToggleMode tMode = new ToggleMode(adminSock);
        tMode.start();
        System.out.printf("Mohammed's Joke Server 1.8 starting up, listening at port 4545. \n", serverPort); 

        while(true){
            sock = servSock.accept();  //this will wait and accept and another client 
            new Worker(sock).start(); 
        }
    }
}

/** -----------------------------------------------------------------------------------------------------------
 * some resources I used, 
 * used some resources online 
 * https://stackoverflow.com/questions/4615958/make-a-client-server-java-application
 */

class Message_Type {
	 public static final Message_Type JOKE = new Message_Type();
	 public static final Message_Type PROVERB = new Message_Type();
}

class Client_State {

    int client_id = -1;
    String clientName;
    int [] jokeOrder = {0, 1, 2, 3};
    int [] proverbOrder = {0, 1, 2, 3};
    int jokeIndex = 0;
    int proverbIndex = 0;
}

//Class worker inherits from thread, and every worker class is created as a different thread.
class Worker extends Thread{

    Socket socket;
    String header;
    private static HashMap<Integer, Client_State> Client_State = new HashMap<>();

    // joke template
    private static String jokes[]= {
        "JA = What do you call cheese that's not yours? .... its nacho cheese",
        "JB = what do you call a candle in a suit of armour? ... A knight light",
        "JC = Two drums and a cymbal fall off a cliff ... Ba dum Tish!",
        "JD = Why did the policeman smell bad? ... He was on duty"
    };

    //proverb template
    private static String proverbs[] = {
        "PA = I do not fear computers. I fear lack of them - Isaac Asimov",
        "PB = Computer Science is no more about computers than astronomy is about telescopes- Edsger W. Dijkstra",
        "PC = You miss 100% of the shots you never took - Micheal G Scott",
        "PD = The computers was born to solve problems that did not exist before. - Bill Gates"
    };

    Worker (Socket s){ 
    	this.socket = s;
        if (s.getLocalPort() == 4545) {
            this.header = "";
        }
        else {
            this.header = "<S2>";
        }
    }

    public void run(){
        
        PrintStream out = null;
        BufferedReader in = null;

        //accepts input from the client (IP) and outputs to the server
        try{
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream())); //
            out = new PrintStream(this.socket.getOutputStream()); //

            try{
                String name;
                name = in.readLine();
               
                if (name.startsWith("NAME:")){ //get client's name
                    addClient(name);
                }
                else if (name.equals("quit")){ 
                    System.out.println("User cancelled");
                    out.print("Session over \n");
                }
              
                else {
                    try{
                        int client_id = Integer.parseInt(name);
                        printMode(out, client_id);
                    }catch (NumberFormatException e){
                        out.print("Error client ID");
                    }
                    
                }
                              
            } catch(IOException x){       //this will catch and print error messages found during server check
                System.out.println("Server error");
                x.printStackTrace();
            }
          
            socket.close();  //this will catch and print any error found when socket is closing or opening 
        } catch(IOException ioe){
            System.out.println(ioe);  
        }
    }

    //adds new client 
    void addClient(String output){

        String [] input = output.split(" ");
        String name = input[0].substring(5);
        int client_id = Integer.parseInt(input[1]);

        //Random rand = new Random(); 
        if (!Client_State.containsKey(client_id)){
            Client_State Client = new Client_State();
            Client.clientName = name;
            Client.client_id = client_id;
            Client_State.put(client_id, Client);
        }
    }
     
     void printMode(PrintStream out, int client_id){

         // when we get a connection 
            out.println("Joke or Proverb requested");
            Client_State client;

            //Random generator = new Random();
            //int jokeIndex = generator.nextInt(client.get(jokeOrder));
            //int proverbIndex = generator.nextInt(Client.proverbOrder);
             //return;
            
           if (Client_State.containsKey(client_id)){
                client = Client_State.get(client_id);
            }
            else {

               client = new Client_State();
               client.client_id = client_id;

            }
            
           	String response = this.header;
            String clientString =  " (" + client.clientName + ")";

            if (Mode.getMode() == Message_Type.PROVERB){
                System.out.println("Proverbs hmm .."); //prints a nice message to the server depending on what mode you're in

            }
  
            if (Mode.getMode() == Message_Type.JOKE){
                System.out.println("Joke is funny eh?"); 
                int joke = client.jokeOrder[client.jokeIndex++];
                response += clientString;
                response += (jokes[joke]);
           
            
            // when we reach the last joke, the following message will be concatenated,
            // along with the last response (joke). 
            if (client.jokeIndex == client.jokeOrder.length){
                    response += ("\n >>> JOKE CYCLE COMPLETED\n");   
                    client.jokeIndex = 0;
                }

                out.println(response);
            
            } else {

                int proverb = client.proverbOrder[client.proverbIndex++];
                response += clientString;
                response += (proverbs[proverb]);
                
                // when we reach the last proverb, the following message will be concatenated,
                // along with the last response (proverb).  
                if (client.proverbIndex == client.proverbOrder.length){
                    response += ("\n >>> PROVERB CYCLE COMPLETED\n");
                    client.proverbIndex = 0;
                }

                out.println(response);
            }
            
    }

} 

class Mode{ 
    private static Message_Type type = Message_Type.JOKE;
    public static void toggle(){
        if (type == Message_Type.JOKE) {
        type = Message_Type.PROVERB;
        }
        else {
        type = Message_Type.JOKE;
        }
    //toggle mode that prints what your current mode is set to between joke and proverbs
    if (type == Message_Type.JOKE){
        System.out.println("JOKE MODE \n");
    }
    else {
        System.out.println("PROVERB MODE \n");
        }
    }

    public static Message_Type getMode(){
        return type;
    }
}

class ToggleMode extends Thread{
    private ServerSocket serverSocket;
    public ToggleMode(ServerSocket servSock){
        this.serverSocket = servSock;
    }

    public void run(){
        try {
            //accept new clients 
            while (true) {
                this.serverSocket.accept();
                Mode.toggle();
            }
        } catch(IOException e){ //error handling when I/O operation failed 
            e.printStackTrace();
        }
        
    }
}