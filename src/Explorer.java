// @TODO
// - use FixedTreadPool for discovery client thread
// - execute awaitThreadTermination in the end of stop()

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.NetworkInterface;
import java.net.InterfaceAddress;
import java.util.Enumeration;
import java.io.IOException;

//public Explorer( LoggerWindow, ItemListPanel )
//void update()
//void restart()
//void stop()
public class Explorer
{
   private DiscoveryClient discoveryClient;
   private LoggerWindow logger;

   /**
    * <p>Constructor <code>Explorer</code> creates broadcast
    * socket and start explorer thread.</p>
    */
   public Explorer( LoggerWindow logger, ItemListPanel itemList )
   {
      this.logger = logger;
      discoveryClient = new DiscoveryClient( itemList );
   }

   /**
    * <p><code>update</code> method send broadcast DOSCOVERY_REQUEST
    * message.</p>
    */
   public void update()
   {
      discoveryClient.update();
   }

   public void restart()
   {
      stop();
      (new Thread( discoveryClient ) ).start();
   } // end method restart

   public void stop()
   {
      discoveryClient.stop();
   } // end method stop

   private void displayMessage( final String messageToDisplay )
   {
      if( logger != null )
         logger.displayMessage( "Explorer: " + messageToDisplay );
   } // end method displayMessage

   private class DiscoveryClient implements Runnable
   {
      private DatagramSocket socket;
      private ItemListPanel itemList;

      public DiscoveryClient( ItemListPanel list )
      {
         itemList = list;
         displayMessage( "DiscoveryClient created\n" );
      }

      public void update()
      {
         if( socket != null ) {
            try {
               // Try to broadcast to the default
               // broadcast address (255.255.255.255)
               byte[] sendData = TicTacToeServer.DISCOVERY_REQUEST.getBytes();
               try {
                  DatagramPacket sendPacket = new DatagramPacket(
                        sendData, sendData.length,
                        InetAddress.getByName( "255.255.255.255" ),
                        TicTacToeServer.DISCOVERY_PORT );
                  socket.send( sendPacket );
               } catch( IOException e ) {
                  displayMessage( e.getMessage() );
               }
               // Loop over all the computer's network
               // interfaces and get their broadcast
               // addresses
               Enumeration<NetworkInterface> interfaces =
                     NetworkInterface.getNetworkInterfaces();
               while( interfaces.hasMoreElements() ) {
                  NetworkInterface networkInterface = interfaces.nextElement();
                  if( networkInterface.isLoopback() || !networkInterface.isUp() ) {
                     // Don't want to broadcast to the loopback interface
                     continue;
                  }
                  for( InterfaceAddress interfaceAddress :
                        networkInterface.getInterfaceAddresses() ) {
                     InetAddress broadcast = interfaceAddress.getBroadcast();
                     if( broadcast == null ) {
                        continue;
                     }
                     // Send the UDP packet inside the loop
                     // to the interface's broadcast address
                     try {
                        DatagramPacket sendPacket = new DatagramPacket(
                              sendData, sendData.length, broadcast,
                              TicTacToeServer.DISCOVERY_PORT );
                        socket.send( sendPacket );
                     } catch( IOException e ) {
                        displayMessage( e.getMessage() );
                     }
                  }
               }
            }
            catch( IOException e ) {
               displayMessage( e.getMessage() );
            }
            displayMessage( "done broadcasting\n" );
         } else {
            displayMessage( "cannot broadcast - socket closed\n" );
         }
      } // end method update

      @Override
      public void run()
      {
         displayMessage( "DiscoveryClient restarted\n" );
         try
         {
            socket = new DatagramSocket( 12345, InetAddress.getByName( "" ) );
            socket.setBroadcast( true );

            update();

            byte[] recvBuf = new byte[ 1000 ];
            DatagramPacket packet = new DatagramPacket( recvBuf, recvBuf.length );

            while( true )
            {
               socket.receive( packet );
               displayMessage( "got packet from: " + packet.getAddress() + "\n" );
               String message = new String( packet.getData() ).trim();
               String[] lines = message.split( "\n" );
               if( lines.length >= 2 &&
                     lines[ 0 ].equals( TicTacToeServer.DISCOVERY_RESPONSE ) )
               {
                  itemList.addServer( new ServerInfo( lines[ 1 ], packet.getAddress() ) );
                  displayMessage( "it's valid server response\n" );
                  displayMessage( "server name: " + lines[ 1 ] + "\n" );
               } // end if
               else
                  displayMessage( "it's not valid server response\n" );
            } // end while
         } // end try
         catch( IOException e ) {
            displayMessage( e.getMessage() + "\n" );
         } // end catch
         finally {
            if( socket != null ) {
               socket.close();
               socket = null;
            }
         } // end finally
      } // end method run

      public void stop()
      {
         if( socket != null )
         {
            socket.close();
            socket = null;
         } // end if
         displayMessage( "DiscoveryClient stopped\n" );
      } // end method stop
   } // end inner class DiscoveryClient
} // end class Explorer
