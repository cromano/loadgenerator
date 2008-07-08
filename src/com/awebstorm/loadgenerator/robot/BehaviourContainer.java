package com.awebstorm.loadgenerator.robot;

import org.jbehave.core.behaviour.Behaviours;

/**
 * Returns all the behaviour classes to be run.
 * @author Cromano
 *
 */
public class BehaviourContainer implements Behaviours {

	/**
	 * Get the behaviours.
	 */
	@SuppressWarnings("unchecked")
	public Class[] getBehaviours() {
		return new Class[] { (new HTMLRobotBehaviour()).getClass() };
	}

}
