import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.ScrollPaneConstants;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

public class SettingListPanel extends BasePanel
{
   private final static String sizeNames[] = { "3", "4", "5" };
   private final static int sizes[] = { 3, 4, 5 };
   private final static String playerNames[] = { Language.OPTION_PLAYER_X,
      Language.OPTION_PLAYER_O, Language.OPTION_RANDOM };
   private final static int players[] = { TicTacToeServer.PLAYER_X,
      TicTacToeServer.PLAYER_O, TicTacToeServer.ANYPLAYER };

   private Box contentBox;
   private IntegerListOption mapSize, playFor, firstMove;
   private TextOption serverName;

   public SettingListPanel()
   {
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

      JScrollPane scrollPane = new JScrollPane( panel2,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
      scrollPane.getVerticalScrollBar().setUnitIncrement( 10 );

      panel.setLayout( new BorderLayout() );
      panel.add( scrollPane, BorderLayout.CENTER );

      setContentPanel( panel );
      setCaption( Language.COMMAND_SETTINGS );

      mapSize = new IntegerListOption( Language.SETTING_MAPSIZE,
            sizeNames, sizes, 0 );
      playFor = new IntegerListOption( Language.SETTING_PLAYFOR,
            playerNames, players, 2 );
      firstMove = new IntegerListOption( Language.SETTING_FIRSTMOVE,
            playerNames, players, 0 );
      serverName = new TextOption( Language.SETTING_HOSTNAME,
            "Tic-Tac-Toe Server" );

      // check settings file to exist
      File fp = new File( "settings" );
      if( fp.exists() )
      {
         try
         {
            // open file
            DataInputStream input =
                  new DataInputStream( new FileInputStream( fp ) );

            // read option's values
            int map_size = input.readInt();
            int play_for = input.readInt();
            int first_move = input.readInt();
            String server_name = input.readUTF();

            // close file
            input.close();

            // set options
            mapSize.setValue( map_size );
            playFor.setValue( play_for );
            firstMove.setValue( first_move );
            serverName.setText( server_name );
         } // end try
         catch( IOException e )
         {
            e.printStackTrace();
         } // end catch
      } // end if

      SettingPanel sp1 = new SettingPanel( mapSize );
      SettingPanel sp2 = new SettingPanel( playFor );
      SettingPanel sp3 = new SettingPanel( firstMove );
      SettingPanel sp4 = new SettingPanel( serverName );

      MouseListener listener = new MouseAdapter() {
         @Override
         public void mouseReleased( MouseEvent event ) {
            Option option = ((SettingPanel) event.getSource()).getOption();

            JPanel optionPanel = null;
            if( option instanceof ListOption )
            {
               ListOption listOption = (ListOption) option;
               optionPanel = new ListOptionPanel( listOption.getItemNames(),
                     listOption.getCurrentItemIndex() );
            } // else if
            else if( option instanceof TextOption )
            {
               optionPanel = new TextOptionPanel( ((TextOption) option).getText() );
            } // end else if

            if( optionPanel != null )
            {
               optionPanel.setBorder( BorderFactory.createEmptyBorder( 10,10,10,10 ) );
               OptionDialog dialog = new OptionDialog( null, option.getOptionName() );
               dialog.setContent( optionPanel );
               dialog.pack();
               dialog.setLocationRelativeTo( SettingListPanel.this );
               dialog.showDialog();
               if( dialog.getCommand() == OptionDialog.COMMAND_OK )
               {
                  if( option instanceof ListOption )
                  {
                     ((ListOption) option).setCurrentItemIndex(
                           ((ListOptionPanel) optionPanel).getSelectedOption() );
                  }
                  else /*if( option instanceof TextOption )*/
                  {
                     String text = ((TextOptionPanel) optionPanel).getText();
                     ((TextOption) option).setText( text );
                  }
                  ((SettingPanel) event.getSource()).update();
               } // end if
               dialog.dispose();
            } // end if
         } // end method mouseReleased
      };
      sp1.addMouseListener( listener );
      sp2.addMouseListener( listener );
      sp3.addMouseListener( listener );
      sp4.addMouseListener( listener );

      contentBox.add( sp1 );
      contentBox.add( Box.createVerticalStrut( 3 ) );
      contentBox.add( sp2 );
      contentBox.add( Box.createVerticalStrut( 3 ) );
      contentBox.add( sp3 );
      contentBox.add( Box.createVerticalStrut( 3 ) );
      contentBox.add( sp4 );
   } // end constructor SettingListPanel

   public int getMapSize() {
      return mapSize.getValue();
   } // end method getMapSize

   public int getPlayFor() {
      return playFor.getValue();
   } // end method getPlayFor

   public int getFirstMove() {
      return firstMove.getValue();
   } // end method getPlayFor

   public String getServerName() {
      return serverName.getText();
   } // end method getServerName

   public void saveOptions()
   {
      try
      {
         // open or create settings file
         DataOutputStream output =
            new DataOutputStream( new FileOutputStream( "settings" ) );

         // write settings
         output.writeInt( mapSize.getValue() );
         output.writeInt( playFor.getValue() );
         output.writeInt( firstMove.getValue() );
         output.writeUTF( serverName.getText() );

         // close file
         output.close();
      } // end try
      catch( IOException e )
      {
         e.printStackTrace();
      } // end catch
   } // end method saveOptions
} // end class SettingListPanel
