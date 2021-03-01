/**
 * InterSAG: Particle2DAvatar.java
 * Created: Mar 14, 2010 2:46:29 PM
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
package edu.marist.csmath.isag.ui.avatar;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Element;

import edu.marist.csmath.isag.swarm.Swarmable;
import edu.marist.csmath.isag.ui.SwarmAvatar;
import edu.marist.csmath.isag.ui.SwarmView;

/**
 * @author M Johnson, S Khanal, S Sampath
 */
public class Particle2DAvatar extends SwarmAvatar
{
	/**
	 * @param agent
	 */
	public Particle2DAvatar(Swarmable agent, SwarmView world)
	{
		super(agent, world);
		agent.registerObserver(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * edu.marist.csmath.isag.ui.SwarmAvatar#observe(edu.marist.csmath.isag.swarm
	 * .Particle)
	 */
	@Override
	public void observe(Swarmable agent)
	{
		final String x = Integer.toString((int) agent.getPosition(0));
		final String y = Integer.toString((int) agent.getPosition(1));
		int zscaled = (int)(255 * 3 * (1.0 - agent.getFitness()));
		final String z;
		if (zscaled <= 255)
			z = "rgb("+zscaled+",0,255)";
		else if (zscaled <= 510)
			z = "rgb(255,0,"+(510-zscaled)+")";
		else //if (zscaled <= 765)
			z = "rgb(255,"+(zscaled-510)+",0)";
		//else
		//	z = "rgb(255,"+(1020-zscaled)+",0)";

		final SwarmAvatar self = this;
		world.getUpdateManager().getUpdateRunnableQueue().invokeLater(
		    new Runnable() {
			    public void run()
			    {
				    self.setAttribute("transform", "translate(" + x + "," + y + ")");
				    self.getFirstElementChild().setAttribute("fill", z);
			    }
		    });
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * edu.marist.csmath.isag.ui.SwarmAvatar#buildRepresentation(org.w3c.dom.Document
	 * )
	 */
	@Override
	protected void buildInternalRepresentation()
	{
		Element dot = ownerDocument.createElementNS(
		    SVGDOMImplementation.SVG_NAMESPACE_URI, "circle");
		dot.setAttributeNS(null, "r", "1");
		dot.setAttributeNS(null, "fill", "#090");
		this.appendChild(dot);
	}

	/**
   * 
   */
	private static final long	serialVersionUID	= 1130210719918281201L;
}
