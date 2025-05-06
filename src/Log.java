import javax.swing.*;
import java.awt.*;

public class Log {      //same process as Cars class
    int x,y,w,h,vx;
    private Image[]pics;
    //constructor
    public Log(int xx, int yy, int ww, int hh, int speed, String name, int n){
        x = xx;
        y = yy;
        w = ww;
        h = hh;
        vx = speed;
        pics = new Image[n];
        for(int i = 0; i<n; i ++){
            pics[i] = new ImageIcon("Images"+"/"+name+i+".png").getImage();
        }
    }

    public void update(){
        x += vx;
        if (x>= FroggerPanel.WIDTH+w){
            x = -w;
        }
    }
    public void draw(Graphics g){
        g.drawImage(pics[0], x, y, w, h, null);
    }
}

