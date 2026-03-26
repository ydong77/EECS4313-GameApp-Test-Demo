package ca.yorku.eecs3311.util;

/**
 * Implement a modification of the Observer/Observable 
 * Design Pattern. See https://www.oodesign.com/observer-pattern.html 
 *
 * @author student
 *
 */
public interface Observer {
	public void update(Observable o);
}
