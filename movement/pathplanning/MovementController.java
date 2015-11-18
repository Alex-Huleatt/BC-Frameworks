/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package General.movement.pathplanning;

import General.util.Common;
import General.util.Geom;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import battlecode.common.Team;

/**
 *
 * @author alexhuleatt
 */
public class MovementController {

    public final RobotController rc;
    private AStar astr;
    private HashSet<MapLocation> obs;

    private MapLocation start;
    private MapLocation end;
    private boolean reverse;
    private int dir;
    private boolean bug;
    private int closest;

    private MapLocation cycle_loc;
    private boolean cycle_check;

    private MapLocation me;

    public MovementController(RobotController rc) {
        this.rc = rc;
    }

    public void logObstacle(MapLocation ob) {
        if (obs == null) {
            obs = new HashSet<MapLocation>();
        }
        obs.add(ob);
    }

    public void logObstacles(MapLocation[] ob_arr) {
        getObs().addAll(Arrays.asList(ob_arr));
    }

    public HashSet<MapLocation> getObs() {
        if (obs == null) {
            obs = new HashSet<MapLocation>();
        }
        return obs;
    }

    /**
     * This will likely take a while. This is dependent on the distance between
     * start and end, and the topology of the map.
     *
     * @param start
     * @param end
     * @return
     */
    public MapLocation[] getPath(MapLocation start, MapLocation end) {
        if (astr == null) {
            astr = new AStar(getObs());
        }
        return astr.pathfind(start, end);
    }

    public void bug(MapLocation goal) throws Exception {
        me = rc.getLocation();
        rc.setIndicatorString(0, "moveTowards " + goal);
        if (goal == null) {
            return;
        }
        if (start == null || end == null || !goal.equals(end)) {
            // setup
            start = rc.getLocation();
            end = goal;
            bug = false;
            closest = Integer.MAX_VALUE;
            cycle_check = false;
            cycle_loc = null;

        }

        if (Math.random() < .05) {
            cycle_check = false;
            cycle_loc = me;
        }

        int disToGoal = me.distanceSquaredTo(goal);
        Direction dirToGoal = me.directionTo(goal);

        if (disToGoal == 0) {
            return;
        }

        if (bug && Geom.locOnLine(start, goal, me)
                && disToGoal < closest && !isObs(me.add(dirToGoal))) {
            // we can stop bugging.
            bug = false;
            dir = Common.dirToInt(dirToGoal);
            move(dirToGoal);
            return;
        }

        if (!bug && !isObs(me.add(dirToGoal))
                && Geom.locOnLine(start, goal, me)) {
            // we can move straight on the line
            move(dirToGoal);
            return;
        }

        if (bug && cycle_loc != null && me.distanceSquaredTo(cycle_loc) == 0) {
            if (cycle_check) {
                //CYCLING
                start = null;
                bug(goal);
                return;
            } else {
                cycle_check = true;
            }
        }

        if (!bug) { //the initial transition. 2Spooky4me.
            initBug();
            return;
        }

        MapLocation nextMove = trace();
        if (nextMove != null) {
            move(me.directionTo(nextMove));
        } else {
            dir = (dir + 4) % 8; //let us turn around
            nextMove = trace();
            if (nextMove != null) {
                move(me.directionTo(nextMove));
            } else {
                //failure entirely. This should only happen with changing env.
                //We'll restart bugging from the start.
                start = null;
                bug(goal); //this is really hacky and dangerous.
                return;
            }
        }
        simpleMove(goal);
    }

    private MapLocation trace() throws Exception {
        rc.setIndicatorString(0, "Tracing.");
        MapLocation temp;
        int mindir = -1;
        int mindis = Integer.MAX_VALUE;

        int sd = (reverse) ? 1 : -1;
        Direction[] dirs = Common.directions;
        for (int i = -2; i <= 2; i++) {
            int d = (dir + i + 8) % 8;
            if (Common.isObstacle(rc, Common.directions[d])) {
                continue;
            }
            temp = me.add(dirs[d]);
            int dis = temp.distanceSquaredTo(me.add(dirs[((dir + 2 * sd) + 8) % 8]));
            if ((isObs(me.add(dirs[(d + sd + 8) % 8])) && dis < mindis)) {
                mindir = d;
                mindis = dis;
            }
        }
        if (mindir == -1) {
            return null;
        } else {
            return me.add(Common.directions[mindir]);
        }
    }

    private boolean initReverse(MapLocation goal) {
        int offset = Math.min(8 - dir, dir);
        int dir_to_obs = Common.dirToInt(me.directionTo(goal));
        return ((dir_to_obs + offset) % 8) < 4;
    }

    public void move(Direction dir) throws Exception {
        if (rc.isCoreReady() && rc.canMove(dir)) {
            rc.move(dir);
            this.dir = Common.dirToInt(dir);
        } else {
            //System.out.println("rc failed to move in dir");
        }
    }

    public void move(int dir) throws Exception {
        move(Common.directions[dir]);
    }

    public boolean simpleMove(MapLocation goal) throws Exception {
        rc.setIndicatorString(0, "Simple move: " + goal);
        Direction d = me.directionTo(goal);
        int d_2_g = Common.dirToInt(d);
        MapLocation next;
        int td;
        for (int i = 0; i <= 4; i++) {
            td = (d_2_g + i) % 8;
            next = me.add(Common.directions[td]);
            if (!Common.isObstacle(rc, next)) {
                move(td);
                return true;
            }
            td = (d_2_g - i + 8) % 8;
            next = me.add(Common.directions[td]);
            if (!Common.isObstacle(rc, next)) {
                move(td);
                return true;
            }
        }
        return false;
    }

    public void initBug() throws Exception {
        //the initial move is important.
        bug = true;
        rc.setIndicatorString(0, "Initial.");
        MapLocation t_clos = null;
        int minDis = Integer.MAX_VALUE;
        for (int i = 0; i <= 7; i++) {
            MapLocation t = me.add(Common.directions[(dir + i + 8) % 8]);
            int t_dis = t.distanceSquaredTo(end);
            if (!isObs(t) && t_dis < minDis) {
                t_clos = t;
                minDis = t_dis;
            }
        }
        if (t_clos == null) {
            simpleMove(end);
            return;
        }
        move(me.directionTo(t_clos));
        reverse = initReverse(end);

        closest = me.distanceSquaredTo(end);
        cycle_loc = me;
        cycle_check = false;
    }

    public boolean isObs(MapLocation m) throws Exception {
        boolean pathable = rc.isPathable(rc.getType(), m);
        boolean hasUnit = rc.senseRobotAtLocation(m) != null;
        return !(pathable || (hasUnit && Math.random() > .9));
    }
}
