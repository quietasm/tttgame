import java.awt.Graphics;
import javax.swing.JPanel;

public class FramedPanel extends JPanel {
   @Override
   public void paintComponent( Graphics g ) {
      super.paintComponent( g );
      g.drawRect( 2, 2, getWidth()-5, getHeight()-5 );
   }
}
