import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Vector;

public class Server {

   private ServerSocket ss = null;
   private Socket cs = null;
   private int port = 16789;
   
   //Keep list of one printwriter per client
   private Vector <PrintWriter> writers = new Vector <PrintWriter>();
   
   //NEED A UDP SERVER BUT DONT NEED A THREAD?
   //RECIEVE MESSAGE SLIDE HERE
   
   /**
    * Class constructor
    */
    //TCP SERVER
   public Server() {
      try {
         ss = new ServerSocket(port);
         System.out.println("Now listening on host " + InetAddress.getByName("localhost") + " and port " + port);
         
         while (true) {
            //get connection
            cs = ss.accept();
                                    
            //create ThreadServer object & start it
            ThreadServer ts = new ThreadServer(cs);
            ts.start();  
         } 
      } catch(IOException ioe) {
         ioe.printStackTrace();
      }
   }
   
   /**
    * Main method
    */
   public static void main (String [] args) {
      new Server(); 
   }
   
   /**
    * Opens Input/Output Streams for each client
    * Copied from Week 12 Slides - Page 15
    */
    
   class ThreadServer extends Thread {
      Socket cs;
      BufferedReader br;
      PrintWriter pw;

      public ThreadServer(Socket cs) {
         this.cs = cs;
      }  
      
      public void run() {
         try {
            br = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(cs.getOutputStream()));
                        
            //Give connection message and add user to client list
            pw.println(">>> Welcome to the chat");
            pw.flush();
            writers.add(pw);
            
            String username = br.readLine();
            
            for (PrintWriter writer : writers) {
               writer.println(">>> New user " + username + " entered chat room");
               writer.flush();
            }
            
            while (true) {
               //get a message
               String clientMsg = br.readLine();
               if (clientMsg == null) {
                  return;
               }
               
               //send message to all chat users
               for (PrintWriter writer : writers) {
                  writer.println(clientMsg);
                  writer.flush();
               }
            }              
         }
         catch(IOException ioe) {
            ioe.printStackTrace();
         } finally {
            if (pw != null) {
               writers.remove(pw);  
            }
            
            try {
               cs.close();
            } catch (IOException ioe) {
               ioe.printStackTrace();    
            }
         }
      }

   }
}
