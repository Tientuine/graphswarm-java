/**
 * InterSAG: SwarmViewer.java
 * Created Mar 12, 2010 7:24:00 PM
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

import java.awt.event.*;
import javax.swing.*;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author M Johnson, S Khanal, S Sampath
 */
public class SwarmViewer extends JFrame
{
	/**
	 * 
	 */
	SwarmView	swarmHabitat;

	/**
	 * @param swarm
	 */
	SwarmViewer()
	{
		this.swarmHabitat = new SwarmView();
		this.swarmHabitat.setDocumentState(JSVGCanvas.ALWAYS_DYNAMIC);

		// We are using a constant available on the SVGDOMImplementation,
		// but we could have used "http://www.w3.org/2000/svg".
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		Document doc = impl.createDocument(svgNS, "svg", null);
		this.swarmHabitat.setDocument(doc);

		// this.swarmHabitat.getInteractors().add(new SwarmInteractor());

		this.getContentPane().add(this.createComponents());

		swarmHabitat.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
			public void gvtRenderingPrepare(GVTTreeRendererEvent e)
			{
				; // do nothing?
			}

			public void gvtRenderingCompleted(GVTTreeRendererEvent e)
			{
				// swarmHabitat.swarm = new SwarmManager(new
				// GlobalObjective(swarmHabitat));
				// swarmHabitat2.swarm = new SwarmManager(swarmHabitat2);
				drawIndicators();
				// new Thread(swarmHabitat.swarm).start();
				// SwarmTestCases.createTestSwarm00(swarmHabitat);
				// SwarmTestCases.createFullyConnectedSwarms(swarmHabitat, 7);
				SwarmTestCases.createWebOfSwarms(swarmHabitat, 8);
				// createDummySwarm(swarmHabitat.swarm);
				// new Thread(swarmHabitat.swarm).start();
			}
		});

		// Display the frame.
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		this.setSize(800, 600);
		this.setVisible(true);
	}

	JComponent createComponents()
	{
		return swarmHabitat; // TODO do we need anything else here?
	}

	void drawIndicators()
	{
		final String SVGNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		Document doc = swarmHabitat.getSVGDocument();

		final Element bounds = doc.createElementNS(SVGNS, "rect");
		final Element center = doc.createElementNS(SVGNS, "rect");

		bounds.setAttribute("fill", "none");
		center.setAttribute("fill", "none");
		bounds.setAttribute("stroke", "#ccc");
		center.setAttribute("stroke", "#ccc");
		bounds.setAttribute("x", Integer.toString((int) (0.02 * swarmHabitat
		    .getWidth())));
		bounds.setAttribute("y", Integer.toString((int) (0.02 * swarmHabitat
		    .getHeight())));
		center.setAttribute("x", Integer.toString(swarmHabitat.getWidth() / 2 - 2));
		center
		    .setAttribute("y", Integer.toString(swarmHabitat.getHeight() / 2 - 2));
		bounds.setAttribute("height", Integer.toString((int) (0.96 * swarmHabitat
		    .getHeight())));
		bounds.setAttribute("width", Integer.toString((int) (0.96 * swarmHabitat
		    .getWidth())));
		center.setAttribute("height", "5");
		center.setAttribute("width", "5");

		final Element root = doc.getDocumentElement();

		swarmHabitat.getUpdateManager().getUpdateRunnableQueue().invokeLater(
		    new Runnable() {
			    public void run()
			    {
				    root.appendChild(bounds);
				    root.appendChild(center);
			    }
		    });
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws InterruptedException
	{
		new SwarmViewer();
		while (true)
			{
				Thread.sleep(5000);
				Runtime.getRuntime().gc();
			}
	}

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1982785512380765403L;
}
