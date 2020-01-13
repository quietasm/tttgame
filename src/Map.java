public class Map {
   private String board[];
   private int boardSize;

   public Map( int size ) {
      //if( size < 3 || size > 7 )
      //   throw new IllegalArgumentException( "illegal map size" );
      boardSize = size;
      board = new String[ boardSize * boardSize ];
      clear();
   }

   public int getSize() {
      return boardSize;
   }

   public void clear() {
      for( int i = 0; i < board.length; i++ ) {
         board[ i ] = "";
      }
   }

   public void setMark( int location, String mark ) {
      //if( location < 0 || location >= boardSize * boardSize )
      //   throw new IllegalArgumentException( "illegal location" );
      board[ location ] = mark;
   }

   public String getMark( int location ) {
      return board[ location ];
   }
} // end class Map
