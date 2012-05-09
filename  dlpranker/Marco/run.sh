#!/bin/bash

java -Xmx1024M -cp .:bin:lib/*:lib/jena/*:lib/owlapi/* $@
