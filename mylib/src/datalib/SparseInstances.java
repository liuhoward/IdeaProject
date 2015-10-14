package datalib;

/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    Instances.java
 *    Copyright (C) 1999 University of Waikato, Hamilton, New Zealand
 *
 */


import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.SparseInstance;

/**
 * Class for handling an ordered set of weighted instances.
 * <p>
 *
 * Typical usage:
 * <p>
 *
 * <pre>
 * import weka.core.converters.ConverterUtils.DataSource;
 * ...
 *
 * // Read all the instances in the file (ARFF, CSV, XRFF, ...)
 * DataSource source = new DataSource(filename);
 * Instances instances = source.getDataSet();
 *
 * // Make the last attribute be the class
 * instances.setClassIndex(instances.numAttributes() - 1);
 *
 * // Print header and instances.
 * System.out.println("\nDataset:\n");
 * System.out.println(instances);
 *
 * ...
 * </pre>
 * <p>
 *
 * All methods that change a set of instances are safe, ie. a change of a set of
 * instances does not affect any other sets of instances. All methods that
 * change a datasets's attribute information clone the dataset before it is
 * changed.
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @author Len Trigg (trigg@cs.waikato.ac.nz)
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10497 $
 */

public class SparseInstances {

    public final static String FILE_EXTENSION = ".sarff";

    public final static String SARFF_Attribute = "@attribute";

    public final static String SARFF_DATA = "@data";

    /** The attribute information. */
    protected FastVector m_Attributes;

    /** The instances. */
    protected FastVector m_Instances;

    public SparseInstances(){

        m_Attributes = new FastVector();
        m_Instances = new FastVector();

    }

    public SparseInstances(int attCapacity, int instCapacity){
        if(attCapacity < 1 || instCapacity < 1){
            System.out.println("error: wrong parameter!\n");
        }else {
            m_Attributes = new FastVector(attCapacity);
            m_Instances = new FastVector(instCapacity);
        }
    }

    /**
     * Returns the number of attributes.
     *
     * @return the number of attributes as an integer
     */
    public int numAttributes() {

        return m_Attributes.size();
    }

    /**
     * Returns the number of instances in the dataset.
     *
     * @return the number of instances in the dataset as an integer
     */
    public int numInstances() {

        return m_Instances.size();
    }

    /**
     * Adds one instance to the end of the set. Shallow copies instance before it
     * is added. Increases the size of the dataset if it is not large enough. Does
     * not check if the instance is compatible with the dataset. Note: String or
     * relational values are not transferred.
     *
     * @param instance the instance to be added
     */
    public void addInstance(String id, SparseInstance instance) {

        ESparseInstance newInstance = new ESparseInstance(id, instance);

        m_Instances.addElement(newInstance);
    }

    public void addInstance(/* @non_null@ */Instance instance) {

        SparseInstance newInstance = new SparseInstance(instance);

        m_Instances.addElement(newInstance);
    }

    public void addAttribute(/* @non_null@ */Attribute attribute) {

        Attribute newAttribute = (Attribute)attribute.copy();

        m_Attributes.addElement(newAttribute);
    }

    public void addAttribute(/* @non_null@ */String attributeName) {

        Attribute newAttribute = new Attribute(attributeName);

        m_Attributes.addElement(newAttribute);
    }


    public/* @pure@ */Attribute attribute(int index) {

        return (Attribute) m_Attributes.elementAt(index);
    }

    public/* @pure@ */Attribute attribute(String name) {

        for (int i = 0; i < m_Attributes.size(); i++) {
            if (attribute(i).name().equals(name)) {
                return attribute(i);
            }
        }
        return null;
    }

    public int indexOfAttribute(String name) {

        for (int i = 0; i < m_Attributes.size(); i++) {
            if (attribute(i).name().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public double getElement(int instIndex, int attrIndex){
        if(instIndex < 0 ||instIndex > m_Instances.size() || attrIndex < 0 || attrIndex > m_Attributes.size())
        {
            System.out.println("error: wrong parameter!\n");
            return -1.0;
        }
        SparseInstance instance = (SparseInstance)m_Instances.elementAt(instIndex);
        return instance.value(attrIndex);
    }

    public ESparseInstance getESparseInstance(int instIndex){
        if(instIndex < 0 ||instIndex > m_Instances.size()){
            System.out.println("error: wrong parameter!\n");
            return null;
        }

        return (ESparseInstance)m_Instances.elementAt(instIndex);

    }

}




