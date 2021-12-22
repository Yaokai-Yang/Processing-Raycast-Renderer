public class HitInfo
{
  public float dist;
  public color col;      //Color or textures
  public boolean hit;
  
  public HitInfo(float dist, color col, boolean hit)
  {
    this.dist = dist;
    this.col = col;
    this.hit = hit;
  }
}
