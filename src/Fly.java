import java.awt.*;
import java.util.ArrayList;

public class Fly {
    int x,y,w,h,score,pos;

    public Fly(int xx, int yy, int ww, int hh, int score, int pos){
        x = xx;
        y=yy;
        w=ww;
        h=hh;
        this.score=score;
        this.pos =pos;
    }

    public boolean on(Rectangle other){ //checks if fly is on the rectangel
        return ((this.x >= other.x  && this.x + this.w <= other.x+ other.width));
    }
    public void available(Rectangle [] safe, ArrayList<Rectangle> taken){
        ArrayList<Rectangle> good_place = new ArrayList<>();    //list will hold all rectangle spots where a fly can possibly land on
        for(Rectangle options:safe){    //
            if(!taken.contains(options)){
                good_place.add(options);
            }
        }
        if(x == -50 && y ==-50){    //if the fly is already off the screen, the x and y are set to the position of an open spot
            x = good_place.get(pos).x;
            y = good_place.get(pos).y;
        }
    }
    public void set_pos(int n){
        pos = n;
    }
    public void reset(){
        x = -50;
        y=-50;
    }

}
