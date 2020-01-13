// IntegerListOption.java

public class IntegerListOption extends ListOption {
   private int itemValues[];

   public IntegerListOption( String name, String[] items,
         int[] values, int index )
   {
      super( name, items, index );
      if( items.length != values.length )
      {
         throw new IllegalArgumentException( "one name must match one value" );
      } // end if
      itemValues = values;
   } // end constructor

   public int getValue()
   {
      return itemValues[ getCurrentItemIndex() ];
   } // end method getValue

   public void setValue( int x )
   {
      int index = itemValues.length - 1;
      while( index >= 0 && itemValues[ index ] != x ) index--;
      if( index >= 0 )
      {
         setCurrentItemIndex( index );
      } // end if
   } // end method setValue

   public void setValueByIndex( int index )
   {
      setCurrentItemIndex( index );
   } // end method setValueByIndex
} // end class IntegerListOption
