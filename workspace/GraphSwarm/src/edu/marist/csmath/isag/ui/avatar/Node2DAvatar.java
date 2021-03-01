/**
 * GraphSwarm: Node2DAvatar.java
 *
 * Copyright (c) 2010 M Johnson, S Khanal, S Sampath.
 * Created: Apr 16, 2010 2:00:18 PM
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
public class Node2DAvatar extends SwarmAvatar
{
	/**
	 * @param agent
	 * @param world
	 */
	public Node2DAvatar(Swarmable agent, SwarmView world)
	{
		super(agent, world);
		agent.registerSwarmObserver(this);
	}

	/* (non-Javadoc)
	 * @see edu.marist.csmath.isag.ui.SwarmAvatar#buildInternalRepresentation()
	 */
	@Override
	protected void buildInternalRepresentation()
	{
		Element dot = ownerDocument.createElementNS(
		    SVGDOMImplementation.SVG_NAMESPACE_URI, "circle");
		dot.setAttributeNS(null, "r", "3");
		dot.setAttributeNS(null, "fill", "#0f0");
		dot.setAttributeNS(null, "stroke", "#000");
		this.setAttributeNS(null, "style", "z-index:100");
		this.appendChild(dot);
	}

	/* (non-Javadoc)
	 * @see edu.marist.csmath.isag.ui.SwarmAvatar#observe(edu.marist.csmath.isag.swarm.Swarmable)
	 */
	@Override
	public void observe(Swarmable agent)
	{
		final SwarmAvatar self = this;

		Swarmable leader = agent.getLeader();
		final String x = Integer.toString((int) leader.getPositionBest(0));
		final String y = Integer.toString((int) leader.getPositionBest(1));
		world.getUpdateManager().getUpdateRunnableQueue().invokeLater(
		    new Runnable() {
			    public void run()
			    {
				    self.setAttribute("transform", "translate(" + x + "," + y + ")");
			    }
		    });
	}

	/**
   * 
   */
	private static final long	serialVersionUID	= 3265816856602019591L;
}
