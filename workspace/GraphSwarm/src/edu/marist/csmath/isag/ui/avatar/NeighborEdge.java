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
import org.w3c.dom.Element; //import org.w3c.dom.Node;
//import org.w3c.dom.Text;

import edu.marist.csmath.isag.swarm.Swarmable;
import edu.marist.csmath.isag.ui.SwarmAvatar;
import edu.marist.csmath.isag.ui.SwarmView;

/**
 * @author M Johnson, S Khanal, S Sampath
 */
public class NeighborEdge extends SwarmAvatar
{
	Swarmable	from;
	Swarmable	to;
	String	  edgeLabel	= null;

	/**
	 * @param agent
	 * @param world
	 */
	public NeighborEdge(Swarmable f, Swarmable t, SwarmView g)
	{
		super(f, g);
		f.registerSwarmObserver(this);
		t.registerSwarmObserver(this);
		from = f;
		to = t;
	}

	/* (non-Javadoc)
	 * @see edu.marist.csmath.isag.ui.SwarmAvatar#buildInternalRepresentation()
	 */
	@Override
	protected void buildInternalRepresentation()
	{
		Element line = ownerDocument.createElementNS(
		    SVGDOMImplementation.SVG_NAMESPACE_URI, "line");
		line.setAttributeNS(null, "x1", "0"); // Isn't here x & y values passed???
		line.setAttributeNS(null, "y1", "0");
		line.setAttributeNS(null, "x2", "0");
		line.setAttributeNS(null, "y2", "0");
		line.setAttributeNS(null, "style", "stroke:#ccc;stroke-width:1;z-index:10");
		this.setAttributeNS(null, "style", "z-index:10");
		this.appendChild(line);

		/*
		Element text = ownerDocument.createElementNS(
		    SVGDOMImplementation.SVG_NAMESPACE_URI, "text");
		text.appendChild(ownerDocument.createTextNode("0px"));
		this.appendChild(text);
		*/
	}

	/* (non-Javadoc)
	 * @see edu.marist.csmath.isag.ui.SwarmAvatar#observe(edu.marist.csmath.isag.swarm.Swarmable)
	 */
	@Override
	public void observe(final Swarmable agent)
	{
		SwarmAvatar self = this;
		final Element line = self.getFirstElementChild();
		// final Element text = ((Element) line.getNextSibling());
		final String xattr, yattr;

		Swarmable leader = agent.getLeader();
		final String x = Integer.toString((int) leader.getPositionBest(0));
		final String y = Integer.toString((int) leader.getPositionBest(1));

		// final String dist;
		// final String xmid, ymid;
		if (agent == from)
			{
				xattr = "x1";
				yattr = "y1";
				/* * /
				int dx = (Integer.parseInt(line.getAttribute("x2")) - (int) leader.getPositionBest(0));
				int dy = (Integer.parseInt(line.getAttribute("y2")) - (int) leader.getPositionBest(1));
				dist = Integer.toString((int) Math.sqrt(dx * dx + dy * dy));
				xmid = Integer.toString((int) (leader.getPositionBest(0) + dx/2.0));
				ymid = Integer.toString((int) (leader.getPositionBest(1) + dy/2.0));
				/**/
			}
		else
			{
				xattr = "x2";
				yattr = "y2";
				/* * /
				int dx = ((int) leader.getPositionBest(0)) - Integer.parseInt(line.getAttribute("x1"));
				int dy = ((int) leader.getPositionBest(1)) - Integer.parseInt(line.getAttribute("y1"));
				dist = Integer.toString((int) Math.sqrt(dx * dx + dy * dy));
				xmid = Integer.toString((int) (leader.getPositionBest(0) - dx/2.0));
				ymid = Integer.toString((int) (leader.getPositionBest(1) - dy/2.0));
				/**/
			}

		world.getUpdateManager().getUpdateRunnableQueue().invokeLater(
		    new Runnable() {
			    public void run()
			    {
				    line.setAttribute(xattr, x);
				    line.setAttribute(yattr, y);
				    /*
				    text.setAttribute("x", xmid);
				    text.setAttribute("y", ymid);
				    ((Text)text.getFirstChild()).setData(dist+"px");
				    */
			    }
		    });
	}

	/**
   * 
   */
	private static final long	serialVersionUID	= 1501622128060317246L;
}
