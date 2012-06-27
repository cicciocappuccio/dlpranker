package it.uniba.di.lacam.fanizzi.features;

import it.uniba.di.lacam.fanizzi.OntologyModel;

import java.util.HashSet;
import java.util.Set;

import org.dllearner.core.owl.Description;
import org.dllearner.core.owl.Individual;
import org.dllearner.utilities.owl.OWLAPIConverter;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.impl.OWLClassNodeSet;

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
	public static Set<OWLClassExpression> superClass(PelletReasoner reasoner, OWLClassExpression concetto)
	{
		Set<OWLClass> ritorno = new HashSet<OWLClass>();
//		for (OWLNamedIndividual esempio : esempi)
		{
			NodeSet<OWLClass> nodiEsempio = new OWLClassNodeSet((OWLClass) concetto);
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
	public static Set<OWLClassExpression> subClass(PelletReasoner reasoner, OWLClassExpression concetto)
	{
		Set<OWLClass> ritorno = new HashSet<OWLClass>();
//		for (OWLNamedIndividual esempio : esempi)
		{
			NodeSet<OWLClass> nodiEsempio = new OWLClassNodeSet((OWLClass) concetto);
			ritorno.addAll(FeaturesSelection.subClass(reasoner, nodiEsempio));
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
	
	
	
	
	public static Set<Description> subSuperClass(String urlFile, Set<Individual> individui)
	{
		
		OntologyModel om = new OntologyModel(urlFile);
		
		
		Set<OWLNamedIndividual> owI = new HashSet<OWLNamedIndividual>();
		
		for (Individual i : individui)
			owI.add(OWLAPIConverter.getOWLAPIIndividual(i).asOWLNamedIndividual());
		
		Set<OWLClassExpression> tutto = new HashSet<OWLClassExpression>();
		
		tutto.addAll(superClass(om.getReasoner(),owI));
		tutto.addAll(subClass(om.getReasoner(),owI));
		
		Set<Description> tutti = new HashSet<Description>();
		
		for (OWLClassExpression i : tutto)
			tutti.add(OWLAPIConverter.convertClass(i.asOWLClass()));
		
		
		
		return tutti;
		
	}
}
