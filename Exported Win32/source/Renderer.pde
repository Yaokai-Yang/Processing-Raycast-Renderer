public class Renderer
{
  private float brightness = 1f;
  public float bkgdColVal = 15f;
  private color bkgdCol = color(bkgdColVal);
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
    color col = color(200,200,200);
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
