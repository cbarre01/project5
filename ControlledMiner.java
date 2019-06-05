import processing.core.PImage;

import java.util.List;

public class ControlledMiner extends Moving {


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
            if (world.getOccupant(newPos).get() instanceof Coin)
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

    @Override
    protected Point nextPosition(WorldModel world, Point destPos) {
        return null;
    }

    @Override
    protected boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        return false;
    }

    @Override
    protected void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

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


