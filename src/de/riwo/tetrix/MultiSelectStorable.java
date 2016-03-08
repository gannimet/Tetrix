package de.riwo.tetrix;

/**
 * Classes implementing this interface show that they are able
 * to handle the data (i.e. the selected items) of a dialog view
 * with multiple selectable elements.
 * @author richard
 *
 */
public interface MultiSelectStorable {

	/**
	 * Called whenever an item of the multi-select view was clicked,
	 * either to select or deselect it.
	 * @param index zero-based index of the clicked item
	 * @param value {@code true}, if the element was selected,
	 * {@code false} if it was deselected
	 */
	public void clicked(int index, boolean value);
	
}