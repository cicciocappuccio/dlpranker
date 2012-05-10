package it.uniba.di.lacam.fanizzi.features;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;

public class FeaturesSelection
{
	/**
	 * @param reasoner
	 * @param esempi
	 * @return
	 */
	public static Set<OWLClassExpression> superClass(PelletReasoner reasoner, Set<OWLNamedIndividual> esempi)
	{
		Set<OWLClass> ritorno = new HashSet<OWLClass>();
		for (OWLNamedIndividual esempio : esempi)
		{
			NodeSet<OWLClass> nodiEsempio = reasoner.getTypes(esempio, true);
			ritorno.addAll(FeaturesSelection.superClass(reasoner, nodiEsempio));
		}
		
		Set<OWLClassExpression> featuresD = new HashSet<OWLClassExpression>();
		for(OWLClass classe : ritorno)
			featuresD.add((OWLClassExpression) classe);
		
		return featuresD;
	}
	
	/**
	 * @param reasoner
	 * @param esempi
	 * @return
	 */
	public static Set<OWLClassExpression> subClass(PelletReasoner reasoner, Set<OWLNamedIndividual> esempi)
	{
		Set<OWLClass> ritorno = new HashSet<OWLClass>();
		for (OWLNamedIndividual esempio : esempi)
		{
			NodeSet<OWLClass> nodiEsempio = reasoner.getTypes(esempio, true);
			ritorno.addAll(FeaturesSelection.subClass(reasoner, nodiEsempio));
		}
		
		Set<OWLClassExpression> featuresD = new HashSet<OWLClassExpression>();
		for(OWLClass classe : ritorno)
			featuresD.add((OWLClassExpression) classe);
		
		return featuresD;
	}
	
	/**
	 * @param reasoner
	 * @param nodi insieme di nodi da cui partire per risalire la gerarchia 
	 * @return  tutte le super classi senza duplicati
	 */
	public static Set<OWLClass> superClass (PelletReasoner reasoner, NodeSet<OWLClass> nodi)
	{
		Set<OWLClass> ritorno = new HashSet<OWLClass>();
		Set<OWLClass> classi = nodi.getFlattened();
		for (OWLClass classe : classi)
		{
			ritorno.addAll(superClass(reasoner, reasoner.getSuperClasses(classe, true)));
			ritorno.add(classe);
		}
		return ritorno;
	}
	
	/**
	 * @param reasoner
	 * @param nodi
	 * @return
	 */
	public static Set<OWLClass> subClass (PelletReasoner reasoner, NodeSet<OWLClass> nodi)
	{
		Set<OWLClass> ritorno = new HashSet<OWLClass>();
		Set<OWLClass> classi = nodi.getFlattened();
		for (OWLClass classe : classi)
		{
			ritorno.addAll(subClass(reasoner, reasoner.getSubClasses(classe, true)));
			ritorno.add(classe);
		}
		return ritorno;
		
	}
	
}
