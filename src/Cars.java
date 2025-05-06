import javax.swing.*;
import java.awt.*;

public class Cars {
    int x,y,h,w,vx;
    int direction, frame,delay;
    public static final int WAIT = 5;
    private Image[]pics;

    //  Constructor
    public Cars(int xx, int yy, int ww, int hh, int speed, int direc,String name, int n){
        x = xx;
        y = yy;
        w = ww;
        h = hh;
        vx = speed;
        frame=0;
        delay=0;
        direction = direc;
        pics = new Image[n];
        for(int i = 0; i<n; i ++){
            pics[i] = new ImageIcon("Images"+"/"+name+i+".png").getImage();
        }

    }
    public void update(){   //method that moves the object in the specified direction and speed
        x += vx*direction;
        if (x+w<=-w){
            x = FroggerPanel.WIDTH;
        }
        else if (x>= FroggerPanel.WIDTH){
            x = -w;
        }
        delay+=1;
        if(delay % WAIT == 0){
            frame = (frame + 1) % pics.length;
        }
    }
    public void draw(Graphics g){
        g.drawImage(pics[frame], x, y, w, h, null);
    }
}

