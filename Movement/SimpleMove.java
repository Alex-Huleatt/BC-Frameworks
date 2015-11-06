/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package General.Movement;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;
import team016.Const;

/**
 *
 * @author alexhuleatt
 */
public class SimpleMove extends Mover {

    public SimpleMove(RobotController rc, MapLocation goal) {
        super(rc, goal);
    }

    @Override
    public MapLocation nextMove() throws Exception {
        Direction d = me.directionTo(goal);
        MapLocation next = me.add(d);
        if (rc.isActive()) {
            if (!Const.isObstacle(rc, next)) {
                return next;
            }
            Team mine = rc.senseMine(next);
            if (mine == team.opponent() || mine == Team.NEUTRAL) {
                return next;
            }
        }
        return null;
    }

}
