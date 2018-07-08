package tma.o2.robot;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.util.concurrent.ThreadLocalRandom;

import robocode.BulletHitEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.MessageEvent;
import robocode.RobotDeathEvent;
import robocode.tma.TTeamMemberRobot;
import tma.o2.action.ActionPool;
import tma.o2.misc.RobotColors;
import tma.o2.misc.Strategy.FIRE_STRATEGY;
import tma.o2.misc.Strategy.MOVE_STRATEGY;
import tma.o2.misc.Target;

public class Dummy extends TTeamMemberRobot {


	public void run() {
		doNothing();
	}

	
}
