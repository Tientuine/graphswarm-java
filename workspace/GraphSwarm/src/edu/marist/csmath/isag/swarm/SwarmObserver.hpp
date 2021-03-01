/**
 * InterSAG: SwarmObserver.hpp
 * Created Mar 14, 2010 12:43:00 AM
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

#ifndef HPP_SWARM_OBSERVER
#define HPP_SWARM_OBSERVER

namespace isag { namespace swarm {

/**
 * @author M Johnson, S Khanal, S Sampath
 */
struct SwarmObserver
{
	/**
	 * Implements the notify/update functionality from the Observer pattern.
	 * 
	 * @param agent
	 */
	virtual void observe( Swarmable const& agent ) = 0;
};

} }

#endif
