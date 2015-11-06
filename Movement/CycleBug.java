/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team016.Movement;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team016.Const;

/**
 *
 * @author alexhuleatt
 */
public class CycleBug extends Mover{
    
    Direction last_dir;
    public CycleBug(RobotController rc, MapLocation goal) {
        super(rc, goal);
        last_dir = me.directionTo(goal);
    }
    
    @Override
    public MapLocation nextMove() throws Exception {
        int dir_temp;
        int dir_int = Const.directionToInt(last_dir);
        for (int i = 0; i<=5; i++) {
            dir_temp = (dir_int+i)%8;
            if (!Const.isObstacle(rc, dir_temp)) {
                return me.add(directions[dir_temp]);
            }
            dir_temp = (dir_int+(8-i))%8;
            if (!Const.isObstacle(rc, dir_temp)) {
                return me.add(directions[dir_temp]);
            }
        }
        return null;
    }
}
