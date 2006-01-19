/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 1.3.25
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.omnetpp.scave.engine;

class ScaveEngineJNI {

  static {
    try {
      System.loadLibrary("scave_engine");
    } catch (UnsatisfiedLinkError e) {
      System.err.println("Native code library failed to load. \n" + e);
      System.exit(1);
    }
  }

  public final static native long new_StringSet__SWIG_0();
  public final static native long new_StringSet__SWIG_1(long jarg1);
  public final static native long StringSet_size(long jarg1);
  public final static native boolean StringSet_empty(long jarg1);
  public final static native void StringSet_clear(long jarg1);
  public final static native void StringSet_insert(long jarg1, String jarg2);
  public final static native void StringSet_del(long jarg1, String jarg2);
  public final static native boolean StringSet_has_key(long jarg1, String jarg2);
  public final static native long StringSet_keys(long jarg1);
  public final static native void delete_StringSet(long jarg1);
  public final static native long new_StringVector__SWIG_0();
  public final static native long new_StringVector__SWIG_1(long jarg1);
  public final static native long StringVector_size(long jarg1);
  public final static native boolean StringVector_isEmpty(long jarg1);
  public final static native void StringVector_clear(long jarg1);
  public final static native void StringVector_add(long jarg1, String jarg2);
  public final static native String StringVector_get(long jarg1, int jarg2);
  public final static native void StringVector_set(long jarg1, int jarg2, String jarg3);
  public final static native void delete_StringVector(long jarg1);
  public final static native long new_IDVector__SWIG_0();
  public final static native long new_IDVector__SWIG_1(long jarg1);
  public final static native long IDVector_size(long jarg1);
  public final static native boolean IDVector_isEmpty(long jarg1);
  public final static native void IDVector_clear(long jarg1);
  public final static native void IDVector_add(long jarg1, long jarg2);
  public final static native long IDVector_get(long jarg1, int jarg2);
  public final static native void IDVector_set(long jarg1, int jarg2, long jarg3);
  public final static native void delete_IDVector(long jarg1);
  public final static native long new_IDVectorVector__SWIG_0();
  public final static native long new_IDVectorVector__SWIG_1(long jarg1);
  public final static native long IDVectorVector_size(long jarg1);
  public final static native boolean IDVectorVector_isEmpty(long jarg1);
  public final static native void IDVectorVector_clear(long jarg1);
  public final static native void IDVectorVector_add(long jarg1, long jarg2);
  public final static native long IDVectorVector_get(long jarg1, int jarg2);
  public final static native void IDVectorVector_set(long jarg1, int jarg2, long jarg3);
  public final static native void delete_IDVectorVector(long jarg1);
  public final static native long new_RunList__SWIG_0();
  public final static native long new_RunList__SWIG_1(long jarg1);
  public final static native long RunList_size(long jarg1);
  public final static native boolean RunList_isEmpty(long jarg1);
  public final static native void RunList_clear(long jarg1);
  public final static native void RunList_add(long jarg1, long jarg2);
  public final static native long RunList_get(long jarg1, int jarg2);
  public final static native void RunList_set(long jarg1, int jarg2, long jarg3);
  public final static native void delete_RunList(long jarg1);
  public final static native long new_FileList__SWIG_0();
  public final static native long new_FileList__SWIG_1(long jarg1);
  public final static native long FileList_size(long jarg1);
  public final static native boolean FileList_isEmpty(long jarg1);
  public final static native void FileList_clear(long jarg1);
  public final static native void FileList_add(long jarg1, long jarg2);
  public final static native long FileList_get(long jarg1, int jarg2);
  public final static native void FileList_set(long jarg1, int jarg2, long jarg3);
  public final static native void delete_FileList(long jarg1);
  public final static native int strdictcmp(String jarg1, String jarg2);
  public final static native long new_IDList__SWIG_0();
  public final static native long new_IDList__SWIG_1(long jarg1);
  public final static native long new_IDList__SWIG_2(long jarg1);
  public final static native void delete_IDList(long jarg1);
  public final static native long IDList_size(long jarg1);
  public final static native boolean IDList_isEmpty(long jarg1);
  public final static native void IDList_clear(long jarg1);
  public final static native void IDList_set__SWIG_0(long jarg1, long jarg2);
  public final static native void IDList_add(long jarg1, long jarg2);
  public final static native long IDList_get(long jarg1, int jarg2);
  public final static native void IDList_set__SWIG_1(long jarg1, int jarg2, long jarg3);
  public final static native void IDList_erase(long jarg1, int jarg2);
  public final static native void IDList_substract__SWIG_0(long jarg1, long jarg2);
  public final static native void IDList_merge(long jarg1, long jarg2);
  public final static native void IDList_substract__SWIG_1(long jarg1, long jarg2);
  public final static native void IDList_intersect(long jarg1, long jarg2);
  public final static native long IDList_getSubsetByIndices(long jarg1, int[] jarg2);
  public final static native long IDList_dup(long jarg1);
  public final static native void IDList_sortByFileAndRunRef(long jarg1, long jarg2);
  public final static native int IDList_itemTypes(long jarg1);
  public final static native boolean IDList_areAllScalars(long jarg1);
  public final static native boolean IDList_areAllVectors(long jarg1);
  public final static native void IDList_sortByDirectory(long jarg1, long jarg2, boolean jarg3);
  public final static native void IDList_sortByFileName(long jarg1, long jarg2, boolean jarg3);
  public final static native void IDList_sortByRun(long jarg1, long jarg2, boolean jarg3);
  public final static native void IDList_sortByModule(long jarg1, long jarg2, boolean jarg3);
  public final static native void IDList_sortByName(long jarg1, long jarg2, boolean jarg3);
  public final static native void IDList_sortScalarsByValue(long jarg1, long jarg2, boolean jarg3);
  public final static native void IDList_sortVectorsByLength(long jarg1, long jarg2, boolean jarg3);
  public final static native void IDList_sortVectorsByMean(long jarg1, long jarg2, boolean jarg3);
  public final static native void IDList_sortVectorsByStdDev(long jarg1, long jarg2, boolean jarg3);
  public final static native void IDList_reverse(long jarg1);
  public final static native void IDList_toByteArray(long jarg1, byte[] jarg2);
  public final static native void IDList_fromByteArray(long jarg1, byte[] jarg2);
  public final static native void set_ResultItem_run(long jarg1, long jarg2);
  public final static native long get_ResultItem_run(long jarg1);
  public final static native String ResultItem_getModuleName(long jarg1);
  public final static native String ResultItem_getName(long jarg1);
  public final static native long new_ResultItem();
  public final static native void delete_ResultItem(long jarg1);
  public final static native void set_ScalarResult_value(long jarg1, double jarg2);
  public final static native double get_ScalarResult_value(long jarg1);
  public final static native long new_ScalarResult();
  public final static native void delete_ScalarResult(long jarg1);
  public final static native void set_VectorResult_vectorId(long jarg1, int jarg2);
  public final static native int get_VectorResult_vectorId(long jarg1);
  public final static native long new_VectorResult();
  public final static native void delete_VectorResult(long jarg1);
  public final static native int get_File_VECTOR_FILE();
  public final static native int get_File_SCALAR_FILE();
  public final static native void set_File_fileType(long jarg1, int jarg2);
  public final static native int get_File_fileType(long jarg1);
  public final static native void set_File_resultFileManager(long jarg1, long jarg2);
  public final static native long get_File_resultFileManager(long jarg1);
  public final static native String File_getFilePath(long jarg1);
  public final static native void File_setFilePath(long jarg1, String jarg2);
  public final static native String File_getDirectory(long jarg1);
  public final static native void File_setDirectory(long jarg1, String jarg2);
  public final static native String File_getFileName(long jarg1);
  public final static native void File_setFileName(long jarg1, String jarg2);
  public final static native long new_File();
  public final static native void delete_File(long jarg1);
  public final static native void set_Run_file(long jarg1, long jarg2);
  public final static native long get_Run_file(long jarg1);
  public final static native void set_Run_runNumber(long jarg1, int jarg2);
  public final static native int get_Run_runNumber(long jarg1);
  public final static native void set_Run_lineNum(long jarg1, int jarg2);
  public final static native int get_Run_lineNum(long jarg1);
  public final static native String Run_getNetworkName(long jarg1);
  public final static native void Run_setNetworkName(long jarg1, String jarg2);
  public final static native String Run_getDate(long jarg1);
  public final static native void Run_setDate(long jarg1, String jarg2);
  public final static native String Run_getRunName(long jarg1);
  public final static native void Run_setRunName(long jarg1, String jarg2);
  public final static native String Run_getFileAndRunName(long jarg1);
  public final static native void Run_setFileAndRunName(long jarg1, String jarg2);
  public final static native long new_Run();
  public final static native void delete_Run(long jarg1);
  public final static native long new_ResultFileManager();
  public final static native void delete_ResultFileManager(long jarg1);
  public final static native long ResultFileManager_getFiles(long jarg1);
  public final static native long ResultFileManager_getRunsInFile(long jarg1, long jarg2);
  public final static native long ResultFileManager_getDataInFile(long jarg1, long jarg2);
  public final static native long ResultFileManager_getDataInRun(long jarg1, long jarg2);
  public final static native long ResultFileManager_getItem(long jarg1, long jarg2);
  public final static native boolean ResultFileManager_isVector(long jarg1, long jarg2);
  public final static native boolean ResultFileManager_isScalar(long jarg1, long jarg2);
  public final static native long ResultFileManager_getUniqueFiles(long jarg1, long jarg2);
  public final static native long ResultFileManager_getUniqueRuns(long jarg1, long jarg2);
  public final static native long ResultFileManager_getUniqueModuleNames(long jarg1, long jarg2);
  public final static native long ResultFileManager_getUniqueNames(long jarg1, long jarg2);
  public final static native long ResultFileManager_getAllScalars(long jarg1);
  public final static native long ResultFileManager_getScalar(long jarg1, long jarg2);
  public final static native long ResultFileManager_getAllVectors(long jarg1);
  public final static native long ResultFileManager_getVector(long jarg1, long jarg2);
  public final static native long ResultFileManager_uncheckedGetItem(long jarg1, long jarg2);
  public final static native long ResultFileManager_uncheckedGetScalar(long jarg1, long jarg2);
  public final static native long ResultFileManager_uncheckedGetVector(long jarg1, long jarg2);
  public final static native long ResultFileManager_getFilteredList(long jarg1, long jarg2, String jarg3, String jarg4, String jarg5);
  public final static native long ResultFileManager_loadScalarFile(long jarg1, String jarg2);
  public final static native long ResultFileManager_loadVectorFile(long jarg1, String jarg2);
  public final static native void ResultFileManager_unloadFile(long jarg1, long jarg2);
  public final static native boolean ResultFileManager_isFileLoaded(long jarg1, String jarg2);
  public final static native long ResultFileManager_getFile(long jarg1, String jarg2);
  public final static native long ResultFileManager_getRunByName(long jarg1, long jarg2, String jarg3);
  public final static native long ResultFileManager_getItemByName(long jarg1, long jarg2, String jarg3, String jarg4);
  public final static native long ResultFileManager_addScalarFile(long jarg1);
  public final static native long ResultFileManager_addVectorFile(long jarg1);
  public final static native long ResultFileManager_addRun(long jarg1, long jarg2);
  public final static native long ResultFileManager_getRunNameFilterHints(long jarg1, long jarg2);
  public final static native long ResultFileManager_getModuleFilterHints(long jarg1, long jarg2);
  public final static native long ResultFileManager_getNameFilterHints(long jarg1, long jarg2);
  public final static native long new_ScalarDataSorter(long jarg1);
  public final static native long ScalarDataSorter_groupByRunAndName(long jarg1, long jarg2);
  public final static native long ScalarDataSorter_groupByModuleAndName(long jarg1, long jarg2);
  public final static native long ScalarDataSorter_prepareScatterPlot(long jarg1, long jarg2, String jarg3, String jarg4);
  public final static native long ScalarDataSorter_getModuleAndNamePairs(long jarg1, long jarg2, int jarg3);
  public final static native long ScalarDataSorter_prepareCopyToClipboard(long jarg1, long jarg2);
  public final static native void delete_ScalarDataSorter(long jarg1);
  public final static native long new_StringMap__SWIG_0();
  public final static native long new_StringMap__SWIG_1(long jarg1);
  public final static native long StringMap_size(long jarg1);
  public final static native boolean StringMap_empty(long jarg1);
  public final static native void StringMap_clear(long jarg1);
  public final static native String StringMap_get(long jarg1, String jarg2);
  public final static native void StringMap_set(long jarg1, String jarg2, String jarg3);
  public final static native void StringMap_del(long jarg1, String jarg2);
  public final static native boolean StringMap_has_key(long jarg1, String jarg2);
  public final static native void delete_StringMap(long jarg1);
  public final static native long new_NodeTypeVector__SWIG_0();
  public final static native long new_NodeTypeVector__SWIG_1(long jarg1);
  public final static native long NodeTypeVector_size(long jarg1);
  public final static native boolean NodeTypeVector_isEmpty(long jarg1);
  public final static native void NodeTypeVector_clear(long jarg1);
  public final static native void NodeTypeVector_add(long jarg1, long jarg2);
  public final static native long NodeTypeVector_get(long jarg1, int jarg2);
  public final static native void NodeTypeVector_set(long jarg1, int jarg2, long jarg3);
  public final static native void delete_NodeTypeVector(long jarg1);
  public final static native long Port_node(long jarg1);
  public final static native long Node_nodeType(long jarg1);
  public final static native long Node_getArray(long jarg1);
  public final static native void delete_NodeType(long jarg1);
  public final static native String NodeType_name(long jarg1);
  public final static native String NodeType_category(long jarg1);
  public final static native String NodeType_description(long jarg1);
  public final static native boolean NodeType_isHidden(long jarg1);
  public final static native void NodeType_getAttributes(long jarg1, long jarg2);
  public final static native void NodeType_getAttrDefaults(long jarg1, long jarg2);
  public final static native void NodeType_validateAttrValues(long jarg1, long jarg2);
  public final static native long NodeType_create(long jarg1, long jarg2, long jarg3);
  public final static native long NodeType_getPort(long jarg1, long jarg2, String jarg3);
  public final static native long NodeTypeRegistry_instance();
  public final static native boolean NodeTypeRegistry_exists(long jarg1, String jarg2);
  public final static native long NodeTypeRegistry_getNodeType(long jarg1, String jarg2);
  public final static native long NodeTypeRegistry_getNodeTypes(long jarg1);
  public final static native void NodeTypeRegistry_add(long jarg1, long jarg2);
  public final static native void NodeTypeRegistry_remove(long jarg1, long jarg2);
  public final static native long new_DataflowManager();
  public final static native void delete_DataflowManager(long jarg1);
  public final static native void DataflowManager_connect(long jarg1, long jarg2, long jarg3);
  public final static native void DataflowManager_execute(long jarg1);
  public final static native void DataflowManager_dump(long jarg1);
  public final static native void delete_XYArray(long jarg1);
  public final static native int XYArray_length(long jarg1);
  public final static native double XYArray_getX(long jarg1, int jarg2);
  public final static native double XYArray_getY(long jarg1, int jarg2);
  public final static native long SWIGScalarResultUpcast(long jarg1);
  public final static native long SWIGVectorResultUpcast(long jarg1);
}
