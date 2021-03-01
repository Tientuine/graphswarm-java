/**
 * GraphSwarm: Solution.java
 *
 * Copyright (c) 2010 M Johnson, S Khanal, S Sampath.
 * Created: Apr 18, 2010 9:49:51 PM
 */
package edu.marist.csmath.isag.swarm;

import java.util.Arrays;

/**
 * Inner class to abstract the notion of solution as a position in n-dimensional
 * space.
 * 
 * @author M Johnson, S Khanal, S Sampath
 */
class Solution
{
	/**
	 * @param n
	 */
	Solution(int n)
	{
		this.allocate(n);
		this.initialize();
	}

	/**
	 * @param n
	 */
	protected void allocate(int n)
	{
		this.position = new double[n];
	}

	@Override
	protected Object clone()
	{
		try
			{
				Solution copy = (Solution) super.clone();
				copy.position = Arrays.copyOf(this.position, this.position.length);
				return copy;
			}
		catch (CloneNotSupportedException exc)
			{
				return null;
			}
	}

	/**
	 * 
	 */
	protected void initialize()
	{
		for (int i = 0; i < this.position.length; ++i)
			this.position[i] = 0;
		this.fitness = Double.POSITIVE_INFINITY;
	}

	/** Printable string */
	@Override
	public String toString()
	{
		String str = "(" + ((float) this.position[0]);
		for (int i = 1; i < this.position.length; i++)
			str += "," + ((float) this.position[i]);
		str += ")";
		return str;
	}

	double[]	position;
	double	 fitness;
}
