#include <omnetpp.h>

USING_NAMESPACE

class Sink : public cSimpleModule
{
  protected:
    virtual void handleMessage(cMessage *msg);
};

Define_Module(Sink);

void Sink::handleMessage(cMessage *msg)
{
    delete msg;
}


