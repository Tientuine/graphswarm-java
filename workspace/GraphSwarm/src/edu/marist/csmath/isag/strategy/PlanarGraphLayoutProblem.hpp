/**
 * GraphSwarm: PointGraphLayoutProblem.hpp
 *
 * Copyright (c) 2010 M Johnson, S Khanal, S Sampath.
 * Created: Apr 17, 2010 6:41:57 PM
 */
#include <set>
#include "Swarmable.hpp"
#include "ProblemSpace.hpp"

namespace isag
{
namespace strategy
{

using swarm::Swarmable;

/**
 * This class represents the general problem of planar layout of graphs. It
 * contains various objective functions that may be useful in the spatial
 * arrangement of arbitrary point graphs.
 * 
 * @author M Johnson, S Khanal, S Sampath
 */
class PlanarGraphLayoutProblem : public ProblemSpace
{
public:
	enum TargetType {
		VIEWCENTER, EQUIDISTANT, CENTER, EQUICENTER, EQUIVIEW,
		EQUIVIEWCENTER, CENTERVIEWCENTER, ADJACENT
        };

protected:
	std::shared_ptr<Objective> objective;
public:
	PlanarGraphLayoutProblem( std::set<Swarmable> swarms )
		: objective(new EquidistantObjective(swarms)) {}

	public PlanarGraphLayoutProblem( std::set<Swarmable> swarms, TargetType objType)
		: objective(nullptr)
	{
		switch (objType)
		{
		default: /* fall-through! */
		case EQUIDISTANT:
			objective = new EquidistantObjective(swarms);
			break;
		case CENTER:
			objective = new CenterObjective(swarms);
			break;
		case VIEWCENTER:
			objective = new ViewCenterObjective();
			break;
		case EQUICENTER:
			objective = new CompositeObjective(new EquidistantObjective(swarms),
								 new CenterObjective(swarms));
			break;
		case EQUIVIEW:
			objective = new CompositeObjective(new EquidistantObjective(swarms),
								new ViewCenterObjective());
			break;
		case EQUIVIEWCENTER:
			objective = new CompositeObjective(new CompositeObjective(
								new EquidistantObjective(swarms),
								new ViewCenterObjective()),
								new CenterObjective(swarms));
			break;
		case CENTERVIEWCENTER:
			objective = new CompositeObjective(new CompositeObjective(
								new EquidistantObjective(swarms),
								new ViewCenterObjective()),
								new CenterObjective(swarms),
								CompositeObjective.MULTIPLICATIVE);
			break;
		case ADJACENT:
			objective = new AdjacentObjective(swarms);
			break;
		}
	}

	/* (non-Javadoc)
	 * @see edu.marist.csmath.isag.strategy.ProblemSpace#dimension()
	 */
	int dimension() const { return 2; }

	/* (non-Javadoc)
	 * @see edu.marist.csmath.isag.strategy.ProblemSpace#getObjective()
	 */
	@Override
	Objective getObjective() { return this.objective; }

	class EquidistantObjective : public Objective
	{
		std::set<Swarmable> adjacentSwarms;

		EquidistantObjective ( std::set<Swarmable> swarms ) : adjacentSwarms(swarms) {}

		@Override
		double computeFitness ( Swarmable agent )
		{
			double max =  std::numeric_limits<double>::infinity();
			double min = -std::numeric_limits<double>::infinity();

			for ( auto p : adjacentSwarms )
			{
				double m {0.0};
				for ( int i {0}; i < agent.dimension(); ++i )
				{
					// compute distance from particle to neighbor
					m += std::pow(agent.getPosition(i) - p.getLeader().getPosition(i), 2);
				}
				double dist = std::sqrt(m);
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

	class CenterObjective : public Objective
	{
		std::set<Swarmable> adjacentSwarms;
	public:
		CenterObjective ( std::set<Swarmable> swarms ) : adjacentSwarms(swarms) {}

		@Override
		double computeFitness ( Swarmable agent )
		{
			double dist {0.0};
			for (int i {0}; i < agent.dimension(); ++i)
				{
					double ci = 0;
					for (auto p : adjacentSwarms)
						{
							// compute distance from particle to neighbor
							ci += p.getLeader().getPosition(i);
						}
					ci /= adjacentSwarms.size();
					dist += std::pow(agent.getPosition(i) - ci, 2);
				}
			dist = std::sqrt(dist);

			double score = dist / std::max(300, dist); // normalize difference
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
			for (int i = 0; i < agent.dimension(); ++i)
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
					for (int i = 0; i < agent.dimension(); ++i)
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
