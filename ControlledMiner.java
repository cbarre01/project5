import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ControlledMiner extends Entity {


    private static final String CONTROLLED_KEY = "controlledMiner";
    //private PathingStrategy pathing = new SingleStepPathingStrategy();
    private PathingStrategy pathing = new AStarPathingStrategy();

    public ControlledMiner(String id, Point position,
                           List<PImage> images) {
        this.setPosition(position);
        this.setImages(images);
        this.setImageIndex(0);
    }



    public boolean moveRight(WorldModel world)
    {
        Point curPos = getPosition();
        Point newPos = new Point(curPos.getX() + 1, curPos.getY());
        if (world.isOccupied(newPos))
        {
            return false;
        }
        setPosition(newPos);
        return true;
    }

    public boolean moveLeft(WorldModel world)
    {
        Point curPos = getPosition();
        Point newPos = new Point(curPos.getX() - 1, curPos.getY());
        if (world.isOccupied(newPos))
        {
            return false;
        }
        setPosition(newPos);
        return true;
    }

    public boolean moveUp(WorldModel world)
    {
        Point curPos = getPosition();
        Point newPos = new Point(curPos.getX(), curPos.getY() - 1);
        if (world.isOccupied(newPos))
        {
            return false;
        }
        setPosition(newPos);
        return true;
    }

    public boolean moveDown(WorldModel world)
    {
        Point curPos = getPosition();
        Point newPos = new Point(curPos.getX(), curPos.getY() + 1);
        if (world.isOccupied(newPos))
        {
            return false;
        }
        setPosition(newPos);
        return true;
    }

    public static ControlledMiner createControlledMiner(String id,
                                                        Point position,
                                                        List<PImage> images) {
        return new ControlledMiner(id, position, images);
    }

}


