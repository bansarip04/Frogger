import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

//Frogger.java
//Bansari Patel
//This program is a frogger game. Objective of the game is the get the frog into the 5 safe spots at the end of the screen. If the frog comes in contact
//with the cars or water, it will die and have 1 less chance to get there. I've added, a timer and sinking turtles which make it more challenging to get across.
// A fly will also randomly show up at one of the empty spots, more points are awarded if the frog comes in contact with the flies. If 500 points are surpassed, the frog will gain a life in the next level.
public class Frogger extends JFrame {
    FroggerPanel game;
    public Frogger() {
        super("Basic Graphics");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game = new FroggerPanel();
        add(game);
        pack();
        setVisible(true);
    }
    public static void main(String[] arguments) {
        Frogger frame = new Frogger();
    }
}
class FroggerPanel extends JPanel implements MouseListener, ActionListener, KeyListener {
    private String screen = "menu";
    private String highscore = "";
    private Frog frog;
    private Fly fly;
    private static ArrayList<Cars> car_list;
    private static ArrayList<Log> log_list;
    private static ArrayList<Turtles> turtle_list;
    private Rectangle[] safe_spots;
    private File hit_sound,hop_sound,next_sound,died_sound,got_sound;
    private boolean []keys;

    public static ArrayList<Rectangle> done_spots;
    public static final int JUMP_POINTS = 10;
    public static int level =0;     // first level

    Timer myTimer;
    Image back, purple, safe,lives,done_frog,menu_pic, level_pic,fly_pic, help_pic,highscore_pic;
    Font font = null;

    public static final int WIDTH=700, HEIGHT=650;
    public static int grid = 46;
    public static double timer,fly_time;
    public static int score =0;
    static boolean[] jump_check;

    public FroggerPanel(){
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addMouseListener(this);
        addKeyListener(this);

//--------------------------------------Pictures
        back = new ImageIcon("back.png").getImage();
        purple = new ImageIcon("images/purple part.png").getImage();
        safe = new ImageIcon("images/safe_patch.png").getImage();
        lives = new ImageIcon("images/frog0.png").getImage();
        done_frog = new ImageIcon("images/set_frog.png").getImage();
        menu_pic = new ImageIcon("images/menu_pic.png").getImage();
        fly_pic = new ImageIcon("images/fly.png").getImage();
        help_pic = new ImageIcon("images/help_pic.png").getImage();
        level_pic = new ImageIcon("images/next_level.png").getImage();
        highscore_pic = new ImageIcon("images/highscore_pic.png").getImage();
//----------------------------initializing key/timer
        keys = new boolean[KeyEvent.KEY_LAST+1];
        int []k1 = {KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT};
        timer = 200.0;
        myTimer = new Timer(80, this);
        fly_time = 0;
//----------------------------object holders/frog&fly making
        frog = new Frog((int) (grid *7+8), (int) (grid*12+8),30,30,k1, true, 3,"frog",3,2 );
        fly = new Fly(-50,-50,35,35,150,1);
        fly.set_pos((int) (Math.random()*5));
        car_list = new ArrayList<Cars>();
        log_list = new ArrayList<Log>();
        turtle_list = new ArrayList<>();

        safe_spots = new Rectangle[5];
        done_spots = new ArrayList<Rectangle>();
//-------------------------------- Font
        String game_font = "frogger_font.ttf";
        InputStream is = FroggerPanel.class.getResourceAsStream(game_font);
        try{
            font = Font.createFont(Font.TRUETYPE_FONT, new File("frogger_font.ttf")).deriveFont(22f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,new File("frogger_font.ttf")));
        }
        catch (IOException | FontFormatException e){
        }
//-----------------------------------making Sound files
        hit_sound = new File("sound-frogger-squash.wav");
        hop_sound = new File("hop_sound.wav");
        next_sound = new File("next level.wav");
        died_sound = new File("died.wav");
        got_sound = new File("spot_got.wav");

//----------------------------------- objects are made
        MakeObjects();
//-----------------------------------all possible safe rectangle spots are added to safe spots array
        for (int i = 0; i<5; i++){
            int x_pos = 20+i*(150);     //spacing
            safe_spots[i] = new Rectangle(x_pos,14,55,30);
        }
        setFocusable(true);
        requestFocus();
    }
//--------------------------------method that makes objects that will be used in the beginning of the game
    static void MakeObjects(){
// --------------------------clears object lists
        car_list.clear();   //this is done so when method is used for a next level objects from the previous level aren't piling up
        log_list.clear();
        turtle_list.clear();
        jump_check = new boolean[]{false,false,false, false, false, false, false, false, false, false, false, false, false};
//--------------------------car objects are made
        String[] car_pics = {"green_car", "roll", "pink_car","yellow_car","truck"};
        int [][] speed = {{4,4,4,5,3},{6,20,10,15,10}};int[][] distance = {{200,200,200,200,200},{150,25,150,30,150}};
        int[][] num_car_row = {{3,4,3,3,3},{3,3,4,3,3}};int[] car_pics_len = {1,3,1,1,1};int [] car_length = {55,55,50,55,75};
        int [] car_start_pos = {50,200,75,190,85};

        for (int row  = 0; row <5; row++) {
            for (int car = 0; car < num_car_row[level][row]; car++) {
                int x_pos = car * (car_length[row] + distance[level][row]) + (car_start_pos[row]);  //spaced out according to "distance"
                int y_pos = (11 - row) * grid + 5;
                int direct;

                //so cars go opposite direction
                if (row % 2 == 0) {
                    direct = -1;
                } else {
                    direct = 1;
                }
                Cars car_made = new Cars(x_pos, y_pos, car_length[row], (int) grid - 12, speed[level][row], direct, car_pics[row], car_pics_len[row]);
                car_list.add(car_made);
            }
        }
//------------------------------log objects made
        int[] log_length = {150,200,250};
        int[] log_y_pos = {0,1,3};
        int[][] log_speeds = {{4,8,6},{10,14,12}};

        for (int row  = 0; row <3; row++){
            for (int log = 0; log <3; log++){
                int x_pos = log *(log_length[row]+150); //they will be 150 pixels apart
                int y_pos = (4-log_y_pos[row])*grid;
                Log log_made = new Log(x_pos, y_pos+8,log_length[row], 30,log_speeds[level][row], "log",1);
                log_list.add(log_made);
            }
        }
//-------------------------------making turtle objects
        int[] tur_x_pos = {90, 180};
        int[] tur_y_pos = {0,3};
        int [] tur_num = {12,9};
        int tur_w = 30;
        int space;
        for (int row  = 0; row <2; row++){
            for (int turtle = 1; turtle <tur_num[row]; turtle++){
                space = (turtle%4 ==0) ? tur_w*4:0;     //every 3 turtles they will be spaced apart
                int num = 3;
                if(turtle>8 && level>0 && row ==0||turtle<4 && level>0 && row ==1 ){    //depending on the row and level, sinking turtles are added(sinking turtles animation have 7 frames)
                    num = 7;
                }
                int y_pos = (5-tur_y_pos[row])*grid;
                int x_pos = turtle*(tur_w+20)+turtle*space;     // spaced out accordingly
                Turtles turtle_made = new Turtles(x_pos+tur_x_pos[row], y_pos+8,tur_w+5, tur_w,8, "turtle",num,false);
                turtle_list.add(turtle_made);
            }
        }
    }
//------------------------------jump score
    public void check_jump_score(){
        int pos = frog.getY()/grid;     //if the frog has not yet advanced to that row of the game, 10 points are given
        if (jump_check[pos] == false && frog.getY()<grid*12){
            score+=JUMP_POINTS;
            jump_check[pos] = true;
        }
    }
//-----------------------------sound maker
    static void PlaySound(File Sound){
        try{
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(Sound));
            clip.start();
        }
        catch (Exception e){
        }
    }

    @Override

    public void actionPerformed(ActionEvent e){
        repaint();  //everytime an action by the user is given, the following methods are called
        collide();
        timer-=.5;
        fly_time+=1;
        if(timer<=0){   //when time runs out frog loses a life
            PlaySound(hit_sound);
            frog.hurt();
        }
//--------------------------------if frog is dead
        if (frog.get_lives()<=0){
            highscore = GetHighScore(); //initiallized GetHighScore method
            CheckScore();   //checks if current score is higher than the previous best score
            PlaySound(died_sound);
            screen = "menu";
            repaint();
            level =0; // start from beggining
            MakeObjects();  //new objects are re-made with any lists cleared
            myTimer.stop();
        }
//-------------------------------- if player wins level
        if (done_spots.size() == 5){
            screen = "next_level";
            PlaySound(next_sound);  //special sound is played
            myTimer.stop();

        }
    }
//-----------------------------------returns current highscore in the format of the person's name:their score
    public String GetHighScore() {
        FileReader readFile = null; //corresponds with actual file
        BufferedReader reader = null; //to extract what we need from file
        try {
            readFile = new FileReader("highscore.txt");
            reader = new BufferedReader(readFile);
            return reader.readLine();   //returns first line of file
        } catch (Exception e) {
            return "Nobody:0";
        } finally {
        }
    }
//--------------------------------------method checks if players score is higher than the current highscore, if it is, then the player can enter their name and their score overwrites the previous one
    public void CheckScore(){
        System.out.println(highscore);
        if (highscore.equals(""))  // if the highscore is nothing, then score is not checked
            return;
        if(score > Integer.parseInt(highscore.split(":")[1])){  //checks if score is greater than current highscore in text file
            String name = JOptionPane.showInputDialog("Amazing Job! You set a new highscore, Enter your name!");    // if the current score is larger, name of user is taken and score is overwritten in the text file
            highscore = name+ ":" + score;
            try{
                Files.write(Paths.get("highscore.txt"),highscore.getBytes(), StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.CREATE);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        if (highscore.equals(""))
            return;
    }
//---------------------------------------method called everytime frog moves
    public void move(){
        frog.move(keys);
        PlaySound(hop_sound);
        check_jump_score(); //checks if the frog has been to that level already
    }
//----------------------------------------method called for when collision happens
    public void collide(){
        boolean ok = false;     // flag to keep track if the frog is still alive
        for (Cars car:car_list){
            if (frog.intersects(car)){
                PlaySound(hit_sound);
                frog.hurt();
            }
        }
        for (Log log:log_list){
            if (frog.intersects(log)){
                ok = true;
                frog.travel(log);
            }
        }
        for(Turtles turtle:turtle_list){
            if (frog.intersects(turtle)){
                ok = true;
                frog.travel(turtle);
            }
        }
//------------------------------------ if frog reaches one of the ending spots
            for(Rectangle rect:safe_spots){ //if frog gets a fly
                if(frog.intersects(rect) && fly.on(rect)){
                    PlaySound(got_sound);
                    score += 50;
                    done_spots.add(rect);   //one of the from spots is filled or completed
                    ok = true;
                    score+= 150;
                    fly.set_pos((int) (Math.random()*(safe_spots.length-done_spots.size())));   //fly is moved to one of the other random open spots
                    frog.done_spots();
                    frog.frog_reset();
                    timer =200;
                }
                else if (frog.intersects(rect)) {   //if frog reaches the end normally
                    PlaySound(got_sound);
                    if(!done_spots.contains(rect)){ //if the spot isn't already taken up
                        done_spots.add(rect);
                        score += 50;
                        ok = true;
                        fly.set_pos((int) (Math.random()*(safe_spots.length-done_spots.size())));
                        frog.done_spots();
                        frog.frog_reset();
                        timer =200;
                    }
                    else {
                        frog.hurt();    //if th spot is already taken up
                    }
                }
            }
        if (!ok && !frog.ground()){ // if the frog doesn't meet the requirements for being ok then a life is lost
            PlaySound(hit_sound);
            frog.hurt();
            timer = 200;
        }
//--------------------------------------------------timer system for fly
        if(done_spots.size()<5 && fly_time>=70 && fly_time<=140){   //for every 70 frames the fly will be on the screen, then it will "exit" or go to a position off the screen
            fly.available(safe_spots,done_spots);
        }
        if(fly_time == 141){
            fly.set_pos((int) (Math.random()*(safe_spots.length-done_spots.size())));
            fly.reset();
            fly_time*=0;    //fly time is reset so the fly will be off the screen for 70 frames and on for 70
        }
    }
    @Override
//-------------------------------------method deals with all the graphics
    public void paint(Graphics g){
        if(screen == "menu"){
            g.drawImage(menu_pic,0,0,null);
        }
        if(screen == "next_level"){
            g.drawImage(level_pic,0,0,null);
        }
        if(screen == "help"){
            g.drawImage(help_pic,0,0,700,650,null);
        }
            if (screen == "highscore"){
                g.drawImage(highscore_pic,0,0,null);
                if(highscore.equals("")){
                highscore = this.GetHighScore(); //initialized GetHighScore method
                }
                g.setColor(Color.yellow);
                g.setFont(font);
                g.drawString("♕ "+highscore+" ♕", (int) (WIDTH/3.5),HEIGHT/2);
            }
//-------------------------------------------------in game graphics
        if (screen == "game"){

            g.drawImage(back,0,0,null);
            g.drawImage(purple,0, grid*6,700, grid,null); // safe lanes
            g.drawImage(purple,0, grid*12,700, grid,null);
            g.drawImage(safe,0,-5,700, grid*1, null);   //frog spots
            g.setColor(Color.white);    //draws updated score
            g.setFont(font);
            g.drawString("SCORE "+(score),(int) (grid*2.5), grid *14-8 );    //score is updated

            for (Cars car:car_list) {
                car.update();
                car.draw(g);
            }
            for (Log log:log_list){
                log.update();
                log.draw(g);
            }
            if(frog.get_lives()>0){
                for(int i=0; i<frog.get_lives(); i++){
                    g.drawImage(lives,20+i*25, (int) (FroggerPanel.grid*13)+15,22,22,null);
                }
            }
            for(Turtles turtle:turtle_list){
                turtle.update();
                turtle.draw(g);
            }
            frog.draw(g);

            g.setColor(Color.red);  //timer
            g.drawString("TIME",(int) (grid*8.5),(int)grid*14-8 );
            g.fillRect((int)(grid*15-timer),(int)grid*13+10, (int) timer,30);

            g.drawImage(fly_pic,fly.x+8, fly.y, fly.w-4, fly.h-4,null);

            for(Rectangle rect:done_spots) {
                g.drawImage(done_frog,rect.x,rect.y,rect.width,rect.height,null);
            }
        }
    }
    @Override
//----------------------------------------------mouse/key events
    public void	mousePressed(MouseEvent e){
        Rectangle start_rec = new Rectangle(290,300,180,48);    //rectangles for each of the "buttons"
        Rectangle help_rec = new Rectangle(70,300,156,48);
        Rectangle score_rec = new Rectangle(530,300,156,48);
        Rectangle back_rec = new Rectangle(22,30,96,42);

        if (screen == "menu" && start_rec.contains(e.getX(),e.getY())){
            screen = "game";
            score =0;   //when game is started game is set up;
            frog.frog_set();
            myTimer.start();
            level =0;
        }
        if(screen == "menu" && help_rec.contains(e.getX(),e.getY())){
            screen = "help";
            repaint();
        }
        if(screen == "menu" && score_rec.contains(e.getX(),e.getY())){
            screen = "highscore";
            repaint();
        }
        if (screen == "help"||screen == "highscore"){
            if(back_rec.contains(e.getX(),e.getY())){
                screen = "menu";
                repaint();
            }
        }
        if(screen == "next_level" && start_rec.contains(e.getX(),e.getY())){
            screen = "game";
            frog.frog_set();
            level+=1;
            MakeObjects();  //method is called to make new objects and clear the ones form the previous level
            myTimer.start();
        }
    }
    public void	keyPressed(KeyEvent e){
        System.out.println(screen);
        keys[e.getKeyCode()] = true;
        if((e.getKeyCode()==KeyEvent.VK_RIGHT ||e.getKeyCode()==KeyEvent.VK_UP||e.getKeyCode()==KeyEvent.VK_LEFT||e.getKeyCode()==KeyEvent.VK_DOWN)){
            move();
        }
    }
    public void	keyReleased(KeyEvent e){
        keys[e.getKeyCode()] = false;
    }
    public void	keyTyped(KeyEvent e){}
    public void	mouseClicked(MouseEvent e){}
    public void	mouseEntered(MouseEvent e){}
    public void	mouseExited(MouseEvent e){}
    public void	mouseReleased(MouseEvent e){}
}








