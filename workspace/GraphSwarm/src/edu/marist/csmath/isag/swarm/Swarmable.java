/**
 * InterSAG: Swarmable.java
 * Created Feb 28, 2010 1:54:00 AM
 *
 * Copyright 2010 Matthew Johnson, Sanjeev Khanal, Sivaraman Sampath
 *
 * This file is part of InterSAG.
 *
 * InterSAG is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * InterSAG is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with InterSAG.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.marist.csmath.isag.swarm;

import java.util.*;

import edu.marist.csmath.isag.strategy.Objective;
import edu.marist.csmath.isag.strategy.ProblemSpace;

/**
 * Implements the fundamental behavior patterns for any member of a Swarm.
 * Patterns provided include: Iterator, Observer, Runnable, Comparable.
 * 
 * @author M Johnson, S Khanal, S Sampath
 */
public class Swarmable implements Comparable<Swarmable>, Iterable<Swarmable>,
    Runnable
{
	/* --- Constructors and initializers --- */

	/**
	 * Create a new swarm (initially of size 1) without a problem to solve.
	 */
	public Swarmable()
	{
		this.neighbors = new java.util.Vector<Swarmable>();
		initialize();
		join(this);
	}

	/**
	 * Create a new swarm (initially of size 1) without a problem to solve.
	 */
	public Swarmable(int swarmSize)
	{
		this.neighbors = new java.util.Vector<Swarmable>();
		initialize();
		join(this);
		for (int i = 1; i < swarmSize; ++i)
			new Swarmable(this);
	}

	/**
	 * Create a new swarm (initially of size 1) to solve the specified problem.
	 * 
	 * @param s
	 *          a problem specification
	 */
	public Swarmable(ProblemSpace s)
	{
		this.objective = s.getObjective();
		this.neighbors = new java.util.Vector<Swarmable>();
		this.currState = new Solution(s.getDimension());
		this.bestState = new Solution(s.getDimension());
		initialize();
		join(this);
	}

	/**
	 * Create a new swarm to solve the specified problem.
	 * 
	 * @param s
	 *          a problem specification
	 * @param swarmSize
	 *          size of the new swarm
	 */
	public Swarmable(ProblemSpace s, int swarmSize)
	{
		this.objective = s.getObjective();
		this.neighbors = new java.util.Vector<Swarmable>();
		this.currState = new Solution(s.getDimension());
		this.bestState = new Solution(s.getDimension());
		initialize();
		join(this);
		for (int i = 1; i < swarmSize; ++i)
			new Swarmable(this);
	}

	/**
	 * Create a new particle in the same swarm as the specified instance.
	 * 
	 * @param agent
	 *          an existing swarm particle
	 */
	public Swarmable(Swarmable agent)
	{
		this.objective = agent.objective;
		this.currState = new Solution(agent.getDimension());
		this.bestState = new Solution(agent.getDimension());
		initialize();
		join(agent);
	}

	/**
	 * Launch separate threads for each particle in the swarm.
	 */
	public void beginSwarming()
	{
		for (Swarmable p : this)
			new Thread(p, p.getClass().getSimpleName() + "-" + id())
			    .start();
	}

	/* --- Comparable implementation --- */

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Swarmable agent)
	{
		return (id() < agent.id() ? -1 : (id() > agent.id() ? 1 : 0));
	}

	/**
	 * Two particles are considered equal if they belong to the same swarm. Use
	 * operator == to distinguish between individual particles in the same swarm.
	 */
	@Override
	public boolean equals(Object p)
	{
		return (p instanceof Swarmable && ((Swarmable)p).neighbors == this.neighbors);
	}

	/**
	 * Set the specified particle as the leader.
	 * 
	 * @param agent
	 *          a swarm particle
	 */
	public synchronized void follow(Swarmable agent)
	{
		this.leader = agent;
		notifySwarmObservers();
	}

	/**
	 * Get the number of variables in the problem space.
	 * 
	 * @return dimension of the search space
	 */
	public int getDimension()
	{
		if (this.currState == null) return 0;
		return this.currState.position.length;
	}

	/**
	 * Get the maximum distance between any two particles in the swarm.
	 * 
	 * @param coord
	 * @return
	 */
	public double getExtent(int coord)
	{
		double max = Double.NEGATIVE_INFINITY, min = Double.POSITIVE_INFINITY;
		for (Swarmable p : this)
			{
				max = Math.max(p.currState.position[coord], max);
				min = Math.min(p.currState.position[coord], min);
			}
		return max - min;
	}

	/**
	 * Compute and store the fitness of this particle given its current position.
	 * 
	 * @return this particle's current fitness score
	 */
	public double getFitness()
	{
		return this.currState.fitness;
	}

	/* --- Observer/Neighbor pattern implementation --- */

	/**
	 * Get the best known fitness for this individual particle.
	 * 
	 * @return this particle's own best fitness score
	 */
	public double getFitnessBest()
	{
		return this.bestState.fitness;
	}

	/**
	 * Get the particle currently leading the swarm to which this instance
	 * belongs.
	 * 
	 * @return the leader of this particle's swarm
	 */
	public Swarmable getLeader()
	{
		return this.leader;
	}

	/**
	 * Get the current position of this particle in the search space.
	 * 
	 * @param coord
	 * @return this particle's current position
	 */
	public double getPosition(int coord)
	{
		return this.currState.position[coord];
	}

	/**
	 * Get the best known position achieved by this particle.
	 * 
	 * @param coord
	 * @return this particle's personal best position
	 */
	public double getPositionBest(int coord)
	{
		return this.bestState.position[coord];
	}

	/**
	 * Determines whether the given swarming agent is a neighbor of this instance.
	 * 
	 * @param agent
	 * @return
	 */
	public boolean isNeighbour(Swarmable agent)
	{
		return this.neighbors.contains(agent);
	}

	/* --- Iterator pattern implementation --- */

	@Override
	public Iterator<Swarmable> iterator()
	{
		return this.neighbors.iterator();
	}

	/**
	 * Add this instance to the specified particle's swarm.
	 * 
	 * @param agent
	 *          a swarm particle
	 */
	public synchronized void join(Swarmable agent)
	{
		if (this.leader != null) leave();
		this.neighbors = agent.neighbors;
		this.neighbors.add(this);
		follow(agent.getLeader());
		this.objective = agent.objective;
	}

	/**
	 * Remove this particle from its current swarm.
	 */
	public synchronized void leave()
	{
		this.neighbors.remove(this);
		if (this.leader == this)
		  this.neighbors.iterator().next().notifySwarm();
		this.neighbors = null;
		this.leader = null;
		this.currState.fitness = this.bestState.fitness = Double.POSITIVE_INFINITY;		
	}

	/**
	 * Inform all particles in the swarm that this instance is now the leader.
	 */
	public void notifySwarm()
	{
		for (Swarmable agent : this)
			agent.follow(this);
	}

	/**
	 * Notify all observers of this particle. TODO See {@link #observers} for note
	 * regarding separating swarm observers from particle observers.
	 */
	public void notifyObservers()
	{
		synchronized (this.particleObservers)
			{
				for (SwarmObserver observer : this.particleObservers)
					synchronized (observer)
						{
							observer.observe(this);
						}
			}
	}

	/**
	 * Notify all observers of this particle. TODO See {@link #observers} for note
	 * regarding separating swarm observers from particle observers.
	 */
	public void notifySwarmObservers()
	{
		synchronized (this.swarmObservers)
			{
				for (SwarmObserver observer : this.swarmObservers)
					synchronized (observer)
						{
							observer.observe(this);
						}
			}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * edu.marist.csmath.isag.swarm.SwarmObserver#observe(edu.marist.csmath.isag
	 * .swarm.Swarmable)
	 */
	public void observe(Swarmable agent)
	{
		; // TODO do we need anything in here?
	}

	/**
	 * Resets this particle to a random position in the problem space, forgetting
	 * any prior best. TODO This implementation is useful for our current problem,
	 * but is not general enough to be useful for other conceivable applications.
	 * 
	 * @param lowerbound
	 * @param upperbound
	 */
	public void randomize(double[] lowerbounds, double[] upperbounds)
	{
		for (int i = 0; i < getDimension(); ++i)
			{
				this.bestState.position[i] = this.currState.position[i] = lowerbounds[i]
				    + (upperbounds[i] - lowerbounds[i]) * Math.random();
				this.velocity[i] = Math.random();
			}
		this.currState.fitness = this.bestState.fitness = Double.POSITIVE_INFINITY;
	}

	/**
	 * Add the specified object to the list of observers.
	 * 
	 * @param observer
	 *          an object watching changes to this particle
	 */
	public synchronized void registerObserver(SwarmObserver observer)
	{
		this.particleObservers.listIterator(this.particleObservers.size()).add(
		    observer);
	}

	/**
	 * Add the specified object to the list of observers.
	 * 
	 * @param observer
	 *          an object watching changes to this particle
	 */
	public synchronized void registerSwarmObserver(SwarmObserver observer)
	{
		this.swarmObservers.listIterator(this.swarmObservers.size()).add(observer);
	}

	/* --- Runnable implementation --- */

	/**
	 * Main driver method of the swarm algorithm, allows for running each particle
	 * in its own Thread for parallel computation.
	 */
	@Override
	public void run()
	{
		while (true)
			{
				evaluate();
				update();
				breathe(50);
			}
	}

	/**
	 * Compute the current fitness and update recorded best known fitness values.
	 */
	private void evaluate()
	{
		this.currState.fitness = this.objective.computeFitness(this);

		if (this.currState.fitness < this.bestState.fitness)
			{
				this.bestState.fitness = this.currState.fitness;
				this.bestState.position = this.currState.position.clone();

				if (this.currState.fitness < this.leader.bestState.fitness)
				  notifySwarm();
			}
	}

	/**
   * 
   */
	protected void update()
	{
		for (int i = 0; i < getDimension(); i++)
			{
				double rself = Math.random(), rswarm = Math.random();
				this.velocity[i] = (this.velocity[i] * this.inertia)
				    + (rself * this.learningFactor)
				    * (this.leader.bestState.position[i] - this.currState.position[i])
				    + (rswarm * this.learningFactor)
				    * (this.bestState.position[i] - this.currState.position[i]);
				this.currState.position[i] += this.velocity[i];
			}

		notifyObservers();
	}

	/**
	 * Redirect this particle (and its swarm) to solve a new problem.
	 * 
	 * @param s
	 *          a problem specification
	 */
	public void setProblemSpace(ProblemSpace s)
	{
		for (Swarmable p : this) // loop over all particles including self
			{
				p.objective = s.getObjective();
				p.velocity = new double[s.getDimension()];
				p.currState = new Solution(s.getDimension());
				p.bestState = new Solution(s.getDimension());
				p.bestState.fitness = p.currState.fitness = Double.POSITIVE_INFINITY;
			}
	}

	/**
	 * Gets the number of particles in the same swarm as this instance.
	 * 
	 * @return number of particles in the swarm
	 */
	public int sizeOfSwarm()
	{
		return this.neighbors.size();
	}

	/** Printable string */
	@Override
	public String toString()
	{
		String str = getClass().getSimpleName() + "[" + id()
		    + (this.leader == this ? "*" : "") + "]: ";
		str += "pcurr=" + ((float) this.currState.fitness) + this.currState;
		str += "; pbest=" + ((float) this.bestState.fitness) + this.bestState;
		str += "; vcurr=(" + ((float) this.velocity[0]);
		for (int i = 1; i < this.velocity.length; i++)
			str += "," + ((float) this.velocity[i]);
		str += ")";
		return str;
	}

	/**
	 * Remove the specified object from the list of observers.
	 * 
	 * @param observer
	 *          an object formerly interesting in watching this particle
	 */
	public synchronized void unregisterObserver(SwarmObserver observer)
	{
		int pos = this.particleObservers.indexOf(observer);
		if (pos >= 0)
			{
				ListIterator<SwarmObserver> itr = this.particleObservers.listIterator(pos);
				itr.next();
				itr.remove();
			}
	}

	/**
	 * Remove the specified object from the list of observers.
	 * 
	 * @param observer
	 *          an object formerly interesting in watching this particle
	 */
	public synchronized void unregisterSwarmObserver(SwarmObserver observer)
	{
		int pos = this.swarmObservers.indexOf(observer);
		if (pos >= 0)
			{
				ListIterator<SwarmObserver> itr = this.swarmObservers.listIterator(pos);
				itr.next();
				itr.remove();
			}
	}

	/**
	 * Returns a unique identifier for this Swarmable instance.
	 * 
	 * @return an integral code that uniquely identifies this object
	 */
	protected int id()
	{
		return hashCode();
	}

	/**
	 * Assign starting values to various parameters.
	 */
	protected void initialize()
	{
		this.velocity = new double[getDimension()];
		this.inertia = 0.86; // 0.95; // 0.95; //0.8;
		this.learningFactor = 0.45; // 0.6; // 2; //0.4;
		this.particleObservers = new java.util.Vector<SwarmObserver>();
		this.swarmObservers = new java.util.Vector<SwarmObserver>();
	}

	/**
	 * Used to introduce dynamic delays into the parallel swarm algorithm.
	 * 
	 * @param millis
	 */
	private void breathe(long millis)
	{
		try
			{
				Thread.sleep(millis);
			}
		catch (InterruptedException exc)
			{
				exc.printStackTrace();
			}
	}

	/**
	 * Used for to display algorithm data for tuning and debugging.
	 */
	private void printTestDiagnostics()
	{
		// System.err.println(this);
		if (this.leader == this)
			{
				System.err.print("Scores[" + this.neighbors.hashCode() + "]: "
				    + this.currState.fitness + "*");
				for (Swarmable p : this.neighbors)
					if (p != this)
					  System.err.print(" " + p.currState.fitness);
				System.err.println();
			}
	}

	/** Always refers to the particle that has achieved the best fitness so far. */
	protected Swarmable	          leader = null;

	/** List of all particles (including this one!) in the same swarm. */
	protected List<Swarmable>	    neighbors;

	/**
	 * List of all objects interested in the current state of this particle.
	 */
	protected List<SwarmObserver>	particleObservers;

	/**
	 * List of all objects interested in the current state of this swarm.
	 */
	protected List<SwarmObserver>	swarmObservers;

	/** Refers to the objective function that governs this swarm. */
	protected Objective	          objective = null;

	/** Encapsulates the essential solution state represented by this particle. */
	protected Solution	          currState = null;

	/** Encapsulates the best known solution state achieved by this particle. */
	protected Solution	          bestState = null;

	/** Dampening factor to short-term memory of this particle. */
	protected double	            inertia;

	/** Dampening factor to control the long-term memory of this particle. */
	protected double	            learningFactor;

	/** Current search velocity for this particle. */
	protected double[]	          velocity	= null;

}
