/**
 * GraphSwarm: PointGraphLayoutProblem.java
 *
 * Copyright (c) 2010 M Johnson, S Khanal, S Sampath.
 * Created: Apr 17, 2010 6:41:57 PM
 */
package edu.marist.csmath.isag.strategy;

import java.util.Set;

import edu.marist.csmath.isag.swarm.Swarmable;

/**
 * This class represents the general problem of planar layout of graphs. It
 * contains various objective functions that may be useful in the spatial
 * arrangement of arbitrary point graphs.
 * 
 * @author M Johnson, S Khanal, S Sampath
 */
public class PlanarGraphLayoutProblem implements ProblemSpace
{
	public final static int	VIEWCENTER	     = 0;
	public final static int	EQUIDISTANT	     = 1;
	public final static int	CENTER	         = 2;
	public final static int	EQUICENTER	     = 3;
	public final static int	EQUIVIEW	       = 4;
	public final static int	EQUIVIEWCENTER	 = 5;
	public final static int	CENTERVIEWCENTER	= 6;
	public final static int	ADJACENT	       = 7;

	protected Objective	    objective;

	public PlanarGraphLayoutProblem(Set<Swarmable> swarms)
	{
		this.objective = new EquidistantObjective(swarms);
	}

	public PlanarGraphLayoutProblem(Set<Swarmable> swarms, int objType)
	{
		switch (objType)
		{
		default: /* fall-through! */
		case EQUIDISTANT:
			this.objective = new EquidistantObjective(swarms);
			break;
		case CENTER:
			this.objective = new CenterObjective(swarms);
			break;
		case VIEWCENTER:
			this.objective = new ViewCenterObjective();
			break;
		case EQUICENTER:
			this.objective = new CompositeObjective(new EquidistantObjective(swarms),
			    new CenterObjective(swarms));
			break;
		case EQUIVIEW:
			this.objective = new CompositeObjective(new EquidistantObjective(swarms),
			    new ViewCenterObjective());
			break;
		case EQUIVIEWCENTER:
			this.objective = new CompositeObjective(new CompositeObjective(
			    new EquidistantObjective(swarms), new ViewCenterObjective()),
			    new CenterObjective(swarms));
			break;
		case CENTERVIEWCENTER:
			this.objective = new CompositeObjective(new CompositeObjective(
			    new EquidistantObjective(swarms), new ViewCenterObjective()),
			    new CenterObjective(swarms), CompositeObjective.MULTIPLICATIVE);
			break;
		case ADJACENT:
			this.objective = new AdjacentObjective(swarms);
			break;
		}
	}

	/* (non-Javadoc)
	 * @see edu.marist.csmath.isag.strategy.ProblemSpace#getDimension()
	 */
	@Override
	public int getDimension()
	{
		return 2;
	}

	/* (non-Javadoc)
	 * @see edu.marist.csmath.isag.strategy.ProblemSpace#getObjective()
	 */
	@Override
	public Objective getObjective()
	{
		return this.objective;
	}

	public final class EquidistantObjective implements Objective
	{
		Set<Swarmable>	adjacentSwarms;

		EquidistantObjective(Set<Swarmable> swarms)
		{
			this.adjacentSwarms = swarms;
		}

		@Override
		public double computeFitness(Swarmable agent)
		{
			double max = Double.NEGATIVE_INFINITY;
			double min = Double.POSITIVE_INFINITY;

			for (Swarmable p : this.adjacentSwarms)
				{
					double m = 0;
					for (int i = 0; i < agent.getDimension(); ++i)
						{
							// compute distance from particle to neighbor
							m += Math.pow(
							    agent.getPosition(i) - p.getLeader().getPosition(i), 2);
						}
					double dist = Math.sqrt(m);
					if (min > dist) min = dist;
					if (max < dist) max = dist;
				}
			double score = (max - min) / max; // compute normalized difference
			if (max > 320)
				score = 1;
			else if (min < 80) score = 1;
			return score;
		}
	}

	public final class CenterObjective implements Objective
	{
		Set<Swarmable>	adjacentSwarms;

		CenterObjective(Set<Swarmable> swarms)
		{
			this.adjacentSwarms = swarms;
		}

		@Override
		public double computeFitness(Swarmable agent)
		{
			double dist = 0;
			for (int i = 0; i < agent.getDimension(); ++i)
				{
					double ci = 0;
					for (Swarmable p : this.adjacentSwarms)
						{
							// compute distance from particle to neighbor
							ci += p.getLeader().getPosition(i);
						}
					ci /= this.adjacentSwarms.size();
					dist += Math.pow(agent.getPosition(i) - ci, 2);
				}
			dist = Math.sqrt(dist);

			double score = dist / Math.max(300, dist); // normalize difference
			return score;
		}
	}

	public final class ViewCenterObjective implements Objective
	{
		ViewCenterObjective()
		{}

		@Override
		public double computeFitness(Swarmable agent)
		{
			double[] c = { 398, 287 };
			double dist = 0;
			for (int i = 0; i < agent.getDimension(); ++i)
				{
					dist += Math.pow(agent.getPosition(i) - c[i], 2);
				}
			dist = Math.sqrt(dist);

			double score = dist / Math.max(400, dist); // normalize difference
			return score;
		}
	}

	public final class AdjacentObjective implements Objective
	{
		Set<Swarmable>	adjacentSwarms;

		AdjacentObjective(Set<Swarmable> swarms)
		{
			this.adjacentSwarms = swarms;
		}

		@Override
		public double computeFitness(Swarmable agent)
		{
			double max = Double.NEGATIVE_INFINITY;
			double min = Double.POSITIVE_INFINITY;

			for (Swarmable p : this.adjacentSwarms)
				{
					double m = 0;
					for (int i = 0; i < agent.getDimension(); ++i)
						{
							// compute distance from particle to neighbor
							m += Math.pow(
							    agent.getPosition(i) - p.getLeader().getPosition(i), 2);
						}
					double dist = Math.sqrt(m);
					if (min > dist) min = dist;
					if (max < dist) max = dist;
				}
			double score = (max - min) / max; // compute normalized difference
			score += (Math.abs(min - 210) / 210) * (Math.abs(max - 250) / 250);
			return score / 2;
		}
	}

}
