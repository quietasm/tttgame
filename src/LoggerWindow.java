// LoggerWindow.java

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class LoggerWindow extends JFrame {
   private JTextArea displayArea;

   public LoggerWindow() {
      super( "Logger" );

      displayArea = new JTextArea();
      add( new JScrollPane( displayArea ), BorderLayout.CENTER );
      displayArea.setText( "" );
      displayArea.setEditable( false );

      JButton clearButton = new JButton( "Clear" );
      clearButton.addActionListener(
         new ActionListener()
         {
            @Override
            public void actionPerformed( ActionEvent evt ) {
               displayArea.setText( "" );
            }
         }
      );
      add( clearButton, BorderLayout.SOUTH );

      setSize( 280, 210 );
   }

   public void clear()
   {
      displayArea.setText( "" );
   }

   public void displayMessage( final String message ) {
      SwingUtilities.invokeLater(
         new Runnable() {
            public void run() {
               displayArea.append( message );
            }
         }
      );
   }
}
