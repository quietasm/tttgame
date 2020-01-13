// TextOptionPanel.java

import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextOptionPanel extends JPanel {
   private JTextField textField;

   public TextOptionPanel( String text )
   {
      setLayout( new FlowLayout( FlowLayout.CENTER ) );
      textField = new JTextField( text, 15 );
      add( textField );
   } // end constructor

   public String getText()
   {
      return textField.getText();
   } // end method getText

   public void setText( String text )
   {
      textField.setText( text );
   } // end method setText
} // end class TextOptionPanel
