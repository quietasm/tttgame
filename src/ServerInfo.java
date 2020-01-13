import java.net.InetAddress;

/**
 * <p><code>ServerInfo</code> class keeps information about
 * server such its name and network address.</p>
 *
 * @since   version 1.1
 */
public class ServerInfo
{
   private String serverName;
   private InetAddress serverAddress;

   public ServerInfo( String name, InetAddress address )
   {
      serverName = name;
      serverAddress = address;
   } // end constructor ServerInfo

   public String getName()
   {
      return serverName;
   } // end method getName

   public InetAddress getAddress()
   {
      return serverAddress;
   } // end method getAddress
} // end class ServerInfo
