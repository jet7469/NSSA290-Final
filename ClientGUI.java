import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JTextPane;

public class ClientGUI extends JFrame implements ActionListener {
   
   private JMenuBar jmb;
   private JMenu jmFile;
   protected JMenuItem jmiDisconnect;
   protected JMenuItem jmiExit;
   
   private JPanel jpChat;
   protected JTextPane jtpChat;
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
      jmiDisconnect = new JMenuItem("Disconnect");
      jmFile.add(jmiDisconnect);
      jmiExit = new JMenuItem("Exit");
      jmFile.add(jmiExit);
      
      
      //chat area
      jpChat = new JPanel();
      jtpChat = new JTextPane();
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
      
      
      //actionlisteners
      //TODO: Move ActionListeners to the client class?
      jmiDisconnect.addActionListener(client);
      jmiExit.addActionListener(client);
      jbSend.addActionListener(client);


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