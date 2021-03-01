/**
 * GraphSwarm: ProblemSpace.java
 *
 * Copyright (c) 2010 M Johnson, S Khanal, S Sampath.
 * Created: Apr 17, 2010 5:37:21 PM
 */
package edu.marist.csmath.isag.strategy;

/**
 * @author M Johnson, S Khanal, S Sampath
 */
public interface ProblemSpace
{
	/**
	 * @return
	 */
	public int getDimension();

	/**
	 * @return
	 */
	public Objective getObjective();
}
