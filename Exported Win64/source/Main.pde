public Map map;
public Player player;
public Renderer renderer;

void setup()
{
  //Screen settings
  rectMode(CENTER);
  fullScreen();
  
  //noLoop();
  //size(90, 800);
  
  //Generate new Map
  map = new Map(true);
  player = new Player(true, 90, width, 50, 1, 1, 0, -5, 0, 0.5, 0.10);
  renderer = new Renderer(map);
  
  //Map to array
  
  //Initiate Renderer
  renderer = new Renderer(map);
}

void draw()
{
  background(renderer.bkgdColVal);
  
  player.move();  
    
  player.update();
  renderer.update(player.Xpos, player.Ypos, player.Dir, player.fov, player.vertScale, player.renderDist, player.nearDist);
  map.update();
}

void mouseWheel(MouseEvent event)
{
  player.rot(event.getCount());
}
