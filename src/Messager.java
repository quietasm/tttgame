// Messager.java => MessageStream & Message

import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class Messager {
   private Component parentComponent;
   private JOptionPane pane;
   private JDialog dialog;
   private boolean closed;

   public Messager( Component parent ) {
      parentComponent = parent;
      pane = new JOptionPane();
      closed = true;
   }

   public void asyncMessage( String text ) {
      pane.setMessage( text );
      dialog = pane.createDialog( parentComponent, "Message" );
      Runnable task = new Runnable() {
         public void run() {
            //dialog.show();
            dialog.setVisible( true );
            dialog.dispose();
            closed = true;
         }
      };
      closed = false;
      (new Thread( task ) ).start();
   }

   public boolean ready() {
      return closed;
   }

   public void stop() {
      if( !closed ) {
         //dialog.hide();
         dialog.setVisible( false );
      }
   }
} // end class Messager