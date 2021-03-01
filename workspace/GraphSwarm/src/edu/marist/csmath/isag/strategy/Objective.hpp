/**
 * InterSAG: Objective.cpp
 * Created Feb 20, 2010 4:40:00 PM
 *
 * Copyright 2010-2012 Matthew Johnson, Sanjeev Khanal, Sivaraman Sampath
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
#include "Swarmable.hpp"

namespace isag { namespace strategy {

using swarm::Swarmable;

/**
 * Implements the Strategy pattern where the context is a Swarmable that invokes
 * the desired objective function through a generic interface.
 * @author M Johnson
 */
class Objective
{
public:
	/**
	 * Calculate a fitness score for the specified swarm particle.
	 * @param agent a particle in a swarm
	 * @return a normalize score indicating the fitness of the particle
	 */
	virtual double computeFitness ( Swarmable const& ) = 0;
};

}
}
