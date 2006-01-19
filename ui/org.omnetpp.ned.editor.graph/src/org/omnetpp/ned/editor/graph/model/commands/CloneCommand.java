/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.omnetpp.ned.editor.graph.model.commands;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.commands.Command;

import org.omnetpp.ned.editor.graph.misc.MessageFactory;
import org.omnetpp.ned.editor.graph.model.*;

/**
 * Clones a set of parts (copy operation)
 * @author rhornig
 *
 */
public class CloneCommand extends Command {

    private List parts, newTopLevelParts, newConnections;
    private Container parent;
    private Map bounds, indices, connectionPartMap;
    private ChangeGuideCommand vGuideCommand, hGuideCommand;
    private Guide hGuide, vGuide;
    private int hAlignment, vAlignment;

    public CloneCommand() {
        super(MessageFactory.CloneCommand_Label);
        parts = new LinkedList();
    }

    public void addPart(NedElement part, Rectangle newBounds) {
        parts.add(part);
        if (bounds == null) {
            bounds = new HashMap();
        }
        bounds.put(part, newBounds);
    }

    public void addPart(NedElement part, int index) {
        parts.add(part);
        if (indices == null) {
            indices = new HashMap();
        }
        indices.put(part, new Integer(index));
    }

    protected void clonePart(NedElement oldPart, Container newParent, Rectangle newBounds,
            List newConnections, Map connectionPartMap, int index) {
        NedElement newPart = null;

        if (oldPart instanceof SimpleModule) {
            newPart = new SimpleModule();
        } else if (oldPart instanceof Module) {
            newPart = new Module();
        } else if (oldPart instanceof Comment) {
            newPart = new Comment();
            ((Comment) newPart).setLabelContents(((Comment) oldPart).getLabelContents());
        }

        if (oldPart instanceof Container) {
            Iterator i = ((Container) oldPart).getChildren().iterator();
            while (i.hasNext()) {
                // for children they will not need new bounds
                clonePart((NedElement) i.next(), (Container) newPart, null, newConnections,
                        connectionPartMap, -1);
            }
        }

        Iterator i = oldPart.getTargetConnections().iterator();
        while (i.hasNext()) {
            Wire connection = (Wire) i.next();
            Wire newConnection = new Wire();
            newConnection.setTarget(newPart);
            newConnection.setTargetGate(connection.getTargetGate());
            newConnection.setSourceGate(connection.getSourceGate());
            newConnection.setSource(connection.getSource());

            Iterator b = connection.getBendpoints().iterator();
            Vector newBendPoints = new Vector();

            while (b.hasNext()) {
                WireBendpoint bendPoint = (WireBendpoint) b.next();
                WireBendpoint newBendPoint = new WireBendpoint();
                newBendPoint.setRelativeDimensions(bendPoint.getFirstRelativeDimension(), bendPoint
                        .getSecondRelativeDimension());
                newBendPoint.setWeight(bendPoint.getWeight());
                newBendPoints.add(newBendPoint);
            }

            newConnection.setBendpoints(newBendPoints);
            newConnections.add(newConnection);
        }

        if (index < 0) {
            newParent.addChild(newPart);
        } else {
            newParent.addChild(newPart, index);
        }

        newPart.setSize(oldPart.getSize());

        if (newBounds != null) {
            newPart.setLocation(newBounds.getTopLeft());
        } else {
            newPart.setLocation(oldPart.getLocation());
        }

        // keep track of the new parts so we can delete them in undo
        // keep track of the oldpart -> newpart map so that we can properly
        // attach
        // all connections.
        if (newParent == parent) newTopLevelParts.add(newPart);
        connectionPartMap.put(oldPart, newPart);
    }

    public void execute() {
        connectionPartMap = new HashMap();
        newConnections = new LinkedList();
        newTopLevelParts = new LinkedList();

        Iterator i = parts.iterator();

        NedElement part = null;
        while (i.hasNext()) {
            part = (NedElement) i.next();
            if (bounds != null && bounds.containsKey(part)) {
                clonePart(part, parent, (Rectangle) bounds.get(part), newConnections, connectionPartMap, -1);
            } else if (indices != null && indices.containsKey(part)) {
                clonePart(part, parent, null, newConnections, connectionPartMap,
                        ((Integer) indices.get(part)).intValue());
            } else {
                clonePart(part, parent, null, newConnections, connectionPartMap, -1);
            }
        }

        // go through and set the source of each connection to the proper
        // source.
        Iterator c = newConnections.iterator();

        while (c.hasNext()) {
            Wire conn = (Wire) c.next();
            NedElement source = conn.getSource();
            if (connectionPartMap.containsKey(source)) {
                conn.setSource((NedElement) connectionPartMap.get(source));
                conn.attachSource();
                conn.attachTarget();
            }
        }

        if (hGuide != null) {
            hGuideCommand = new ChangeGuideCommand((NedElement) connectionPartMap.get(parts.get(0)), true);
            hGuideCommand.setNewGuide(hGuide, hAlignment);
            hGuideCommand.execute();
        }

        if (vGuide != null) {
            vGuideCommand = new ChangeGuideCommand((NedElement) connectionPartMap.get(parts.get(0)), false);
            vGuideCommand.setNewGuide(vGuide, vAlignment);
            vGuideCommand.execute();
        }
    }

    public void setParent(Container parent) {
        this.parent = parent;
    }

    public void redo() {
        for (Iterator iter = newTopLevelParts.iterator(); iter.hasNext();)
            parent.addChild((NedElement) iter.next());
        for (Iterator iter = newConnections.iterator(); iter.hasNext();) {
            Wire conn = (Wire) iter.next();
            NedElement source = conn.getSource();
            if (connectionPartMap.containsKey(source)) {
                conn.setSource((NedElement) connectionPartMap.get(source));
                conn.attachSource();
                conn.attachTarget();
            }
        }
        if (hGuideCommand != null) hGuideCommand.redo();
        if (vGuideCommand != null) vGuideCommand.redo();
    }

    public void setGuide(Guide guide, int alignment, boolean isHorizontal) {
        if (isHorizontal) {
            hGuide = guide;
            hAlignment = alignment;
        } else {
            vGuide = guide;
            vAlignment = alignment;
        }
    }

    public void undo() {
        if (hGuideCommand != null) hGuideCommand.undo();
        if (vGuideCommand != null) vGuideCommand.undo();
        for (Iterator iter = newTopLevelParts.iterator(); iter.hasNext();)
            parent.removeChild((NedElement) iter.next());
    }

}