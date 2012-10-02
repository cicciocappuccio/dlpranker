package utils;

import org.jblas.DoubleMatrix;
import org.jblas.MatrixFunctions;

import cern.colt.matrix.tdcomplex.DComplexMatrix2D;
import cern.colt.matrix.tdcomplex.impl.DenseDComplexMatrix2D;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;
import cern.colt.matrix.tdouble.algo.decomposition.DenseDoubleEigenvalueDecomposition;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.tdouble.impl.DiagonalDoubleMatrix2D;
import cern.jet.math.tdcomplex.DComplexFunctions;
import cern.jet.math.tdouble.DoubleFunctions;

//import com.neuralnoise.transduction.kernel.AbstractKernel;

public class MatrixUtils {

	private MatrixUtils() { }
	
	public static DoubleMatrix2D identityMatrix(int n) {
		return diagonal(n, 1.0);
	}
	
	public static DiagonalDoubleMatrix2D diagonal(int n, double diagonal) {
		DiagonalDoubleMatrix2D D = new DiagonalDoubleMatrix2D(n, n, 0);
		for (int i = 0; i < n; ++i) {
			D.set(i, i, diagonal);
		}
		return D;
	}
	
	public static DiagonalDoubleMatrix2D diagonal(DoubleMatrix2D M) {
		final int n = Math.min(M.rows(), M.columns());
		DiagonalDoubleMatrix2D D = new DiagonalDoubleMatrix2D(n, n, 0);
		for (int i = 0; i < n; ++i) {
			D.set(i, i, M.get(i, i));
		}
		return D;
	}
	
	public static DiagonalDoubleMatrix2D diagonal(int n, int l, double diagonal) {
		DiagonalDoubleMatrix2D D = new DiagonalDoubleMatrix2D(n, n, 0);
		for (int i = 0; i < Math.min(n, l); ++i) {
			D.set(i, i, diagonal);
		}
		return D;
	}
	
	public static DiagonalDoubleMatrix2D diagonal(double[] diagonal) {
		int n = diagonal.length;
		DiagonalDoubleMatrix2D D = new DiagonalDoubleMatrix2D(n, n, 0);
		for (int i = 0; i < n; ++i) {
			D.set(i, i, diagonal[i]);
		}
		return D;
	}
	
	public static DiagonalDoubleMatrix2D fractionalPow(DiagonalDoubleMatrix2D D, double power) {
		DiagonalDoubleMatrix2D _D = new DiagonalDoubleMatrix2D(D.rows(), D.columns(), 0);
		for (int i = 0; i < Math.min(_D.rows(), _D.columns()); ++i) {
			double dii = D.get(i, i);
			_D.set(i, i, (dii != 0.0 ? Math.pow(dii, power) : 0.0));
		}
		return _D;
	}
	
	public static DoubleMatrix2D fractionalPow(DoubleMatrix2D M, double power) {
		DenseDoubleEigenvalueDecomposition ed =	new DenseDoubleEigenvalueDecomposition(M);
		DenseDoubleAlgebra dalg = new DenseDoubleAlgebra();
		DComplexMatrix2D _D = new DenseDComplexMatrix2D(ed.getD());
		_D.assign(DComplexFunctions.pow1(power));
		for (int i = 0; i < _D.rows(); ++i) {
			for (int j = 0; j < _D.rows(); ++j) {
				if (i != j) {
					_D.set(i, j, new double[] { 0, 0 });
				}
			}
		}
		return dalg.mult(dalg.mult(ed.getV(), _D.getRealPart()), dalg.inverse(ed.getV()));
	}
	
	public static DoubleMatrix1D sum(DoubleMatrix1D A, DoubleMatrix1D B) {
		DoubleMatrix1D S = clone(A);
		S.assign(B, DoubleFunctions.plus);
		return S;
	}
	
	public static DoubleMatrix2D sum(DoubleMatrix2D A, DoubleMatrix2D B) {
		DoubleMatrix2D S = clone(A);
		S.assign(B, DoubleFunctions.plus);
		return S;
	}

	public static DoubleMatrix2D subtraction(DoubleMatrix2D A, DoubleMatrix2D B) {
		DoubleMatrix2D S = clone(A);
		S.assign(B, DoubleFunctions.minus);
		return S;
	}
	
	public static DoubleMatrix1D subtraction(DoubleMatrix1D A, DoubleMatrix1D B) {
		DoubleMatrix1D S = clone(A);
		S.assign(B, DoubleFunctions.minus);
		return S;
	}

	public static DoubleMatrix2D product(DoubleMatrix2D A, DoubleMatrix2D B) {
		DenseDoubleAlgebra alg = new DenseDoubleAlgebra();
		return alg.mult(A, B);
	}
	
	public static DoubleMatrix1D product(DoubleMatrix2D A, DoubleMatrix1D B) {
		DenseDoubleAlgebra alg = new DenseDoubleAlgebra();
		return alg.mult(A, B);
	}
	
	public static DoubleMatrix2D products(DoubleMatrix2D ... As) {
		DenseDoubleAlgebra alg = new DenseDoubleAlgebra();
		DoubleMatrix2D R = As[0];
		for (int i = 1; i < As.length; ++i) {
			R = alg.mult(R, As[i]);
		}
		return R;
	}
	
	public static DoubleMatrix1D product(DoubleMatrix1D A, double b) {
		DoubleMatrix1D bA = clone(A);
		bA.assign(DoubleFunctions.mult(b));
		return bA;
	}
	
	public static DoubleMatrix2D product(DoubleMatrix2D A, double b) {
		DoubleMatrix2D bA = clone(A);
		bA.assign(DoubleFunctions.mult(b));
		return bA;
	}

	public static DoubleMatrix1D vec(DoubleMatrix2D M) {
		int m = M.rows(), n = M.columns();
		DoubleMatrix1D vec = new DenseDoubleMatrix1D(m * n);
		for (int i = 0; i < m; ++i) {
			for (int j = 0; j < n; ++j) {
				vec.set(i + (m * j), M.get(i, j));
			}
		}
		return vec;
	}
	
	public static DoubleMatrix2D kron(DoubleMatrix2D A, DoubleMatrix2D B) {
		DenseDoubleAlgebra alg = new DenseDoubleAlgebra();
		return alg.kron(A, B);
	}

	public static DoubleMatrix2D inverse(DoubleMatrix2D A) {
		DenseDoubleAlgebra alg = new DenseDoubleAlgebra();
		return alg.inverse(A);
	}

	public static DoubleMatrix1D clone(DoubleMatrix1D A) {
		return new DenseDoubleMatrix1D(A.toArray());
	}
	
	public static DoubleMatrix2D clone(DoubleMatrix2D A) {
		return new DenseDoubleMatrix2D(A.toArray());
	}

	public static DoubleMatrix2D transpose(DoubleMatrix2D A) {
		DenseDoubleAlgebra alg = new DenseDoubleAlgebra();
		return alg.transpose(A);
	}
	
	public static DoubleMatrix2D subMatrix(DoubleMatrix2D A, int a, int b, int c, int d) {
		DenseDoubleAlgebra alg = new DenseDoubleAlgebra();
		return alg.subMatrix(A, a, b, c, d);
	}

	public static DoubleMatrix2D normalizeKernel(DoubleMatrix2D K) {
		DoubleMatrix2D _K = clone(K);
		DiagonalDoubleMatrix2D Kii = diagonal(K);
		for (int i = 0; i < _K.rows(); ++i) {
			for (int j = 0; j < _K.columns(); ++j) {
				double val = _K.get(i, j);
				_K.set(i, j, val / Math.sqrt(Kii.get(i, i) * Kii.get(j, j)));
			}
		}
		return _K;
	}
	
	public static DoubleMatrix2D KtoE(DoubleMatrix2D K) {
		DoubleMatrix2D E = new DenseDoubleMatrix2D(K.rows(), K.columns());
		for (int i = 0; i < E.rows(); ++i) {
			double Kii = K.get(i, i);
			for (int j = 0; j < E.columns(); ++j) {
				double Kjj = K.get(j, j);
				double val = K.get(i, j);
				E.set(i, j, val - 0.5 * (Kii + Kjj));
			}
		}
		return E;
	}
	
/*
	public static DoubleMatrix2D EtoW(DoubleMatrix2D E, AbstractKernel kernel) {
		DoubleMatrix2D W = new DenseDoubleMatrix2D(E.rows(), E.columns());
		for (int i = 0; i < E.rows(); ++i) {
			for (int j = 0; j < E.columns(); ++j) {
				double val = E.get(i, j);
				W.set(i, j, kernel.givenEuclideanDistance(val));
			}
		}
		return W;
	}
*/
	
	public static DoubleMatrix2D eigenvalues(DoubleMatrix2D M) {
		DenseDoubleEigenvalueDecomposition ed =	new DenseDoubleEigenvalueDecomposition(M);
		return ed.getD();
	}
	
	public static DoubleMatrix2D expm(DoubleMatrix2D M) {
		DoubleMatrix _M = new DoubleMatrix(M.toArray());
		DoubleMatrix _expM = MatrixFunctions.expm(_M);
		
		DoubleMatrix2D expB = new DenseDoubleMatrix2D(_expM.toArray2());
		return expB;
	}
	
	
}
