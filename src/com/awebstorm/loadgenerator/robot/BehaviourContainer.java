package com.awebstorm.loadgenerator.robot;

import org.jbehave.core.behaviour.Behaviours;

public class BehaviourContainer implements Behaviours {

	public Class[] getBehaviours() {
		return new Class[] { (new HTMLRobotBehaviour()).getClass() };
	}

}
