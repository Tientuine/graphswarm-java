/**
 * GraphSwarm: CompositeObjective.java
 *
 * Copyright (c) 2010 M Johnson, S Khanal, S Sampath.
 * Created: Apr 20, 2010 9:32:22 PM
 */
package edu.marist.csmath.isag.strategy;

import edu.marist.csmath.isag.swarm.Swarmable;

/**
 * Provides a convenient means of composing two distinct objective functions.
 * This is useful for testing purposes, but it may be inefficient. Some
 * objectives may have overlapping computations, in which case an specialized
 * implementation can avoid repeating the same work.
 * 
 * @author M Johnson, S Khanal, S Sampath
 */
public class CompositeObjective implements Objective
{
	/** Indicates additive composition (i.e., avg[f,g]). */
	public static final int	ADDITIVE	     = 1;

	/** Indicates multiplicative composition (i.e., sqrt[f*g]). */
	public static final int	MULTIPLICATIVE	= 2;

	/**
	 * Create an objective that combines the two two specified objectives.
	 * 
	 * @note The default combination is {@link #ADDITIVE}.
	 * @param f
	 *          an objective function
	 * @param g
	 *          an objective function
	 */
	public CompositeObjective(Objective f, Objective g)
	{
		this.operation = ADDITIVE;
		this.f = f;
		this.g = g;
	}

	/**
	 * Create an objective that combines the two two specified objectives.
	 * 
	 * @param f
	 *          an objective function
	 * @param g
	 *          an objective function
	 * @param optype
	 *          the desired method of composing the objectives
	 */
	public CompositeObjective(Objective f, Objective g, int optype)
	{
		this.operation = optype;
		this.f = f;
		this.g = g;
	}

	/* (non-Javadoc)
	 * @see edu.marist.csmath.isag.strategy.Objective#computeFitness(edu.marist.csmath.isag.swarm.Swarmable)
	 */
	@Override
	public double computeFitness(Swarmable agent)
	{
		double score = 0;
		switch (this.operation)
		{
		default: /* fall-through! */
		case ADDITIVE:
			score = (f.computeFitness(agent) + g.computeFitness(agent)) / 2;
		case MULTIPLICATIVE:
			score = Math.sqrt(f.computeFitness(agent) * g.computeFitness(agent));

		}
		return score;
	}

	private final int	      operation;
	private final Objective	f;
	private final Objective	g;
}
