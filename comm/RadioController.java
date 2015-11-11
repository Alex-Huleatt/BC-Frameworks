/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package General.comm;

import General.util.Rand;
import battlecode.common.Clock;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;

/**
 * Substantially more expensive than other possible controller implementations.
 * Very simple, super lazy. 
 * @author alexhuleatt
 */
public class RadioController {

    private final RobotController rc;

    public RadioController(RobotController rc) {
        this.rc = rc;
    }

    public Message read(String channel) throws Exception {
        int chan = getChannel(channel);
        int raw = rc.readBroadcast(chan);
        if (!isSigned(raw, Clock.getRoundNum())) {
            return Message.UNSIGNED;
        }
        int unsigned = unsign(raw, Clock.getRoundNum());
        Message[] allM = Message.values();
        if (unsigned < 0 || unsigned > allM.length) {
            return Message.ERROR;
        }
        return allM[unsigned];
    }

    public void write(String channel, Message m) throws Exception {
        int chan = getChannel(channel);
        rc.broadcast(chan, signMessage(m.ordinal(), Clock.getRoundNum()));
    }

    private int getChannel(String channel) {
        byte[] barr = channel.getBytes();
        long s = 0;
        for (int i = 0; i < barr.length; i++) { //hash the string.
            s = s ^ Rand.xorshiftstar(barr[i]);
        }
        return (int) (Math.abs(s) % GameConstants.BROADCAST_MAX_CHANNELS);
    }

    private static int signMessage(int message, int round_num) {
        return message | getMask(round_num);
    }

    private static boolean isSigned(int message, int round_num) {
        int mask = getMask(round_num);
        return (message & mask) == mask;
    }

    private static int unsign(int message, int round_num) {
        return message ^ getMask(round_num);
    }

    public static int getMask(int round_num) {
        return ((int) Rand.xorshiftstar(round_num+1)) << 24;
    }

}