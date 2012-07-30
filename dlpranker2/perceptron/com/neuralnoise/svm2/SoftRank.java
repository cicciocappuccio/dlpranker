package com.neuralnoise.svm2;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBQuadExpr;
import gurobi.GRBVar;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

public class SoftRank<T> extends AbstractSVRank<T> {

	public SoftRank(GRBEnv env, Set<T> xs, Map<T, Integer> ys, int ranks, Table<T, T, Double> kernel, double v) throws GRBException {
		super(xs, ys, ranks, kernel);
		
		GRBModel model = new GRBModel(env);
		
		this.xs = Sets.intersection(xs, ys.keySet());
		double l = this.xs.size();
		Map<T, GRBVar> _alphasL = Maps.newHashMap();
		Map<T, GRBVar> _alphasU = Maps.newHashMap();
		
		int c = 0;
		
		// 0 \leq alpha
		for (T x : this.xs) {
			// 0 <= aLi <= (1/vl)
			GRBVar alphaL = model.addVar(- GRB.INFINITY, 1.0 / (v * l), 1.0, GRB.CONTINUOUS, "alphaL" + (c));
		
			// aUi >= 0
			GRBVar alphaU = model.addVar(0.0, GRB.INFINITY, 1.0, GRB.CONTINUOUS, "alphaU" + (c));
			
			_alphasL.put(x, alphaL);
			_alphasU.put(x, alphaU);
			
			c += 1;
		}
		
		model.update();
		
		// MAXIMIZE: W(alphaU, alphaL) = - \sum_{i, j = 1}^{l}
		//		(alphaU_i - alphaL_i) (alphaU_j - alphaL_j) k(xi, xj)
		// -> - \sum_ij aU_i aU_j Kij - aU_i aL_j Kij - aL_i aU_j Kij + aL_i aL_j Kij
		// -> \sum - aU_i aU_j Kij + aU_i aL_j Kij + aL_i aU_j Kij - aL_i aL_j Kij
		GRBQuadExpr obj = new GRBQuadExpr();
		for (T xi : this.xs) {
			GRBVar aLi = _alphasL.get(xi);
			GRBVar aUi = _alphasU.get(xi);
			for (T xj : this.xs) {
				GRBVar aLj = _alphasL.get(xj);
				GRBVar aUj = _alphasU.get(xj);
				
				Double Kij = this.kernel.get(xi, xj);
				
				obj.addTerm(- Kij, aUi, aUj);
				obj.addTerm(Kij, aUi, aLj);
				obj.addTerm(Kij, aLi, aUj);
				obj.addTerm(- Kij, aLi, aLj);
			}
		}
		
		model.setObjective(obj, GRB.MAXIMIZE);
		
		// \sum_{i:yi=y} aLi = \sum_{i:yi=y-1} aUi
		
		for (int rank = 2; rank <= this.ranks; ++rank) {
			GRBLinExpr expr = new GRBLinExpr();
			for (Entry<T, Integer> entry : ys.entrySet()) {
				if (entry.getValue() == rank) {
					GRBVar aLi = _alphasL.get(entry.getKey());
					expr.addTerm(1.0, aLi);
				} else if (entry.getValue() == (rank - 1)) {
					GRBVar aUi = _alphasU.get(entry.getKey());
					expr.addTerm(- 1.0, aUi);
				}
			}
			model.addConstr(expr, GRB.EQUAL, 0.0, "eq0");
		}
		
		
		// \sum aUi + aLi = 1
		GRBLinExpr expr = new GRBLinExpr();
		for (T x : this.xs) {
			GRBVar aUi = _alphasU.get(x);
			GRBVar aLi = _alphasL.get(x);
			
			expr.addTerm(1.0, aUi);
			expr.addTerm(1.0, aLi);
		}
		model.addConstr(expr, GRB.EQUAL, 1.0, "eq1");
		
		model.optimize();

		Map<T, Double> alphasL = Maps.newHashMap();
		Map<T, Double> alphasU = Maps.newHashMap();
		
		this.alphas = Maps.newHashMap();
		
		
		for (T x : this.xs) {
			
			GRBVar alphaL = _alphasL.get(x);
			GRBVar alphaU = _alphasU.get(x);

			double aL = alphaL.get(GRB.DoubleAttr.X);
			double aU = alphaU.get(GRB.DoubleAttr.X);
			
			alphasL.put(x, aL);
			alphasU.put(x, aU);
			
			this.alphas.put(x, Double.valueOf(aU - aL));
		}
		
		for (int rank = 1; rank < ranks; ++rank) {
		
			T xi = null, xj = null;
			for (T x : this.xs) {
				if (xi == null) {
					if (ys.get(x) == rank) {
						double alphaL = alphasL.get(x);
						if (alphaL > EPS && alphaL < (1.0 / (v * l))) {
							xi = x;
						}
					}
				}
				
				if (xj == null) {
					if (ys.get(x) == (rank + 1)) {
						double alphaU = alphasL.get(x);
						if (alphaU > EPS && alphaU < (1.0 / (v * l))) {
							xj = x;
						}
					}
				}
			}
			
			this.b[rank - 1] = 0.5 * (_evaluate(xi) + _evaluate(xj));
		}
		
		model.dispose();
	}
	
}
