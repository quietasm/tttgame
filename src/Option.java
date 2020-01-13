// Option.java

public abstract class Option {
   private String optionName;

   public Option( String name )
   {
      optionName = name;
   } // end constructor

   public String getOptionName()
   {
      return optionName;
   } // end method getOptionName

   public abstract String getValueName();
} // end class Option
