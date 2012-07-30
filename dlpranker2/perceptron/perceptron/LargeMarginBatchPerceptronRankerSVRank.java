package perceptron;

import gurobi.GRBEnv;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.neuralnoise.svm.AbstractSVM;
import com.neuralnoise.svm.SVMUtils;
import com.neuralnoise.svm.SoftMarginSVML1;
import com.neuralnoise.svm2.AbstractSVRank;
import com.neuralnoise.svm2.SoftRank;

public class LargeMarginBatchPerceptronRankerSVRank<T> extends AbstractPerceptronRanker<T> {

	// map: rank - classifier
	private BiMap<Integer, AbstractSVRank<T>> classifiers;
	private double[] C = new double[] { 1, 1e-8, 1e-7, 1e-6, 1e-5, 1e-4, 1e-3, 1e-2, 1e-1, 1e0, 1e1, 1e2, 1e3, 1e4, 1e5, 1e6, 1e7, 1e8 };
	
	public LargeMarginBatchPerceptronRankerSVRank(Set<T> objects, Table<T, T, Double> K, int ranks) {
		super(objects, K, ranks);
	}
	
	public void train(List<ObjectRank<T>> stream) throws Exception {
		GRBEnv env = SVMUtils.buildEnvironment();
		
		classifiers = HashBiMap.create();
		
		for (int r = 1; r < ranks && classifiers != null; ++r) {
			Map<T, Integer> map = Maps.newHashMap();
			
			for (ObjectRank<T> o : stream) {
				map.put(o.getObject(), o.getRank());
			}
		
			AbstractSVRank<T> svm = null;
			
			for (int i = 0; i < C.length && svm == null; ++i) {
				try {
					double c = C[i];
					System.out.println("C: " + c);
					svm = new SoftRank<T>(env, objects, map, ranks, K, c);
				} catch (gurobi.GRBException e) {
					svm = null;
				}
			}
			
			if (svm == null)
				classifiers = null;
			
			if (classifiers != null)
				classifiers.put(new Integer(r), svm);
		}

	}
	// ################################ DA CONTROLLARE ################################
	@Override
	public int rank(T o) {
		if (classifiers == null)
			return Integer.MAX_VALUE;
		
		for (int i = 1; i <= classifiers.size(); ++i) {
			AbstractSVRank<T> classifier = classifiers.get(i);
			return (int) classifier._evaluate(o);
		}
		return classifiers.size() + 1;
	}

}
