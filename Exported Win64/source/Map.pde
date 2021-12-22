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
