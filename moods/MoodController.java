/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package General.moods;

import battlecode.common.RobotController;

/**
 *
 * @author alexhuleatt
 */
public class MoodController {
    public final RobotController rc;
    public Mood m;
    
    public MoodController(RobotController rc) {
        this.rc = rc;
    }
    
    public final void run() {
        m.update();
        Mood swung = m.swing();
        if (swung != null && m.getClass() != swung.getClass()) {
            m = swung;
            m.transfer(swung); //transfer over expensive data to the receiver.
        }
        m.act();
    }
}
