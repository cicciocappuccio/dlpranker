package test;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.thoughtworks.xstream.XStream;

public class Test1 {

	public static void main(String[] args) {
		String urlOwlFile = "res/dataset2.rdf";
		System.out.println(urlOwlFile);
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		OWLOntology ontology = null;

		try {
			ontology = manager.loadOntologyFromOntologyDocument(new File(
					urlOwlFile));
		} catch (OWLOntologyCreationException e1) {
			e1.printStackTrace();
		}

		OWLDataFactory dataFactory = manager.getOWLDataFactory();
		System.out.println("ONTOLOGY: " + ontology);
		Object reasoner = PelletReasonerFactory.getInstance().createReasoner(
				ontology);

		OWLClass film = new OWLClassImpl(dataFactory,
				IRI.create("http://dbpedia.org/ontology/Film"));
		OWLClass[] mio = { film };

		OWLAnnotationProperty ratingAnnProp = new OWLAnnotationPropertyImpl(
				dataFactory, IRI.create("http://purl.org/stuff/rev#hasReview"));
		OWLAnnotationProperty ratingValue = new OWLAnnotationPropertyImpl(
				dataFactory, IRI.create("http://purl.org/stuff/rev#rating"));

		ResourceRating[] vettore;
		Set<ResourceRating> insieme = new HashSet<ResourceRating>();
		int i = 0;
		for (OWLClass concept : mio) {
			Set<OWLIndividual> conceptIndividuals = concept
					.getIndividuals(ontology);
			System.out.println(conceptIndividuals.size());

			for (OWLIndividual individual : conceptIndividuals) {
				Set<OWLIndividual> sameInds = individual
						.getSameIndividuals(ontology);
				for (OWLIndividual sameInd : sameInds) {
					boolean trovato = false;

					OWLNamedIndividual sameNamedInd = (OWLNamedIndividual) sameInd;
					Set<OWLAnnotation> individualAnnotations = sameNamedInd
							.getAnnotations(ontology, ratingAnnProp);
					// rev#hasReview

					for (OWLAnnotation ann : individualAnnotations) { // se
																		// esiste
																		// un
																		// individuo
																		// con
																		// uri
																		// ann
						if (ontology.containsIndividualInSignature(IRI
								.create(ann.getValue().toString()))) {
							OWLNamedIndividual rating = new OWLNamedIndividualImpl(
									dataFactory, IRI.create(ann.getValue()
											.toString()));
							Set<OWLAnnotation> valore = rating.getAnnotations(
									ontology, ratingValue);
							for (OWLAnnotation val : valore) {
								String str = val.getValue().toString();
								str = str.substring(1, str.length() - 1);

								insieme.add(new ResourceRating(individual
										.toString(), Integer.parseInt(str)));
								// ratingFilm.put(rating, (OWLNamedIndividual)
								// individual);
								trovato = true;
								i++;
							}
						}
					}
					if (trovato) {
						/*
						 * System.out.println(individual);
						 * System.out.println(numeroDiIndividui);
						 */
						// numeroDiIndividui++;
						// individuals.add((OWLNamedIndividual) individual);
					}
				}
			}
		}
		vettore = new ResourceRating[i];
		vettore = insieme.toArray(vettore);
/*
		for (ResourceRating a : vettore) {
			System.out.println(a.getUri() + " - " + a.getRating());
		}
*/
		System.out.println(i);

		XMLEncoder encoder;
		try {
			encoder = new XMLEncoder(new BufferedOutputStream(
					new FileOutputStream("xmlProva.xml")));
			encoder.writeObject(vettore);
			encoder.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		XMLDecoder decoder;
		try {
			decoder = new XMLDecoder(new BufferedInputStream(
					new FileInputStream("xmlProva.xml")));
			vettore = (ResourceRating[]) decoder.readObject();
			decoder.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (ResourceRating a : vettore) {
			System.out.println(a.getUri() + " - " + a.getRating());
		}
		System.out.println(vettore.length);
		
		System.out.println(ontology.getClassesInSignature().size());
		Set<OWLClass> mia =ontology.getClassesInSignature();
		
		for(OWLClass c : mia)
			System.out.println(c);
		
	}

}
