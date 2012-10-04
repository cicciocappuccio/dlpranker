#!/bin/bash

export GRB_LICENSE_FILE=$HOME/gurobi.lic
export LD_LIBRARY_PATH=$HOME/stuff/gurobi/5/gurobi500/linux32/lib/

export STHIS=`cat .classpath | grep kind=\"lib\" | tr " " "\n" | grep path= | tr "\"" " " | awk '{ print $2 }' | tr "\n" ":"`

# 7168, 12288
java -Xmx1024m -cp .:$STHIS:bin $@
