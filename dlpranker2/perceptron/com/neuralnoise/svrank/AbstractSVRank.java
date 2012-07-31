package com.neuralnoise.svrank;

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

public class AbstractSVRank<T> {

	public static final double EPS = 1e-8;
	
	protected Set<T> xs;
	protected Table<T, T, Double> kernel;
	protected Map<T, Integer> ys;
	protected double[] b;
	protected int ranks;
	
	protected Map<T, Double> alphas;

	public AbstractSVRank(Set<T> xs, Map<T, Integer> ys, int ranks, Table<T, T, Double> kernel) {
		this.xs = xs;
		this.ys = ys;
		this.ranks = ranks;
		this.b = new double[ranks];
		this.b[ranks - 1] = Double.POSITIVE_INFINITY;
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
	
	// f(x) = \sum_{i = 1}^{l} alpha_i k(x_{i}, x)
	public double _evaluate(T nx) {
		double ret = 0;
		for (Entry<T, Integer> ae : this.ys.entrySet()) {
			T x = ae.getKey();
			double alpha = alphas.get(x);
			ret += alpha * this.kernel.get(x, nx);
		}
		return ret;
	}
	
	public int rank(T object) {
		double sum = 0.0;
		for (T o : this.xs) {
			sum += (this.alphas.get(o) * this.kernel.get(object, o));
		}
		int ret = 0;
		while (b[ret] <= sum) {
			ret++;
		}
		ret++;
		return ret;
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
	
    public static DiagonalDoubleMatrix2D diagonal(DoubleMatrix2D M) {
        final int n = Math.min(M.rows(), M.columns());
        DiagonalDoubleMatrix2D D = new DiagonalDoubleMatrix2D(n, n, 0);
        for (int i = 0; i < n; ++i) {
                D.set(i, i, M.get(i, i));
        }
        return D;
    }
	
    public static DoubleMatrix2D normalizeKernel(DoubleMatrix2D K) {
        DoubleMatrix2D _K = new DenseDoubleMatrix2D(K.toArray());
        DiagonalDoubleMatrix2D Kii = diagonal(K);
        for (int i = 0; i < _K.rows(); ++i) {
                for (int j = 0; j < _K.columns(); ++j) {
                        double val = _K.get(i, j);
                        _K.set(i, j, val / Math.sqrt(Kii.get(i, i) * Kii.get(j, j)));
                }
        }
        return _K;
    }
	
}
