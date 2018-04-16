import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Vector;

public class Server {

   private ServerSocket ss = null;
   private Socket cs = null;

   /**
    * Class constructor
    * Copied from Week 12 Slides - Page 14
    */
   public Server() {
      try {
         ss = new ServerSocket(16789);
         
         while (true) {
            //get connection
            cs = ss.accept();
                                    
            //create ThreadServer object & start it
            ThreadServer ts = new ThreadServer(cs);
            ts.start();  
         } 
      } catch(IOException ioe) {
         System.out.println("ioe catch");
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

      public ThreadServer(Socket cs) {
         this.cs = cs;
      }  
      
      public void run() {
         BufferedReader br;
         PrintWriter pw;
         String clientMsg;
         try {
            br = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            pw = new PrintWriter(new OutputStreamWriter(cs.getOutputStream()));
            
            clientMsg = br.readLine();
            System.out.println("Server read: " + clientMsg);
            pw.println(clientMsg);
            pw.flush();                  
         }
         catch(IOException ioe) {
            ioe.printStackTrace();
         }
      }

   }
}
