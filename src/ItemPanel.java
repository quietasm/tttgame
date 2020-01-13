import java.awt.Graphics;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JLabel;

//ItemPanel( ServerInfo info )
//ServerInfo getInfo()
//void setSelected( boolean flag )
public class ItemPanel extends /* FramedPanel */ JPanel
{
   private ServerInfo serverInfo;
   private boolean selected;

   public ItemPanel( ServerInfo info )
   {
      serverInfo = info;

      Box box = Box.createVerticalBox();
      //BoxLayout layout = new BoxLayout( this, BoxLayout.Y_AXIS );
      //setLayout( layout );

      JLabel name = new JLabel( serverInfo.getName() );
      JLabel address = new JLabel( serverInfo.getAddress().getHostAddress() );

      box.add( name );
      box.add( address );

      add( box );

      selected = false;
   }

   public ServerInfo getInfo()
   {
      return serverInfo;
   } // end method getInfo

   public void setSelected( boolean flag )
   {
      selected = flag;
   } // end method setSelected

   @Override
   public void paintComponent( Graphics g )
   {
      super.paintComponent( g );
      if( selected )
      {
         g.drawRect( 0, 0, getWidth()-1, getHeight()-1 );
      } // end if
   } // end method paintComponent
} // end class ItemPanel
