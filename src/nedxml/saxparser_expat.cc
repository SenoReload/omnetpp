//==========================================================================
//   SAXPARSER.CC -
//            part of OMNeT++
//
//==========================================================================

/*--------------------------------------------------------------*
  Copyright (C) 2002-2004 Andras Varga

  This file is distributed WITHOUT ANY WARRANTY. See the file
  `license' for details on this and other legal matters.
*--------------------------------------------------------------*/


#include <string.h>
#include <stdio.h>
#include <assert.h>
#include <stdio.h>
#include "expat.h"
#include "saxparser.h"


static void expatStartElementHandler(void *userData, const XML_Char *name, const XML_Char **atts)
{
    SAXHandler *sh = (SAXHandler *)userData;
    sh->startElement(name,atts);
}

static void expatEndElementHandler(void *userData, const XML_Char *name)
{
    SAXHandler *sh = (SAXHandler *)userData;
    sh->endElement(name);
}

static void expatCharacterDataHandler(void *userData, const XML_Char *s, int len)
{
    SAXHandler *sh = (SAXHandler *)userData;
    sh->characterData(s,len);
}

static void expatProcessingInstructionHandler(void *userData, const XML_Char *target, const XML_Char *data)
{
    SAXHandler *sh = (SAXHandler *)userData;
    sh->processingInstruction(target,data);
}

static void expatCommentHandler(void *userData, const XML_Char *data)
{
    SAXHandler *sh = (SAXHandler *)userData;
    sh->comment(data);
}


static void expatStartCdataSectionHandler(void *userData)
{
    SAXHandler *sh = (SAXHandler *)userData;
    sh->startCdataSection();
}

static void expatEndCdataSectionHandler(void *userData)
{
    SAXHandler *sh = (SAXHandler *)userData;
    sh->endCdataSection();
}


SAXParser::SAXParser()
{
    saxhandler = NULL;
}

void SAXParser::setHandler(SAXHandler *sh)
{
    saxhandler = sh;
    sh->setParser(this);
}

bool SAXParser::parse(const char *filename)
{
    // open file
    FILE *f = fopen(filename,"r");
    if (!f)
    {
        sprintf(errortext, "Cannot open file");
        return false;
    }

    // prepare parser
    XML_Parser parser = XML_ParserCreate(NULL);
    XML_SetUserData(parser, saxhandler);
    XML_SetStartElementHandler(parser, expatStartElementHandler);
    XML_SetEndElementHandler(parser, expatEndElementHandler);
    XML_SetCharacterDataHandler(parser, expatCharacterDataHandler);
    XML_SetProcessingInstructionHandler(parser, expatProcessingInstructionHandler);
    XML_SetCommentHandler(parser, expatCommentHandler);
    XML_SetStartCdataSectionHandler(parser, expatStartCdataSectionHandler);
    XML_SetEndCdataSectionHandler(parser, expatEndCdataSectionHandler);
    currentparser = &parser;

    // read file chunk-by-chunk and parse it
    char buf[BUFSIZ];
    bool done;
    bool err=false;
    do {
        size_t len = fread(buf, 1, sizeof(buf), f);
        done = len < sizeof(buf);
        if (!XML_Parse(parser, buf, len, done))
        {
            sprintf(errortext, "%s at line %d",
                    XML_ErrorString(XML_GetErrorCode(parser)),
                    XML_GetCurrentLineNumber(parser));
            err=true;
            done=true;
        }
    } while (!done);

    // cleanup and return
    XML_ParserFree(parser);
    return !err;
}

int SAXParser::getCurrentLineNumber()
{
    return XML_GetCurrentLineNumber(*(XML_Parser *)currentparser);
}


