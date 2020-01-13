import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.net.URI;
import java.net.URISyntaxException;

public class AboutPanel2 extends BasePanel {
   public AboutPanel2()
   {
      JPanel content = new JPanel();
      content.setLayout( new BoxLayout( content, BoxLayout.Y_AXIS ) );

      content.add( createContacts() );
      content.add( createInfo() );

      JPanel content2 = new JPanel( new BorderLayout() );
      content2.add( content, BorderLayout.NORTH );

      setContentPanel( content2 );
      setCaption( Language.COMMAND_ABOUT );
   }

   private JPanel createInfo()
   {
      JPanel panel = new JPanel();
      panel.setLayout( new BorderLayout() );
      JTextArea text =  new JTextArea();
      text.setBackground( Color.WHITE );
      text.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
      text.setEditable( false );
      text.setLineWrap( true );
      text.setWrapStyleWord( true );
      text.setText( Language.TEXT_ABOUT );
      panel.add( text, BorderLayout.CENTER );
      return panel;
   } // end method createInfo

   private JPanel createContacts()
   {
      JPanel panel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
      panel.add( new JLabel( Language.DEVELOPER_TITLE ) );
      URI uri;
      try {
         uri = new URI( TicTacToe.VK_DEVELOPER_URL );
      }
      catch( URISyntaxException e ) {
         uri = null;
      }
      LinkLabel link = new LinkLabel( Language.COPYRIGHT );
      link.setTarget( uri );
      panel.add( link );
      return panel;
   }
} // end class AboutPanel
