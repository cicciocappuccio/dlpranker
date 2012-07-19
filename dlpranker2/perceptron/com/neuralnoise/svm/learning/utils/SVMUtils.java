package com.neuralnoise.svm.learning.utils;

import gurobi.GRB;
import gurobi.GRBEnv;

import java.util.Set;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DiagonalDoubleMatrix2D;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

public class SVMUtils {

	private SVMUtils() { }
	
	public static GRBEnv buildEnvironment() throws Exception {
		GRBEnv env = new GRBEnv();
		env.set(GRB.IntParam.OutputFlag, 0);
		return env;
	}
	
}
