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
   
   //Keep list of ports for each client
   private Vector <Integer> ports = new Vector <Integer>();
   
   //Starting port number for each client
   //Increment for each new connection
   private int udpPortNums = 5665;
   
   /**
    * Class constructor
    */
   public Server() {
      try {
         //start up tcp stuff
         ss = new ServerSocket(tcpPort);
         System.out.println("Now listening on TCP host " + InetAddress.getByName("localhost") + " and port " + tcpPort);
         
         //Thread to wait for TCP connections
         TCPThread tt = new TCPThread(ss);
         tt.start();
         
         //Open DatagramSocket with the connection port
         DatagramSocket ds = new DatagramSocket(udpPort);
         System.out.println("Now listening on UDP port " + udpPort);
         
         //Thread to wait for UDP connections
         UDPThread ut = new UDPThread(ds);
         ut.start();          
         
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
    * Thread for UDP connections
    * Waits for a DatagramPacket
    * Starts new connection if message is "new connection"
    * Sends message to all clients if else
    *
    * @param DatagramSocket ds   the socket for the server to get messages 
    */
   class UDPThread extends Thread {
      DatagramSocket ds;

      public UDPThread(DatagramSocket ds) {
         this.ds = ds;
      }
      
      public void run() {
         try {
            while (true) {
               //DatagramSocket waits to receive messages
               DatagramPacket getdp = new DatagramPacket(new byte[1024], 1024);
               ds.receive(getdp);
               String msg = new String(getdp.getData());
               
               //if "new connection" user just connected
               //give them a unique port and add it to list
               if (msg.trim().equals("new connection")) {
                  System.out.println("Received UDP connection");
                  DatagramSocket newDS = new DatagramSocket();
                  udpPortNums++;
                  System.out.println("New port for user: " + udpPortNums);
                  ports.add(udpPortNums);
                  byte[] portNum = new byte[1024];
                  portNum = new String("" + udpPortNums).getBytes();
                  DatagramPacket newDp = new DatagramPacket(portNum, portNum.length, InetAddress.getByName("localhost"), getdp.getPort()); 
                  newDS.send(newDp);
                  newDS.close();                     
               } else {
                  // got a chat message from one of the clients
                  System.out.println("Received UDP msg: " + msg.trim());
                  InetAddress ipAddr = InetAddress.getByName("localhost");
                  byte[] sendData = getdp.getData();
                  
                  //Send message to all ports
                  //Really don't know if this is how you're supposed to do it
                  //But hey I tried
                  for (int port : ports) {
                     DatagramPacket sendDp = new DatagramPacket(sendData, sendData.length, ipAddr, port);
                     ds.send(sendDp);
                  }                             
               } 
            }
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
      }
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
