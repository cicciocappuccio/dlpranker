package utils;

import java.util.List;

import com.google.common.collect.Lists;

import dataset.ExperimentDataset;
import dataset.Tupla;

public class Balancing {

	public static void main(String args[])
	{
		List<Tupla> lista = XMLFilmRatingStream.leggi();
		
		List<Tupla> _utenti = ExperimentDataset.getUsers(lista);

		List<Tupla> ratings = Lists.newArrayList();
		
		for (Tupla u : _utenti) {
			List<Tupla> _ratingsUser = ExperimentDataset.getRatingsOfUser(lista, u.getUser());
			if (_ratingsUser.size() >= 10) {
				
				int[] counts = new int[5];
				
				for (Tupla i : _ratingsUser)
				{
					counts[i.getValue() - 1]++;
				}
				
				int min = Integer.MAX_VALUE;
				
				for (int i : counts)
				{
					if (i < min)
					{
						min = i;
					}
				}
				
				counts = new int[5];
				if (min > 2)
				{
					for (Tupla i : _ratingsUser)
					{
						if (counts[i.getValue() - 1] < min)
						{
							ratings.add(i);
							counts[i.getValue() - 1]++;
						}
						
					}
							
				}

	
				
			}
		}

		
		XMLFilmRatingStream.scrivi(ratings);
	}
}
