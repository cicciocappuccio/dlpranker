package com.neuralnoise.svm;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DiagonalDoubleMatrix2D;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

public class AbstractSVM<T> {

	public static final double EPS = 1e-8;
	
	protected Set<T> xs;
	protected Table<T, T, Double> kernel;
	protected Map<T, Boolean> ys;
	
	protected Map<T, Double> alphas;
	protected double b;
	
	public AbstractSVM(Set<T> xs, Map<T, Boolean> ys, Table<T, T, Double> kernel) {
		this.xs = xs;
		this.ys = ys;
		this.kernel = normalizeKernel(kernel);
	}
	
	public Table<T, T, Double> normalizeKernel(Table<T, T, Double> kernel) {
		Set<T> objects = kernel.rowKeySet();
		BiMap<T, Integer> indices = HashBiMap.create();
		int n = 0;
		for (T o : objects) {
			indices.put(o, n++);
		}
		DoubleMatrix2D K = new DenseDoubleMatrix2D(n, n);
		for (Cell<T, T, Double> cell : kernel.cellSet()) {
			T xi = cell.getRowKey(), xj = cell.getColumnKey();
			int i = indices.get(xi), j = indices.get(xj);
			K.set(i, j, cell.getValue());
		}
		DoubleMatrix2D nK = normalizeKernel(K);
		Table<T, T, Double> normalizedKernel = HashBasedTable.create();
		for (Cell<T, T, Double> cell : kernel.cellSet()) {
			T xi = cell.getRowKey(), xj = cell.getColumnKey();
			int i = indices.get(xi), j = indices.get(xj);
			normalizedKernel.put(xi, xj, nK.get(i, j));
		}
		return normalizedKernel;
	}
	
    public static DoubleMatrix2D normalizeKernel(DoubleMatrix2D K) {
    	final int n = K.rows();
        DoubleMatrix2D _K = new DenseDoubleMatrix2D(K.toArray());
        DiagonalDoubleMatrix2D Kii = new DiagonalDoubleMatrix2D(n, n, 0);
        for (int i = 0; i < _K.rows(); ++i) {
                for (int j = 0; j < _K.columns(); ++j) {
                        double val = _K.get(i, j);
                        _K.set(i, j, val / Math.sqrt(Kii.get(i, i) * Kii.get(j, j)));
                }
        }
        return _K;
    }
	
	// f(x) = sign(b + \sum_i alpha_i y_i K(x_i, x))
	public boolean evaluate(T nx) {
		double aykb = this.b;
		for (Entry<T, Double> ae : this.alphas.entrySet()) {
			T x = ae.getKey();
			double alpha = ae.getValue();
			aykb += alpha * (ys.get(x) ? + 1.0 : - 1.0) * this.kernel.get(x, nx);
		}
		return aykb >= 0;
	}
	
	@Override
	public String toString() {
		final String NL = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		for (Entry<T, Double> ae : this.alphas.entrySet()) {
			T x = ae.getKey();
			double alpha = ae.getValue();
			if (alpha > EPS) {
				sb.append(x + "'s alpha: " + alpha + NL);
			}
		}
		sb.append("b: " + this.b + NL);
		return sb.toString();
	}
	
	protected BiMap<T, Integer> makeIndices(Set<T> labeled, Set<T> unlabeled) {
		BiMap<T, Integer> indices = HashBiMap.create();
		int c = 0;
		for (T x : labeled)
			indices.put(x, Integer.valueOf(c++));
		for (T x : unlabeled)
			indices.put(x, Integer.valueOf(c++));
		return indices;
	}
	
	public Map<T, Boolean> getYs() {
		return this.ys;
	}
	
	public Table<T, T, Double> getKernel() {
		return this.kernel;
	}
	
	public Set<T> getXs() {
		return this.xs;
	}

	public Map<T, Double> getAlphas() {
		return this.alphas;
	}
	
	public double getB() {
		return this.b;
	}
}
