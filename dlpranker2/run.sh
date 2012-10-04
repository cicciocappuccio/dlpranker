#!/bin/bash

export GRB_LICENSE_FILE=$HOME/gurobi.lic
export LD_LIBRARY_PATH=$HOME/stuff/gurobi/5/gurobi500/linux32/lib/

export OL=../../workspace2/OptimizationLight/
export SOL=`cat $OL/.classpath |  grep kind=\"lib\" | tr " " "\n" | grep path= | tr "\"" " " | awk '{ print "$OL" $2 }' | tr "\n" ":"`

export STHIS=`cat .classpath | grep kind=\"lib\" | tr " " "\n" | grep path= | tr "\"" " " | awk '{ print $2 }' | tr "\n" ":"`

java -Xmx1024m -cp .:$STHIS:$OL/bin:$SOL:bin $@
