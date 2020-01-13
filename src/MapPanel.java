import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
//import javax.swing.JPanel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

public class MapPanel extends FramedPanel {
   private Map map;
   private int crossed;
   private int crossedValue;
   private LocationListener locationListener;

   public MapPanel( Map mapObject, LocationListener listener ) {
      setBackground( Color.WHITE );
      locationListener = listener;
      addMouseListener( new MouseAdapter() {
         @Override
         public void mouseReleased( MouseEvent event ) {
            int x = event.getX();
            int y = event.getY();
            if( locationListener != null &&
                  x >= 0 && x < getWidth() && y >= 0 && y < getWidth() ) {
               int cellSize = getWidth() / map.getSize();
               int column = x / cellSize;
               int row = y / cellSize;
               locationListener.processLocation( row*map.getSize() + column );
            }
         }
      });
      map = mapObject;
      crossed = TicTacToeServer.CROSS_NOTHING;
      crossedValue = 0;
   }
   public void setMap( Map mapObject ) {
      map = mapObject;
   }
   public void setCrossed( int type, int number ) {
      crossed = type;
      crossedValue = number;
   }
   @Override
   public Dimension getPreferredSize() {
      int w = getParent().getWidth();
      int h = getParent().getHeight();
      int size = (w >= h)? h : w;
      return new Dimension( size-10, size-10 );
   }
   @Override
   public void paintComponent( Graphics g ) {
      super.paintComponent( g );
      // draw board
      int boardSize = map.getSize();
      int cellSize = getWidth() / boardSize;
      for( int i = 1; i < boardSize; i++ ) {
         g.drawLine( i*cellSize, 0, i*cellSize, getWidth() );
         g.drawLine( 0, i*cellSize, getWidth(), i*cellSize );
      }
      // fill cells
      for( int i = 0; i < boardSize; i++ ) {
         for( int j = 0; j < boardSize; j++ ) {
            drawMark( g, i, j, map.getMark( i*boardSize+j ), cellSize );
         }
      }
      // cross line if needed
      if( crossed != TicTacToeServer.CROSS_NOTHING ) {
         int padding = cellSize / 5;
         ((Graphics2D) g).setStroke( new BasicStroke( 5.0f ) );
         switch( crossed ) {
            case TicTacToeServer.CROSS_ROW:
               if(map.getMark( crossedValue*map.getSize() ).equals( "X" )) {
                  g.setColor( Color.RED );
               } else {
                  g.setColor( Color.BLUE );
               }
               g.drawLine( padding, crossedValue*cellSize+cellSize/2,
                     getWidth()-padding, crossedValue*cellSize+cellSize/2 );
               break;
            case TicTacToeServer.CROSS_COLUMN:
               if(map.getMark( crossedValue ).equals( "X" )) {
                  g.setColor( Color.RED );
               } else {
                  g.setColor( Color.BLUE );
               }
               g.drawLine( crossedValue*cellSize+cellSize/2, padding,
                     crossedValue*cellSize+cellSize/2, getWidth()-padding );
               break;
            case TicTacToeServer.CROSS_DIAGONAL:
               if( crossedValue == 0 ) {
                  if(map.getMark( 0 ).equals( "X" )) {
                     g.setColor( Color.RED );
                  } else {
                     g.setColor( Color.BLUE );
                  }
                  g.drawLine( padding, padding,
                        getWidth()-padding, getWidth()-padding );
               } else {
                  if(map.getMark( map.getSize()-1 ).equals( "X" )) {
                     g.setColor( Color.RED );
                  } else {
                     g.setColor( Color.BLUE );
                  }
                  g.drawLine( padding, getWidth()-padding,
                        getWidth()-padding, padding );
               }
               break;
         }
      }
   }
   private void drawMark( Graphics g, int row, int column,
         String mark, int cellSize ) {
      int y = row * cellSize;
      int x = column * cellSize;
      int delta = cellSize / 5;
      ((Graphics2D) g).setStroke( new BasicStroke( 5.0f ) );
      if( mark.equals( "X" ) ) {
         g.setColor( Color.RED );
         g.drawLine( x+delta, y+delta, x+cellSize-delta, y+cellSize-delta );
         g.drawLine( x+cellSize-delta, y+delta, x+delta, y+cellSize-delta );
      } else if( mark.equals( "O" ) ) {
         g.setColor( Color.BLUE );
         g.drawOval( x+delta, y+delta, cellSize-2*delta, cellSize-2*delta );
      }
   }
} // end class MapPanel