/**
 * InterSAG: SwarmAvatar.java
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
package edu.marist.csmath.isag.ui;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGOMGElement;
import edu.marist.csmath.isag.swarm.SwarmObserver;
import edu.marist.csmath.isag.swarm.Swarmable;

/**
 * @author M Johnson, S Khanal, S Sampath
 */
public abstract class SwarmAvatar extends SVGOMGElement implements
    SwarmObserver
{
	/*
	 * (non-Javadoc)
	 * @see
	 * edu.marist.csmath.isag.swarm.SwarmObserver#observe(edu.marist.csmath.isag
	 * .swarm.Swarmable)
	 */
	@Override
	public abstract void observe(Swarmable agent);

	/**
	 * 
	 */
	protected abstract void buildInternalRepresentation();

	/**
	 * 
	 */
	protected SwarmAvatar(Swarmable agent, SwarmView world)
	{
		super("", (AbstractDocument) world.getSVGDocument());
		this.world = world;
		buildInternalRepresentation();
	}

	protected SwarmView	      world;

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -9111485788000245396L;

}
