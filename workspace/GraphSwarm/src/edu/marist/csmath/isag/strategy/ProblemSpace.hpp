/**
 * GraphSwarm: ProblemSpace.hpp
 *
 * Copyright (c) 2010 M Johnson, S Khanal, S Sampath.
 * Created: Apr 17, 2010 5:37:21 PM
 */
#include <memory>
#include "Objective.hpp"

namespace isag
{
namespace strategy
{

/**
 * @author M Johnson, S Khanal, S Sampath
 */
class ProblemSpace
{
public:
	/**
	 * @return
	 */
	virtual int getDimension() const = 0;

	/**
	 * @return
	 */
	virtual std::shared_ptr<Objective> getObjective() const = 0;
};

}
}
