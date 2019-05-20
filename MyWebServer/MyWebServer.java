/*--------------------------------------------------------
1. Mohammed Abdulkadir

2. Java 

3. Instructions
> javac MyWebServer.java

4. Run Instructions
> java MyWebServer
> in browser run on localhost:2540

5. List of files needed for running the program.
 a. MyWebServer.java
 b. http-streams.txt
 c. serverlog.txt
 d. addNums.html

6. Notes: reused code from Inet & Joke files, and from Readfiles.java provided in condor/elliot
----------------------------------------------------------*/

import java.net.*;
import java.io.*;
import java.util.*;

public class MyWebServer {

    public static boolean control = true;
  
    public static void main(String a[]) throws IOException {
      int q_len = 6;  //max length in queue 
      int port = 2540; //port number
      Socket sock;
  
      ServerSocket servsock = new ServerSocket(port, q_len); //no need to close socket for now, because it should keep waiting for incoming requests. 

      System.out.println("Mohammed's WebServer starting up, listening at port 2540.\n");
      while (control) { //waits for a new connection 
        sock = servsock.accept();
        new Worker(sock).start(); // creates new server worker object

      }
      if (control = false) { 
      servsock.close();
      }
    }
  
  }

class Worker extends Thread { //worker class inheriting from Thread 
  Socket sock;
  Worker(Socket s) {
    sock = s;
  }

  public void run() {
    PrintStream in = null;
    BufferedReader out = null;

    try {
      //accepts inputstream and outputs to the server
       in = new PrintStream(sock.getOutputStream());
       out = new BufferedReader(new InputStreamReader(sock.getInputStream()));

      String fromBrowser = out.readLine();

      if (fromBrowser == null || fromBrowser.length() == 0) 
          System.out.println("not found");

      else {
        String fileName = fromBrowser.substring(4, fromBrowser.length() - 9); //endIndex-beginIndex
         
           //.text and .java are treated as text/plain so it can be viewed in the browser
          if (fileName.endsWith(".java") || fileName.endsWith(".txt")){
            System.out.println(fileName);
            String contentType = "text/plain";
            OutputToBrowser(fileName, contentType, in);

          }

          else if (fileName.endsWith(".html")){
            System.out.println(fileName);
            String contentType = "text/html";  //as to MIME types html files will have text/html type 
            OutputToBrowser(fileName, contentType, in);
        
        }
          else if (!fileName.endsWith("/")){
          System.out.println(fileName);
          String contentType = "text/plain";
          OutputToBrowser(fileName, contentType, in);
        }

        //check if the html file has a "cgi" in it then call addnum function
        else if (fileName.contains("cgi")){
          System.out.println(fileName);
          String contentType = "text/html";
          getAddNums(fileName, contentType, in);
      
        }
        else if (fileName.endsWith("/")){
          System.out.println(fileName);
          String contentType = "text/html";
          BrowserHeader(fileName, contentType, in);
        }
      }
  
    System.out.flush();
    sock.close();

    } catch(IOException ie){
      ie.printStackTrace();
    }
  } 

  //reused code from condor/elliot Readfiles.java
  //reading java files from a directory and display to output 

  public void BrowserHeader(String fileName, String contentType, PrintStream in){
    int dataSize = 0;
    in.print("HTTP/1.1 200 OK");      //send back the correct header to show
    in.print("Content-Length: " + 47); 
    in.print("Content-type: " + contentType + "\r\n\r\n");

  
    File f1 = new File( "./"+ fileName + "/");
    File[] stringFiles = f1.listFiles( ); //get all files under your directory 

    if (stringFiles != null){
      for ( int i = 0 ; i < stringFiles.length ; i ++ ) {
        if (stringFiles[i].isDirectory()) {
          in.print("<a href=\"" + stringFiles[i].getName()  + "/\">/" + stringFiles[i].getName() + "/</a><br>");
        } else if (stringFiles[i].isFile()) {
          in.print("<a href=\"" + stringFiles[i].getName() + "\">" + stringFiles[i].getName() + "</a> (" + stringFiles[i].length() + ")<br>");

        }

      }
    }
  }
   
//creating buffer array of unknown in java - link provided 
//https://stackoverflow.com/questions/13554302/how-to-declare-a-byte-array-of-infinite-size-dynamic-in-java/13554334

  public void OutputToBrowser (String fileName, String contentType, PrintStream in ){
    final int DEFAULT_BUFFER_SIZE = 10024 * 4;  //uknown size of buffer just in case so we can print everything in the file                                           
    File f2 = new File( fileName);              //to hold data in given filename. 

    if (!fileName.equals("/") && !f2.isFile()) {

      try {
        InputStream readInput = new FileInputStream(fileName.substring(1, fileName.length()));
        
        in.print("HTTP/1.1 200 OK");  //correct HTTP header response will be sent back.
        in.print("Content-Length: " + 47);
        in.print("Content-type: "+ contentType + "\r\n\r\n");

        byte[] data = new byte[DEFAULT_BUFFER_SIZE];
        int Bytebuffer = readInput.read(data);
        in.write(data, 0, Bytebuffer);                                                             
        in.flush();                                                                                
        readInput.close();  

      } catch (IOException x){
        x.printStackTrace();
        System.out.println("Error connecting");
      }
    }
  }

class getAddNums {

  String name;
  String num1;
  String num2;
  int sum;

}
  public void getAddNums (String fileName, String contentType, PrintStream in){
    //GET fake cgi form input person=num1=num2 
    String f = fileName.substring(22, fileName.length());
    String [] fields = f.split("[=&]");

     String name = fields[1]; //name holder
     String num1 = fields[3]; //first number holder
     String num2 = fields[5]; //second number holder
     int sum = Integer.valueOf(num1) + Integer.valueOf(num2);    //adds the sum of num1 and num2 in converted strings to int

    //sends back a new HTML page with the input name that shows the result of sum of num1 and num2 
    String result  = "Dear "+ name +", the sum of "+ num1 +" and "+ num2 +" is "+ sum;
  
    in.print("HTTP/1.1 200 OK");
    in.print("Content-Length: " + 47);
    in.print("Content-type: "+ contentType + "\r\n\r\n");  
    in.print("<p>" + result + "</p>");

  } 

}

  