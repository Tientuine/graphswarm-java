/**
 * GraphSwarm: Solution.hpp
 *
 * Copyright (c) 2010 M Johnson, S Khanal, S Sampath.
 * Created: Apr 18, 2010 9:49:51 PM
 */
#ifndef HPP_SOLUTION
#define HPP_SOLUTION

#include <vector>

namespace isag { namespace swarm {

/**
 * Inner class to abstract the notion of solution as a position in n-dimensional
 * space.
 * 
 * @author M Johnson, S Khanal, S Sampath
 * @todo convert this to class template and static-sized array?
 */
class Solution : public std::vector<double>
{
	using super = std::vector<double>;
public:
	/**
	 * @param n
	 */
	Solution (int n)
	 : super(n, 0.0),
	   fitness(std::numeric_limits<double>::infinity()) {}

	Solution ( Solution const& s )
	 : super(s), fitness(s.fitness) {}


	/** Printable string */
	operator std::string() const
	{
		std::string str = "(" + ((float) front());
		for (int i = 1; i < size(); i++)
			str += "," + ((float) at(i));
		str += ")";
		return str;
	}

private:
	double fitness;
};

} }

#endif
