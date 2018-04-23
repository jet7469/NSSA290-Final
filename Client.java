import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;
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
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;


public class Client implements ActionListener {

   private Socket s = null; 
   private DatagramSocket ds = null;
   private DatagramPacket dp = null;
   private BufferedReader br;
   private PrintWriter pw;
   private PrintStream ps;
   private DataInputStream dis;
   private String username;
   private String host;
   private int port;
   private int newPort;
   private DatagramSocket receiveSocket;
   
   //The GUI stuff
   private ClientGUI gui;
   private JTextPane jtpChat;
   private JTextArea jtaMessage;
   private JButton jbSend;
   private JMenuItem jmiExit;
   
   //use these instead of strings for selected protocol
   private static final int TCP_PROTOCOL = 0;
   private static final int UDP_PROTOCOL = 1;
   
   private static int protocol;      
   
   
   /**
    * Class Constructor
    */
   public Client() {
      gui = new ClientGUI(this); //initiate GUI
         
      //Load these components from the GUI class so they can be referenced
      jtpChat = gui.jtpChat;
      jtaMessage = gui.jtaMessage;
      jbSend = gui.jbSend;
      jmiExit = gui.jmiExit;
      
      getConnectInfo(); //show various connection dialogs for user
      
      //Make the connections with the info you get from user
      try {
         if(protocol == TCP_PROTOCOL){
            System.out.println("Now contacting Server '" + host + "' with TCP/IP connection from client TCP/IP address");
            s = new Socket(host, port);
            ps = new PrintStream(s.getOutputStream());
            dis = new DataInputStream(s.getInputStream());
            System.out.println("Receiving communication from server using host '" + host
                           + "' and port " + port);
         } else if(protocol == UDP_PROTOCOL) {
            //send connect info to socket on server side
            System.out.println("Now contacting Server with UDP connection");
            DatagramSocket ds = new DatagramSocket();
            String newConn = "new connection";
            byte[] connMsg = newConn.getBytes();
            DatagramPacket dp = new DatagramPacket(connMsg, connMsg.length, InetAddress.getByName("localhost"), port);
            ds.send(dp);
            //wait for response with port number
            dp = new DatagramPacket(new byte[1024], 1024);
            ds.receive(dp);
            System.out.println("Received connection from server using port " + port);
            gui.insertServerMsg(">>>Welcome to the chat room");
            String portMsg = new String(dp.getData());
            newPort = Integer.parseInt(portMsg.trim());
            receiveSocket = new DatagramSocket(newPort);
         }          
      } catch (IOException ioe) {
         ioe.printStackTrace();
      }
      
      //Start correct threads
      if(protocol == TCP_PROTOCOL){
         if (s != null && ps != null && dis != null) {
            ClientThread ct = new ClientThread(s, gui);
            ct.start();
            System.out.println("TCP Thread started");   
         }
      } else if(protocol == UDP_PROTOCOL) {
         ClientThread ct2 = new ClientThread(gui, receiveSocket);
         ct2.start();
         System.out.println("UDP Thread started");
      }
   }
   
   
   /**
    * Main method
    */
   public static void main (String [] args) {
      new Client();    
   }
 
   
   /**
    * Displays the various Dialogs:
    * 1. Gets username
    * 2. Gets connection protocol
    * 3. Gets server (if TCP/IP selected)
    */
   private void getConnectInfo() {
      //Get username for user
      username = (String) JOptionPane.showInputDialog(gui, "Enter a username:");

      //Get user's choice for connection protocol
      Object[] protocols = {"TCP/IP", "UDP"};
      String protocolChoice = (String) JOptionPane.showInputDialog(gui, "Choose how to connect:",
                        "Protocol Selection", JOptionPane.PLAIN_MESSAGE, null, protocols, protocols[0]);
                        
      //save protocol selection as int for easier reference
      if (protocolChoice == null || protocolChoice.equals(protocols[0])) {
         protocol = TCP_PROTOCOL;
      } else {
         protocol = UDP_PROTOCOL;
      }
      
      port = Integer.parseInt(JOptionPane.showInputDialog(gui, "Enter Port Number:"));

      //get hostname if TCP
      if (protocol == TCP_PROTOCOL) {
          host = (String) JOptionPane.showInputDialog(gui, "Enter Hostname:");
      }            
   }
   
   
   /**
    * ActionListener Handler
    */
   public void actionPerformed(ActionEvent ae) {
      
      //String command = ae.getActionCommand();
      Object choice = ae.getSource();
      
      //User pressed the send button
      if (choice == jbSend) {
         //see if username has been created, create one if not
         while (username == null) {
            username = JOptionPane.showInputDialog(null, "Enter a username:");
         }
          
         //get text from the chat box
         String text = username +":  " + jtaMessage.getText();
         System.out.println("Sending message: " + jtaMessage.getText());

               
         //send it as a string msg with a username
         if (protocol == TCP_PROTOCOL) {
            ps.println(text);
            ps.flush();
         } else if (protocol == UDP_PROTOCOL) {
            try {
               byte[] sendData = new byte[1024];
               sendData = text.getBytes();
               InetAddress addr = InetAddress.getByName("localhost");
               DatagramSocket ds = new DatagramSocket();
               DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length, addr, port);
               ds.send(sendPack);
               System.out.println("Sent message to server");
            } catch (UnknownHostException uhe) {
               uhe.printStackTrace();
            } catch (SocketException se) {
               se.printStackTrace();
            } catch (IOException ioe) {
               ioe.printStackTrace();
            }
         }
         
         jtaMessage.setText("");
                              

      } else if (choice == jmiExit) {
         //show confirmation and then close if yes
         int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?",
                                               "Confirm Exit", JOptionPane.YES_NO_OPTION);
                                                    
         if (n == 0) {
            System.out.println("Ending communications with Server '" + host + "'");
            System.exit(0);
         }
      }     
   }
   
   //UDP CLIENT DATAGRAM SEND MESSAGE SLIDE
   //MIGHT NEED A THREAD
   
   
   /**
    * Inner Client Thread class
    */
   class ClientThread extends Thread {
      
      Socket s = null;
      ClientGUI gui;
      DatagramSocket ds;
   
      //TCP
      public ClientThread(Socket s, ClientGUI gui) {
         this.s = s;
         this.gui = gui;
      }
      
      //UDP
      public ClientThread(ClientGUI gui, DatagramSocket ds){
         this.gui = gui;
         this.ds = ds;
      }
      
      public void run() {
         try {
            String serverMsg;
            if(s != null){
               br = new BufferedReader(new InputStreamReader(s.getInputStream()));
               
               //send over username
               ps.println("" + username);
               ps.flush();
               
               while ((serverMsg = br.readLine()) != null) {
                  if (serverMsg.startsWith(">>>")) {
                     gui.insertServerMsg(serverMsg);
                  } else {
                     gui.insertChatMsg(serverMsg);
                  }          
               }
            } else if (ds != null) {
               byte[] receiveData = new byte[1024];
               boolean done = false;
               
               while(!done){
                  DatagramPacket dp = new DatagramPacket(receiveData, receiveData.length);
                  ds.receive(dp);
                  serverMsg = new String(dp.getData());
                  if(serverMsg.equals("")){
                     done = true;
                  }
                  serverMsg.trim();
                  if (serverMsg.startsWith(">>>")) {
                     gui.insertServerMsg(serverMsg);
                  } else {
                     gui.insertChatMsg(serverMsg);
                  } 
               }
            }
         
         }
         catch (IOException ioe) {
            ioe.printStackTrace();
         }         
      
      }
   
   }
   
}


