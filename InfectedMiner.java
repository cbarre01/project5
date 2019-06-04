import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class InfectedMiner extends Moving {

    private int resourceLimit;
    private int resourceCount;
    //private PathingStrategy pathing = new SingleStepPathingStrategy();
    private PathingStrategy pathing = new AStarPathingStrategy();

    public InfectedMiner(String id, Point position,
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

        System.out.println("New infectedMiner at " + this.getPosition());
    }



    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> infectedTarget = world.findNearest(getPosition(),
                MinerFull.class);
        if (!infectedTarget.isPresent())
        {
            infectedTarget = world.findNearest(getPosition(), MinerNotFull.class);
        }


        if (!infectedTarget.isPresent() ||
                !moveTo(world, infectedTarget.get(), scheduler) ||
                !eatMiner(world, infectedTarget.get(), scheduler)){
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    getActionPeriod());
        }
        // System.out.println("MinerNF: " + getPosition());
        //System.out.println("MinerInf" + getPosition());
    }


    public boolean moveTo(WorldModel world,
                          Entity target, EventScheduler scheduler) {

        Point newPos = target.getPosition();
        if (adjacent(getPosition(), target.getPosition())) {
            world.removeEntity(target);
            scheduler.unscheduleAllEvents(target);
            world.moveEntity(this, newPos);
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
        //System.out.println("enter nextPos, position: " + getPosition() + ",dest: " + destPos);

        Predicate<Point> canPassThrough = new Predicate<Point>()
        {
            public boolean test(Point p)
            {
                Optional<Entity> occupant = world.getOccupant(p);
                if (occupant.isPresent() && !(occupant.get() instanceof MinerFull))
                {
                    return false;
                }
                return true;

            }
        };
        //System.out.println("enter pathing");
        List<Point> path = pathing.computePath(getPosition(),
                destPos,
                canPassThrough,
                (p1, p2) -> world.neighbors(p1, p2),
                PathingStrategy.CARDINAL_NEIGHBORS);

        //System.out.println("path complete");
        Point newPos = getPosition();

        if (path.size() > 0)
        {
            newPos = path.get(0);
        }
        //System.out.println(" Exit Next pos: " + newPos);
        return newPos;
    }

    public boolean eatMiner(WorldModel world, Entity target, EventScheduler scheduler)
    {
        if (adjacent(getPosition(), target.getPosition()))
        {
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            //world.addEntity(miner);
            //miner.scheduleActions(scheduler, world, imageStore);
            return true;
        }
        return false;
    }

    public static InfectedMiner createInfectedMiner(String id, int resourceLimit,
                                                    Point position, int actionPeriod, int animationPeriod,
                                                    List<PImage> images)
    {
        return new InfectedMiner(id, position, images,
                resourceLimit, 0, actionPeriod, animationPeriod);
    }

}

