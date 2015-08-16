package maps;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseInterpreter implements MouseMotionListener, 
MouseListener, MouseWheelListener {

	protected int x = -1, y = -1;
	
	protected int click_x = -1, click_y = -1;
	
	protected volatile int zoom = 0;

	private Boolean clicked = new Boolean(false);
	
	public enum MouseState { NONE, LEFT, RIGHT, LEFT_SHIFT, RIGHT_SHIFT }

	public enum DragState { DRAG, NOT_DRAG }
	
	private MouseState curr = MouseState.NONE;
	
	
	public MouseState getMouseClicked() {
		synchronized (clicked) {
			if (clicked) {
				clicked = false;
				return curr;
			} else {
				return MouseState.NONE;
			}
		}
	}

	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		zoom = e.getWheelRotation();
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		synchronized (clicked) {
			click_x = e.getX();
			click_y = e.getY();
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (e.isShiftDown()) {
					curr = MouseState.LEFT_SHIFT;
				} else {
					curr = MouseState.LEFT;
				}
			} else {
				if (e.isShiftDown()) {
					curr = MouseState.RIGHT_SHIFT;
				} else {
					curr = MouseState.RIGHT;
				}
			}
			clicked = true;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}

}

