package General.moods;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

/**
 * The Mood class is effectively a Turing-machine.
 * Each Mood is a state that uses the robot's environment to perform an action
 * and transfer states. 
 * @author alexhuleatt
 */
public abstract class Mood {

    public final RobotController rc;
    public MapLocation me;

    public final MapLocation ehq;
    public final MapLocation hq;
    public final Team team;

    public Mood(RobotController rc) {
        this.rc = rc;
        this.hq = rc.senseHQLocation();
        this.ehq = rc.senseEnemyHQLocation();
        this.team = rc.getTeam();
    }

    /**
     * Updates all the variables that the robot will most likely need. current
     * location, nearby units, etc. So that run and swing can see them without
     * having to calculate them.
     */
    void update() {
        me = rc.getLocation();
    }

    /**
     * Transitions the robot to a different mood.
     *
     * @return The new mood, null or the same type for no swing.
     */
    Mood swing() {
        return null;
    }

    /**
     * This function is used for a mood swing to transfer over expensively
     * calculated information. e.g. Paths, any queues, etc.
     *
     * @param swung The new mood
     */
    void transfer(Mood swung) {
    }

    void init() { }
    
    /**
     * This function actually changes the robot's state.
     * Does not make sense to have a default, really.
     */
    abstract void act();

}
