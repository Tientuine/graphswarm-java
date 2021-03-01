/**
 * GraphSwarm: SwarmTestCases.java
 *
 * Copyright (c) 2010 M Johnson, S Khanal, S Sampath.
 * Created: Apr 11, 2010 9:11:39 AM
 */
package edu.marist.csmath.isag.ui;

import java.util.HashSet;

import org.w3c.dom.Element;

import edu.marist.csmath.isag.strategy.PlanarGraphLayoutProblem;
import edu.marist.csmath.isag.swarm.Swarmable;
import edu.marist.csmath.isag.ui.avatar.NeighborEdge;
import edu.marist.csmath.isag.ui.avatar.Node2DAvatar;
import edu.marist.csmath.isag.ui.avatar.Particle2DAvatar;

/**
 * @author M Johnson, S Khanal, S Sampath
 */
public class SwarmTestCases
{
	static double[]	lowerbounds	= { 0, 0 };

	public static void createFullyConnectedSwarms(SwarmView view, int n)
	{
		final double[] upperbounds = { view.getWidth(), view.getHeight() };
		final int graphSize = n, swarmSize = 50;

		Swarmable[] swarms = new Swarmable[graphSize];
		for (int i = 0; i < graphSize; ++i)
			swarms[i] = new Swarmable(swarmSize);

		Element root = view.getSVGDocument().getDocumentElement();
		for (Swarmable swarm : swarms)
			{
				HashSet<Swarmable> adjacentSwarms = new HashSet<Swarmable>();
				for (Swarmable s : swarms)
					if (s != swarm)
						{
							adjacentSwarms.add(s);
							root.appendChild(new NeighborEdge(swarm, s, view));
						}
				swarm.setProblemSpace(new PlanarGraphLayoutProblem(adjacentSwarms));

				root.appendChild(new Node2DAvatar(swarm, view));
				for (Swarmable p : swarm)
					{
						p.randomize(lowerbounds, upperbounds);
						root.appendChild(new Particle2DAvatar(p, view));
						p.notifyObservers();
					}
				swarm.notifySwarm();
				swarm.notifyObservers();
			}
		for (Swarmable swarm : swarms)
			swarm.beginSwarming();
	}

	public static void createWebOfSwarms(SwarmView view, int n)
	{
		final double[] upperbounds = { view.getWidth(), view.getHeight() };
		final int graphSize = n, swarmSize = 50;

		Swarmable[] swarms = new Swarmable[graphSize];
		for (int i = 0; i < graphSize; ++i)
			swarms[i] = new Swarmable(swarmSize);

		Element root = view.getSVGDocument().getDocumentElement();
			{
				HashSet<Swarmable> adjacentSwarms = new HashSet<Swarmable>();
				for (int i = 1; i < graphSize; ++i)
					{
						adjacentSwarms.add(swarms[i]);
						root.appendChild(new NeighborEdge(swarms[0], swarms[i], view));
					}
				swarms[0].setProblemSpace(new PlanarGraphLayoutProblem(adjacentSwarms,
				    PlanarGraphLayoutProblem.CENTERVIEWCENTER));
			}
		for (int i = 1; i < graphSize; ++i)
			{
				HashSet<Swarmable> adjacentSwarms = new HashSet<Swarmable>();
				adjacentSwarms.add(swarms[0]);
				if (i == graphSize - 1)
					adjacentSwarms.add(swarms[1]);
				else
					adjacentSwarms.add(swarms[i + 1]);
				if (i == 1)
					adjacentSwarms.add(swarms[graphSize - 1]);
				else
					adjacentSwarms.add(swarms[i - 1]);

				for (Swarmable p : adjacentSwarms)
					root.appendChild(new NeighborEdge(swarms[i], p, view));
				swarms[i].setProblemSpace(new PlanarGraphLayoutProblem(adjacentSwarms,
				    PlanarGraphLayoutProblem.ADJACENT));
			}

		for (int i = 0; i < graphSize; ++i)
			{
				for (Swarmable p : swarms[i])
					{
						p.randomize(lowerbounds, upperbounds);
						root.appendChild(new Particle2DAvatar(p, view));
						p.notifyObservers();
					}
				root.appendChild(new Node2DAvatar(swarms[i], view));
				swarms[i].notifySwarm();
				swarms[i].notifyObservers();
			}
		for (Swarmable swarm : swarms)
			swarm.beginSwarming();
	}

	public static void createNewSwarm04_01(SwarmView view)
	{
		final double[] upperbounds = { view.getWidth(), view.getHeight() };
		final int graphSize = 4, swarmSize = 50;

		Swarmable[] swarms = new Swarmable[graphSize];
		for (int i = 0; i < graphSize; ++i)
			swarms[i] = new Swarmable();

		Element root = view.getSVGDocument().getDocumentElement();
		for (Swarmable swarm : swarms)
			{
				HashSet<Swarmable> adjacentSwarms = new HashSet<Swarmable>();
				for (Swarmable p : swarms)
					if (p != swarm) adjacentSwarms.add(p);
				swarm.setProblemSpace(new PlanarGraphLayoutProblem(adjacentSwarms));
				swarm.randomize(lowerbounds, upperbounds);
				root.appendChild(new Particle2DAvatar(swarm, view));
				root.appendChild(new Node2DAvatar(swarm, view));
				swarm.notifyObservers();
			}

		for (int j = 1; j < swarmSize; ++j)
			{
				Swarmable p = new Swarmable(swarms[0]);
				p.randomize(lowerbounds, upperbounds);
				root.appendChild(new Particle2DAvatar(p, view));
				p.notifyObservers();
			}
		for (Swarmable p : swarms)
			if (p != swarms[0])
				{
					root.appendChild(new NeighborEdge(p, swarms[0], view));
					p.notifyObservers();
				}
		swarms[0].beginSwarming();
	}

}
