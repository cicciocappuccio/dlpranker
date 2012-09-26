package scripts.september27;

import java.util.List;
import java.util.Set;

import org.dllearner.core.owl.Individual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scripts.AbstractRankExperiment;
import utils.Inference;
import utils.XMLFilmRatingStream;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import dataset.ExperimentDataset;
import dataset.OntologyAsGraph;
import dataset.Tupla;

public class Test extends AbstractRankExperiment{

	public static final Logger log = LoggerFactory.getLogger(AbstractRankExperiment.class);
	
	public static void main(String[] args) throws Exception {
		
		int d = 2;
		double lambda = 1.0;
		
		Inference inference = getInference();
		OntologyAsGraph onto = new OntologyAsGraph(inference);
		
		List<Tupla> lista = XMLFilmRatingStream.leggi();

		List<Tupla> _utenti = ExperimentDataset.getUsers(lista);
		List<Tupla> utenti = Lists.newArrayList();
		
		for (Tupla u : _utenti) {
			List<Tupla> ratingsUser = ExperimentDataset.getRatingsOfUser(lista, u.getUser());
			if (ratingsUser.size() >= 10) {
				utenti.add(u);
			}
		}

		for (Tupla utente : utenti) {
			List<Tupla> ratingsUser = ExperimentDataset.getRatingsOfUser(lista, utente.getUser());

			Set<Individual> filmsUser = Sets.newHashSet();

			for (Tupla i : ratingsUser) {
				filmsUser.add(i.getFilm());
			}
			
			Table<Individual, Individual, Double> K = buildLoeschKernel(onto, filmsUser, d, lambda);

			for (Cell<Individual, Individual, Double> k : K.cellSet()) {
				System.out.println("(" + k.getRowKey() + ", " + k.getColumnKey() + ", " + k.getValue() + ")");
			}
		}
	}
}
