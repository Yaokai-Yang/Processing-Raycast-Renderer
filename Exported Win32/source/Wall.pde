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
