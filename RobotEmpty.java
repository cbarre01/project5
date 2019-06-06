import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class RobotEmpty extends Moving {


    private static final String EVIL_KEY = "infectedMiner";
    private int resourceLimit;
    private int resourceCount;
    //private PathingStrategy pathing = new SingleStepPathingStrategy();
    private PathingStrategy pathing = new AStarPathingStrategy();

    public RobotEmpty(String id, Point position,
                      List<PImage> images, int resourceLimit, int resourceCount,
                      int actionPeriod, int animationPeriod) {
        this.setId(id);
        this.setPosition(position);
        this.setImages(images);
        this.setImageIndex(0);
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.setActionPeriod(actionPeriod);
        this.setAnimationPeriod(animationPeriod);
    }



    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> notFullTarget = world.findNearest(getPosition(),
                Coin.class);
        if (!notFullTarget.isPresent() ||
                !moveTo(world, notFullTarget.get(), scheduler) ||
                !transformNotFull(world, scheduler, imageStore) ||
                !transformInfected(world, scheduler, imageStore)){
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    getActionPeriod());
        }
    }


    public boolean moveTo(WorldModel world,
                          Entity target, EventScheduler scheduler) {

        if (adjacent(getPosition(), target.getPosition())) {
            resourceCount += 1;
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

    private boolean transformNotFull(WorldModel world,
                                     EventScheduler scheduler, ImageStore imageStore) {
        if (resourceCount >= resourceLimit) {
            RobotFull miner = createRobotFull(getId(), resourceLimit,
                    getPosition(), getActionPeriod(), getAnimationPeriod(),
                    getImages());

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    private boolean transformInfected(WorldModel world,
                                      EventScheduler scheduler, ImageStore imageStore) {
        if (this.adjacentToAny(world.getGasLocs())) {
            EvilRobot miner = EvilRobot.createEvilRobot(getId(), resourceLimit,
                    getPosition(), getActionPeriod(), getAnimationPeriod(),
                    imageStore.getImageList(EVIL_KEY));

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            miner.scheduleActions(scheduler, world, imageStore);
            return true;



        }
        return false;
    }

    public Point nextPosition(WorldModel world,
                              Point destPos) {

        Predicate<Point> canPassThrough = new Predicate<Point>()
        {
            public boolean test(Point p)
            {
                Optional<Entity> occupant = world.getOccupant(p);
                if (occupant.isPresent())
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
        return newPos;
    }


    private RobotFull createRobotFull(String id, int resourceLimit,
                                      Point position, int actionPeriod, int animationPeriod,
                                      List<PImage> images) {
        return new RobotFull(id, position, images,
                resourceLimit, actionPeriod, animationPeriod);
    }



    public static RobotEmpty createRobotEmpty(String id, int resourceLimit,
                                              Point position, int actionPeriod, int animationPeriod,
                                              List<PImage> images)
    {
        return new RobotEmpty(id, position, images,
                resourceLimit, 0, actionPeriod, animationPeriod);
    }


}


