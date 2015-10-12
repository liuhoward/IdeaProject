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

    /** for serialization */
    static final long serialVersionUID = -19412345060742748L;

    /** The filename extension that should be used for arff files */
    public final static String FILE_EXTENSION = ".sarff";

    /**
     * The filename extension that should be used for bin. serialized instances
     * files
     */
    public final static String SERIALIZED_OBJ_FILE_EXTENSION = ".bsi";

    /** The keyword used to denote the start of an arff header */
    public final static String SARFF_RELATION = "@relation";

    public final static String SARFF_Attribute = "@attribute";

    /** The keyword used to denote the start of the arff data section */
    public final static String SARFF_DATA = "@data";

    /** The dataset's name. */
    protected/* @spec_public non_null@ */String m_RelationName;

    /** The attribute information. */
    protected/* @spec_public non_null@ */FastVector m_Attributes;
  /*
   * public invariant (\forall int i; 0 <= i && i < m_Attributes.size();
   * m_Attributes.elementAt(i) != null);
   */

    /** The instances. */
    protected/* @spec_public non_null@ */FastVector m_Instances;

    /** The class attribute's index */
    protected int m_ClassIndex;
    // @ protected invariant classIndex() == m_ClassIndex;

    /**
     * The lines read so far in case of incremental loading. Since the
     * StreamTokenizer will be re-initialized with every instance that is read, we
     * have to keep track of the number of lines read so far.
     *
     */
    protected int m_Lines = 0;

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




