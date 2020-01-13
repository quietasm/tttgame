import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class OptionDialog extends JDialog
{
   public final static int COMMAND_OK = 0;
   public final static int COMMAND_CANCEL = 1;

   private JButton commands[];
   private int option;
   private JPanel content;

   public OptionDialog( Frame parent, String title ) {
      super( parent, title, true );

      // create command panel
      FlowLayout layout = new FlowLayout();
      layout.setAlignment( FlowLayout.RIGHT );

      JPanel panel = new JPanel();
      panel.setLayout( layout );

      commands = new JButton[2];
      commands[ 0 ] = new JButton( "Ok" );
      commands[ 1 ] = new JButton( "Cancel" );

      ActionListener commandListener = new ActionListener() {
         @Override
         public void actionPerformed( ActionEvent event ) {
            int i = 0;
            while( i < commands.length && event.getSource() != commands[ i ] )
               i++;
            option = i;
            setVisible( false );
         }
      };

      for( JButton command : commands ) {
         command.addActionListener( commandListener );
         panel.add( command );
      }

      setLayout( new BorderLayout() );
      add( panel, BorderLayout.SOUTH );
      pack();
   }

   public void setContent( JPanel panel ) {
      if( content != null )
         remove( content );
      content = panel;
      add( content, BorderLayout.CENTER );
      pack();
   }

   public void showDialog() {
      option = COMMAND_CANCEL;
      setVisible( true );
   }

   public int getCommand() {
      return option;
   }
} // end class OptionDialog
