#! /bin/bash

# Skip running javadoc, so I do not have to bother to install it.

mvn -Dmaven.javadoc.skip=true clean install
