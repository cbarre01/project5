import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import processing.core.*;
import java.util.*;

public final class VirtualWorld
        extends PApplet
{
   private static final int TIMER_ACTION_PERIOD = 100;

   private static final int VIEW_WIDTH = 1000;
   private static final int VIEW_HEIGHT = 1000;
   private static final int TILE_WIDTH = 32;
   private static final int TILE_HEIGHT = 32;
   private static final int WORLD_WIDTH_SCALE = 2;
   private static final int WORLD_HEIGHT_SCALE = 2;
   private static final int TILE_SIZE = 32;

   private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
   private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
   private static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
   private static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

   private static final String IMAGE_LIST_FILE_NAME = "imagelist";
   private static final String DEFAULT_IMAGE_NAME = "background_default";
   private static final int DEFAULT_IMAGE_COLOR = 0x808080;

   private static final String LOAD_FILE_NAME = "gaia.sav";

   private static final String FAST_FLAG = "-fast";
   private static final String FASTER_FLAG = "-faster";
   private static final String FASTEST_FLAG = "-fastest";
   private static final double FAST_SCALE = 0.5;
   private static final double FASTER_SCALE = 0.25;
   private static final double FASTEST_SCALE = 0.10;


   private static final String GAS_KEY = "gas";
   private static final int GAS_NUM_PROPERTIES = 4;
   private static final int GAS_ID = 1;
   private static final int GAS_COL = 2;
   private static final int GAS_ROW = 3;

   private static final String FROG_KEY = "frog";
   private static final int FROG_NUM_PROPERTIES = 7;
   private static final int FROG_ID = 1;
   private static final int FROG_COL = 2;
   private static final int FROG_ROW = 3;
   private static final int FROG_ACTION_PERIOD = 4;
   private static final int FROG_ANIMATION_PERIOD = 6;
   private static double timeScale = 1.0;

   private static final String CONTROLLED_KEY = "controlledMiner";



   private ImageStore imageStore;
   private WorldModel world;
   private WorldView view;
   private EventScheduler scheduler;
   private ControlledMiner mainChar;

   private long next_time;

   public static void main(String [] args)
   {
      parseCommandLine(args);
      PApplet.main(VirtualWorld.class);
   }

   private static void parseCommandLine(String[] args)
   {
      for (String arg : args)
      {
         switch (arg)
         {
            case FAST_FLAG:
               timeScale = Math.min(FAST_SCALE, timeScale);
               break;
            case FASTER_FLAG:
               timeScale = Math.min(FASTER_SCALE, timeScale);
               break;
            case FASTEST_FLAG:
               timeScale = Math.min(FASTEST_SCALE, timeScale);
               break;
         }
      }
   }


   public void settings()
   {
      size(VIEW_WIDTH, VIEW_HEIGHT);
   }

   /*
      Processing entry point for "sketch" setup.mouseToPoint
   */
   public void setup()
   {
      this.imageStore = new ImageStore(
              createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
      this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
              createDefaultBackground(imageStore));
      this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world,
              TILE_WIDTH, TILE_HEIGHT);
      this.scheduler = new EventScheduler(timeScale);

      System.out.println(imageStore.getImageList(CONTROLLED_KEY));

      loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
      mainChar = ControlledMiner.createControlledMiner(CONTROLLED_KEY, new Point(1, 1),
              imageStore.getImageList(CONTROLLED_KEY));
      world.addEntity(mainChar);
      loadWorld(world, LOAD_FILE_NAME, imageStore);


      scheduleActions(world, scheduler, imageStore);



      next_time = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
   }

   public void draw()
   {
      long time = System.currentTimeMillis();
      if (time >= next_time)
      {
         this.scheduler.updateOnTime(time);
         next_time = time + TIMER_ACTION_PERIOD;
      }
      if (mainChar.getHp() < 1)
      {
         world.removeEntity(mainChar);
         System.out.println("Game over! score: " + mainChar.getScore());
         System.exit(0);
      }
      view.drawViewport();
   }

   public void keyPressed()
   {
      //System.out.println(keyCode);
      if (key == CODED)
      {
         int dx = 0;
         int dy = 0;
         switch (keyCode)
         {
            case UP:
               dy = -1;
               break;
            case DOWN:
               dy = 1;
               break;
            case LEFT:
               dx = -1;
               break;
            case RIGHT:
               dx = 1;
               break;

         }
         view.shiftView(dx, dy);
      }

      switch (keyCode) {

         case 65: //A
            mainChar.moveMC(world, new Point(-1, 0));
            break;
         case 83: //S
            mainChar.moveMC(world, new Point(0, 1));
            break;
         case 68: //D
            mainChar.moveMC(world, new Point(1, 0));
            break;
         case 87: //W
            mainChar.moveMC(world, new Point(0, -1));
            break;
      }
   }

   private Point mouseToPoint(int x, int y)
   {
      return new Point(mouseX/TILE_SIZE, mouseY/TILE_SIZE);
   }

   public void mousePressed()
   {
      Point pressed = mouseToPoint(mouseX, mouseY);
      Point newPressed = mouseToPoint(mouseX +4, mouseY +4);
      List<Point> allAdjacents = world.allAdjacents(pressed);

      Entity[] newGasArray = new Entity[9];
      for (int i = 0; i < 9; i++)
      {
         newGasArray[i] = Gas.createGas(GAS_ID + " " + String.valueOf(i),
                 allAdjacents.get(i),
                 imageStore.getImageList(GAS_KEY));

         world.addEntity(newGasArray[i]);
      }
      PoisonFrog frog = PoisonFrog.createPoisonFrog(FROG_KEY, newPressed,
              imageStore.getImageList(FROG_KEY),FROG_ACTION_PERIOD, FROG_ANIMATION_PERIOD);
      world.addEntity(frog);
      frog.scheduleActions(scheduler, world, imageStore);


   }

   private static PImage createImageColored(int width, int height, int color)
   {
      PImage img = new PImage(width, height, RGB);
      img.loadPixels();
      for (int i = 0; i < img.pixels.length; i++)
      {
         img.pixels[i] = color;
      }
      img.updatePixels();
      return img;
   }

   private static Background createDefaultBackground(ImageStore imageStore)
   {
      return new Background(
              imageStore.getImageList(DEFAULT_IMAGE_NAME));
   }

   private static void loadImages(String filename, ImageStore imageStore,
                                  PApplet screen)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         imageStore.loadImages(in, screen);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   private static void loadWorld(WorldModel world, String filename,
                                 ImageStore imageStore)
   {
      try
      {
         Scanner in = new Scanner(new File(filename));
         world.load(in, imageStore);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }

   private static void scheduleActions(WorldModel world,
                                       EventScheduler scheduler, ImageStore imageStore)
   {
      for (Entity entity : world.getEntities())
      {
         if (entity instanceof Actor)
         {
            ((Actor)entity).scheduleActions(scheduler, world, imageStore);
         }

      }
   }
}