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
