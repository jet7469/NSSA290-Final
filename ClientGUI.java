import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class ClientGUI extends JFrame implements ActionListener {
   
   private JMenuBar jmb;
   private JMenu jmFile;
   private JMenuItem jmiDisconnect;
   private JMenuItem jmiExit;
   
   private JPanel jpChat;
   private JTextArea jtaChat;
   private JScrollPane jspChat;
   
   private JPanel jpControls;
   private JTextArea jtaMessage;
   private JScrollPane jspMessage;
   private JButton jbSend;

   public static void main (String [] args) {
      new ClientGUI();
      
      //TODO: Display dialog asking for protocol and username when GUI opens
   }
   
   public ClientGUI() {
      //set up frame
      setTitle("Chat");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLayout(new BorderLayout());
      setSize(700, 600);
      setLocationRelativeTo(null);    
      
      //menu bar
      jmb = new JMenuBar();
      setJMenuBar(jmb);
      jmFile = new JMenu("File");
      jmb.add(jmFile);
      jmiDisconnect = new JMenuItem("Disconnect");
      jmFile.add(jmiDisconnect);
      jmiExit = new JMenuItem("Exit");
      jmFile.add(jmiExit);
      
      
      //chat area
      jpChat = new JPanel();
      jtaChat = new JTextArea(30,50);
      jtaChat.setEditable(false);
      jtaChat.setLineWrap(true);
      jspChat = new JScrollPane(jtaChat);
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
      
      
      //actionlisteners
      jmiDisconnect.addActionListener(this);
      jmiExit.addActionListener(this);
      jbSend.addActionListener(this);


      //last step -- display frame
      setVisible(true);
           
   }
   
   public void actionPerformed(ActionEvent ae) {
      Object choice = ae.getSource();
      
      if (choice == jmiExit) {
         //show confirmation and then close if yes
         int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?",
                                               "Confirm Exit", JOptionPane.YES_NO_OPTION);
                                                    
         if (n == 0) {
            System.exit(0);
         }
      
      } else if (choice == jmiDisconnect) {
         //show confirmation and then reset everything
         int m = JOptionPane.showConfirmDialog(null, "Are you sure you want to disconnect?",
                                               "Confirm Disconnect", JOptionPane.YES_NO_OPTION);
                                               
         if (m == 0) {
            //TODO: reset everything and go back to original screen
         }
      } else if (choice == jbSend) {
         //TODO: send message
      }
   }

}