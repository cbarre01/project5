
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class RunAwayPathingStrategy2
        implements PathingStrategy
{


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        Point tryMoveLeft = new Point(start.getX() -1 , start.getY());
        Point tryMoveRight = new Point(start.getX() +1 , start.getY());
        Point tryMoveDown = new Point(start.getX() , start.getY() + 1);
        Point tryMoveUp = new Point(start.getX(), start.getY() - 1);

        List<Point> neighbors = potentialNeighbors.apply(start)
                .filter(canPassThrough)
                .collect(Collectors.toList());
        int totaldistance = 0;
                Point pointReturned = start;
        for (Point p : neighbors)
        {
            int curDist = Math.abs(p.getX() - end.getX()) + Math.abs(p.getY() - end.getY());
            if (curDist > totaldistance)
            {
                totaldistance = curDist;
                pointReturned = p;
            }
        }
        List<Point> returnList = new LinkedList<Point>();
        returnList.add(pointReturned);
        return returnList;

    }

    public static int hdist(Point p, Point end)
    {
        return Math.abs(end.getX() - p.getX()) + Math.abs(end.getY() - p.getY());
    }

    public boolean containsNode(List<Node> nodeList, Node node)
    {
        for (Node cur: nodeList)
        {
            if (cur.samePoint(node))
            {
                return true;
            }
        }
        return false;
    }

    public Point getOppositeDirection(Point cur, Point prior)
    {
        Point translation = new Point(cur.getX() - prior.getX(), cur.getY() - prior.getY());
        int translateX = translation.getX();
        int translateY = translation.getY();
        int prevY = 0-translateY;
        int prevX = 0-translateX;
        return new Point(cur.getX() + translateX, cur.getY() + translateY);

    }
}
