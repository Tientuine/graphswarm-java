/**
 * InterSAG: SwarmInteractor.java
 * Created: Mar 14, 2010 4:33:12 PM
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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.apache.batik.swing.gvt.Interactor;
import org.w3c.dom.Element;

import edu.marist.csmath.isag.swarm.Swarmable;
import edu.marist.csmath.isag.ui.avatar.Particle2DAvatar;

/**
 * @author M Johnson, S Khanal, S Sampath
 */
public class SwarmInteractor implements Interactor
{
	private boolean	isInteracting	= false;

	/*
	 * (non-Javadoc)
	 * @see org.apache.batik.swing.gvt.Interactor#endInteraction()
	 */
	@Override
	public boolean endInteraction()
	{
		// TODO Auto-generated method stub
		return !isInteracting;
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.apache.batik.swing.gvt.Interactor#startInteraction(java.awt.event.
	 * InputEvent)
	 */
	@Override
	public boolean startInteraction(InputEvent evt)
	{
		if (evt instanceof MouseEvent)
		  return ((MouseEvent) evt).getClickCount() > 0;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent arg0)
	{
		return; // for now, do nothing on keypress
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent arg0)
	{
		return; // for now, do nothing on key release
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent arg0)
	{
		return; // for now, do nothing on key type
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent evt)
	{
		// synchronized (this)
		// {
		isInteracting = true;
		Object src = evt.getSource();
		if (src instanceof SwarmView)
			{
				final SwarmView view = (SwarmView) src;
				//final SwarmManager swarm = view.swarm;
				//final Swarmable particle = new Particle2D(swarm, evt.getX(), evt.getY());
				/*
				view.getUpdateManager().getUpdateRunnableQueue().invokeLater(
				    new Runnable() {
					    public void run()
					    {
						    Element root = view.getSVGDocument().getDocumentElement();
						    // synchronized (root)
						    // {
						    root.appendChild(new Particle2DAvatar(particle, view));
						    // }
					    }
				    });
			  */
			}
		isInteracting = false;
		// }
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent arg0)
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent arg0)
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent arg0)
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent arg0)
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent arg0)
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseMoved(MouseEvent arg0)
	{
	// TODO Auto-generated method stub

	}

}
