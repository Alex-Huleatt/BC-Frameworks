/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Movement;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TerrainTile;
import static team016.Movement.Planning.SimplePather.isStationary;

/**
 *
 * @author alexhuleatt
 */
public abstract class Mover {

    public final RobotController rc;
    public MapLocation me;
    public final MapLocation goal;
    public final Team team;
    public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST,
        Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH,
        Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

    public Mover(RobotController rc, MapLocation goal) {
        this.rc = rc;
        this.goal = goal;
        this.team = rc.getTeam();
    }

    public void step() {
        me = rc.getLocation();
    }

    /**
     * Before this func is called we have these guarantees: We are where we said
     * we should go last time, or we were just made. ??? ???
     *
     * @param goal
     * @return
     */
    public MapLocation nextMove() throws Exception {
        return null;

    }

    public MapLocation tryMove(RobotController rc, Direction d) {
        int offsetIndex = 0;
        int[] offsets = {0, 1, -1, 2, -2};
        int dirint = dirToInt(d);
        while (offsetIndex < 5
                && !rc.canMove(directions[(dirint + offsets[offsetIndex] + 8) % 8])) {
            offsetIndex++;
        }
        if (offsetIndex < 5 && rc.isActive()) {
            return me.add(directions[(dirint + offsets[offsetIndex] + 8) % 8]);
        }
        return null;
    }

    public static int dirToInt(Direction d) {
        switch (d) {
            case NORTH:
                return 0;
            case NORTH_EAST:
                return 1;
            case EAST:
                return 2;
            case SOUTH_EAST:
                return 3;
            case SOUTH:
                return 4;
            case SOUTH_WEST:
                return 5;
            case WEST:
                return 6;
            case NORTH_WEST:
                return 7;
            default:
                return -1;
        }
    }

    public boolean impassable(MapLocation m) {
        try {
            TerrainTile tt = rc.senseTerrainTile(m);
            if (tt == TerrainTile.VOID || tt == TerrainTile.OFF_MAP) {
                return true;
            }
            if (rc.canSenseSquare(m)) { //canSenseLocation(m)
                RobotInfo ri = null;
                            //~~~~~
                //ri = rc.senseRobotAtLocation(m);
                //if (ri != null && isStationary(ri.type)) return true;
                //~~~~~

                //$$$$$
                GameObject o = rc.senseObjectAtLocation(m);
                if (o instanceof Robot) {
                    ri = rc.senseRobotInfo((Robot) o);
                }

                if (ri != null && isStationary(ri.type)) {
                    return true;
                }
                //$$$$$
            }
            return false;
        } catch (GameActionException e) {
            return impassable(m);
        }
    }
        /**
     * 
     * @param rt
     * @return 
     */
    public static boolean isStationary(RobotType rt) {
        return (rt != null && rt != RobotType.SOLDIER);
    }

    
}
