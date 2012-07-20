package perceptron;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jsc.distributions.Bernoulli;

import org.dllearner.core.owl.Individual;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

public class OAP_BPM<T> extends AbstractPerceptronRanker<T> {

	public OAP_BPM(Set<T> objects, Table<T, T, Double> K, int r) {
		super(objects, K, r);
	}

	public void feed(List<ObjectRank> z, int N, double r) {
		
		Bernoulli bernoulli = new Bernoulli(r);
		
		Table<Integer, Individual, Integer> w = HashBasedTable.create();
		Table<Integer, Individual, Integer> c = HashBasedTable.create();
		
		Map<Individual, Double> wTilde = Maps.newHashMap();
		Map<Individual, Double> cTilde = Maps.newHashMap();
		
		for (int ti = 0; ti < z.size(); ti++)
		{
			Individual t = (Individual) z.get(ti).getObject();
			Individual t1 = (Individual) z.get(ti + 1).getObject();

			for(Integer j = 1; j < N; j++)
			{
				
				double bjt = bernoulli.random();
				
				if (bjt == 1.0)
				{
					//int pr = prank
					int pr = 0;
					
					w.put(j,t1 , pr);
					c.put(j,t1 , pr);
				}
				else
				{
					w.put(j,t1 , w.get(j, t));
					c.put(j,t1 , c.get(j, t));
				}
				
				wTilde.put(t1, wTilde.get(t1) + (w.get(j, t1)/N));
				cTilde.put(t1, cTilde.get(t1) + (c.get(j, t)/N));
			}
			
			
			
		}
		
	
		
	}
	

}
