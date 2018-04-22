import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Vector;

public class Server {

   private ServerSocket ss = null;
   private Socket cs = null;
   private int tcpPort = 16789;
   private int udpPort = 5665;
   
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
         //start up tcp stuff
         ss = new ServerSocket(tcpPort);
         System.out.println("Now listening on TCP host " + InetAddress.getByName("localhost") + " and port " + tcpPort);
         
         //Need the thread to wait for tcp connections
         //so it doesn't get stuck waiting on ss.accept();
         TCPThread tt = new TCPThread(ss);
         tt.start();
         
         //udp stuff
         DatagramSocket ds = new DatagramSocket(udpPort);
         System.out.println("Now listening on UDP port " + udpPort);
         byte receiveData[] = new byte[1024];
         byte sendData[] = new byte[1024];
         
         while (true) {
            DatagramPacket getdp = new DatagramPacket(receiveData, receiveData.length);
            ds.receive(getdp);
            String msgStr = new String(getdp.getData());
            System.out.println("Received UDP msg: " + msgStr.trim());
            InetAddress ipAddr = getdp.getAddress();
            int port = getdp.getPort();
            
            //send it to everyone?
            sendData = getdp.getData();
            DatagramPacket senddp = new DatagramPacket(sendData, sendData.length, ipAddr, port);
            //I can't tell if it's sending or not?
            ds.send(senddp);         
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
    
   
   /*
    * Thread to listen for TCP connections
    * So it doesn't get stuck on accept()
    * Starts ThreadServer when a connection is made
    */
   class TCPThread extends Thread {
      
      ServerSocket ss;
      Socket cs;
      
      public TCPThread(ServerSocket ss) {
         this.ss = ss;
      }
      
      public void run() {
         while (true) {
            try {
               cs = ss.accept();
               
               ThreadServer ts = new ThreadServer(cs);
               ts.start();
            } catch (IOException ioe) {
               ioe.printStackTrace();
            }
         }
      }
   
   }
    
   /**
    * Opens Input/Output Streams for each client
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
