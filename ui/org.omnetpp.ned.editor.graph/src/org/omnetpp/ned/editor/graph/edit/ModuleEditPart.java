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
package org.omnetpp.ned.editor.graph.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;
import org.eclipse.gef.editparts.ViewportAutoexposeHelper;
import org.eclipse.gef.editparts.ViewportExposeHelper;
import org.eclipse.gef.editparts.ViewportMouseWheelHelper;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.eclipse.gef.rulers.RulerProvider;
import org.omnetpp.ned.editor.graph.edit.policies.ContainerHighlightEditPolicy;
import org.omnetpp.ned.editor.graph.edit.policies.NedLayoutEditPolicy;
import org.omnetpp.ned.editor.graph.figures.ModuleFigure;
import org.omnetpp.ned.editor.graph.model.Module;

/**
 * Holds a circuit, which is a container capable of holding other
 * LogicEditParts.
 */
public class ModuleEditPart extends ContainerEditPart {

    protected void createEditPolicies() {
        super.createEditPolicies();
        installEditPolicy(EditPolicy.LAYOUT_ROLE, new NedLayoutEditPolicy((XYLayout) getContentPane()
                .getLayoutManager()));
// No container highlighting for the moment
//        installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new ContainerHighlightEditPolicy());
        installEditPolicy("Snap Feedback", new SnapFeedbackPolicy()); //$NON-NLS-1$
    }

    /**
     * Creates a new Module Figure and returns it.
     * 
     * @return Figure representing the module.
     */
    protected IFigure createFigure() {
        return new ModuleFigure();
    }

    /**
     * Returns the Figure of this as a ModuleFigure.
     * 
     * @return ModuleFigure of this.
     */
    protected ModuleFigure getModuleFigure() {
        return (ModuleFigure) getFigure();
    }

    public IFigure getContentPane() {
        return getModuleFigure().getContentsPane();
    }

    protected Module getModule() {
        return (Module) getModel();
    }

    public Object getAdapter(Class key) {
        if (key == AutoexposeHelper.class) return new ViewportAutoexposeHelper(this);
        if (key == ExposeHelper.class) return new ViewportExposeHelper(this);
        if (key == AccessibleAnchorProvider.class) return new DefaultAccessibleAnchorProvider() {
            public List getSourceAnchorLocations() {
                List list = new ArrayList();
                Vector sourceAnchors = getNedFigure().getSourceConnectionAnchors();
                Vector targetAnchors = getNedFigure().getTargetConnectionAnchors();
                for (int i = 0; i < sourceAnchors.size(); i++) {
                    ConnectionAnchor sourceAnchor = (ConnectionAnchor) sourceAnchors.get(i);
                    ConnectionAnchor targetAnchor = (ConnectionAnchor) targetAnchors.get(i);
                    list
                            .add(new Rectangle(sourceAnchor.getReferencePoint(), targetAnchor
                                    .getReferencePoint()).getCenter());
                }
                return list;
            }

            public List getTargetAnchorLocations() {
                return getSourceAnchorLocations();
            }
        };
        if (key == MouseWheelHelper.class) return new ViewportMouseWheelHelper(this);
        // snap to grig/guide adaptor
        if (key == SnapToHelper.class) {
            List snapStrategies = new ArrayList();
            Boolean val = (Boolean) getViewer().getProperty(RulerProvider.PROPERTY_RULER_VISIBILITY);
            if (val != null && val.booleanValue()) snapStrategies.add(new SnapToGuides(this));
            val = (Boolean) getViewer().getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED);
            if (val != null && val.booleanValue()) snapStrategies.add(new SnapToGeometry(this));
            val = (Boolean) getViewer().getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
            if (val != null && val.booleanValue()) snapStrategies.add(new SnapToGrid(this));

            if (snapStrategies.size() == 0) return null;
            if (snapStrategies.size() == 1) return (SnapToHelper) snapStrategies.get(0);

            SnapToHelper ss[] = new SnapToHelper[snapStrategies.size()];
            for (int i = 0; i < snapStrategies.size(); i++)
                ss[i] = (SnapToHelper) snapStrategies.get(i);
            return new CompoundSnapToHelper(ss);
        }
        return super.getAdapter(key);
    }

}
