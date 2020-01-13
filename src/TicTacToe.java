// TicTacToe.java

// 29.08.18:
//     Panel Swapping problem (class Launcher) ( 1:41 )

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.imageio.ImageIO;

public class TicTacToe extends JFrame
{
   public final static String VK_DEVELOPER_URL = "http://vk.com/id324718378";
   public final static String VK_PUBLIC_URL = "http://vk.com/public171002166";
   public final static String DEVELOPER = "Горобейко В.С.";
   public final static String COPYRIGHT = "2018 (c) Горобейко В.С.";

   private JFrame frame;
   private JPanel contentPanel;
   private MainMenuPanel mainMenu;
   private TicTacToeClient playPanel;
   private TicTacToeServer gameServer;
   private LoggerWindow loggerWindow;
   private AboutPanel2 aboutPanel;
   private ItemListPanel networkMenu;
   private Explorer explorer;
   private SettingListPanel settingsMenu;
   private AboutPanel2 about2;

   public static void main( String[] args )
   {
      new TicTacToe();
   }

   public TicTacToe()
   {
      super( Language.APP_CAPTION );

      frame = this;
      mainMenu = new MainMenuPanel();
      loggerWindow = null /*new LoggerWindow()*/;
      playPanel = new TicTacToeClient( loggerWindow );
      gameServer = new TicTacToeServer( loggerWindow );
      aboutPanel = new AboutPanel2();
      networkMenu = new ItemListPanel();
      explorer = new Explorer( loggerWindow, networkMenu );
      settingsMenu = new SettingListPanel();
      about2 = new AboutPanel2();
/*
      JMenuItem loggerItem = new JMenuItem( "Show Logger" );
      loggerItem.addActionListener(
         new ActionListener()
         {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
               loggerWindow.setVisible( true );
            }
         }
      );
      JMenuBar menuBar = new JMenuBar();
      menuBar.add( loggerItem );
      setJMenuBar( menuBar );
*/
      mainMenu.buttonPlay.addActionListener(
         new ActionListener()
         {
            @Override
            public void actionPerformed( ActionEvent e )
            {
               ( new Launcher() ).startAIGame();
            }
         }
      );

      networkMenu.setCommandCreate(/*);
      mainMenu.buttonMultiplayer.addActionListener(*/
         new ActionListener()
         {
            @Override
            public void actionPerformed( ActionEvent e )
            {
               explorer.stop();
               ( new Launcher() ).startLANServer();
            }
         }
      );

      networkMenu.setCommandConnect(/*)
      mainMenu.buttonRemoteGame.addActionListener(*/
         new ActionListener()
         {
            @Override
            public void actionPerformed( ActionEvent e )
            {
               ItemPanel item = networkMenu.getSelectedServer();
               if( item != null ) {
                  explorer.stop();
                  ServerInfo info = item.getInfo();
                  ( new Launcher() ).startLANClient( info.getAddress().getHostAddress() );
               } else {
                  JOptionPane.showMessageDialog( frame, Language.ERROR_NOTSELECTED,
                        Language.CAPTION_ERROR, JOptionPane.PLAIN_MESSAGE );
               }
            }
         }
      );

      networkMenu.setCommandRefresh(
         new ActionListener()
         {
            @Override
            public void actionPerformed( ActionEvent e )
            {
               networkMenu.clear();
               explorer.update();
            }
         }
      );

      networkMenu.setBackCommandListener(
         new ActionListener()
         {
            @Override
            public void actionPerformed( ActionEvent event )
            {
               explorer.stop();
               setContentPanel( mainMenu );
            }
         }
      );
      mainMenu.buttonMultiplayer.addActionListener(
         new ActionListener()
         {
            @Override
            public void actionPerformed( ActionEvent event )
            {
               networkMenu.clear();
               explorer.restart();
               setContentPanel( networkMenu );
            }
         }
      );

      settingsMenu.setBackCommandListener(
         new ActionListener()
         {
            @Override
            public void actionPerformed( ActionEvent event )
            {
               setContentPanel( mainMenu );
            }
         }
      );
      mainMenu.buttonRemoteGame.addActionListener(
         new ActionListener()
         {
            @Override
            public void actionPerformed( ActionEvent event )
            {
               setContentPanel( settingsMenu );
            }
         }
      );

      mainMenu.aboutButton.addActionListener(
         new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {
               //JOptionPane.showMessageDialog( frame, aboutPanel,
               //   Language.CAPTION_ABOUT, JOptionPane.PLAIN_MESSAGE );
               setContentPanel( about2 );
            }
         }
      );
      about2.setBackCommandListener(
         new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent event )
            {
               setContentPanel( mainMenu );
            }
         }
      );

      playPanel.backButton.addActionListener(
      /*playPanel.setBackCommandListener(*/
         new ActionListener()
         {
            @Override
            public void actionPerformed( ActionEvent e )
            {
               playPanel.stopClient();
            }
         }
      );

      mainMenu.buttonExit.addActionListener(
         new ActionListener()
         {
            @Override
            public void actionPerformed( ActionEvent e )
            {
               settingsMenu.saveOptions();
               //JOptionPane.showMessageDialog( frame, "Options are saved",
               //      "", JOptionPane.PLAIN_MESSAGE );
               System.exit( 0 );
            } // end method actionPerformed
         } // end anonymous inner class
      ); // end call to addActionListener

      addWindowListener(
         new WindowAdapter()
         {
            @Override
            public void windowClosing( WindowEvent evt )
            {
               settingsMenu.saveOptions();
               //JOptionPane.showMessageDialog( frame, "Options are saved",
               //      "", JOptionPane.PLAIN_MESSAGE );
               System.exit( 0 );
            } // end method windowClosing
         } // end anonymous inner class
      ); // end call to addWindowListener

      setContentPanel( mainMenu );
      setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      setSize( 300, 450 );
      setResizable( false );
      setVisible( true );

      //if( loggerWindow != null )
      //   loggerWindow.setVisible( true );
   } // end TicTacToe constructor

   public void setContentPanel( JPanel newContentPanel )
   {
      if( newContentPanel != null )
      {
         if( contentPanel != null )
         {
            frame.remove( contentPanel );
         }
         contentPanel = newContentPanel;
         frame.add( contentPanel, BorderLayout.CENTER );
         frame.revalidate();
         frame.repaint();
      }
   } // end method setContentPanel

   /*
   static class Launcher {
      private final static LauncherThread INSTANCE =
         new LauncherThread();
      private final static ExecutorService executor =
         Executors.newFixedThreadPool( 1 );

      void startAIGame() {
         ...
         executor.execute( INSTANCE );
      }
      void startLANServer() {
         ...
      }
      void startLANClient() {
         ...
      }

      class LauncherThread implements Runnable {
         private String host, role, password;

         public void run() {
         }
      }
   }
   */
   private class Launcher implements Runnable
   {
      private String host, role, password;

      void startAIGame() { // "user", ""
         // start server in dedicated thread
         int status = gameServer.restartServer( true, settingsMenu.getMapSize(),
               settingsMenu.getPlayFor(),  settingsMenu.getFirstMove(),
               settingsMenu.getServerName() );
         if( status != 0 ) {
            JOptionPane.showMessageDialog( frame, Language.ERROR_STARTUP,
                  Language.CAPTION_ERROR, JOptionPane.PLAIN_MESSAGE );
         } else {
            host = "127.0.0.1";
            role = "admin";
            password = gameServer.getServerPassword();
            ( new Thread( this ) ).start();
         }
      }

      void startLANServer() // "admin", gameServer.getServerPassword()
      {
         int status = gameServer.restartServer( false, settingsMenu.getMapSize(),
               settingsMenu.getPlayFor(),  settingsMenu.getFirstMove(),
               settingsMenu.getServerName() );
         if( status != 0 ) {
            JOptionPane.showMessageDialog( frame, Language.ERROR_STARTUP,
                  Language.CAPTION_ERROR, JOptionPane.PLAIN_MESSAGE );
         } else {
            host = "127.0.0.1";
            role = "admin";
            password = gameServer.getServerPassword();
            ( new Thread( this ) ).start();
         }
      }

      void startLANClient( String target ) // "user", ""
      {
         host = target;
         role = "user";
         password = "";
         ( new Thread( this ) ).start();
      }

      @Override
      public void run() {
         setContentPanel( playPanel );
         playPanel.resetClient();
         playPanel.startClient( host, role, password ); // linked thread
         if( !gameServer.isStopped() )
         {
            gameServer.stop();
         }
         setContentPanel( mainMenu );
      }
   } // end class Launcher
/*
   private class AboutPanel extends JPanel
   {
      public AboutPanel()
      {
         JLabel banner;
         try {
            BufferedImage image = ImageIO.read(
                  this.getClass().getResourceAsStream( "sg_banner_388x86.png" ) );
            Icon icon = new ImageIcon( image );
            banner = new JLabel( icon );
         } catch( IllegalArgumentException | IOException e ) {
            //System.err.println( "can't load resource: " + e.getMessage() );
            banner = null;
         }

         JTextArea text =  new JTextArea();
         // WARNING! Setting background to "new Color( 255,255,255, 0 )"
         // leads to incorrect painting on "Windows 8" (text spreads over) 
         text.setBackground( Color.WHITE );
         text.setBorder( BorderFactory.createLineBorder( Color.WHITE, 1 ) );
         text.setEditable( false );
         text.setLineWrap( true );
         text.setWrapStyleWord( true );
         text.setRows( 7 );
         text.setText( Language.TEXT_ABOUT );

         LinkLabel vkLink = new LinkLabel( "Мы в ВКонтакте" );
         URI uri;
         try {
            uri = new URI( TicTacToe.VK_PUBLIC_URL );
         }
         catch( URISyntaxException e ) {
            uri = null;
         }
         vkLink.setTarget( uri );
         JPanel vkPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
         vkPanel.add( vkLink );

         setLayout( new BorderLayout() );
         if( banner != null )
            add( banner, BorderLayout.NORTH );
         add( new JScrollPane( text ), BorderLayout.CENTER );
         add( vkPanel, BorderLayout.SOUTH );
      } // end constructor AboutPanel
   } // end class AboutPanel
*/
} // end TicTacToe class