import processing.core.PImage;

import java.util.*;

public class TreasureChest extends Actor

{
    private static final String TREASURECHEST_ID_PREFIX = "ore -- ";
    private static final int TREASURECHEST_CORRUPT_MIN = 20000;
    private static final int TREASURECHEST_CORRUPT_MAX = 30000;
    private static final String TREASURECHEST_KEY = "ore";


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
            Actor coin = createCoin(TREASURECHEST_ID_PREFIX + getId(),
                    openPt.get(), TREASURECHEST_CORRUPT_MIN +
                            rand.nextInt(TREASURECHEST_CORRUPT_MAX - TREASURECHEST_CORRUPT_MIN),
                    imageStore.getImageList(TREASURECHEST_KEY));
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
