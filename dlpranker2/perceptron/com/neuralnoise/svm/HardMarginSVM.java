package com.neuralnoise.svm;

import static org.junit.Assert.assertEquals;
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

public class HardMarginSVM<T> extends AbstractSVM<T> {
	
	public HardMarginSVM(GRBEnv env, Set<T> xs, Map<T, Boolean> ys, Table<T, T, Double> kernel) throws GRBException {
		super(xs, ys, kernel);

		for (Cell<T, T, Double> cell : this.kernel.cellSet()) {
			System.out.println(cell);
		}
		
		GRBModel model = new GRBModel(env);
		
		this.xs = Sets.intersection(xs, ys.keySet());
		Map<T, GRBVar> _alphas = Maps.newHashMap();
		
		int c = 0;
		
		// 0 \leq alpha
		for (T x : this.xs) {
			GRBVar alpha = model.addVar(0.0, GRB.INFINITY, 1.0, GRB.CONTINUOUS, "alpha" + (c++));
			_alphas.put(x, alpha);
		}
		
		model.update();
		
		// MAXIMIZE: W(alpha) = + alpha^T 1 - 1/2 alpha^T H alpha
		// Note: (H)ij = yi yj K(xi, xj)
		GRBQuadExpr obj = new GRBQuadExpr();
		for (T xi : this.xs) {
			obj.addTerm(1.0, _alphas.get(xi));
			for (T xj : this.xs) {
				double w = (ys.get(xi) ? 1.0 : -1.0) * (ys.get(xj) ? 1.0 : -1.0) * this.kernel.get(xi, xj);
				obj.addTerm(- w / 2.0, _alphas.get(xi), _alphas.get(xj));
			}
		}
		model.setObjective(obj, GRB.MAXIMIZE);
		
		// alpha^T y = 0
		GRBLinExpr expr = new GRBLinExpr();
		for (T x : this.xs) {
			expr.addTerm((ys.get(x) ? + 1.0 : - 1.0), _alphas.get(x));
		}
		model.addConstr(expr, GRB.EQUAL, 0.0, "eq");
		
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
			aty += alpha * (this.ys.get(x) ? + 1.0 : - 1.0);
		}
		assertEquals(aty, 0.0, EPS);
	}

}
