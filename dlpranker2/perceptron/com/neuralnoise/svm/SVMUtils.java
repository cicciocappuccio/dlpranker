package com.neuralnoise.svm;

import gurobi.*;

public class SVMUtils {

	private SVMUtils() { }
	
	public static GRBEnv buildEnvironment() throws Exception {
		GRBEnv env = new GRBEnv();
		env.set(GRB.IntParam.OutputFlag, 0);
		return env;
	}
	
}
