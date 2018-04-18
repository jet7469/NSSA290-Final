import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


public class ClientGUI extends JFrame {
   
   private JMenuBar jmb;
   private JMenu jmFile;
   protected JMenuItem jmiExit;
   
   private JPanel jpChat;
   protected JTextPane jtpChat;
   private StyledDocument doc;
   private JScrollPane jspChat;
  
   private JPanel jpControls;
   protected JTextArea jtaMessage;
   private JScrollPane jspMessage;
   protected JButton jbSend;
   
   public ClientGUI(Client client) {
      //set up frame
      setTitle("Chat");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLayout(new BorderLayout());
      setSize(700, 600);
      setMinimumSize(new Dimension(700, 600));
      setLocationRelativeTo(null);    
      
      //menu bar
      jmb = new JMenuBar();
      setJMenuBar(jmb);
      jmFile = new JMenu("File");
      jmb.add(jmFile);
      jmiExit = new JMenuItem("Exit");
      jmFile.add(jmiExit);
      
      
      //chat area
      jpChat = new JPanel();
      jtpChat = new JTextPane();
      doc = jtpChat.getStyledDocument();
      jpChat.setLayout(null);
      jpChat.setSize(new Dimension(700, 450));
      jtpChat.setEditable(false);
      jspChat = new JScrollPane(jtpChat);
      jspChat.setBounds(20,20,660,430);
      jspChat.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      jpChat.add(jspChat);
      add(jpChat, BorderLayout.CENTER);
      
      
      //text box area
      jpControls = new JPanel();
      jtaMessage = new JTextArea(5,44);
      jspMessage = new JScrollPane(jtaMessage);
      jspMessage.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      jpControls.add(jspMessage);
      jbSend = new JButton("Send");
      jpControls.add(jbSend);
      add(jpControls, BorderLayout.SOUTH);
      
      
      //add actionlisteners
      jmiExit.addActionListener(client);
      jbSend.addActionListener(client);

      //display frame
      setVisible(true);         
   }
   
   /**
    * Insert formatted text for messages from server
    * Sets color to red to distinguish from chat messages
    *
    * @param String msg    The message from the server
    */
   public void insertServerMsg(String msg) {
      Style serverStyle = jtpChat.addStyle("Server Messages", null);
      StyleConstants.setForeground(serverStyle, Color.red);
      
      try {
         doc.insertString(doc.getLength(), "\n" + msg, serverStyle);
      } catch (BadLocationException ble) {
         ble.printStackTrace();
      }
   }
   
   /**
    * Insert formatted text for chat messages
    * Sets user's own text blue for readability
    *
    * @param String msg       The chat message
    */
   public void insertChatMsg(String msg) {
      Style userStyle = jtpChat.addStyle("Chat Username", null);
      StyleConstants.setForeground(userStyle, Color.black);
      
      try {
         doc.insertString(doc.getLength(), "\n" + msg, userStyle);
      } catch (BadLocationException ble) {
         ble.printStackTrace();
      }
   
   }
   
}