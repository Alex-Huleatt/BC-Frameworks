/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package General.movement;

import General.movement.AStar;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import team016.Movement.Planning.PathMove;
import team016.Movement.Planning.SimplePather;

/**
 *
 * @author alexhuleatt
 */
public class MovementController {

    public final RobotController rc;
    private AStar astr;
    private HashSet<MapLocation> obs;
    private MapLocation last_goal;
    private TBug bugger;

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

    public boolean bug(MapLocation goal) throws Exception {
        if (goal != last_goal) {
            last_goal = goal;
            bugger = new TBug(rc, goal);
        }
        MapLocation me = rc.getLocation();
        Direction d = me.directionTo(bugger.nextMove());
        if (rc.canMove(d)) {
            rc.move(d);
            return true;
        }
        return false;
    }

}
