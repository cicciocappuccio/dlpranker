package scripts;

import it.uniba.di.lacam.fanizzi.features.utils.Inference;
import it.uniba.di.lacam.fanizzi.features.utils.Inference.LogicValue;

import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

public class AbstractRankExperiment {
	
	private static final Logger log = LoggerFactory.getLogger(AbstractRankExperiment.class);
	
	protected static Table<Individual, Individual, Double> buildKernel(
			Inference inference, Set<Description> features, Set<Individual> films) {
		
		log.info("Creating Kernel..");
		
		Table<Description, Individual, Double> Pi = HashBasedTable.create();

		for (Description feature : features) {
			for (Individual individual : films) {
				LogicValue b = inference.cover(feature, individual);
				Pi.put(feature, individual, (b == LogicValue.TRUE ? 0 : (b == LogicValue.FALSE ? 1 : 0.5)));
			}
		}

		if (inference.getCache() != null)
			inference.getCache().save();
		
		Table<Individual, Individual, Double> K = HashBasedTable.create();
		Set<Individual> toCheck = Sets.newHashSet(films);

		for (Individual i : films) {
			for (Individual j : toCheck) {
				double sum = 0;
				for (Description feature : features)
					sum += Math.pow(1 - Math.abs((Pi.get(feature, i) - Pi.get(feature, j))), 2);
				sum = (Math.sqrt(sum));
				K.put(i, j, sum);
				K.put(j, i, sum);
			}
			toCheck.remove(i);
		}
		
		return K;
	}
	
}
