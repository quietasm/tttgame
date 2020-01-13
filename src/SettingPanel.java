import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JLabel;

public class SettingPanel extends JPanel
{
   private Option option;
   private JLabel valueLabel;

   public SettingPanel( Option op )
   {
      option = op;

      // create UI
      setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) );
      setLayout( new GridLayout( 2, 1, 2, 2 ) );

      JLabel title = new JLabel( option.getOptionName() );
      valueLabel = new JLabel( option.getValueName() );

      JPanel panel1 = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
      panel1.add( title );
      JPanel panel2 = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
      panel2.add( valueLabel );

      add( panel1 );
      add( panel2 );
   }

   public Option getOption()
   {
      return option;
   }

   public void update()
   {
      valueLabel.setText( option.getValueName() );
      repaint();
   }
} // end class SettingPanel
