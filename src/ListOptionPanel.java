// ListOptionPanel.java

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ListOptionPanel extends JPanel {
   private JRadioButton[] options;
   private ButtonGroup group;

   public ListOptionPanel( String[] names, int selected )
   {
      options = new JRadioButton[ names.length ];
      for( int i = 0; i < options.length; i++ )
      {
         options[ i ] = new JRadioButton( names[ i ] );
      } // end for

      group = new ButtonGroup();

      BoxLayout layout = new BoxLayout( this, BoxLayout.Y_AXIS );
      setLayout( layout );

      for( int i = 0; i < options.length; i++ )
      {
         group.add( options[ i ] );
         add( options[ i ] );
         add( Box.createVerticalStrut( 3 ) );
      } // end for

      if( selected >= 0 )
      {
         options[ selected ].setSelected( true );
      } // end for
   } // end constructor

   public int getSelectedOption()
   {
      int i = 0;
      while( i < options.length && !options[ i ].isSelected() )
         i++;
      if( i >= options.length )
         i = -1;
      return i;
   } // end method getSelectedOption
} // end class ListOptionPanel
