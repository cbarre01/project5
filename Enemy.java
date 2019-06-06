import processing.core.PImage;

import java.util.*;
import java.util.function.Predicate;

public class Enemy extends Moving {
    private static final String DUSTCLOUD_KEY = "quake";
    private static final int DUSTCLOUD_ACTION_PERIOD = 1100;
    private static final int DUSTCLOUD_ANIMATION_PERIOD = 100;
    //private PathingStrategy pathing = new SingleStepPathingStrategy();
    private PathingStrategy pathing = new AStarPathingStrategy();



    public Enemy(String id, Point position,
                 List<PImage> images,
                 int actionPeriod, int animationPeriod) {
        this.setPosition(position);
        this.setImages(images);
        this.setImageIndex(0);
        this.setActionPeriod(actionPeriod);
        this.setAnimationPeriod(animationPeriod);
    }


    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> blobTarget = world.findNearest(getPosition(), GreenMan.class);
        long nextPeriod = getActionPeriod();

        if (blobTarget.isPresent()) {
            Point tgtPos = blobTarget.get().getPosition();

            if (moveTo(world, blobTarget.get(), scheduler)) {
                Animated quake = createDustCloud(tgtPos,
                        imageStore.getImageList(DUSTCLOUD_KEY));

                world.addEntity(quake);
                nextPeriod += getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);
                ((GreenMan) blobTarget.get()).reduceHp();
            }
        }

        scheduler.scheduleEvent(this,
                createActivityAction(world, imageStore),
                nextPeriod);
        //System.out.println("Blob: " + getPosition());
    }

    public boolean moveTo(WorldModel world,
                          Entity target, EventScheduler scheduler) {
        if (adjacent(getPosition(), target.getPosition())) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    public Point nextPosition(WorldModel world,
                              Point destPos) {
        if (world.getPowerState() == 0)
        {
            pathing = new AStarPathingStrategy();
        }
        if (world.getPowerState() == 1)
        {
            pathing = new RunAwayPathingStrategy2();
        }

        Predicate<Point> canPassThrough = new Predicate<Point>()
        {
            public boolean test(Point p)
            {
                Optional<Entity> occupant = world.getOccupant(p);
                if (occupant.isPresent() && !(occupant.get() instanceof Coin))
                {
                    return false;
                }
                return true;

            }
        };

        List<Point> path = pathing.computePath(getPosition(),
                destPos,
                canPassThrough,
                (p1, p2) -> world.neighbors(p1, p2),
                PathingStrategy.CARDINAL_NEIGHBORS);

        Point newPos = getPosition();

        if (path.size() > 0)
        {
            newPos = path.get(0);
        }
        //System.out.println("Next pos: " + newPos);
        return newPos;
    }


    private DustCloud createDustCloud(Point position, List<PImage> images) {
        return new DustCloud(position, images,
                DUSTCLOUD_ACTION_PERIOD, DUSTCLOUD_ANIMATION_PERIOD);
    }








}
