/**
 * GraphSwarm: CompositeObjective.java
 *
 * Copyright (c) 2010 M Johnson, S Khanal, S Sampath.
 * Created: Apr 20, 2010 9:32:22 PM
 */
#include "Objective.hpp"
#include "Swarmable.hpp"

namespace isag
{
namespace strategy
{
using swarm::Swarmable;
/**
 * Provides a convenient means of composing two distinct objective functions.
 * This is useful for testing purposes, but it may be inefficient. Some
 * objectives may have overlapping computations, in which case an specialized
 * implementation can avoid repeating the same work.
 * 
 * @author M Johnson, S Khanal, S Sampath
 */
class CompositeObjective : public Objective
{
public:
	/** Indicates additive composition (i.e., avg[f,g]). */
	/** Indicates multiplicative composition (i.e., sqrt[f*g]). */
	enum Method { ADDITIVE = 1, MULTIPLICATIVE };

	/**
	 * Create an objective that combines the two specified objectives.
	 * 
	 * @note The default combination is {@link #ADDITIVE}.
	 * @param f an objective function
	 * @param g an objective function
	 */
	CompositeObjective (Objective f, Objective g, Method op = ADDITIVE )
	  : operation(op), f(f), g(g) {}

	/* (non-Javadoc)
	 * @see edu.marist.csmath.isag.strategy.Objective#computeFitness(edu.marist.csmath.isag.swarm.Swarmable)
	 */
	@Override
	virtual double computeFitness ( Swarmable agent )
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
private:
	int const operation;
	Objective f;
	Objective g;
}
