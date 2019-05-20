import java.io.*;
import java.net.*;

class ListenWorker extends Thread {    
    Socket sock;                  
    ListenWorker (Socket s) {
        sock = s;
      } 
                                        
    public void run(){
      PrintStream out = null;
      BufferedReader in = null;
      try {
  
        out = new PrintStream(sock.getOutputStream());
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
  
        String sockdata = "";
  
        while (!sockdata.equals("quit")) {
          sockdata = in.readLine ();
          if (sockdata != null){
              System.out.println(sockdata);
          }
          System.out.flush ();
        }
  
        out.println("Connected to listener");
        out.flush();
    
      } catch (IOException x) {
        System.out.println("exception error");
      }
    }
  }

  public class MyListener {

    public static boolean controlSwitch = true;
    public static void main(String a[]) throws IOException {
      int q_len = 6;  //max in queue 
      int port = 2540; //port it runs on 
      Socket sock;
  
      ServerSocket servsock = new ServerSocket(port, q_len);
      System.out.println("Mohammed's Port listener running at 2540.\n");
      while (controlSwitch) { // 
        sock = servsock.accept();
        new ListenWorker (sock).start();
      }
    }
  }