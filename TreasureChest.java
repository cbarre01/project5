import processing.core.PImage;

import java.util.*;

public class TreasureChest extends Actor

{
    private static final String ORE_ID_PREFIX = "ore -- ";
    private static final int ORE_CORRUPT_MIN = 20000;
    private static final int ORE_CORRUPT_MAX = 30000;
    private static final String ORE_KEY = "ore";


    public TreasureChest(String id, Point position,
                         List<PImage> images,
                         int actionPeriod) {
        this.setPosition(position);
        this.setImages(images);
        this.setImageIndex(0);
        this.setActionPeriod(actionPeriod);
    }



    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Point> openPt = world.findOpenAround(getPosition());

        if (openPt.isPresent()) {
            Actor coin = createCoin(ORE_ID_PREFIX + getId(),
                    openPt.get(), ORE_CORRUPT_MIN +
                            rand.nextInt(ORE_CORRUPT_MAX - ORE_CORRUPT_MIN),
                    imageStore.getImageList(ORE_KEY));
            world.addEntity(coin);
            coin.scheduleActions(scheduler, world, imageStore);
        }

        scheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                getActionPeriod());
    }

    public static Coin createCoin(String id, Point position, int actionPeriod,
                                 List<PImage> images)
    {
        return new Coin(id, position, images,
                actionPeriod);
    }

    public static TreasureChest createChest(String id, Point position, int actionPeriod,
                                            List<PImage> images)
    {
        return new TreasureChest(id, position, images,
                actionPeriod);
    }




}
