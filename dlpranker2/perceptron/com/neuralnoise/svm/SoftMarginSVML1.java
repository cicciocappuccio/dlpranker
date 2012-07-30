package com.neuralnoise.svm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBQuadExpr;
import gurobi.GRBVar;

import java.util.Map;
import java.util.Set;


import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.neuralnoise.svm.AbstractSVM;

public class SoftMarginSVML1<T> extends AbstractSVM<T> {

	private static final double C = 1;
	private double c;
	
	public SoftMarginSVML1(GRBEnv env, Set<T> xs, Map<T, Boolean> ys, Table<T, T, Double> kernel) throws GRBException {
		this(env, xs, ys, kernel, C);
	}
	
	public SoftMarginSVML1(GRBEnv env, Set<T> xs, Map<T, Boolean> ys, Table<T, T, Double> kernel, double c) throws GRBException {
		super(xs, ys, kernel);
		this.c = c;

		GRBModel model = new GRBModel(env);
		
		this.xs = Sets.intersection(xs, ys.keySet());
		Map<T, GRBVar> _alphas = Maps.newHashMap();
		
		// 0 \leq alpha \leq C1
		int i = 0;
		for (T x : this.xs) {
			GRBVar alpha = model.addVar(0.0, this.c, 1.0, GRB.CONTINUOUS, "alpha" + (i++));
			_alphas.put(x, alpha);
		}
		
		model.update();
		
		// MAXIMIZE: W(alpha) = - alpha^T H alpha
		// Note: (H)ij = yi yj K(xi, xj)
		GRBQuadExpr obj = new GRBQuadExpr();
		for (T xi : this.xs) {
			for (T xj : this.xs) {
				double w = (ys.get(xi) ? + 1.0 : -1.0) * (ys.get(xj) ? + 1.0 : -1.0) * this.kernel.get(xi, xj);
				obj.addTerm(- w, _alphas.get(xi), _alphas.get(xj));
			}
		}
		model.setObjective(obj, GRB.MAXIMIZE);
		
		// alpha^T y = 0
		// \sum alpha_i = 1
		GRBLinExpr expr0 = new GRBLinExpr();
		GRBLinExpr expr1 = new GRBLinExpr();
		for (T x : this.xs) {
			expr0.addTerm((ys.get(x) ? + 1.0 : - 1.0), _alphas.get(x));
			expr1.addTerm(1.0, _alphas.get(x));
		}
		model.addConstr(expr0, GRB.EQUAL, 0.0, "eq0");
		model.addConstr(expr1, GRB.EQUAL, 1.0, "eq1");
		
		model.optimize();

		this.alphas = Maps.newHashMap();
		for (T x : this.xs) {
			GRBVar alpha = _alphas.get(x);
			this.alphas.put(x, alpha.get(GRB.DoubleAttr.X));
		}
		
		this.b = 0.0;
		
		model.dispose();

		validate();
	}
	
	// Constraint 1: \sum_i alpha_i y_i = 0
	// Constraint 2: 0 \leq alpha \leq C
	private void validate() {
		double aty = 0.0;
		for (T x : this.xs) {
			double alpha = this.alphas.get(x);
			//System.out.println("alpha: " + alpha + ", C: " + c);
			assertTrue(0.0 <= alpha && alpha <= (c + EPS));
			aty += alpha * (this.ys.get(x) ? + 1.0 : - 1.0);
		}
		assertEquals(aty, 0.0, EPS);
	}
	
}