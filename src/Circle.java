
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Circle extends JPanel {

    int x, y, width, height;
    boolean filled;

    public Circle(boolean filled) {
        this.x = this.getWidth();
        this.y = this.getHeight();
        this.filled = filled;
    }

    public void paint(Graphics g) {
        int circleSide = (int)(getWidth() * 0.8);
        int location = (getWidth() - circleSide) / 2;

        if(filled) {
            g.fillOval(location, location,circleSide , circleSide);
        }else {
            g.drawOval(location,location,circleSide,circleSide);
        }
    }

}