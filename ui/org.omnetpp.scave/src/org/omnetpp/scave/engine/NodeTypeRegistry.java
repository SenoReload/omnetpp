/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.25
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.omnetpp.scave.engine;

public class NodeTypeRegistry {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected NodeTypeRegistry(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(NodeTypeRegistry obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  public void delete() {
    if(swigCPtr != 0 && swigCMemOwn) {
      swigCMemOwn = false;
      throw new UnsupportedOperationException("C++ destructor does not have public access");
    }
    swigCPtr = 0;
  }

  public static NodeTypeRegistry instance() {
    long cPtr = ScaveEngineJNI.NodeTypeRegistry_instance();
    return (cPtr == 0) ? null : new NodeTypeRegistry(cPtr, false);
  }

  public boolean exists(String name) {
    return ScaveEngineJNI.NodeTypeRegistry_exists(swigCPtr, name);
  }

  public NodeType getNodeType(String name) {
    long cPtr = ScaveEngineJNI.NodeTypeRegistry_getNodeType(swigCPtr, name);
    return (cPtr == 0) ? null : new NodeType(cPtr, false);
  }

  public NodeTypeVector getNodeTypes() {
    return new NodeTypeVector(ScaveEngineJNI.NodeTypeRegistry_getNodeTypes(swigCPtr), true);
  }

  public void add(NodeType nodetype) {
    ScaveEngineJNI.NodeTypeRegistry_add(swigCPtr, NodeType.getCPtr(nodetype));
  }

  public void remove(NodeType nodetype) {
    ScaveEngineJNI.NodeTypeRegistry_remove(swigCPtr, NodeType.getCPtr(nodetype));
  }

}
