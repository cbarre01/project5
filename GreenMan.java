import processing.core.PImage;

import java.util.List;

public class GreenMan extends Entity {


    private static final String CONTROLLED_KEY = "controlledMiner";
    private int score = 0;
    private int hp = 5;
    private int powerUpRemaining = 2;

    public GreenMan(String id, Point position,
                    List<PImage> images) {
        this.setPosition(position);
        this.setImages(images);
        this.setImageIndex(0);
    }



    public boolean moveMC(WorldModel world, Point translate)
    {
        //System.out.println("Score: " + score + ", HP: " + hp);
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
                if (powerUpRemaining > 0)
                {
                    powerUpRemaining --;
                }
                return true;
            }
            if (world.getOccupant(newPos).get() instanceof PowerUp)
            {
                setPosition(newPos);
                world.moveEntity(this, newPos);
                world.removeEntity(world.getOccupant(newPos).get());
                world.setPowerState(1);
                powerUpRemaining = 2;
                System.out.println("Powered up");
                return true;

            }

            if (world.getOccupant(newPos).get() instanceof Gas) {
                setPosition(newPos);
                world.moveEntity(this, newPos);
                world.removeEntity(world.getOccupant(newPos).get());
                this.reduceHp();
            }

            return false;

        }
        setPosition(newPos);
        return true;
    }

    public static GreenMan createGreenMan(String id,
                                          Point position,
                                          List<PImage> images) {
        return new GreenMan(id, position, images);
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

    public int getPowerUpRemaining() {
        return powerUpRemaining;
    }


}


