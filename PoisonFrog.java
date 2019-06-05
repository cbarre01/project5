import processing.core.PImage;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;



public class PoisonFrog extends Moving{

    private PathingStrategy pathing = new SingleStepPathingStrategy();


    private int resourceLimit;
    private int resourceCount;
    private static final int GAS_ID = 1;
    private static final String GAS_KEY = "gas";



    public PoisonFrog(String id, Point position,
                     List<PImage> images,
                     int actionPeriod, int animationPeriod) {
        this.setId(id);
        this.setPosition(position);
        this.setImages(images);
        this.setImageIndex(0);
        this.setActionPeriod(actionPeriod);
        this.setAnimationPeriod(animationPeriod);


    }
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(getPosition(), InfectedMiner.class);

//        if (!target.isPresent())
//        {
//            target = world.findNearest(getPosition(), MinerFull.class);
//        }
        if (!target.isPresent() || !moveTo(world, target.get(), scheduler))
        {
            scheduler.scheduleEvent(this,
                    createActivityAction(world, imageStore),
                    getActionPeriod());
        }
        if(target.isPresent())
        {
            eatMover(world, target.get(), scheduler, imageStore);
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

        Predicate<Point> canPassThrough = new Predicate<Point>()
        {
            public boolean test(Point p)
            {
                Optional<Entity> occupant = world.getOccupant(p);
                if (occupant.isPresent() && !(occupant.get() instanceof Gas))
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
    public boolean eatMover(WorldModel world, Entity target, EventScheduler scheduler, ImageStore imageStore)
    {
        System.out.println("entering eatMover");
        if (adjacent(getPosition(), target.getPosition()))
        {
            System.out.println("frogger found them");
            List<Point> allAdjacents = world.allAdjacents(getPosition());
//            List<Point> newAdjecents = new ArrayList<>();
//            for (int i = 0; i < 4; i++)
//            {
//                Collections.shuffle(allAdjacents);
//                newAdjecents.add(allAdjacents.get(0));
//            }
//            Collections.shuffle(allAdjacents);
            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);
            for (int i = 0; i < 9; i++)
            {
                System.out.println("eating em");
                world.addEntity(Gas.createGas(GAS_ID + " " + String.valueOf(i),
                        allAdjacents.get(i),
                        imageStore.getImageList(GAS_KEY)));
            }
            System.out.println("waht");

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
