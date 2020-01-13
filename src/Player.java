// Player.java
// Player interface definition.

interface Player extends Runnable
{
   public void otherPlayerMoved( int location );

   public String getRole();
   public String getPassword();

   //Inherited from Runnable
   //public void run();

} // end interface Player