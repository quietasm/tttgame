// ListOption.java

public class ListOption extends Option {
   private String itemNames[];
   private int defaultItemIndex;
   private int currentItemIndex;

   public ListOption( String name, String[] items, int index )
   {
      super( name );
      itemNames = items;
      if( index >= 0 && index < items.length )
      {
         defaultItemIndex = index;
         currentItemIndex = index;
      } // end if
      else
      {
         defaultItemIndex = 0;
         currentItemIndex = 0;
      } // end else
   } // end constructor

   @Override
   public String getValueName()
   {
      return itemNames[ currentItemIndex ];
   } // end method getValueName

   public String[] getItemNames()
   {
      String[] names = new String[ itemNames.length ];
      for( int i = 0; i < itemNames.length; i++ )
         names[ i ] = itemNames[ i ];
      return names;
   } // end method getItemNames

   public int getDefaultItemIndex()
   {
      return defaultItemIndex;
   } // end method getDefaultItemIndex

   public int getCurrentItemIndex()
   {
      return currentItemIndex;
   } // end method getCurrentItemIndex

   public void setCurrentItemIndex( int index )
   {
      if( index >= 0  && index < itemNames.length )
      {
         currentItemIndex = index;
      } // end if
   } // end method setCurrentItemIndex
} // end class ListOption
