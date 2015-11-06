package General.Movement.Planning;

import battlecode.common.Direction;
import java.util.Arrays;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;
import team016.Movement.Mover;

public class PathMove extends Mover{

    private int currentNode;
    public boolean finished;

    public final SimplePather sp;
    public MapLocation[] path;

    public PathMove(RobotController rc, MapLocation goal) {
        super(rc,goal);
        sp = new SimplePather(rc, goal);
        finished = false;
        currentNode = 0;
    }

    /**
     * Attempts to find the path, but hopefully faster than refindPath Starts as
     * far along the path as it thinks it might be able to.
     *
     * @param prev
     */
    public void findPath(int prev) {
        try {
            MapLocation myLoc = rc.getLocation();
            boolean good = false;
            for (int i = prev; i < path.length; i++) {
                MapLocation temp = path[i];
                if (!scan(myLoc, temp)) {
                    currentNode = i;
                    good = true;
                }
            }
            if (!good) {
                refindPath();
            }
        } catch (GameActionException e) {
            e.printStackTrace();
        }
    }

    /**
     * This will tend not to work if it was given a path directly generated by
     * the PathBeaver You should decompress those by calling
     * SimplePather.decompress
     *
     * @throws GameActionException
     */
    public void refindPath() throws GameActionException {
        MapLocation myLoc = rc.getLocation();
        for (int i = 0; i < path.length; i++) {
            MapLocation temp = path[i];
            if (!scan(myLoc, temp)) {
                currentNode = i;
                return;
            }
        }
        currentNode = 0;
    }

    /**
     * Be careful about giving this paths generated by the PathBeaver If you are
     * not careful, units will not find the path Call SimplePather.decompress to
     * maximize chances of finding path
     *
     * @param points
     * @throws GameActionException
     */
    public void givePath(MapLocation[] points) throws GameActionException {
        this.path = points;
        refindPath();
    }

    /**
     * Bresenham's Line algorithm
     *
     * @param p1
     * @param p2
     * @return True if the two given locations have nothing impassable between
     * them, false otherwise
     * @throws GameActionException
     */
    public boolean scan(MapLocation p1, MapLocation p2) throws GameActionException {
        if (p1.isAdjacentTo(p2)) {
            return false;
        }
        int x1 = p1.x;
        int y1 = p1.y;
        int x2 = p2.x;
        int y2 = p2.y;
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;
        while (true) {
            int e2 = err << 1;
            if (e2 > -dy) {
                err = err - dy;
                x1 = x1 + sx;
            }
            if (x1 == x2 && y1 == y2) {
                break;
            }
            if (impassable(new MapLocation(x1, y1))) {
                return true;
            }
            if (e2 < dx) {
                err = err + dx;
                y1 = y1 + sy;
            }
            if (x1 == x2 && y1 == y2) {
                break;
            }
            if (impassable(new MapLocation(x1, y1))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to move the unit along the path, does nothing if the path is
     * null Fails to work if you cannot find the path
     *
     * @return 
     * @throws GameActionException
     */
    @Override
    public MapLocation nextMove() throws GameActionException {
        if (path == null) {
            return null;
        }
		//System.out.println(Arrays.toString(path) + " " + currentNode);
        //rc.setIndicatorString(0, "");
        MapLocation myLoc = rc.getLocation();

        if (currentNode >= path.length) {
            return tryMove(rc, myLoc.directionTo(path[path.length - 1]));
        }
        MapLocation ml = myLoc.add(myLoc.directionTo(path[currentNode]));
        if (impassable(ml)) {
            path = sp.pathfind(myLoc, goal);
            findPath(currentNode);
        }
        if (myLoc.equals(path[currentNode])) { //update node
            currentNode++;
        }
        if (currentNode < path.length) {
            return tryMove(rc, myLoc.directionTo(path[currentNode]));
        }
        return null;
    }



}
