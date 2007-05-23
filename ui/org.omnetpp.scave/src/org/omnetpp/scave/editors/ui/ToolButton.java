package org.omnetpp.scave.editors.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.omnetpp.common.color.ColorFactory;

/**
 * A button which looks like ToolItem (flat), but text is rendered to the right 
 * of the image. ToolItem puts text below the image and cannot be talked out of it,
 * while Button has the right layout but it is never rendered as flat. Oh well.
 * 
 * This is expected to be put on white background. If used on gray or other colors,
 * colors used for painting will have to be refined (probably it's best to dynamically
 * calculate them, using RGB class's HSB conversion.)
 * 
 * @author Andras
 */
public class ToolButton extends Canvas {
	private static final int BORDER = 3;
	private List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();
	
	private Image image;
	private String text;
	private Font font;

	private static int NORMAL_STATE = 0;
	private static int ACTIVE_STATE = 1;
	private static int DOWN_STATE = 2;
	private int state = 0;
	
	public ToolButton(Composite parent, int style) {
		super(parent, style);
		setBackground(ColorFactory.WHITE);
		
		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				_repaint(e.gc);
			}
		});

		addMouseTrackListener(new MouseTrackListener() {
			public void mouseEnter(MouseEvent e) {
				if (isEnabled()) {
					state = ACTIVE_STATE;
					redraw();
				}
			}
			public void mouseExit(MouseEvent e) {
				if (isEnabled()) {
					state = NORMAL_STATE;
					redraw();
				}
			}
			public void mouseHover(MouseEvent e) {
			}
		});
		addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
				if (isEnabled() && e.button==1) {
					// Note: when DragSource is installed on the button, the mouseDown event
					// apparently gets intercepted and delayed ~1s by DragSource
					state = DOWN_STATE;
					redraw();
				}
			}

			public void mouseUp(MouseEvent e) {
				if (isEnabled() && e.button==1) {
					state = ACTIVE_STATE;
					redraw();
				
					Event tmp = new Event();
					tmp.display = e.display;
					tmp.widget = e.widget;
					tmp.x = e.x;
					tmp.y = e.y;
					tmp.stateMask = e.stateMask;
					SelectionEvent selectionEvent = new SelectionEvent(tmp);
					for (SelectionListener listener : selectionListeners)
						listener.widgetSelected(selectionEvent);
				}
			}
		});
	}

	//XXX experimental
	protected void _repaintY(GC gc) {
		final int SIZE = 24;
		final int R = 13;
		if (isEnabled()) {
			Rectangle r = getBounds();
			if (state==ACTIVE_STATE) {
				gc.setBackground(ColorFactory.GREY96);
				gc.fillRoundRectangle(0, 0, SIZE-1, SIZE-1, R, R);
			}
			else if (state==DOWN_STATE) {
				gc.setBackground(ColorFactory.GREY90);
				gc.fillRoundRectangle(0, 0, SIZE-1, SIZE-1, R, R);
			}

			if (image != null)
				gc.drawImage(image, SIZE/2 - image.getBounds().width/2, SIZE/2 - image.getBounds().height/2);
			if (text != null) {
				if (font != null)
					gc.setFont(font);
				gc.setBackground(getBackground());
				gc.drawText(text, SIZE + BORDER, BORDER); //XXX y alignment!
			}
			if (state==ACTIVE_STATE) {
				gc.setForeground(ColorFactory.GREY40);
				gc.setAntialias(SWT.ON);
				gc.drawRoundRectangle(0, 0, SIZE-1, SIZE-1, R, R);
				//gc.setForeground(ColorFactory.ORANGE);
				gc.setForeground(ColorFactory.GREY75);
				gc.drawRoundRectangle(1, 1, SIZE-3, SIZE-3, R-2, R-2);
			}
			else if (state==DOWN_STATE) {
				gc.setForeground(ColorFactory.GREY40);
				gc.setAntialias(SWT.ON);
				gc.drawRoundRectangle(0, 0, SIZE-1, SIZE-1, R, R);
				gc.setForeground(ColorFactory.GREY95);
				gc.drawRoundRectangle(1, 1, SIZE-3, SIZE-3, R-2, R-2);
			}
		}
		else {
			if (image != null)
				gc.drawImage(image, BORDER, BORDER); //TODO paint disabled image
			if (text != null) {
				if (font != null)
					gc.setFont(font);
				gc.setForeground(ColorFactory.GREY50);
				gc.drawText(text, BORDER+image.getBounds().width+BORDER, BORDER); //XXX y alignment!
			}
		}
	}

	//XXX experimental
	protected void _repaintZ(GC gc) {
		final int SIZE = 26;
		if (isEnabled()) {
			Rectangle r = getBounds();
			if (state==ACTIVE_STATE) {
				gc.setBackground(ColorFactory.GREY96);
				//gc.fillRoundRectangle(0, 0, r.width-1, r.height-1, 5, 5);
				gc.fillOval(0, 0, SIZE-1, SIZE-1);
			}
			else if (state==DOWN_STATE) {
				gc.setBackground(ColorFactory.GREY90);
				//gc.fillRoundRectangle(0, 0, r.width-1, r.height-1, 5, 5);
				gc.fillOval(0, 0, SIZE-1, SIZE-1);
			}

			if (image != null)
				gc.drawImage(image, SIZE/2 - image.getBounds().width/2, SIZE/2 - image.getBounds().height/2);
			if (text != null) {
				if (font != null)
					gc.setFont(font);
				gc.drawText(text, SIZE + BORDER, BORDER); //XXX y alignment!
			}
			if (state==ACTIVE_STATE) {
				gc.setForeground(ColorFactory.GREY40);
				gc.setAntialias(SWT.ON);
				//gc.drawRoundRectangle(0, 0, r.width-1, r.height-1, 5, 5);
				gc.drawOval(0, 0, SIZE-1, SIZE-1);
				gc.setForeground(ColorFactory.ORANGE);
				//gc.drawRoundRectangle(1, 1, r.width-3, r.height-3, 3, 3);
				gc.drawOval(1, 1, SIZE-3, SIZE-3);
			}
			else if (state==DOWN_STATE) {
				gc.setForeground(ColorFactory.GREY40);
				gc.setAntialias(SWT.ON);
				//gc.drawRoundRectangle(0, 0, r.width-1, r.height-1, 5, 5);
				gc.drawOval(0, 0, SIZE-1, SIZE-1);
				gc.setForeground(ColorFactory.GREY95);
				//gc.drawRoundRectangle(1, 1, r.width-3, r.height-3, 3, 3);
				gc.drawOval(1, 1, SIZE-3, SIZE-3);
			}
		}
		else {
			if (image != null)
				gc.drawImage(image, BORDER, BORDER); //TODO paint disabled image
			if (text != null) {
				if (font != null)
					gc.setFont(font);
				gc.setForeground(ColorFactory.GREY50);
				gc.drawText(text, BORDER+image.getBounds().width+BORDER, BORDER); //XXX y alignment!
			}
		}
	}

	//XXX experimental
	protected void _repaint(GC gc) {
		if (isEnabled()) {
			Rectangle r = getBounds();
			if (state==ACTIVE_STATE) {
				gc.setBackground(ColorFactory.GREY96);
				gc.fillRoundRectangle(0, 0, r.width-1, r.height-1, 5, 5);
			}
			else if (state==DOWN_STATE) {
				gc.setBackground(ColorFactory.GREY90);
				gc.fillRoundRectangle(0, 0, r.width-1, r.height-1, 5, 5);
			}

			if (image != null)
				gc.drawImage(image, BORDER, BORDER);
			if (text != null) {
				if (font != null)
					gc.setFont(font);
				gc.drawText(text, BORDER+image.getBounds().width+BORDER, BORDER); //XXX y alignment!
			}
			if (state==ACTIVE_STATE) {
				gc.setForeground(ColorFactory.GREY40);
				gc.setAntialias(SWT.ON);
				gc.drawRoundRectangle(0, 0, r.width-1, r.height-1, 5, 5);
				gc.setForeground(ColorFactory.ORANGE);
				gc.drawRoundRectangle(1, 1, r.width-3, r.height-3, 3, 3);
			}
			else if (state==DOWN_STATE) {
				gc.setForeground(ColorFactory.GREY40);
				gc.setAntialias(SWT.ON);
				gc.drawRoundRectangle(0, 0, r.width-1, r.height-1, 5, 5);
				gc.setForeground(ColorFactory.GREY95);
				gc.drawRoundRectangle(1, 1, r.width-3, r.height-3, 3, 3);
			}
		}
		else {
			if (image != null)
				gc.drawImage(image, BORDER, BORDER); //TODO paint disabled image
			if (text != null) {
				if (font != null)
					gc.setFont(font);
				gc.setForeground(ColorFactory.GREY50);
				gc.drawText(text, BORDER+image.getBounds().width+BORDER, BORDER); //XXX y alignment!
			}
		}
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		checkWidget();
		int border = getBorderWidth();
		int width = image==null ? 0 : image.getBounds().width;
		int height = image==null ? 0 : image.getBounds().height;
		width += 80; //XXX
		if (wHint != SWT.DEFAULT) width = wHint;
		if (hHint != SWT.DEFAULT) height = hHint;

		width += border * 2 + BORDER * 2; 
		height += border * 2 + BORDER * 2;
		//height = 27; //XXX
		return new Point(width, height);
	}

	public Image getImage() {
		checkWidget();
		return image;
	}

	public String getText() {
		checkWidget();
		return text;
	}

	public void setImage(Image image) {
		checkWidget();
		if (image != null && image.isDisposed()) SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		this.image = image;
	}

	public void setText(String string) {
		checkWidget();
		if (string == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		text = string;
	}

	public void addSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		if (!selectionListeners.contains(listener))
			selectionListeners.add(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		selectionListeners.remove(listener);
	}

}
