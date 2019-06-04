import java.util.*;

abstract class Moving extends Animated {

    protected boolean adjacent(Point p1, Point p2) {
        return (p1.getX() == p2.getX() && Math.abs(p1.getY() - p2.getY()) == 1) ||
                (p1.getY() == p2.getY() && Math.abs(p1.getX() - p2.getX()) == 1);
    }
    protected abstract boolean moveTo(WorldModel world,
                                      Entity target, EventScheduler scheduler);
    protected abstract Point nextPosition(WorldModel world,Point destPos);

    protected boolean adjacentToAny(ArrayList<Point> pointlist)
    {
        boolean adjacentTest = false;
        for (Point p2 : pointlist)
        {
            if ((getPosition().getX() == p2.getX() && Math.abs(getPosition().getY() - p2.getY()) == 1) ||
                    (getPosition().getY() == p2.getY() && Math.abs(getPosition().getX() - p2.getX()) == 1))
            {
                adjacentTest = true;

            }
        }
        return adjacentTest;
    }
}
