#!/bin/bash

java -cp build/jar/SurfStore.jar:lib/commons-codec-1.13.jar:lib/xmlrpc-2.0.jar:lib/xmlrpc-2.0-applet.jar Server $@
