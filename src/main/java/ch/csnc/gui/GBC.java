package ch.csnc.gui;

import java.awt.*;

/**
 * This takes the explanations provided on
 * https://cglab.ca/~mathieu/COMP1006/notes/COMP1406_2/1406Notes2.html#GridBagLayout
 * and puts them into a helper class that makes constructing a layout more convenient and readable.
 * I am used to the mathematical notation where a cell is indexed by specifying the row first and then the column.
 * Thus, the order is changed for all methods.
 */
public class GBC extends GridBagConstraints {
    /**
     * Construct a GridBagConstraints object and set x and y positions.
     * This is done by specifying the grid cell that the top-left corner of the component will be displayed in.
     *
     * @param row row index (y position)
     * @param row column index (x position)
     */
    public GBC(int row, int column) {
        this.gridx = column;
        this.gridy = row;
        this.anchor = WEST;
    }


    /**
     * Specify the number of rows (gridheight) and columns (gridwidth)  that the component will occupy.
     * The default value is 1.
     *
     * @param gridwidth  number of columns to occupy
     * @param gridheight number of rows to occupy
     * @return GBC object for chained calls
     */
    public GBC setSize(int gridheight, int gridwidth) {
        this.gridwidth = gridwidth;
        this.gridheight = gridheight;
        return this;
    }


    /**
     * Used in resizing when the component's display area is larger than the component's requested size to determine how to resize the component.
     * Possible values are:
     * <ul>
     * <li>GridBagConstraints.NONE (the default - the component will not grow in either direction)</li>
     * <li>GridBagConstraints.HORIZONTAL (make the component wide enough to fill its display area horizontally, but don't change its height)</li>
     * <li>GridBagConstraints.VERTICAL (make the component tall enough to fill its display area vertically, but don't change its width)</li>
     * <li>GridBagConstraints.BOTH (make the component fill its display area entirely)</li>
     * </ul>
     *
     * @param value
     * @return GBC object for chained calls
     */
    public GBC fill(int value) {
        this.fill = value;
        return this;
    }


    /**
     * Set internal padding within the layout
     *
     * @param ipady vertical padding
     * @param ipadx horizontal padding
     * @return GBC object for chained calls
     */
    public GBC setPadding(int ipady, int ipadx) {
        this.ipadx = ipadx;
        this.ipady = ipady;
        return this;
    }


    /**
     * Set external padding (=margin?) that defines the amount of space between the component and the edges of its display area.
     *
     * @param top    distance to top edge
     * @param left   distance to left edge
     * @param bottom distance to bottom edge
     * @param right  distance to right edge
     * @return GBC object for chained calls
     */
    public GBC setMargin(int top, int left, int bottom, int right) {
        this.insets = new Insets(top, left, bottom, right);
        return this;
    }


    /**
     * An anchor is used if the component is smaller than its display area.
     * It determines where to place the component within the available area.
     * If the area is resized, the anchor fixes the component to a corner or edge.
     *
     * @param anchor Anchor value
     * @return GBC object for chained calls
     */
    public GBC setAnchor(int anchor) {
        this.anchor = anchor;
        return this;
    }


    /**
     * Set weights to determine how space should be distributed when resizing the window.
     * If no weight is defined, all components will clump together in their container's center.
     * Thus, to shift everything towards the edge of the container, a non-zero weight must be defined for at least the last element.
     *
     * @param y vertical weight to distribute rows
     * @param x horizontal weight to distribute columns
     * @return GBC object for chained calls
     */
    public GBC setWeights(int y, int x) {
        this.weightx = x;
        this.weighty = y;
        return this;
    }


}
