// MainMenuPanel.java
// Panel with game name and menu options.

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.awt.Color;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.imageio.ImageIO;

public class MainMenuPanel extends JPanel
{
   private final static String DEVELOPER = "2018 (c) Горобейко В.С.";
   private JLabel titleField;
   public JButton buttonPlay;
   public JButton buttonMultiplayer;
   public JButton buttonRemoteGame;
   public JButton aboutButton;
   public JButton buttonExit;
   private LinkLabel copyright;

   public MainMenuPanel()
   {
      buttonPlay = new JButton( Language.COMMAND_PLAY );

      buttonMultiplayer = new JButton( Language.COMMAND_NETMENU );

      buttonRemoteGame = new JButton( Language.COMMAND_SETTINGS );

      aboutButton = new JButton( Language.COMMAND_ABOUT );

      buttonExit = new JButton( Language.COMMAND_EXIT );

      try {
         BufferedImage image = ImageIO.read(
               this.getClass().getResourceAsStream( "sg_ttt_banner.png" ) );
         Icon icon = new ImageIcon( image );
         titleField = new JLabel( icon );
      } catch( IllegalArgumentException | IOException exc ) {
         titleField = new JLabel( "<html><font size='32pt'>" +
               "<font color='red'>Крестики</font><font color='blue'>Нолики</font>" + 
               "</font></html>" );
      }

      URI uri;
      try {
         uri = new URI( TicTacToe.VK_DEVELOPER_URL );
      }
      catch( URISyntaxException e ) {
         uri = null;
      }
      copyright = new LinkLabel( Language.COPYRIGHT );
      copyright.setTarget( uri );

      buttonPlay.setMargin( new Insets( 5, 5, 5, 5 ) );
      buttonMultiplayer.setMargin( new Insets( 5, 5, 5, 5 ) );
      buttonRemoteGame.setMargin( new Insets( 5, 5, 5, 5 ) );
      aboutButton.setMargin( new Insets( 5, 5, 5, 5 ) );
      buttonExit.setMargin( new Insets( 5, 5, 5, 5 ) );

      JPanel buttonsPanel = new JPanel();
      buttonsPanel.setLayout( new GridLayout( 5, 1, 5, 5 ) );
      buttonsPanel.add( buttonPlay );
      buttonsPanel.add( buttonMultiplayer );
      buttonsPanel.add( buttonRemoteGame );
      buttonsPanel.add( aboutButton );
      buttonsPanel.add( buttonExit );
      JPanel panel2 = new JPanel();
      panel2.add( buttonsPanel );

      JPanel panel3 = new JPanel();
      panel3.add( copyright );

      setLayout( new BorderLayout() );
      add( titleField, BorderLayout.NORTH );
      add( panel2, BorderLayout.CENTER );
      add( panel3, BorderLayout.SOUTH );
   } // end MainMenuPanel constructor
} // end class MainMenuPanel