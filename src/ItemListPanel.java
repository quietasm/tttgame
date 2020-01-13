import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;

import javax.swing.Box;
import javax.swing.ScrollPaneConstants;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BorderFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

//void setCommandRefresh( ActionListener listener )
//void setCommandConnect( ActionListener listener )
//void setCommandCreate( ActionListener listener )
//ItemPanel getSelectedServer()
//void addServer( ServerInfo info )
//void clear()
public class ItemListPanel extends /* FramedPanel */ BasePanel
{
   private Box contentBox;
   private JScrollPane scrollPane;
   private MouseListener mouseListener;

   private ItemPanel selectedItem;

   private ActionListener commandRefresh;
   private ActionListener commandConnect;
   private ActionListener commandCreate;


   public ItemListPanel()
   {
      setCommandRefresh( null );
      setCommandConnect( null );
      setCommandCreate( null );

      mouseListener = new MouseAdapter() {
         @Override
         public void mouseReleased( MouseEvent event )
         {
            if( selectedItem != null ) {
               selectedItem.setSelected( false );
               selectedItem.repaint();
            } // end if
            selectedItem = ( ItemPanel ) event.getSource();
            selectedItem.setSelected( true );
            selectedItem.repaint();
         } // end method mouseReleased
      };

      JPanel panel = new JPanel();

      // create content box, wrap in scroll pane and add to panel
      contentBox = Box.createVerticalBox();
      contentBox.setBorder( BorderFactory.createEmptyBorder( 3,3,3,3 ) );
      JPanel panel2 = new JPanel();
      GridBagLayout layout2 = new GridBagLayout();
      panel2.setLayout( layout2 );

      GridBagConstraints constraints = new GridBagConstraints();
      constraints.anchor = GridBagConstraints.NORTH;
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.weightx = 10;
      constraints.weighty = 100;

      layout2.setConstraints( contentBox, constraints );
      panel2.add( contentBox );

      scrollPane = new JScrollPane( panel2,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
      scrollPane.getVerticalScrollBar().setUnitIncrement( 10 );

      panel.setLayout( new BorderLayout() );
      panel.add( scrollPane, BorderLayout.CENTER );

      // create stub listeners, initialize and add south button panel
      ActionListener refreshListener = new ActionListener() {
         @Override
         public void actionPerformed( ActionEvent event ) {
            if( commandRefresh != null ) {
               commandRefresh.actionPerformed( event );
            } // end if
         } // end method actionPerformed
      };

      ActionListener connectListener = new ActionListener() {
         @Override
         public void actionPerformed( ActionEvent event ) {
            if( commandConnect != null ) {
               commandConnect.actionPerformed( event );
            } // end if
         } // end method actionPerformed
      };

      ActionListener createListener = new ActionListener() {
         @Override
         public void actionPerformed( ActionEvent event ) {
            if( commandCreate != null ) {
               commandCreate.actionPerformed( event );
            } // end if
         } // end method actionPerformed
      };

      JPanel southPanel = createSouthPanel(
            refreshListener, connectListener, createListener );
      panel.add( southPanel, BorderLayout.SOUTH );
      setContentPanel( panel );
      setCaption( Language.COMMAND_NETMENU );
   } // end constructor ItemListPanel

   private static JPanel createSouthPanel( ActionListener refresh,
         ActionListener connect, ActionListener create )
   {
      GridBagConstraints constraints = new GridBagConstraints();
      //Defaults as follows:
      //   constraints.anchor = GridBagConstraints.CENTER;
      //   constraints.fill = GridBagConstraints.NONE;
      //   constraints.gridx = GridBagConstraints.RELATIVE;
      //   constraints.gridy = GridBagConstraints.RELATIVE;
      //   constraints.gridwidth = 1;
      //   constraints.gridheight = 1;
      //   constraints.weightx = 0;
      //   constraints.weighty = 0;

      JPanel southPanel = new JPanel();
      GridBagLayout layout = new GridBagLayout();
      southPanel.setLayout( layout );

      constraints.fill = GridBagConstraints.BOTH;
      constraints.gridheight = 1;
      constraints.weightx = 1;

      constraints.gridy = 0;
      constraints.gridwidth = 1;

      JButton refreshButton = new JButton( Language.COMMAND_REFRESH );
      refreshButton.addActionListener( refresh );
      constraints.gridx = 0;
      layout.setConstraints( refreshButton, constraints );
      southPanel.add( refreshButton );

      JButton connectButton = new JButton( Language.COMMAND_CONNECT );
      connectButton.addActionListener( connect );
      constraints.gridx = 1;
      layout.setConstraints( connectButton, constraints );
      southPanel.add( connectButton );

      JButton createButton = new JButton( Language.COMMAND_CREATE );
      createButton.addActionListener( create );
      constraints.gridx = 0;
      constraints.gridy = 1;
      constraints.gridwidth = 2;
      layout.setConstraints( createButton, constraints );
      southPanel.add( createButton );

      //refreshButton.setMargin( new Insets( 5, 5, 5, 5 ) );
      //connectButton.setMargin( new Insets( 5, 5, 5, 5 ) );
      //createButton.setMargin( new Insets( 5, 5, 5, 5 ) );

      return southPanel;
   } // end method createSouthPanel

   public void addServer( ServerInfo info )
   {
      ItemPanel serverPanel = new ItemPanel( info );
      serverPanel.addMouseListener( mouseListener );
      contentBox.add( serverPanel );
      contentBox.revalidate();
      //contentBox.repaint();
      scrollPane.revalidate();
      scrollPane.repaint();
   } // end method addServer

   public void clear() // inherited from java.awt.Container
   {
      contentBox.removeAll();
      selectedItem = null;
      contentBox.revalidate();
      contentBox.repaint();
   } // end method clear

   public ItemPanel getSelectedServer()
   {
      return selectedItem;
   } // end method getSelectedServer

   public void setCommandRefresh( ActionListener listener )
   {
      commandRefresh = listener;
   } // end method setCommandRefresh

   public void setCommandConnect( ActionListener listener )
   {
      commandConnect = listener;
   } // end method setCommandConnect

   public void setCommandCreate( ActionListener listener )
   {
      commandCreate = listener;
   } // end method setCommandCreate
}
