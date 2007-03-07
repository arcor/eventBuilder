# 
# The contents of this file are subject to the Mozilla Public
# License Version 1.1 (the "License"); you may not use this file
# except in compliance with the License. You may obtain a copy of
# the License at http://www.mozilla.org/MPL/
# 
# Software distributed under the License is distributed on an "AS
# IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
# implied. See the License for the specific language governing
# rights and limitations under the License.
# 
# The Original Code is State Machine Compiler (SMC).
# 
# The Initial Developer of the Original Code is Charles W. Rapp.
# Portions created by Charles W. Rapp are
# Copyright (C) 2000 Charles W. Rapp.
# All Rights Reserved.
# 
# Contributor(s):
#
# RCS ID
# $Id: Makefile,v 1.3 2005/08/18 19:05:37 mcp Exp $
#
# CHANGE LOG
# $Log: Makefile,v $
# Revision 1.3  2005/08/18 19:05:37  mcp
# changes for exp control namespace and correct stop sequencing
#
# Revision 1.1  2005/01/17 22:09:24  shseo
# minor changes
#
# Revision 1.1.1.1  2005/01/06 23:33:15  mcp
# initial load
#
# Revision 1.1  2004/08/30 15:25:46  lvoicu
# First event builder template
#
# Revision 1.1.1.1  2004/08/19 20:14:10  mcp
# initial load
#
# Revision 1.1.1.1  2004/08/19 09:17:20  mcp
# initial load
#
# Revision 1.3  2004/05/17 17:52:20  mcp
# changed Makefile to produce mock stringproc for unit testing.
#
# Revision 1.2  2004/05/15 20:40:15  mcp
# fixed problems with state machine generation Makefile
#
# Revision 1.1  2004/05/14 23:20:27  mcp
# added Makefile for Smc engine
#
# Revision 1.1.1.1  2004/04/23 23:49:43  mcp
# initial file load
#
# Revision 1.1.1.1  2001/01/03 03:14:00  cwrapp
#
# ----------------------------------------------------------------------
# SMC - The State Map Compiler
# Version: 1.0, Beta 3
#
# SMC compiles state map descriptions into a target object oriented
# language. Currently supported languages are: C++, Java and [incr Tcl].
# SMC finite state machines have such features as:
# + Entry/Exit actions for states.
# + Transition guards
# + Transition arguments
# + Push and Pop transitions.
# + Default transitions. 
# ----------------------------------------------------------------------
#
# Revision 1.1.1.1  2000/08/02 12:51:01  charlesr
# Initial source import, SMC v. 1.0, Beta 1.
#

all :          EventBuilder

EventBuilder :
	java -classpath ../lib/daq-common.jar icecube.daq.common.CreateStateMachine ../ daq-common/resources/ eventBuilder-prod/resources/ EventBuilder eventBuilder-prod/src/icecube/daq/eventBuilder/
	mv resources/EventBuilderContext.java src/icecube/daq/eventBuilder/EventBuilderContext.java
	mv resources/EventBuilderContextTestContext.java src/icecube/daq/eventBuilder/test/EventBuilderContextTestContext.java

debug :
	java -jar ../tools/lib/Smc.jar -java -synch -g resources/EventBuilder.sm

	mv resources/EventBuilderContext.java src/icecube/daq/eventBuilder/EventBuilderContext.java

clean :
	-rm -f src/icecube/daq/eventBuilder/EventBuilderContext.java
	-rm -f src/icecube/daq/eventBuilder/test/EventBuilderContextTestContext.java
	-rm -f src/icecube/daq/eventBuilder/EventBuilderSigInterface.java
	-rm -f src/icecube/daq/eventBuilder/test/EventBuilderSigInterfaceTst.java
	-rm -f src/icecube/daq/eventBuilder/EventBuilderStateInterface.java
	-rm -f src/icecube/daq/eventBuilder/test/EventBuilderStateInterfaceTst.java
	-rm -f resources/EventBuilder.sm
	-rm -f resources/EventBuilderContextTest.sm
