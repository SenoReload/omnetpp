//=========================================================================
//  VECTORUTILS.H - part of
//                  OMNeT++/OMNEST
//           Discrete System Simulation in C++
//
//  Author: Andras Varga
//
//=========================================================================

/*--------------------------------------------------------------*
  Copyright (C) 1992-2015 Andras Varga
  Copyright (C) 2006-2015 OpenSim Ltd.

  This file is distributed WITHOUT ANY WARRANTY. See the file
  `license' for details on this and other legal matters.
*--------------------------------------------------------------*/

#ifndef __OMNETPP_SCAVE_VECTORUTILS_H
#define __OMNETPP_SCAVE_VECTORUTILS_H

#include <malloc.h>
#include <limits>
#include "scavedefs.h"
#include "resultfilemanager.h"
#include "xyarray.h"
#include "common/stlutil.h"
#include "common/stringutil.h"

namespace omnetpp {
namespace scave {

SCAVE_API int malloc_trim();

/**
 * Read the VectorResult items in the IDList into the XYArrays.
 */
SCAVE_API std::vector<XYArray *> readVectorsIntoArrays(ResultFileManager *manager, const IDList& idlist, bool includePreciseX, bool includeEventNumbers, size_t memoryLimitBytes = std::numeric_limits<size_t>::max(), double simTimeStart = -INFINITY, double simTimeEnd = INFINITY, const InterruptedFlag& interrupted = InterruptedFlag());

class SCAVE_API XYArrayVector {

    std::vector<XYArray *> data;

  public:
    explicit XYArrayVector(std::vector<XYArray *> &&data) : data(data) { }

    XYArrayVector(const XYArrayVector &other) = delete;

    size_t size() const { return data.size(); }
    XYArray &get(int i) { return *data.at(i); }

    ~XYArrayVector() {
        for (XYArray * a: data)
            delete a;
    }
};

SCAVE_API XYArrayVector *readVectorsIntoArrays2(ResultFileManager *manager, const IDList& idlist, bool includePreciseX, bool includeEventNumbers, size_t memoryLimitBytes = std::numeric_limits<size_t>::max(), double simTimeStart = -INFINITY, double simTimeEnd = INFINITY, const InterruptedFlag& interrupted = InterruptedFlag());

} // namespace scave
}  // namespace omnetpp

#endif

