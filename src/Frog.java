import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class Frog {
    private int x,y,w,h;
    private int up,down,left, right,lives,direction,spot_done;
    private boolean ground;
    private boolean hurt;
    private int frame,delay;
    private Image[] pics;
    private final int WAIT = 5, LEFT = -1, RIGHT = 1, UP = 2, DOWN =3;

    //constructor
    public Frog(int xx, int yy, int ww, int hh, int []keys, boolean gg, int ll , String name, int n, int dd){
        x = xx;
        y = yy;
        w = ww;
        h = hh;
        up = keys[0];
        down = keys[1];
        left = keys[2];
        right = keys[3];
        direction = dd;
        spot_done = 0;

        frame = 0;
        pics = new Image[n];
        for(int i = 0; i<n; i ++){  //images are added to array
            pics[i] = new ImageIcon("Images"+"/"+name+i+".png").getImage();
        }
        ground = gg; // true means on road
        lives = ll;
    }
//--------------------------------------------method deals with frogs direction and movement
    public void move(boolean []keys){
        if(keys[left] && x>=w ){
            x-=FroggerPanel.grid;
            direction = LEFT;
        }
        else if(keys[right] && (x+w) <= (FroggerPanel.WIDTH - w)){
            x+=FroggerPanel.grid;
            direction = RIGHT;
        }
        else if(keys[up] && y>=h){
            y-=FroggerPanel.grid;
            direction = UP;
        }
        else if(keys[down] && (y+h)<= (FroggerPanel.HEIGHT - h -FroggerPanel.grid)){
            y+=FroggerPanel.grid;
            direction = DOWN;
        }
        ground = y<= FroggerPanel.grid*6? false:true;   //if frog is on the upper half it won't be considered on the ground

        delay += 1;
        if(delay % WAIT == 0){
            frame++;
            if(frame == pics.length){
                frame =0;
            }
            frame = (frame + 1) % pics.length;
        }
    }
//------------------------------------------------checking intersections between other objects
    boolean intersects(Cars other){
        if (ground == true){    //if the frog is on the bottom portion and comes in contact with a car, health is taken away and true is returned
            if (this.x >= other.x+ other.w|| this.x+w <= other.x ||
                    this.y >= other.y +other.h || this.y+h <= other.y){
                hurt = false;
            }
            else {
                hurt = true;
                return true;
            }
        }
        return false;
    }
    boolean intersects(Log other){  // checking intersections uses the same process from before depending on the object type
        if (ground == false){
            if (this.x >= other.x+ other.w|| this.x+w <= other.x ||
                    this.y >= other.y +other.h || this.y+h <= other.y){
            }
            else {
                return true;
            }
        }
        return false;
    }

    boolean intersects(Turtles other) {
        if (ground == false) {
            if (this.x >= other.x + other.w || this.x + w <= other.x ||
                    this.y >= other.y + other.h || this.y + h <= other.y) {
            } else {
                if(other.frame>4){  //if turtle is on the 4th frame that means it's underwater and frog is exposed
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public boolean intersects(Rectangle other){
        return ((this.x > other.x  && this.x + this.w < other.x+ other.width &&
                this.y <= other.y + other.height && this.y+this.h <= other.y + other.height));
    }
//----------------------------------------------
    public void done_spots(){   //method will be used for keeping track of how many spots have been taken
        spot_done+=1;
    } //setter method
//----------------------------------------travel methods
    public void travel(Log other){  //frog will move with the objects and if the frog exits the screen, damage is given
        this.x += other.vx;
        if(this.x>FroggerPanel.WIDTH){
            hurt();
        }
    }
    public void travel(Turtles other){
        this.x -= other.vx;
        if(this.x+this.w<0){
            hurt();
        }
    }
//---------------------------------------------graphics
    public void draw(Graphics g){
        if(direction == UP){
            g.drawImage(pics[0],x,y,w,h,null);  //index of 0 is frog facing up
        }
        if(direction == LEFT){
            g.drawImage(pics[2],x,y,w,h,null);  //index of 2 corresponds to the frog image facing to the left
        }
        if(direction == RIGHT){
            g.drawImage(pics[2],x+w,y,-w,h,null);
        }
        if(direction == DOWN){
            g.drawImage(pics[0],x,y+h,w,-h,null);
        }
    }
    public int getY(){
        return y;
    }   //getter method for y val

    public void  hurt(){    //when a frog loses one of it's life
        frog_reset();   //frog's position and timer resets
        lives -=1;
        FroggerPanel.timer = 200;
    }
    public int get_lives(){
        return lives;
    }

    public void frog_reset(){   //frogs position is reset
        x = FroggerPanel.grid *8+10;
        y = FroggerPanel.grid*12+10;
    }

    public void frog_set(){ //when a level is advanced, or game begins, this function will clear and reset the positions and free spots
        lives =3;
        if(FroggerPanel.score >500){
            lives+=1;
        }
        FroggerPanel.done_spots.clear();
        frog_reset();
    }
    public boolean ground(){
        return (y>=FroggerPanel.grid*6);
    }   //if frog is on the lower half of the screen, if its on the water portion
}
