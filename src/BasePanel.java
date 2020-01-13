import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;

public class BasePanel extends JPanel
{
   private GridBagLayout mainLayout;
   private JButton backButton;
   private ActionListener backCommandListener;
   private JTextField captionField;
   private JPanel contentPanel;

   public BasePanel()
   {
      mainLayout = new GridBagLayout();
      setLayout( mainLayout );

      backButton = new JButton( Language.COMMAND_BACK );
      //backButton.setMargin( new Insets( 5, 5, 5, 5 ) );
      backButton.addActionListener(
         new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent event ) {
               if( backCommandListener != null ) {
                  backCommandListener.actionPerformed( event );
               } // end if
            } // end method actionPerformed
         } // end anonymous inner class
      );

      captionField = new JTextField();
      captionField.setEditable( false );

      JPanel header = createHeaderPanel( backButton, captionField );
      
      GridBagConstraints constraints = new GridBagConstraints();

      constraints.anchor = GridBagConstraints.NORTH /* GridBagConstraints.CENTER */;
      constraints.fill = GridBagConstraints.HORIZONTAL /* GridBagConstraints.NONE */;
      constraints.gridx = 0 /* GridBagConstraints.RELATIVE */;
      constraints.gridy = 0 /* GridBagConstraints.RELATIVE */;
      //constraints.gridwidth = 1;
      //constraints.gridheight = 1;
      constraints.weightx = 1 /* 0 */;
      constraints.weighty = 1 /* 0 */;

      mainLayout.setConstraints( header, constraints );
      add( header );
   } // end constructor BasePanel

   private JPanel createHeaderPanel( JButton button, JTextField caption )
   {
      GridBagConstraints constraints = new GridBagConstraints();

      JPanel panel = new JPanel();

      GridBagLayout layout = new GridBagLayout();
      panel.setLayout( layout );

      //constraints.anchor = GridBagConstraints.CENTER;
      //constraints.fill = GridBagConstraints.NONE;
      constraints.gridx = 0 /* GridBagConstraints.RELATIVE */;
      constraints.gridy = 0 /* GridBagConstraints.RELATIVE */;
      //constraints.gridwidth = 1;
      //constraints.gridheight = 1;
      //constraints.weightx = 0;
      //constraints.weighty = 0;

      layout.setConstraints( button, constraints );
      panel.add( button );

      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.gridx = 1;
      constraints.weightx = 1;

      layout.setConstraints( caption, constraints );
      panel.add( caption );

      return panel;
   } // end method createHeaderPanel

   public void setBackCommandListener( ActionListener listener )
   {
      backCommandListener = listener;
   } // end method setBackCommandListener

   public void setCaption( String text )
   {
      captionField.setText( text );
   } // end method setCaption

   public void setContentPanel( JPanel newContentPanel )
   {
      if( contentPanel != null ) {
         mainLayout.removeLayoutComponent( contentPanel );
         remove( contentPanel );
      }
      contentPanel = newContentPanel;

      GridBagConstraints constraints = new GridBagConstraints();

      //constraints.anchor = GridBagConstraints.CENTER;
      constraints.fill = GridBagConstraints.BOTH /* GridBagConstraints.NONE */;
      constraints.gridx = 0 /* GridBagConstraints.RELATIVE */;
      constraints.gridy = 1 /* GridBagConstraints.RELATIVE */;
      //constraints.gridwidth = 1;
      //constraints.gridheight = 1;
      constraints.weightx = 1 /* 0 */;
      constraints.weighty = 100 /* 0 */;

      mainLayout.setConstraints( contentPanel, constraints );
      add( contentPanel );

      revalidate();
      repaint();
   } // end method setContentPanel
} // end class BasePanel
