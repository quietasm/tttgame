// TicTacToeClient.java
// Client side of client/server Tic-Tac-Toe program.
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.io.InputStreamReader;

public class TicTacToeClient extends JPanel implements Runnable
{
   private JTextField idField;

   private Map map;
   private MapPanel mapPanel;
   private int currentLocation;

   public JButton backButton;
   private JPanel header;
   private GridBagLayout layout;
   private GridBagLayout headerLayout;

   private Socket connection; // connection to server
   private InputStreamReader reader;
   private Scanner input; // input from server
   private Formatter output; // output to server
   private String myMark; // this client's mark
   private boolean myTurn; // determines which client's turn it is
   private final String X_MARK = "X"; // mark for first client
   private final String O_MARK = "O"; // mark for second client
   private boolean gameover;
   private LoggerWindow logger;
   private JLabel turnLabel;
   private JPanel contentPanel;
   private GridBagLayout contentLayout;

   public TicTacToeClient( LoggerWindow logger )
   {
      this.logger = logger;
      GridBagConstraints constraints = new GridBagConstraints();

      map = new Map( 3 );
      LocationListener listener = new LocationListener() {
         @Override
         public void processLocation( int location ) {
            currentLocation = location;
            sendClickedSquare( location );
         }
      };
      mapPanel = new MapPanel( map, listener );
      JPanel mapContainer = new FramedPanel();
      mapContainer.add( mapPanel, BorderLayout.CENTER );

      turnLabel = new JLabel( "" );
      contentPanel = new JPanel();

      contentLayout = new GridBagLayout();
      contentPanel.setLayout( contentLayout );

      //constraints.gridx = 0;
      //constraints.gridy = 0;
      //constraints.gridwidth = 1;
      //constraints.gridheight = 1;
      //constraints.anchor = GridBagConstraints.CENTER;
      constraints.fill = GridBagConstraints.BOTH;
      constraints.weightx = 1;
      constraints.weighty = 1;
      //contentLayout.setConstraints( mapPanel, constraints );
      //contentPanel.add( mapPanel );
      contentLayout.setConstraints( mapContainer, constraints );
      contentPanel.add( mapContainer );

      constraints.gridy = 1;
      constraints.fill = GridBagConstraints.NONE;
      constraints.weighty = 0;
      contentLayout.setConstraints( turnLabel, constraints );
      contentPanel.add( turnLabel );
 
      //panel2 = new JPanel(); // set up panel to contain boardPanel
      //panel2.add( boardPanel, BorderLayout.CENTER ); // add board panel

      backButton = new JButton( Language.COMMAND_BACK );

      idField = new JTextField(); // set up textfield
      idField.setEditable( false );

      header = new JPanel();
      headerLayout = new GridBagLayout();
      header.setLayout( headerLayout );

      //constraints.gridx = 0;
      constraints.gridy = 0;
      //constraints.gridwidth = 1;
      //constraints.gridheight = 1;
      //constraints.anchor = GridBagConstraints.CENTER;
      //constraints.fill = GridBagConstraints.NONE;
      constraints.weightx = 0;
      constraints.weighty = 0;
      headerLayout.setConstraints( backButton, constraints );
      header.add( backButton );

      constraints.gridx = 1;
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.weightx = 1;
      headerLayout.setConstraints( idField, constraints );
      header.add( idField );

      layout = new GridBagLayout();
      setLayout( layout );

      constraints.gridx = 0;
      constraints.gridy = 0;
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.weightx = 1;
      constraints.anchor = GridBagConstraints.NORTH;
      layout.setConstraints( header, constraints );
      add( header );

      constraints.gridy = 1;
      constraints.gridwidth = 2;
      constraints.weightx = 0;
      constraints.weighty = 1;
      constraints.fill = GridBagConstraints.BOTH;
      constraints.anchor = GridBagConstraints.CENTER;
      layout.setConstraints( contentPanel, constraints );
      add( contentPanel );
   } // end TicTacToeClient constructor

   // turn client's data into origin state
   public void resetClient() {
      map.clear();
      idField.setText( "" );
      turnLabel.setText( "" );
   } // end method resetClient

   public void startClient( String host, String role, String password )
   {
      try // connect to server and get streams
      {
         // make connection to server
         connection = new Socket(
            InetAddress.getByName( host ), 12345 );

         // get streams for input and output
         reader = new InputStreamReader( connection.getInputStream() );
         input = new Scanner( reader );
         output = new Formatter( connection.getOutputStream() );

         output.format(
               /* String.format( "ENTER\n%s\n%s\n", role, password ) */
               String.format( Gaming.ENTER + "\n%s\n%s\n", role, password ) );
         output.flush();

         gameover = false;
         run();
      } // end try
      catch( IOException e )
      {
         displayMessage( e.getMessage() );
         JOptionPane.showMessageDialog( this,  Language.ERROR_CONNECT,
               Language.CAPTION_ERROR, JOptionPane.PLAIN_MESSAGE );
      } // end catch

      // create and start worker thread for this client
      //ExecutorService worker = Executors.newFixedThreadPool( 1 );
      //worker.execute( this ); // execute client
   } // end method startClient

   public void stopClient() {
      if( connection != null && !connection.isClosed() ) {
         output.format( Gaming.EXIT /*"Exit"*/ + "\n" );
         output.flush();
         try { connection.close(); }
         catch(IOException e ) { e.printStackTrace(); }
      }
      gameover = true;
   }

   public final static String DEVELOPER = "2018 (c) Горобейко В.С.";

   // control thread that allows continuous update of displayArea
   public void run()
   {
      myMark = "Z";
      myTurn = false;
      displayMessage( "Entered message loop\n");
      while( !gameover )
      {
         if( input.hasNextLine() )
            processMessage( input.nextLine() );
      } // end while
      displayMessage( "Left message loop\n" );

      try {
         if( connection != null && !connection.isClosed() )
            connection.close();
      } catch( IOException e ) {
         e.printStackTrace();
      }
      displayMessage( "Client terminated\n" );
   } // end method run

   // process messages received by client
   private void processMessage( String message )
   {
      displayMessage( "Got message \"" + message + "\"\n" );
      if( message.equals( Gaming.ENTER_DENIED /*"ENTER_DENIED"*/ ) ) {
         JOptionPane.showMessageDialog( this, "Error connecting to server",
            Language.CAPTION_ERROR, JOptionPane.PLAIN_MESSAGE );
         stopClient();
      } else if( message.equals( Gaming.MAP /*"MAP"*/ ) ) {
         String map_size_string = input.nextLine();
         int map_size = Integer.parseInt( map_size_string );
         displayMessage( String.format( "map size: %d\n", map_size ) );
         map = new Map( map_size );
         mapPanel.setMap( map );
         repaint();
      } else if( message.equals( Gaming.PLAYER /*"PLAYER"*/ ) ) {
         myMark = input.nextLine();
         displayMessage( "myMark param = " + myMark + "\n" );
         SwingUtilities.invokeLater(
            new Runnable()
            {
               public void run()
               {
                  // display player's mark
                  idField.setText( Language.MESSAGE_PLAYER +
                     " \"" + myMark + "\"" );
               } // end method run
            } // end anonymous inner class
         ); // end call to SwingUtilities.invokeLater
      } // end if
      else if( message.equals( Gaming.TURN /*"TURN"*/ ) )
      {
         myTurn = true;
         turnLabel.setText( Language.MESSAGE_TURN );
      } // end if
      else if( message.equals( Gaming.VALID_MOVE /*"Valid move."*/ ) )
      {
         displayMessage( "Valid move, please wait.\n" );
         map.setMark( currentLocation, myMark );
         mapPanel.repaint();
         turnLabel.setText( Language.MESSAGE_WAIT );
      } // end else if
      else if( message.equals( Gaming.INVALID_MOVE /*"Invalid move, try again"*/ ) )
      {
         displayMessage( message + "\n" ); // display invalid move
         myTurn = true; // still this client's turn
      } // end else if
      else if( message.equals( Gaming.OPPONENT_MOVED /*"Opponent moved"*/ ) )
      {
         int location = input.nextInt(); // get move location
         input.nextLine(); // skip newline after int location
         map.setMark( location,
               ( myMark.equals( X_MARK ) ? O_MARK : X_MARK ) );
         mapPanel.repaint();
         displayMessage( "Opponent moved. You turn.\n" );
         myTurn = true; // now this client's turn
         turnLabel.setText( Language.MESSAGE_TURN );
      } // end else if
      else if( message.equals( Gaming.GAMEOVER /*"Game over"*/ ) ) {
         myTurn = false;
         turnLabel.setText( "" );
         //gameover = true;
         String result = input.nextLine();
         if( result.equals( "WINNER" ) ) {
            result = Language.MESSAGE_WON;
         } else if( result.equals( "LOSER" ) ) {
            result = Language.MESSAGE_LOSE;
         } else {
            result = Language.MESSAGE_DRAW;
         }
         displayMessage( "Game over. " + result + "\n" );
         JOptionPane.showMessageDialog( this, result,
            Language.CAPTION_GAMEOVER, JOptionPane.PLAIN_MESSAGE );
      } // end else if
      else if( message.equals( Gaming.CROSSOUT /*"CROSSOUT"*/ ) ) {
         int type = input.nextInt(); input.nextLine();
         int number = input.nextInt(); input.nextLine();
         mapPanel.setCrossed( type, number );
         mapPanel.repaint();
      } // end else if
      else if( message.equals( Gaming.REPLAY /*"REPLAY"*/ ) ) {
         displayMessage( "Replay confirmed\n" );
         output.format( Gaming.REPLAY_CONFIRMED /*"REPLAYCONFIRMED"*/ + "\n" );
         output.flush();
      } else if(message.equals( Gaming.REPLAY_CONFIRMED /*"REPLAYCONFIRMED"*/ ) ) {
         map.clear();
         mapPanel.setCrossed( TicTacToeServer.CROSS_NOTHING, 0 );
         mapPanel.repaint();
         myTurn = ( myMark.equals( X_MARK) );
         turnLabel.setText( myTurn? Language.MESSAGE_TURN :
            Language.MESSAGE_WAIT );
         displayMessage( "Game cleared\n" );
      } else if(message.equals( Gaming.REPLAY_DENIED /*"REPLAYDENIED"*/ ) ) {
         stopClient();
      } else if(message.equals( Gaming.EXIT /*"Exit"*/ ) ) {
         JOptionPane.showMessageDialog( this, Language.ERROR_DISCONNECT,
            Language.CAPTION_ERROR, JOptionPane.PLAIN_MESSAGE );
         gameover = true;
      } else
         displayMessage( message + "\n" ); // display the message
   } // end method processMessage

   // manipulate displayArea in event-dispatch thread
   private void displayMessage( final String messageToDisplay )
   {
      if( logger != null )
         logger.displayMessage( "Client: " + messageToDisplay );
   } // end method displayMessage

   // send message to server indicating clicked square
   public void sendClickedSquare( int location )
   {
      // if it is my turn
      if( myTurn )
      {
         output.format( "%d\n", location ); // send location to server
         output.flush();
         myTurn = false; // not my turn any more
      } // end if
   } // end method sendClickedSquare
} // end class TicTacToeClient