import javax.swing.*;
import java.awt.*;

public class BeautifulPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        ImageIcon img = new ImageIcon("Background.jpg");
        img.paintIcon(this,g,0,0);
    }
}
