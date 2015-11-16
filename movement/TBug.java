/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package General.movement;

import General.util.Common;
import General.util.Geom;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/**
 *
 * @author alexhuleatt
 */
public class TBug {

    final MapLocation start,goal;
    final RobotController rc;
    MapLocation me;
    MapLocation lastRet;
    MapLocation closest;
    int dir;

    boolean reverse;

    boolean firstMove;

    /**
     * At this point, the agent has encountered an obstacle directly in its path
     *
     * @param rc
     * @param start
     * @param goal
     * @throws java.lang.Exception
     */
    public TBug(RobotController rc, MapLocation goal) {
        this.rc = rc;
        this.me = rc.getLocation();
        this.start = me;
        this.goal = goal;
        this.closest = start;
        this.reverse = initReverse(goal);
        this.firstMove = true;
        this.dir = Common.dirToInt(me.directionTo(goal));
    }

    public boolean done() {
        return Geom.locOnLine(start, goal, me)
                && me.distanceSquaredTo(goal) < closest.distanceSquaredTo(goal);
    }

    public MapLocation nextMove() throws Exception {

        //the initial move is important.
        if (firstMove) {
            firstMove = false;
            MapLocation t_clos = null;
            int minDis = Integer.MAX_VALUE;
            for (int i = 0; i <= 7; i++) {
                MapLocation t = me.add(Common.directions[(dir + i + 8) % 8]);
                int t_dis = t.distanceSquaredTo(goal);
                if (!Common.isObstacle(rc, t) && t_dis < minDis) {
                    t_clos = t;
                    minDis = t_dis;
                }
            }
            if (t_clos == null) {
                return null;
            }
            return t_clos;
        }

        MapLocation n = trace();

        if (n != null) {
            dir = Common.dirToInt(me.directionTo(n));
            return n;
        }

        return null;
    }

    private MapLocation trace() throws Exception {
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
            if ((Common.isObstacle(rc, dirs[(d + sd + 8) % 8]) && dis < mindis)) {
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



}
