// TextOption.java

public class TextOption extends Option {
   private String defaultText;
   private String currentText;

   public TextOption( String name, String text )
   {
      super( name );
      defaultText = text;
      currentText = text;
   } // end constructor

   @Override
   public String getValueName()
   {
      return currentText;
   } // end method getValueName

   public String getDefaultText()
   {
      return defaultText;
   } // end method getDefaultText

   public String getText()
   {
      return currentText;
   } // end method getText

   public void setText( String text )
   {
      if( text.equals( "" ) )
      {
         currentText = defaultText;
      } // end if
      else
      {
         currentText = text;
      } // end else
   } // end method setText
} // end class TextOption
