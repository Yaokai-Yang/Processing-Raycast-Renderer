import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Main extends PApplet {

public Map map;
public Player player;
public Renderer renderer;

public void setup()
{
  //Screen settings
  rectMode(CENTER);
  
  
  //noLoop();
  //size(90, 800);
  
  //Generate new Map
  map = new Map(true);
  player = new Player(true, 90, width, 50, 1, 1, 0, -5, 0, 0.5f, 0.10f);
  renderer = new Renderer(map);
  
  //Map to array
  
  //Initiate Renderer
  renderer = new Renderer(map);
}

public void draw()
{
  background(renderer.bkgdColVal);
  
  player.move();  
    
  player.update();
  renderer.update(player.Xpos, player.Ypos, player.Dir, player.fov, player.vertScale, player.renderDist, player.nearDist);
  map.update();
}

public void mouseWheel(MouseEvent event)
{
  player.rot(event.getCount());
}
public class Map
{
  public Wall walls[];
  private boolean debug = false;
  
  public Map(boolean debug)
  {
    this.debug = debug;
    if(debug)
    {
      walls = new Wall[]
      {
        new Wall(1, -5, 10, 5, 10), 
        new Wall(1, 5, 10, 5, 0),
        new Wall(1, -5, 10, -5, 0),
        
        //new Wall(1, 5, 0, 15, 0), 
        //new Wall(1, 5, -10, 15, -10), 
        //new Wall(1, 15, 0, 15, -10), 
        
        new Wall(1, random(-51, 51), random(-51, 51), random(-51, 51), random(-51, 51)),
        new Wall(1, random(-51, 51), random(-51, 51), random(-51, 51), random(-51, 51)),
        new Wall(1, random(-51, 51), random(-51, 51), random(-51, 51), random(-51, 51)),
        new Wall(1, random(-51, 51), random(-51, 51), random(-51, 51), random(-51, 51)),
        new Wall(1, random(-51, 51), random(-51, 51), random(-51, 51), random(-51, 51)),
        new Wall(1, random(-51, 51), random(-51, 51), random(-51, 51), random(-51, 51))
      };
    }
  }
  
  public void update()
  {
    
    //debugging
    if(debug)
    {
      strokeWeight(3);
      stroke(200);
      for(Wall wall : walls)
      {
        //draw walls
        line(wall.x1 + width - 100, wall.y1 + 51, wall.x2 + width - 100, wall.y2 + 51);
      }
    }
  }
}
public class Player
{
  //Variables
  public float Xpos;
  public float Ypos;
  
  public float Dir;
  public float fov;
  public float vertScale;
  public float renderDist;
  public float nearDist;
  
  private float speed;
  private float acceleration;
  private float friction;
  
  private PVector movement = new PVector(0, 0);
  private PVector resistance = new PVector(0, 0);
  
  private boolean debug = false;
  
  public Player(boolean debug, float fov, float vertScale, float renderDist, float nearDist, float speed, float Xpos, float Ypos, float Dir, float acceleration, float friction)
  {
    this.fov = fov;
    this.vertScale = vertScale;
    this.renderDist = renderDist;
    this.nearDist = nearDist;
    this.speed = speed;
    this.Xpos = Xpos;
    this.Ypos = Ypos;
    this.Dir = Dir;
    this.acceleration = acceleration;
    this.friction = friction;
    
    this.debug = debug;
  }
  
  //Updates
  public void update()
  {
    //Movements
    PVector netMovement = new PVector(movement.x, movement.y);
    netMovement.rotate(radians(-Dir));
    Xpos += netMovement.x;
    Ypos += netMovement.y;
    
    //Debug
    if(debug)
    {
      fill(255);
      text("Pos: " + Math.round(Xpos*1000)/1000f + ", " + Math.round(Ypos*1000)/1000f, 15, 15);
      text("Dir: " + Math.round(Dir*1000)/1000f, 15, 30);
      text("Movement: [" + Math.round(netMovement.x*1000)/1000f + ", " + Math.round(netMovement.y*1000)/1000f + "]", 15, 45);
      text("Facing Vector: " + new PVector(0, 1).rotate(radians(-Dir)), 15, 60);
      
      stroke(color(255, 0, 0));
      strokeWeight(5);
      point(Xpos + width - 100, Ypos + 51);
      strokeWeight(2);
      line(Xpos + width - 100, Ypos + 51, Xpos + width - 100 + 20 * sin(radians(Dir)), Ypos + 51 + 20 * cos(radians(Dir)));
    }
  }
  
  //Movements
  public void move()
  {
    movement = new PVector(0, 0);
    if(keyPressed)
    {
      if (keyCode == UP)
      {
        movement.y += acceleration;
      }
      if (keyCode == DOWN)
      {
        movement.y -= acceleration;
      }
      if (keyCode == RIGHT)
      {
        movement.x -= acceleration;
      }
      if (keyCode == LEFT)
      {
        movement.x += acceleration;
      }
      
      if (!(keyCode == UP || keyCode == DOWN || keyCode == RIGHT || keyCode == LEFT))
      {
        movement = new PVector(0, 0);
      }
      
      if(movement.mag() > speed)
      {
        movement.normalize().setMag(speed);
      }
    }
  }
  
  public void rot(float val)
  {
    Dir += val;
  }
}
public class Renderer
{
  private float brightness = 1f;
  public float bkgdColVal = 15f;
  private int bkgdCol = color(bkgdColVal);
  Map map;
  
  public Renderer(Map map)
  {
    this.map = map;
  }
   
  public void update(float posX, float posY, float dir, float fov, float vertScale, float renderDist, float nearDist)
  {
    //Formatting
    noStroke();
    
    //For each column of pixels, cast a ray, add hitInfo to array
    HitInfo hits[] = new HitInfo[width];
    float increments = fov / width;
    float nearClippingPlaneWidth = (float) Math.tan(radians(fov / 2f)) * nearDist * 2f;          //Width of clipping plane based on FOV: Width = Tan(FOV / 2) * nearDist * 2 
    
    for(int x = 1; x <= width; x++)
    {
      //get near clipping plane position
      float clippingVal = (x - width / 2f) / width;
      float nearClippingPos = clippingVal * nearClippingPlaneWidth;
      
      //get relative angle dir
      float angle = degrees((float) Math.atan2(nearDist, nearClippingPos));
      angle -= 90f;
      angle += dir;
      
      //angle to vector
      float vx = (float) Math.sin(radians(angle));
      float vy = (float) Math.cos(radians(angle));
      
      hits[x - 1] = ray(new PVector(vx, vy).normalize(), posX, posY, renderDist, angle - dir);
      
      //Debugging    
      //System.out.println(angle + ", " + hits[x-1].dist);
    }
    
    //Draw a column for each pixel
    for(int x = 1; x <= hits.length; x++)
    {
      HitInfo hit = hits[x - 1];
      if(hit.hit)
      {
        fill(hit.col);
        int colHeight = (int) (vertScale / hit.dist);
        rect(x, height / 2, 1, colHeight);
      }
    }
  }
  
  public HitInfo ray(PVector dir, float posX, float posY, float maxLength, float angle)
  {
    float distance = maxLength + 1;
    int col = color(200,200,200);
    boolean hit = false;
    Wall hitTarget;
    
    //Check if ray hits any walls
    for(Wall wall : map.walls)
    {
      //Calculate hit point
      float wd;
      float testDist;
                                                                                                                                    //Math
                                                                                                                                    //posX + dir.x * distance = wall.x1 + wall.vector.x * wd;
                                                                                                                                    //posY + dir.y * distance = wall.y1 + wall.vector.y * wd;
                                                                                                                                    
                                                                                                                                    //distance = (wall.x1 + wall.vector.x * wd - posX) / dir.x = (wall.y1 + wall.vector.y * wd - posY) / dir.y
                                                                                                                                    //(wall.x1 + wall.vector.x * wd - posX) / dir.x = (wall.y1 + wall.vector.y * wd - posY) / dir.y
                                                                                                                                    
                                                                                                                                    //(wall.x1 + wall.vector.x * wd - posX) * dir.y = (wall.y1 + wall.vector.y * wd - posY) * dir.x
                                                                                                                                    //(wall.x1 - posX) * dir.y + (wall.vector.x * wd) * dir.y = (wall.y1 - posY) * dir.x + (wall.vector.y * wd) * dir.x
                                                                                                                                    
                                                                                                                                    //(wall.x1 - posX) * dir.y - (wall.y1 - posY) * dir.x = (wall.vector.y * wd) * dir.x - (wall.vector.x * wd) * dir.y
                                                                                                                                    //(wall.x1 - posX) * dir.y - (wall.y1 - posY) * dir.x = (wall.vector.y * wd * dir.x - wall.vector.x * wd * dir.y
                                                                                                                                    //(wall.x1 - posX) * dir.y - (wall.y1 - posY) * dir.x = wd * (wall.vector.y * dir.x - wall.vector.x * dir.y)
      wd = ((wall.x1 - posX) * dir.y - (wall.y1 - posY) * dir.x) / (wall.vector.y * dir.x - wall.vector.x * dir.y);
      testDist = (wall.x1 + wall.vector.x * wd - posX) / (2 * dir.x);
      
      //Check if hit
      if(wd <= wall.dist && wd > 0 && testDist <= maxLength && testDist > 0)
      {
        hit = true;
        if(testDist < distance)
        {
          distance = testDist;
          hitTarget = wall;
        }
      }
    }
    //Ask map for id hit
    
    //Temp color for debugging
    double lerp = (maxLength - distance) / maxLength;
    col = color(lerp(bkgdColVal, 256, (float) Math.pow(lerp, 2)));
          
    //Euclidean distance to projected distance (euclideanDist * cos(angle))
    distance *= (float) Math.cos(radians(angle));
    
    return new HitInfo(distance, col, hit);
  }
}
public class Wall
{
  int hitId;
  float x1;
  float y1;
  float x2;
  float y2;
  
  public PVector vector;
  public float dist;
  
  public Wall(int matId, float x1, float y1, float x2, float y2)
  {
    this.hitId = matId;
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    
    //get vector based on length
    float dx = x2 - x1;
    float dy = y2 - y1;
    
    vector = new PVector(dx, dy).normalize();
    dist = (float) Math.sqrt((double) (dx * dx + dy * dy));
  }
}
public class HitInfo
{
  public float dist;
  public int col;      //Color or textures
  public boolean hit;
  
  public HitInfo(float dist, int col, boolean hit)
  {
    this.dist = dist;
    this.col = col;
    this.hit = hit;
  }
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--stop-color=#cccccc", "Main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
