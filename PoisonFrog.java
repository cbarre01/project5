import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;



public class PoisonFrog extends Moving{

    private PathingStrategy pathing = new SingleStepPathingStrategy();


    private int resourceLimit;
    private int resourceCount;

    public PoisonFrog(String id, Point position,
                     List<PImage> images,
                     int actionPeriod, int animationPeriod) {
        this.setId(id);
        this.setPosition(position);
        this.setImages(images);
        this.setImageIndex(0);
        this.setActionPeriod(actionPeriod);
        this.setAnimationPeriod(animationPeriod);

        System.out.println("New Poison Frog hopping at " + this.getPosition());

    }
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(getPosition(), MinerFull.class);

        if (!target.isPresent())
        {
            target = world.findNearest(getPosition(), MinerFull.class);
        }
        if (!target.isPresent() ||
                !moveTo(world, target.get(), scheduler) ||
                        !eatMover(world, target.get(), scheduler))
        {
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    getActionPeriod());
        }
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
                if (occupant.isPresent())
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
    public boolean eatMover(WorldModel world, Entity target, EventScheduler scheduler)
    {
        if (adjacent(getPosition(), target.getPosition()))
        {
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            return true;
        }
        return false;
    }

    public static PoisonFrog createPoisonFrog( String id, Point position,
                                         List<PImage> images, int actionPeriod, int animationPeriod)
    {
        return  new PoisonFrog(id, position, images, actionPeriod, animationPeriod);
    }

}
