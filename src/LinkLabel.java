// LinkLabel.java

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import javax.swing.JLabel;
import java.util.Hashtable;
import java.util.Map;

/**
 * LinkLabel class simulates a web browser link. It displays
 * blue underlined text by default and toggles off underline,
 * when mouse is over label. On mouse released it opens specified
 * URI.
 * 
 * @author  Gorobejko V.S.
 * @history 22.12.18 - created.
 * @comment Class creates two Font objects within each instance.
 */
public class LinkLabel extends JLabel
{
   private URI target;
   private Font linkFont, alinkFont;

   public LinkLabel( String text )
   {
      super( text );

      Font font = getFont();
      font = new Font( font.getName(), font.getStyle(), font.getSize() );

      Map< TextAttribute, Object > attributes =
            new Hashtable< TextAttribute, Object >();

      attributes.put( TextAttribute.FOREGROUND, Color.BLUE );
      alinkFont = font.deriveFont( attributes );

      attributes.put( TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON );
      linkFont = font.deriveFont( attributes );

      setFont( linkFont );
      addMouseListener( new MouseAdapter() {
            @Override
            public void mouseReleased( MouseEvent evt )
            {
               if( target != null )
               {
                  try {
                     Desktop.getDesktop().browse( target );
                  }
                  catch( IOException ioException ) {
                  }
               }
            } // end method mouseReleased

            @Override
            public void mouseEntered( MouseEvent evt )
            {
               setFont( alinkFont );
            } // end method mouseEntered

            @Override
            public void mouseExited( MouseEvent evt )
            {
               setFont( linkFont );
            } // end method mouseExited
         }
      );
   } // end constructor

   public void setTarget( URI uri )
   {
      target = uri;
   } // end method setTarget
} // end class LinkLabel
