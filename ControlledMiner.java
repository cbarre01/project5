import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ControlledMiner extends Entity {


    private static final String CONTROLLED_KEY = "controlledMiner";
    //private PathingStrategy pathing = new SingleStepPathingStrategy();
    private PathingStrategy pathing = new AStarPathingStrategy();
    private int score = 0;
    private int hp = 5;

    public ControlledMiner(String id, Point position,
                           List<PImage> images) {
        this.setPosition(position);
        this.setImages(images);
        this.setImageIndex(0);
    }



    public boolean moveMC(WorldModel world, Point translate)
    {
        System.out.println("Score: " + score + ", HP: " + hp);
        Point curPos = getPosition();
        Point newPos = new Point(curPos.getX() + translate.getX(), curPos.getY() + translate.getY());
        if (world.isOccupied(newPos))
        {
            if (world.getOccupant(newPos).get() instanceof Ore)
            {
                score++;
                setPosition(newPos);
                world.moveEntity(this, newPos);
                world.removeEntity(world.getOccupant(newPos).get());
                return true;
            }
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

    public void reduceHp()
    {
        hp--;
    }

    public void setHp(int hpIn)
    {
        hp = hpIn;
    }

    public int getHp()
    {
        return hp;
    }

    public int getScore(){ return score;}

}


