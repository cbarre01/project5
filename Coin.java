import processing.core.PImage;

import java.util.List;

public class Coin extends Actor {


    private static final String ENEMY_KEY = "blob";
    private static final String ENEMY_ID_SUFFIX = " -- blob";
    private static final int BLOB_PERIOD_SCALE = 4;
    private static final int BLOB_ANIMATION_MIN = 50;
    private static final int BLOB_ANIMATION_MAX = 150;


    public Coin(String id, Point position,
                List<PImage> images,
                int actionPeriod) {
        this.setId(id);
        this.setPosition(position);
        this.setImages(images);
        this.setImageIndex(0);
        this.setActionPeriod(actionPeriod);
    }


    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler)
    {
        Point pos = getPosition();  // store current position before removing

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        Animated blob = createEnemy(getId() + ENEMY_ID_SUFFIX,
                pos, getActionPeriod() / BLOB_PERIOD_SCALE,
                BLOB_ANIMATION_MIN +
                        rand.nextInt(BLOB_ANIMATION_MAX - BLOB_ANIMATION_MIN),
                imageStore.getImageList(ENEMY_KEY));

        world.addEntity(blob);
        blob.scheduleActions(scheduler, world, imageStore);
    }

    private Enemy createEnemy(String id, Point position,
                              int actionPeriod, int animationPeriod, List<PImage> images)
    {
        return new Enemy(id, position, images,
                actionPeriod, animationPeriod);
    }

}
