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
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

public class Client implements ActionListener {

   private Socket s = null; 
   private BufferedReader br;
   private PrintWriter pw;
   private String username;
   
   //The GUI stuff
   private ClientGUI gui;
   private JTextPane jtpChat;
   private JTextArea jtaMessage;
   private JButton jbSend;
   private JMenuItem jmiDisconnect;
   private JMenuItem jmiExit;
   
   //use these instead of strings for selected protocol
   private static final int TCP_PROTOCOL = 0;
   private static final int UDP_PROTOCOL = 1;
   
   private static int protocol = TCP_PROTOCOL; //Default is TCP/IP
      
   
   /**
    * Class Constructor
    */
   public Client() {
      ClientGUI gui = new ClientGUI(this); //initiate GUI
         
      //Load these components from the GUI class so they can be referenced
      jtpChat = gui.jtpChat;
      jtaMessage = gui.jtaMessage;
      jbSend = gui.jbSend;
      jmiDisconnect = gui.jmiDisconnect;
      jmiExit = gui.jmiExit;
      
      getConnectInfo(); //show various connection dialogs for user
      
      //TODO: Connection happens here
      try {
         s = new Socket("localhost", 16789);
         pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
         
         ClientThread cth = new ClientThread(s);
         cth.start();
      } catch (IOException ioe) {
         ioe.printStackTrace();
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
      
      //TODO: create random username if user closes the username dialog box
      
      

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
      
      //TODO: Another dialog asking which server to connect to
      //when the user selects TCP as connection protocol
      if (protocol == TCP_PROTOCOL) {
      
      }            
   }
   
   public void actionPerformed(ActionEvent ae) {
      
      //String command = ae.getActionCommand();
      Object choice = ae.getSource();
      
      //User pressed the send button
      if (choice == jbSend) {
         //see if username has been created, create one if not
         if (username == null) {
            username = JOptionPane.showInputDialog(null, "Enter a username:");
         }
         
         //get text from the chat box
         String text = username +":  " + jtaMessage.getText();
               
         //send it as a string msg with a username
         pw.println(text);
         pw.flush();
         
         jtaMessage.setText("");
                              

      } else if (choice == jmiDisconnect) {
         //show confirmation and then reset everything
         int m = JOptionPane.showConfirmDialog(null, "Are you sure you want to disconnect?",
                                               "Confirm Disconnect", JOptionPane.YES_NO_OPTION);
                                               
         if (m == 0) {
            //TODO: reset everything and go back to original screen
         }    
      } else if (choice == jmiExit) {
         //show confirmation and then close if yes
         int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?",
                                               "Confirm Exit", JOptionPane.YES_NO_OPTION);
                                                    
         if (n == 0) {
            //TODO: Disconnect from server
            System.exit(0);
         }

      }
      
   }
   
   class ClientThread extends Thread {
      
      Socket s;
   
      public ClientThread(Socket s) {
         this.s = s;
      }
      
      public void run() {
         try {
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            while(true) {
               String serverMsg = br.readLine();
               //TODO: append to chat area here           
            }
         
         }
         catch (IOException ioe) {
            System.out.println("IOException");
         }         
      
      }
   
   } //end class ClientThread
   
}


