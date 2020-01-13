import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.io.IOException;

/**
 * <p><code>Publisher</code> class is responsible for server
 * discovering through UDP broadcasting. It listens UDP socket,
 * receives and matches <code>SERVER_DISCOVERY_REQUEST</code> packets
 * and responses with <code>SERVER_DISCOVERY_REQUEST</code> packet.</p>
 *
 * @since   version 1.1
 */
public class Publisher
{
   private String serverName;

   private DiscoveryThread discoveryThread;
   private LoggerWindow logger;

   public Publisher( LoggerWindow logger, String name )
   {
      this.logger = logger;
      serverName = name;
      discoveryThread = new DiscoveryThread();
   } // end constructor Publisher

   public void restart()
   {
      stop();
      (new Thread( discoveryThread ) ).start();
   } // end method restart

   public void stop()
   {
      discoveryThread.stop();
   } // end method stop

   public void setName( String name )
   {
      serverName = name;
   } // end method setName

   private void displayMessage( final String messageToDisplay )
   {
      if( logger != null )
         logger.displayMessage( "Publisher: " + messageToDisplay );
   } // end method displayMessage

   private class DiscoveryThread implements Runnable
   {
      private DatagramSocket socket;

      public DiscoveryThread()
      {
         displayMessage( "DiscoveryThread created\n" );
      }

      @Override
      public void run()
      {
         displayMessage( "DiscoveryThread restarted\n" );
         try
         {
            socket = new DatagramSocket( TicTacToeServer.DISCOVERY_PORT,
                  InetAddress.getByName( "" ) );
            socket.setBroadcast( true );

            byte[] recvBuf = new byte[ 1000 ];
            DatagramPacket packet = new DatagramPacket( recvBuf, recvBuf.length );

// missing the ending "\n" caused the server name to be incorrect, e.g.
// long name "very long name" changed by "server" lead to "serverong name".
            byte[] sendData = ( TicTacToeServer.DISCOVERY_RESPONSE + "\n" +
                  serverName + "\n" ).getBytes();

            while( true )
            {
               socket.receive( packet );
               String message = new String( packet.getData() ).trim();
               if( message.equals( TicTacToeServer.DISCOVERY_REQUEST ) )
               {
                  DatagramPacket sendPacket = new DatagramPacket( sendData,
                        sendData.length, packet.getAddress(), packet.getPort() );
                  socket.send( sendPacket );
               } // end if
            } // end while
         } // end try
         catch( IOException e )
         {
            displayMessage( e.getMessage() );
         } // end catch
      } // end method run

      public void stop()
      {
         if( socket != null )
         {
            socket.close();
         } // end if
         displayMessage( "DiscoveryThread stopped\n" );
      } // end method stop
   } // end inner class DiscoveryThread
} // end class Publisher
