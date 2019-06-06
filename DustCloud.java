import processing.core.PImage;

import java.util.List;

public class DustCloud extends Animated {


    public DustCloud(Point position,
                     List<PImage> images,
                     int actionPeriod, int animationPeriod) {
        this.setPosition(position);
        this.setImages(images);
        this.setImageIndex(0);
        this.setActionPeriod(actionPeriod);
        this.setAnimationPeriod(animationPeriod);
    }




    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        scheduler.unscheduleAllEvents(this);
        world.removeEntity(this);
    }




}
