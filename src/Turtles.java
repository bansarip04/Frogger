import javax.swing.*;
import java.awt.*;

public class Turtles {      //same process as previous classes
    int x,y,h,w,vx;
    int frame,delay, WAIT = 5;
    private Image[] pics;
    boolean sinker;

    //constructor
    public Turtles(int xx, int yy, int ww, int hh, int speed,String name, int n, boolean sink) {
        x = xx;
        y = yy;
        w = ww;
        h = hh;
        vx = speed;
        frame = 0;
        delay = 0;
        sinker = sink;

        pics = new Image[n];
        for (int i = 0; i < n; i++) {
            pics[i] = new ImageIcon("Images" + "/" + name + i + ".png").getImage();
        }
    }

    public void update(){
        x -= vx;
        if (x+w<=-w){
            x = FroggerPanel.WIDTH;
        }
        delay+=1;
        if(delay % WAIT == 0){
            frame = (frame + 1) % pics.length;
        }
    }
    public void draw(Graphics g){
        int w = frame==4 ? (int) (this.w * .75) :this.w;    //to center the picture if the frame is 4
        int h = frame==4 ? (int) (this.h * .75) :this.h;
        g.drawImage(pics[frame], x+(this.w-w)/2, y+(this.h-h)/2, w, h, null);
    }
}
