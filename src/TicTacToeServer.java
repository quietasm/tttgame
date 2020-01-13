// TicTacToeServer.java
// Server side of client/server Tic-Tac-Toe program.
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.Formatter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.io.InputStreamReader;
import java.util.Random;
//import java.swing.JOptionPane;

public class TicTacToeServer implements Runnable
{
   private /*final static*/ String[] MARKS = { "X", "O" };
   // X: {X,O}, O: {O,X}, A: {X,O} or {O,X} - play for

   // open constants for external usage in options
   public final static int PLAYER_X = 0;  // Player X or Team X
   public final static int PLAYER_O = 1;  // Player O or Team O
   public final static int ANYPLAYER = 2; // Random

   private final static int NOPLAYER = -1;
   private final static int NOWINNER = NOPLAYER;
   private final static int FRIENDSHIP = ANYPLAYER;

   private final static int ADMIN = 0; // admin's cell index
   private final static int USER = 1; // user's cell index

   public final static String DISCOVERY_REQUEST = "TTTSERVER_DISCOVERY_REQUEST";
   public final static String DISCOVERY_RESPONSE = "TTTSERVER_DISCOVERY_RESPONSE";
   public final static int DISCOVERY_PORT = 8888;

   public final static int CROSS_NOTHING = 0;
   public final static int CROSS_ROW = 1;
   public final static int CROSS_COLUMN = 2;
   public final static int CROSS_DIAGONAL = 3;

   private Lock gameLock;
   private Map map;
   private int step;
   private int winner;
   private int currentPlayer;
   // X: MARKS[ADMIN]=="X"? ADMIN : USER;
   // O: MARKS[ADMIN]=="O"? ADMIN : USER;
   // A: randomNumbers.nextInt() % 2

   private int crossed;
   private int crossedValue;

   private ServerSocket server;
   private Player[] players;
   private ExecutorService runGame;
   private boolean runAI;
   private int playAs, firstMove;
   private LoggerWindow logger;
   private String serverPassword = "1234";
   // int rnd = randomNumbers.nextInt();
   // if( rnd < 0 ) rnd = -rnd;
   // serverPassword = String.format( "%04d", rnd % 10000 );

   private boolean stopped; // stop(), isStopped()
   private int players_count; // enterGame(), isConnected()
   private int players_ready; // getPrepared(), isPrepared()

   private boolean replay_denied = false; // reseted once on start
   private int replayed_count = 0;
   private int reset_count = 0;
   private int reseted_count = 0;

   private Publisher publisher;
   private Random randomNumbers;

   public TicTacToeServer( LoggerWindow logger )
   {
      this.logger = logger;
      runGame = Executors.newCachedThreadPool();
      gameLock = new ReentrantLock();
      players = new Player[ 2 ];
      map = new Map( 3 );
      publisher = new Publisher( logger, "SG_TTT_Server" );
      randomNumbers = new Random();
   } // end TicTacToeServer constructor

   public int restartServer( boolean ai, int map_size,
         int play_as, int first_move, String server_name )
   {
      runAI = ai;
      playAs = play_as;
      firstMove = first_move;
      map = new Map( map_size );
      publisher.setName( server_name );
      try {
         server = new ServerSocket( 12345, 2 );
      } catch( IOException e ) {
         // expect BindException
         displayMessage( e.getMessage() + "\n" );
         return -1;
      }
      if( !runAI ) {
         publisher.restart();
      }
      ( new Thread( this ) ).start();
      return 0;
   }

   public void run()
   {
      // reset server
      stopped = false;
      //players_count = 0; // moved into try-block
      players_ready = 0;
      replay_denied = false;
      replayed_count = 0;
      reset_count = 0;
      reseted_count = 0;

      // reset game
      map.clear();
      step = 0;
      winner = NOWINNER;

      crossed = CROSS_NOTHING;
      crossedValue = 0;

      int admin;
      if( playAs != ANYPLAYER ) {
         admin = playAs;
      } else {
         admin = randomNumbers.nextInt();
         if( admin < 0 )
            admin = -admin;
         admin = admin % 2;
      }
      if( admin == PLAYER_O ) {
         MARKS[ ADMIN ] = "O";
         MARKS[ USER ] = "X";
      } else {
         MARKS[ ADMIN ] = "X";
         MARKS[ USER ] = "O";
      }

      if( firstMove != ANYPLAYER ) { // patch from 15.07.19
         if( firstMove == admin )
            currentPlayer = ADMIN;
         else
            currentPlayer = USER;
      } else {
         currentPlayer = randomNumbers.nextInt();
         if( currentPlayer < 0 )
            currentPlayer = -currentPlayer;
         currentPlayer = currentPlayer % 2;
      }
      displayMessage( "currentPlayer = " + currentPlayer + "\n" );

      for( int i = 0; i < players.length; i++ ) {
         players[ i ] = null;
      }

      try {
         players_count = 0;
         if( runAI ) {
            runGame.execute( new AIPlayer() );
         }
         while( !isStopped() && players_count < players.length ) {
            // server.accept() block thread so server can't be stopped
            runGame.execute( new RemotePlayer( server.accept() ) );
         }
      } // end try
      catch( IOException e ) {
         displayMessage( e.getMessage() + "\n" );
         // should ignore SocketException: socket closed when server is stopped
      } // end catch
      finally {
         publisher.stop();
         try {
            if( server != null && !server.isClosed() )
               server.close();
         } catch( IOException e ) {
            displayMessage( e.getMessage() + "\n" );
         }
      }
   } // end method execute

   public String getAddress() {
      InetAddress addr;
      String name;
      try {
         addr = InetAddress.getLocalHost();
         name = addr.getHostAddress();
      } catch( UnknownHostException e ) {
         name = e.getMessage();
      }
      return name;
   }

   public String getServerPassword() {
      return serverPassword;
   } // end method getServerPassword

   public void delayExecution() {
      try { Thread.sleep( 100 ); }
      catch( InterruptedException e ) { e.printStackTrace(); }
   }

   public void stop() {
      publisher.stop();
      if( server != null && !server.isClosed() ) {
         try {
            server.close();
         } catch( IOException e ) {
            displayMessage( e.getMessage() + "\n" );
         }
      }
      displayMessage( "Server stopped\n" );
      stopped = true;
   } // end method stop

   public boolean isStopped() {
      return stopped;
   } // end method isStopped

   /**
    * <p><code>enterGame()</code> method authenticates user by its role/password
    * and returns either user identifier (uid) or failure status (-1).</p>
    * 
    * <p>Both role and password are stored in <code>Player</code> object and
    * accessible through <code>getRole()</code> and <code>getPassword()</code>
    * methods of <code>Player</code> interface.</p>
    * 
    * @param player
    * @return user identifier
    */
   public int enterGame( Player player ) {
      int uid = NOPLAYER;
      try {
         // MUST BE synchronized as it performs read-modify-write operation
         gameLock.lock();
         if( players_count < 2 ) {
            // convert role name to constant
            if( player.getRole().equalsIgnoreCase( "admin" ) ) {
               // if owner rights, match server password too
               if( serverPassword.equals( player.getPassword() ) ) {
                  uid = ADMIN;
               } // end inner if
               else
                  displayMessage( "invalid password, valid: " + serverPassword + "\n" );
            } else if( player.getRole().equalsIgnoreCase( "user" ) ) {
               uid = USER;
            }
            // if role is valid and corresponding cell hasn't been occupied yet
            if( uid != NOPLAYER && players[ uid ] == null ) {
               players[ uid ] = player;
               players_count++;
            } else {
               uid = NOPLAYER; // if occupied, deny entering
               displayMessage( "either role (" + player.getRole() + ") invalid or\n" +
                     "player cell has already been occupied\n" );
            }
         }
      }
      finally {
         gameLock.unlock();
      }
      return uid;
   } // end method enterGame

   // HAS NOT TO BE synchronous as it performs read-check operation
   public boolean isConnected() {
      return (players_count == 2);
   }

   public void getPrepared() {
      gameLock.lock();
      ++players_ready;
      gameLock.unlock();
   }
   public boolean isPrepared() {
      return (players_ready == 2);
   }

   public boolean isMyTurn( int player ) {
      return (player == currentPlayer);
   }

   public boolean isReplayDenied() {
      return replay_denied;
   }
   public void setReplay( boolean status ) {
      gameLock.lock();
      replay_denied = replay_denied && status;
      replayed_count += 1;
      gameLock.unlock();
   }
   public boolean isReplayed() {
      return (replayed_count == 2); // forgot parenthesis
   }

   public void beginReset() {
      gameLock.lock();
      reset_count += 1;
      if( reset_count == 2 ) { // invalid order or missing synchronization
         // reset game
         map.clear();
         step = 0;
         winner = NOWINNER;
         // patch from 15.07.19
         int admin = MARKS[ ADMIN ].equalsIgnoreCase( "X" )? PLAYER_X : PLAYER_O;
         if( firstMove != ANYPLAYER ) {
            if( firstMove == admin )
               currentPlayer = ADMIN;
            else
               currentPlayer = USER;
         } else {
            currentPlayer = randomNumbers.nextInt();
            if( currentPlayer < 0 )
               currentPlayer = -currentPlayer;
            currentPlayer = currentPlayer % 2;
         }
         displayMessage( "firstMove = " + firstMove + "\n" );
         displayMessage( "Begins player " + currentPlayer + "\n" );
         crossed = CROSS_NOTHING;
         crossedValue = 0;
      }
      gameLock.unlock();
   }
   public boolean isReseted() {
      return (reset_count == 2);
   }
   public void endReset() {
      gameLock.lock();
      reseted_count += 1;
      if( reseted_count == 2) { // invalid order or missing synchronization
         replayed_count = 0;
         reset_count = 0;
         reseted_count = 0;
      }
      gameLock.unlock();
   }

   // use logger & logMessage()
   private void displayMessage( final String messageToDisplay )
   {
      if( logger != null )
         logger.displayMessage( "Server: " + messageToDisplay );
   } // end method displayMessage

   private final static String DEVELOPER = "2018 (c) Горобейко В.С.";

   public void doTurn( int location, int player ) {
      gameLock.lock(); // synchronize access to currentPlayer

      map.setMark( location, MARKS[ currentPlayer ] );
      step++;
      checkGameOver( location, currentPlayer );

      currentPlayer = ( currentPlayer + 1 ) % 2; // change player

      // let new current player know that move occurred
      players[ currentPlayer ].otherPlayerMoved( location );
      gameLock.unlock();
   } // end method move

   // determine whether location is occupied
   public boolean isOccupied( int location )
   {
      String mark = map.getMark( location );
      if( mark.equals( MARKS[ PLAYER_X ] ) ||
            mark.equals( MARKS[ PLAYER_O ] ) )
         return true; // location is occupied
      else
         return false; // location is not occupied
   } // end method isOccupied

   private void checkGameOver( int location, int player )
   {
      String id = MARKS[ player ];
      int row = location / map.getSize();
      int column = location % map.getSize();
      int i, j;

      boolean ended = true;
      for( i=0; i<map.getSize() && ended; i++ ) { // row
         ended = map.getMark( row*map.getSize()+i ).equals( id );
      }
      if( ended ) {
         crossed = CROSS_ROW;
         crossedValue = row;
      }

      if( !ended ) { // column
         crossed = CROSS_COLUMN;
         crossedValue = column;
         for( ended=true, i=0; i<map.getSize() && ended; i++ ) {
            ended = map.getMark( i*map.getSize()+column ).equals( id );
         }
      }
      if( !ended ) { // top-down diagonal
         crossed = CROSS_DIAGONAL;
         crossedValue = 0;
         for( ended=true, i=0; i<map.getSize() && ended; i++ ) {
            ended = map.getMark( i*map.getSize()+i ).equals( id );
         }
      }
      if( !ended ) { // bottom-up diagonal
         crossed = CROSS_DIAGONAL;
         crossedValue = 1;
         for( ended=true, i=0, j=map.getSize()-1;
               i<map.getSize() && ended; i++, j-- ) {
            ended = map.getMark( i*map.getSize()+j ).equals( id );
         }
      }
      if( !ended && step >= map.getSize()*map.getSize()) {
         crossed = CROSS_NOTHING;
         crossedValue = 0;
         winner = FRIENDSHIP;
      } else {
         winner = ended? player : NOWINNER;
      }
   } // end method checkGameOver

   // place code in this method to determine whether game over
   public boolean isGameOver()
   {
      return winner != NOWINNER; // this is left as an exercise
   } // end method isGameOVer

   public String getWinner( int player )
   {
      if( winner == FRIENDSHIP )
         return "MATE";
      return ( winner == player ) ? "WINNER" : "LOSER";
   } // end method getWinner

   public int getCrossed() {
      return crossed;
   }
   public int getCrossedValue() {
      return crossedValue;
   }

   // private inner class Player manages each Player as a runnable
   public class RemotePlayer implements Player
   {
      private Socket connection; // connection to client
      private InputStreamReader reader;
      private Scanner input; // input from client
      private Formatter output; // output to client
      private int playerNumber; // tracks with player this is
      private String mark; // mark for this player
      private boolean exited = false;
      private String playerRole, playerPassword;

      // set up Player thread
      public RemotePlayer( Socket socket )
      {
         playerNumber = NOPLAYER; // initialize playerNumber by default (not entered)
         connection = socket; // store socket for client

         try // obtain streams from Socket
         {
            reader = new InputStreamReader( connection.getInputStream() );
            input = new Scanner( reader );
            output = new Formatter( connection.getOutputStream() );
            output.flush();
         } // end try
         catch( IOException ioException )
         {
            ioException.printStackTrace();
            System.exit( 1 );
         } // end catch
      } // end Player constructor

      // send message that other player moved
      @Override
      public void otherPlayerMoved( int location )
      {
         displayMessage( "Player.otherPlayerMoved() invoked\n" );
         output.format( Gaming.OPPONENT_MOVED /*"Opponent moved"*/ + "\n" );
         output.format( "%d\n", location ); // send location of move
         output.flush(); // flush output
      } // end method otherPlayerMoved

      @Override
      public String getRole() {
         return playerRole;
      } // end method getRole

      @Override
      public String getPassword() {
         return playerPassword;
      } // end method getPassword

      private boolean hasExitMessage() {
         try {
            if( reader.ready() && input.nextLine().equals( Gaming.EXIT ) ) {
                     /*"Exit"*/
               exited = true;
               stop();
               return true;
            }
         } catch( IOException e ) {
            e.printStackTrace();
            stop();
            return true;
         }
         return false;
      }

      private void waitOpponent() {
         while( !isStopped() && !isConnected() && !hasExitMessage() )
            delayExecution();
      } // end method waitOpponent

      private void waitReady() {
         getPrepared();
         while( !isStopped() && !isPrepared() && !hasExitMessage() )
            delayExecution();
      } // end method waitReady

      private void waitTurn( int player ) {
         while( !isStopped() && !isGameOver()
               && !isMyTurn( player ) && !hasExitMessage() )
            delayExecution();
      } // end method waitTurn

      int waitInput() {
         int location = -1;
         while( !isStopped() ) {
            // CANNOT use hasExitMessage since it release input
            try {
               if( reader.ready() ) {
                  String message = input.nextLine();
                  if( message.equals( Gaming.EXIT /*"Exit"*/ ) ) {
                     exited = true;
                     stop();
                     break;
                  }
                  try {
                     location = Integer.parseInt( message );
                     break;
                  } catch( NumberFormatException e ) {
                     e.printStackTrace();
                  }
               }
            } catch( IOException e ) {
               e.printStackTrace();
               stop();
               break;
            }
            delayExecution();
         }
         return location;
      } // end method waitInput

      // control thread's execution
      @Override
      public void run()
      {
         String message;
         // send client its mark (X or O), process messages from client
         try
         {
            message = input.nextLine();
            if( message.equals( Gaming.ENTER /*"ENTER"*/ ) ) {
               playerRole = input.nextLine();
               playerPassword = input.nextLine();
               displayMessage( String.format( "\"%s\"/\"%s\" entering the game...\n",
                     playerRole, playerPassword ) );
               playerNumber = enterGame( this );
            }
            if( playerNumber == NOPLAYER ) {
               displayMessage( "Enter denied...\n" );
               output.format( Gaming.ENTER_DENIED /*"ENTER_DENIED"*/ + "\n" );
               output.flush();
            }
            if( playerNumber != NOPLAYER ) {
               displayMessage( String.format( "Entered as player %d\n", playerNumber ) );
               mark = MARKS[ playerNumber ];
               displayMessage( "Player " + mark + " is waiting opponent\n" );
               waitOpponent();
            }
            if( playerNumber != NOPLAYER && !isStopped() ) {
               output.format( Gaming.MAP /*"MAP"*/ + "\n%d\n", map.getSize() );
               output.format( Gaming.PLAYER /*"PLAYER"*/ + "\n%s\n", mark );
               output.flush();
               displayMessage( "Player " + mark + " wait start\n" );
               waitReady();
            }
            while( playerNumber != NOPLAYER && !isStopped() && !isReplayDenied() ) {
               displayMessage( "Player " + mark + " started game\n" );
               while( !isStopped() && !isGameOver() )
               {
                  displayMessage( "Player " + mark + " waits turn\n" );
                  waitTurn( playerNumber );
                  if( !isStopped() && !isGameOver() ) {
                     displayMessage( "Player " + mark + " turning\n" );
                     output.format( Gaming.TURN /*"TURN"*/ + "\n" );
                     output.flush();
                     do {
                        int location = waitInput();
                        if( !isStopped() ) {
                           if( location < 0 || isOccupied( location ) ) {
                              output.format( Gaming.INVALID_MOVE + "\n" );
                                    /*"Invalid move, try again\n"*/
                              output.flush();
                           } // end if
                           else {
                              displayMessage( "Player " + mark + " do turn\n" );
                              doTurn( location, playerNumber );
                              // notify client to pass turn
                              output.format( Gaming.VALID_MOVE + "\n" );
                                    /*"Valid move.\n"*/
                              output.flush();
                              break;
                           } // end else
                        } // end if
                     } while( !isStopped() );
                  } // end if
               } // end while
               if( !isStopped() ) {
                  displayMessage( "Player " + mark + " gamed over and replays\n" );
                  // send CROSSOUT
                  output.format( Gaming.CROSSOUT /*"CROSSOUT"*/ + "\n%d\n%d\n",
                        getCrossed(), getCrossedValue() );
                  output.flush();
                  // send GAMEOVER command
                  output.format( Gaming.GAMEOVER /*"Game over"*/ + "\n" );
                  output.format( getWinner( playerNumber ) + "\n" );
                  output.flush();
                  // send REPLAY command
                  output.format( Gaming.REPLAY /*"REPLAY"*/ + "\n" );
                  output.flush();
                  // wait for feedback
                  while( !isStopped() && !isReplayDenied() ) {
                     try {
                        if( reader.ready() ) {
                           message = input.nextLine();
                           if( message.equals( Gaming.EXIT /*"Exit"*/ ) ) {
                              exited = true; stop(); break;
                           } else if( message.equals( Gaming.REPLAY_CONFIRMED ) ) {
                                    /*"REPLAYCONFIRMED"*/
                              setReplay( true ); break;
                           } else if( message.equals( Gaming.REPLAY_DENIED ) ) {
                                    /*"REPLAYDENIED"*/
                              setReplay( false ); break;
                           }
                        }
                     }
                     catch( IOException e ) {
                        e.printStackTrace(); stop(); break;
                     }
                     delayExecution();
                  }
                  while( !isStopped() && !isReplayed() && !hasExitMessage() ) {
                     delayExecution();
                  }
                  if( !isStopped() ) {
                     if( isReplayDenied() ) {
                        output.format( Gaming.REPLAY_DENIED + "\n" );
                              /*"REPLAYDENIED"*/
                        output.flush();
                     } else {
                        output.format( Gaming.REPLAY_CONFIRMED + "\n" );
                              /*"REPLAYCONFIRMED"*/
                        output.flush();
                        beginReset();
                        while( !isStopped() && !isReseted() &&
                           !hasExitMessage() )
                        {
                           delayExecution();
                        }
                        endReset();
                     }
                  }
               } else {
                  displayMessage( "Player " + mark + " stopped\n" );
               }
            }
         } // end try
         finally
         {
            try
            {
               if( playerNumber != NOPLAYER && !exited ) {
                  output.format( Gaming.EXIT /*"Exit"*/ + "\n" );
                  output.flush();
               }
               connection.close(); // close connection to client
            } // end try
            catch( IOException ioException )
            {
               ioException.printStackTrace();
            } // end catch
         } // end finally
      } // end method run
   } // end class Player

   public class AIPlayer implements Player {
      private static final int PLAY = 0;
      private static final int WIN = 1;
      private static final int DRAW = 2;
      private static final int LOSE = 3;

      private int playerNumber;
      private String mark;
      private Random randomNumbers;
      public final static String DEVELOPER = "2018 (c) Горобейко В.С.";

      public AIPlayer() {
         playerNumber = NOPLAYER;
         randomNumbers = new Random();
      } // end AIPlayer constructor

      @Override
      public void otherPlayerMoved( int location ) {
         displayMessage( "AI.otherPlayerMoved() invoked\n" );
      } // end method otherPlayerMoved

      @Override
      public String getRole() {
         return "user";
      } // end method getRole

      @Override
      public String getPassword() {
         return "";
      } // end method getPassword

      private void waitOpponent() {
         while( !isStopped() && !isConnected() )
            delayExecution();
      } // end method waitOpponent

      private void waitReady() {
         getPrepared();
         while( !isStopped() && !isPrepared() )
            delayExecution();
      } // end method waitReady

      private void waitTurn( int player ) {
         while( !isStopped() && !isGameOver() && !isMyTurn( player ) )
            delayExecution();
      } // end method waitTurn

      private String arrayToString( int[] a ) {
         StringBuilder sb = new StringBuilder();
         if( a.length == 0 )
            sb.append( "empty" );
         else {
            sb.append( String.format( "%d", a[ 0 ] ) );
            for( int i=1; i<a.length; i++ ) {
               sb.append( String.format( ", %d", a[i] ) );
            }
         }
         return sb.toString();
      }

      private int[] getMoves()
      {
         int n = map.getSize();
         int[] res = new int[ n*n ];
         int count = 0;
         for( int i = 0; i < n*n; i++ )
            if( !isOccupied( i ) )
               res[ count++ ] = i;

         int[] moves = new int[ count ];
         for( int i = 0; i < count; i++ )
            moves[ i ] = res[ i ];

         displayMessage( "getMoves: " + arrayToString( moves ) + "\n" );
         return moves;
      }

      private int checkState( int location, int player )
      {
         String id = MARKS[ player ];
         if( !map.getMark( location ).equals( id ) )
            displayMessage( "INTERNAL: map[location] != id\n" );
         int row = location / map.getSize();
         int column = location % map.getSize();
         int i, j;

         boolean ended = true;
         for( i=0; i<map.getSize() && ended; i++ ) { // row
            ended = map.getMark( row*map.getSize()+i ).equals( id );
         }
         if( !ended ) { // column
            for( ended=true, i=0; i<map.getSize() && ended; i++ ) {
               ended = map.getMark( i*map.getSize()+column ).equals( id );
            }
         }
         if( !ended ) { // top-down diagonal
            for( ended=true, i=0; i<map.getSize() && ended; i++ ) {
               ended = map.getMark( i*map.getSize()+i ).equals( id );
            }
         }
         if( !ended ) { // bottom-up diagonal
            for( ended=true, i=0, j=map.getSize()-1;
                  i<map.getSize() && ended; i++, j-- ) {
               ended = map.getMark( i*map.getSize()+j ).equals( id );
            }
         }

         if( !ended && step >= map.getSize()*map.getSize()) {
            return DRAW;
         }
         return ( ended ? WIN : PLAY );
      }

      private int winning( int[] results )
      {
         int i = results.length-1;
         while( i >= 0 && results[ i ] == PLAY ) i--;
         return i;
      }

      private int[] getResults( int[] moves, int player )
      {
         int[] results = new int[ moves.length ];

         for( int i = 0; i < moves.length; i++ )
         {
            map.setMark( moves[ i ], MARKS[ player ] );
            results[ i ] = checkState( moves[ i ], player );
            map.setMark( moves[ i ], "" );
         }

         return results;
      }

      private int rand()
      {
         int t = randomNumbers.nextInt();
         return ( t >= 0 )? t : -t;
      }

      public int nextMove()
      {
         int[] moves = getMoves();
         displayMessage( "AI has " + moves.length + " available moves\n" );
         // check opponent to win in next move
         int[] results = getResults( moves, (playerNumber + 1) % 2 );
         displayMessage( "Opponent: " + arrayToString( results ) + "\n" );
         int opponent = winning( results );
         // check player to win in next move
         results = getResults( moves, playerNumber );
         displayMessage( "Player: " + arrayToString( results ) + "\n" );
         int player = winning( results );
         int target;
         if( player >= 0 ) // if player wins - accept
            target = player;
         else if( opponent >= 0 ) // else prevent opponent from winning
            target = opponent;
         else { // if no wins choose random move from player cases
            int p = randomNumbers.nextInt();
            if( p < 0 ) p = -p;
            p = p % results.length;
            target = p;
         }
         return moves[ target ];
      }

      @Override
      public void run() {
         playerNumber = enterGame( this );
         if( playerNumber != NOPLAYER ) {
            displayMessage( String.format( "AI player entered as player %d\n", playerNumber ) );
            mark = MARKS[ playerNumber ];

            displayMessage( "Player AI is waiting opponent\n" );
            waitOpponent();

            displayMessage( "Player AI wait start\n" );
            waitReady();
         }
         while( playerNumber != NOPLAYER && !isStopped() && !isReplayDenied() ) {
            displayMessage( "Player AI started game\n" );
            while( !isStopped() && !isGameOver() )
            {
               displayMessage( "Player AI waits turn\n" );
               waitTurn( playerNumber );
               if( !isStopped() && !isGameOver() ) {
                  displayMessage( "Player AI turning\n" );
                  int location = nextMove();
                  if( !isStopped() ) {
                     displayMessage( "Player AI do turn at " + location + "\n" );
                     doTurn( location, playerNumber );
                  }
               }
            }
            if( !isStopped() ) {
               displayMessage( "Player AI gamed over and replays\n" );
               setReplay( true );
               while( !isStopped() && !isReplayed() ) {
                  delayExecution();
               }
               if( !isStopped() ) {
                  if( isReplayDenied() ) {
                     // does nothing
                  } else {
                     beginReset();
                     while( !isStopped() && !isReseted() ) {
                        delayExecution();
                     }
                     endReset();
                  }
               }
            }
         }
         if( playerNumber == NOPLAYER ) {
            displayMessage( "AI player failed to enter the game\n" );
         } else if( !isStopped() ) {
            displayMessage( "Player " + mark + " terminated\n" );
         } else {
            displayMessage( "Player " + mark + " stopped\n" );
         }
      } // end method run
   } // end class AIPlayer
} // end class TicTacToeServer