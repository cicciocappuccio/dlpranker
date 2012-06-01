package test;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class KFolder<T> {

	private ArrayList<List<T>> folds;
	
	public KFolder(List<T> instances, int k) {
		this(instances, k, null);
	}
	
	public KFolder(List<T> instances, int k, Random prng) {
		if (k < 1)
			throw new IllegalArgumentException("k must be > 0");
		
		List<T> is = new LinkedList<T>(instances);
		
		if (prng != null)
			Collections.shuffle(is, prng);
		
		this.folds = new ArrayList<List<T>>();
		
		for (int i = 0; i < k; ++i)
			folds.add(new LinkedList<T>());
		
		Iterator<List<T>> it = folds.iterator();
		
		for (T instance : instances) {
			List<T> fold = null;
			if (it.hasNext())
				fold = it.next();
			else {
				it = folds.iterator();
				fold = it.next();
			}	
			fold.add(instance);
		}
	}
	
	public List<T> getFold(int f) {
		if (f < 0 || f >= folds.size())
			throw new IllegalArgumentException("illegal value for f");
		return folds.get(f);
	}
	
	public List<T> getOtherFolds(int f) {
		if (f < 0 || f >= folds.size())
			throw new IllegalArgumentException("illegal value for f");
		List<T> ret = new LinkedList<T>();
		for (int i = 0; i < folds.size(); ++i) {
			if (i != f) {
				ret.addAll(folds.get(i));
			}
		}
		return ret;
	}
	
}
