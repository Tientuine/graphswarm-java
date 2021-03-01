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
#include <vector>
#include <set>
#include <memory>
#include <nutex>
#include "runnables.hpp"
#include "Objective.hpp"
#include "ProblemSpace.hpp"
#include "SwarmObserver.hpp"

namespace isag
{
namespace swarm
{
using strategy::Objective;
using strategy::ProblemSpace;

/**
 * Implements the fundamental behavior patterns for any member of a Swarm.
 * Patterns provided include: Iterator, Observer, Runnable, Comparable.
 * 
 * @author M Johnson, S Khanal, S Sampath
 */
class Swarmable
        : runnable,
          std::recursive_mutex,
          std::enable_shared_from_this
{
        static double const DEFAULT_INERTIA = 0.86;
        static double const DEFAULT_LEARNRATE = 0.45;
public:
	/* --- Constructors and initializers --- */

	/**
	 * Create a new swarm without a problem to solve.
	 */
	Swarmable ( unsigned neighborCount = 0 )
	 : inertia(DEFAUT_INERTIA),
	   learn_rate(DEFAULT_LEARNRATE),
	   m_prng((std::random_device)())
	{
	        join(*this);
		while ( 0 < neighborCount-- ) { new Swarmable(*this); }
	}

	/**
	 * Create a new swarm to solve the specified problem.
	 * 
	 * @param s
	 *          a problem specification
	 * @param swarmSize
	 *          size of the new swarm
	 */
	Swarmable ( ProblemSpace s, unsigned nborCount = 0 )
	 : Swarmable(nborCount),
	   objective(s.objective()),
	   currState(s.dimension()),
	   bestState(s.dimension())
	{}

	/**
	 * Create a new particle in the same swarm as the specified instance.
	 * 
	 * @param agent
	 *          an existing swarm particle
	 */
	Swarmable ( Swarmable const& agent )
	 : inertia(DEFAUT_INERTIA),
	   learn_rate(DEFAULT_LEARNFACTOR),
	   objective(agent.objective()),
	   currState(agent.currState->size()),
	   bestState(agent.bestState->size())
	   m_prng((std::random_device)())
	{ join(agent); }

	/**
	 * Launch separate threads for each particle in the swarm.
	 */
	void start() { for ( auto p : neighbors ) { p->run(); } }

	/* --- Comparable implementation --- */
	friend bool operator== ( Swarmable agent )
	{ return get_id() == agent.get_id(); }
	friend bool operator<  ( Swarmable agent )
	{ return get_id() < agent.get_id(); }
	friend bool operator>  ( Swarmable agent )
	{ return get_id() > agent.get_id(); }

	/**
	 * Check whether the specified particle belong to my neighborhood.
	 *
	 * @param agent
	 *          a swarm particle
	 */
	bool isNeighbor ( Swarmable agent )
	{ return neighbors == agent.neighbors; }

	/**
	 * Set the specified particle as the leader.
	 * 
	 * @param agent
	 *          a swarm particle
	 * @todo synchronize
	 */
	void follow ( Swarmable const& agent )
	{
	        lock();
		leader = agent.shared_from_this();
		notifySwarmObservers();
		unlock();
	}

	/**
	 * Get the number of variables in the problem space.
	 * 
	 * @return dimension of the search space
	 */
	int dimension() { return ( currState ? currState.size() : 0 ); }

	/**
	 * Get the maximum distance between any two particles in the swarm.
	 * 
	 * @param coord
	 * @return
	 */
	double extent ( int coord )
	{
		double min {std::numeric_limits<double>::infinity()}, max {-min};
		for ( auto p : neighbors )
                {
			max = std::max(p.currState->[coord], max);
			min = std::min(p.currState->[coord], min);
                }
		return max - min;
	}

	/**
	 * Compute and store the fitness of this particle given its current position.
	 * 
	 * @return this particle's current fitness score
	 */
	double fitness () { return currState->fitness; }

	/* --- Observer/Neighbor pattern implementation --- */

	/**
	 * Get the best known fitness for this individual particle.
	 * 
	 * @return this particle's own best fitness score
	 */
	double fitnessBest ()
	{
		return bestState->fitness;
	}

	/**
	 * Get the particle currently leading the swarm to which this instance
	 * belongs.
	 * 
	 * @return the leader of this particle's swarm
	 */
	Swarmable const& getLeader () { return *leader; }

	/**
	 * Get the current position of this particle in the search space.
	 * 
	 * @param coord
	 * @return this particle's current position
	 */
	double position ( int coord ) { return currState->[coord]; }

	/**
	 * Get the best known position achieved by this particle.
	 * 
	 * @param coord
	 * @return this particle's personal best position
	 */
	double positionBest ( int coord ) { return bestState->[coord]; }

	/**
	 * Determines whether the given swarming agent is a neighbor of this instance.
	 * 
	 * @param agent
	 * @return
	 */
	bool isNeighbour ( Swarmable agent ) { return neighbors.contains(agent); }

	/* --- Iterator pattern implementation --- */

	auto begin()  -> decltype(neighbors.begin())  { return neighbors.begin(); }
	auto end()    -> decltype(neighbors.end())    { return neighbors.end(); }
	auto cbegin() -> decltype(neighbors.cbegin()) { return neighbors.cbegin(); }
	auto cend()   -> decltype(neighbors.cend())   { return neighbors.cend(); }

	/**
	 * Add this instance to the specified particle's swarm.
	 * 
	 * @param agent
	 *          a swarm particle
	 * @todo synchronize
	 */
	void join ( Swarmable const& agent )
	{
	        lock();
		if ( leader ) { leave(); }
		objective = agent.objective;
		neighbors = agent.neighbors;
		neighbors->add(std::shared_from_this());
		follow(agent.leader);
		unlock();
	}

	/**
	 * Remove this particle from its current swarm.
	 * @todo synchronize
	 */
	void leave()
	{
	        lock();
		neighbors.remove(shared_from_this());
		if (leader == this)
		        (++(neighbors.begin()))->notifySwarm();
		neighbors.reset();
		leader.reset();
		currState->fitness = bestState->fitness = std::numeric_limits<double>::infinity();
		unlock();
	}

	/**
	 * Inform all particles in the swarm that this instance is now the leader.
	 */
	void notifySwarm () { for ( auto agent : neighbors ) { agent.follow(*this); } }

	/**
	 * Notify all observers of this particle. TODO See {@link #observers} for note
	 * regarding separating swarm observers from particle observers.
	 * @todo sychronize access to observers?
	 */
	void notifyObservers()
	{
		for ( auto observer : particleObservers ) { observer.observe(*this); }
	}

	/**
	 * Notify all observers of this particle. TODO See {@link #observers} for note
	 * regarding separating swarm observers from particle observers.
	 * @todo synchronize access to observers?
	 */
	void notifySwarmObservers ()
	{
		for ( auto observer : swarmObservers ) { observer.observe(*this); }
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * edu.marist.csmath.isag.swarm.SwarmObserver#observe(edu.marist.csmath.isag
	 * .swarm.Swarmable)
	 */
	void observe ( Swarmable agent )
	{
		; // TODO do we need anything in here?
	}

	/**
	 * Resets this particle to a random position in the problem space, forgetting
	 * any prior best.
	 * @todo This implementation is useful for our current problem, but is not
	 * 	general enough to be useful for other conceivable applications.
	 * 
	 * @param lowerbound
	 * @param upperbound
	 */
	void randomize (double[] lowerbounds, double[] upperbounds)
	{
		for (int i = 0; i < getDimension(); ++i)
		{
			bestState->[i] = currState->[i] = lowerbounds[i] + (upperbounds[i] - lowerbounds[i]) * std::generate_canonical<double,16>(m_prng);
			velocity[i] = std::generate_canonical<double,16>(m_prng);
		}
		currState->fitness = bestState->fitness = std::numeric_limits<double>::infinity();
	}

	/**
	 * Add the specified object to the list of observers.
	 * 
	 * @param observer
	 *          an object watching changes to this particle
	 * @todo synchronize
	 */
	void registerObserver ( SwarmObserver const& observer )
	{
	        lock();
		particleObservers.push_back(observer.shared_from_this());
		unlock();
	}

	/**
	 * Add the specified object to the list of observers.
	 * 
	 * @param observer
	 *          an object watching changes to this particle
	 * @todo synchronize
	 */
	void registerSwarmObserver ( SwarmObserver const& observer )
	{
		lock();
		swarmObservers.push_back(observer.shared_from_this());
		unlock();
	}

	/* --- Runnable implementation --- */

	/**
	 * Main driver method of the swarm algorithm, allows for running each particle
	 * in its own Thread for parallel computation.
	 */
	virtual void operator() () override
	{
		while (true)
		{
			evaluate();
			update();
			breathe(50);
		}
	}

private:
	/**
	 * Compute the current fitness and update recorded best known fitness values.
	 */
	void evaluate()
	{
		currState->fitness = objective->computeFitness(*this);

		if (currState->fitness < bestState->fitness)
		{
			*bestState = *currState;
			if (currState->fitness < leader->bestState->fitness) { notifySwarm(); }
		}
	}

protected:
	/**
         * 
         */
	void update()
	{
		for ( auto i = 0; i < dimension(); ++i )
		{
			double rself = random(), rswarm = random(); //!!!
			velocity[i] = (velocity[i] * inertia)
			    + (rself * learn_rate)
			    * (leader->bestState->[i] - currState->[i])
			    + (rswarm * learn_rate)
			    * (bestState->[i] - currState->[i]);
			currState->[i] += velocity[i];
		}
		notifyObservers();
	}

public:
	/**
	 * Redirect this particle (and its swarm) to solve a new problem.
	 * 
	 * @param s
	 *          a problem specification
	 */
	void setProblemSpace ( ProblemSpace s )
	{
		for ( auto p : neighbors ) // loop over all particles including self
			{
				p.objective = s.getObjective();
				p.velocity = new double[s.getDimension()];
				p.currState.reset(new Solution(s.getDimension()));
				p.bestState.reset(new Solution(s.getDimension()));
				p.bestState->fitness = p.currState->fitness = std::numeric_limits<double>::infinity();
			}
	}

	/**
	 * Gets the number of particles in the same swarm as this instance.
	 * 
	 * @return number of particles in the swarm
	 */
	int sizeOfSwarm() { return neighbors.size(); }

	/**
	 * Printable string
	 * @todo FIX THIS BROKEN MESS!
	 */
	/*std::string toString()
	{
		String str = getClass().getSimpleName() + "[" + get_id()
		    + (leader == this ? "*" : "") + "]: ";
		str += "pcurr=" + ((float) currState->fitness) + currState;
		str += "; pbest=" + ((float) bestState->fitness) + bestState;
		str += "; vcurr=(" + ((float) velocity[0]);
		for (int i = 1; i < velocity.length; i++)
			str += "," + ((float) velocity[i]);
		str += ")";
		return str;
	}*/

	/**
	 * Remove the specified object from the list of observers.
	 * 
	 * @param observer
	 *          an object formerly interesting in watching this particle
	 * @todo synchronize
	 */
	void unregisterObserver ( SwarmObserver const& observer )
	{
		lock();
		particleObservers.erase(observer.shared_from_this());
		unlock();
	}

	/**
	 * Remove the specified object from the list of observers.
	 * 
	 * @param observer
	 *          an object formerly interesting in watching this particle
	 * @todo synchronize
	 */
	void unregisterSwarmObserver ( SwarmObserver const& observer )
	{
		lock();
		swarmObservers.erase(observer.shared_from_this());
		unlock();
	}

private:
	/**
	 * Used to introduce dynamic delays into the parallel swarm algorithm.
	 * 
	 * @param millis
	 */
	void breathe ( long millis )
	{ std::this_thread::sleep_for(std::chrono::milliseconds(millis)); }

	/**
	 * Used for to display algorithm data for tuning and debugging.
	 */
	void printTestDiagnostics ()
	{
		// System.err.println(this);
		if (leader == this)
			{
				System.err.print("Scores[" + get_id() + "]: "
				    + currState->fitness + "*");
				for ( auto p : neighbors )
					if (p != this)
					  System.err.print(" " + p.currState->fitness);
				System.err.println();
			}
	}

protected:
	/** Always refers to the particle that has achieved the best fitness so far. */
	std::weak_ptr<Swarmable> leader;

	/** List of all particles (including this one!) in the same swarm. */
	std::shared_ptr<std::set<std:shared_ptr<Swarmable>>> neighbors;

	/**
	 * List of all objects interested in the current state of this particle.
	 */
	std::set<std::weak_ptr<SwarmObserver>> particleObservers;

	/**
	 * List of all objects interested in the current state of this swarm.
	 */
	std::set<std::weak_ptr<SwarmObserver>> swarmObservers;

	/** Refers to the objective function that governs this swarm. */
	std::shared_ptr<Objective> objective;

	/** Encapsulates the essential solution state represented by this particle. */
	std::unique_ptr<Solution> currState;

	/** Encapsulates the best known solution state achieved by this particle. */
	std::unique_ptr<Solution> bestState;

	/** Dampening factor to short-term memory of this particle. */
	double inertia;

	/** Dampening factor to control the long-term memory of this particle. */
	double learn_rate;

	/** Current search velocity for this particle. */
	std::vector<double> velocity;

	/** Internal pseudo-random number generator. */
	std::mt19937 m_prng;
};
