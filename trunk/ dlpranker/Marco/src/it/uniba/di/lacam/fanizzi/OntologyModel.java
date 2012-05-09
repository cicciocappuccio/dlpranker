package it.uniba.di.lacam.fanizzi;

import it.uniba.di.lacam.fanizzi.utils.DebugUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.dllearner.reasoning.OWLAPIReasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

public class OntologyModel {
	
	
    private PelletReasoner reasoner;
    private OWLOntologyManager manager;
	private OWLDataFactory dataFactory;
	private OWLOntology ontology;
	
	
	// i concetti della KB
	//private OWLClass[] allConcepts, allPrimitiveConcepts;				// letti da ontology ogni volta che c'è ne fosse bisogno
	
	// AnnotationProperty
	//private OWLAnnotationProperty[] allAnnotationProperty;			// letti da ontology ogni volta che c'è ne fosse bisogno
	
	// tutti gli esempi
	//private OWLNamedIndividual[] allExamples;							// letti da ontology ogni volta che c'è ne fosse bisogno
	
	// esempi di training
//	static OWLIndividual[] trainingExamples;			//???????????????????????????
	//static OWLObjectProperty[] allRoles;								// letti da ontology ogni volta che c'è ne fosse bisogno
	//static OWLDescription[] negTestConcepts;	
	


	
	public OntologyModel (String urlOwlFile)
	{
		System.out.println(urlOwlFile);
		manager = OWLManager.createOWLOntologyManager();
		
        ontology = null;
        
        try {
        	ontology = manager.loadOntologyFromOntologyDocument(new File(urlOwlFile));
		} catch (OWLOntologyCreationException e1) {
			e1.printStackTrace();
		}
        
        dataFactory = manager.getOWLDataFactory();
        
        System.out.println("ONTOLOGY: " + ontology);
        
        reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
        ar = new OWLAPIReasoner();
        
        
	}


	// ---------------------   M E T O D I     G E T   --------------------------
	
	public OWLOntology getOntology()
	{
		return ontology;
	}
	
	public PelletReasoner getReasoner()
	{
		return reasoner;
	}
	
	public OWLOntologyManager getManager()
	{
		return manager;
	}
	
	public OWLDataFactory getDataFactory()
	{
		return dataFactory;
	}

	// ------------------   F I N E     M E T O D I     G E T   ---------------------
	// ------------------   M E T O D I     di     L E T T U R A        ---------------------
	
	
	public OWLClass[] getAllConcepts()
	{
        System.out.println("\nClasses\n");
		Set<OWLClass> classList = ontology.getClassesInSignature();// getReferencedClasses();
		OWLClass[] allConcepts = new OWLClass[classList.size()];
		int c=0;
        for(OWLClass cls : classList) {
			if (!cls.isOWLNothing() &&	!cls.isAnonymous()) {
				allConcepts[c] = cls;
//				System.out.printf("\n%5d %s",c,cls, cls.isOWLNamedIndividual());
				++c;
			}	        		
		}
        System.out.printf("\n-------------------------------\n%5d %s",c, "\n");
		return allConcepts;
	}

	// ------------------------------------------------------------------------------------
	
	public OWLClass[] allPrimitiveConcepts()
	{
		OWLClass[] allPrimitiveConcepts;
        System.out.println("\nClasses\n");
		
		Set<OWLClass> classList = ontology.getClassesInSignature();// getReferencedClasses();

		OWLClass[] allConcepts = new OWLClass[classList.size()];
		
		int c=0, pc=0;
        for(OWLClass cls : classList) {
			if (!cls.isOWLNothing() &&	!cls.isAnonymous()) {
				allConcepts[c] = cls;
				System.out.printf("\n%5d %s",c,cls, cls.isOWLNamedIndividual());
				++c;
				if (!cls.isDefined(ontology)) 
					pc++;
			}	        		
		}
        System.out.printf("\n-------------------------------\n%5d %s",c, "\n");        
        allPrimitiveConcepts = new OWLClass[pc];
        pc = 0;
        for(OWLClass cls : classList) {
			if (!!cls.isDefined(ontology)) {
				allPrimitiveConcepts[pc++] = cls;
				System.out.printf("%5d %s",c,cls);
			}	        		
		}
        
        System.out.println("\n---------------------------- "+c);
	
		return allPrimitiveConcepts;
	}
	
	// ------------------------------------------------------------------------------------

	public OWLObjectProperty[] getAllRoles()
	{
		OWLObjectProperty[] allRoles;
		
		System.out.println("\nProperties\n");
        //Set<OWLObjectProperty> propList = ontology.getObjectPropertiesInSignature();
		Set<OWLObjectProperty> propList = ontology.getObjectPropertiesInSignature();
		
		//System.out.println("object properties in signature: " + propList.size());
        allRoles = new OWLObjectProperty[propList.size()];
		int op=0;
        for(OWLObjectProperty prop : propList) {
			if (!prop.isAnonymous()) {
				allRoles[op++] = prop;
				
				System.out.println(op + " - " + prop);
			}	   else
				System.out.println("ANOMYMOUS: " + op + " - " + prop);
		}
	
		return allRoles;
	}	

	// ------------------------------------------------------------------------------------
	
	public OWLNamedIndividual[] getAllIndividuals()
	{
		OWLNamedIndividual[] allIndividuals;
		
		System.out.println("\nIndividuals\n");
		Set<OWLNamedIndividual> indList = ontology.getIndividualsInSignature();
		allIndividuals = new OWLNamedIndividual[indList.size()];
	    int i=0;
	    for(OWLNamedIndividual ind : indList) {
			if (!ind.isAnonymous()) {
				allIndividuals[i++] = ind;
				System.out.println(ind);
			}
		}
		
		
	    System.out.println("---------------------------- "+i);
	
		return allIndividuals;
	}	

	// ------------------------------------------------------------------------------------
	
	public OWLAnnotationProperty[] getAllAnnotationProperty()
	{
		OWLAnnotationProperty[] allAnnotationProperty;
		
		Set<OWLAnnotationProperty> annPropList = ontology.getAnnotationPropertiesInSignature();
	    allAnnotationProperty = new OWLAnnotationProperty[annPropList.size()];
	    int cursor=0;
	    for(OWLAnnotationProperty annProp : annPropList)
	    {
			allAnnotationProperty[cursor++] = annProp;
			System.out.println(cursor + " - " + annProp);
			
		}
		
		
	    System.out.println("---------------------------- "+cursor);
	
		return allAnnotationProperty;
	}	

	// ------------------------------------------------------------------------------------
	 
	// ------------------    fine     M E T O D I     di     L E T T U R A        ---------------------

	 


//		private static void kernelPerceptronRank(Integer[] trainingExs, int[] ranks, double[] wc, double[] thetac) {
//									
//			int g=NITER;
//			thetac[NRANKS-1] = Double.MAX_VALUE;
//				
//			do {
//				for (int t = 0; t < trainingExs.length; t++) {
//					int e = trainingExs[t];
//					double[] u = new double[NRANKS];
//					double[] l = new double[NRANKS];
//					int yp = (int)rank(e,wc,thetac);
//					int yt = (int)ranks[e];
//					if (yp != yt) {
//						for (int j = 0; j < NRANKS-1; j++)
//							l[j] = (j < yt) ? +1 : -1;
//						for (int j = 0; j < NRANKS-1; j++) {
//							double f = 0;
//							for (int i=0; i < wc.length; ++i)
//								f += wc[i]*kernel[i][e];
//							if ((f-thetac[j])*l[j] <= 0) 
//								u[j] = l[j]; 	
//						}
//						double upd=0;
//						for (int j = 0; j < NRANKS-1; j++) {
//							upd += u[j];
//							thetac[j] -= u[j];
//						}
//						upd *= yt; // was *= y
//						for (int i = 0; i < wc.length; i++)
//							wc[i] += upd;
//					}		
////					else {					
////					}
//				}
//				--g;
//			} while (g>0);
//			
//		}
		
		
		

		
		/**/
/*
		public static OWLNamedIndividual[] individualSelection(OWLClass[] rating)
		{
			System.out.println("individualSelectionVector");
			//OWLIndividual[] ratingsTot;
			ArrayList<OWLNamedIndividual> ratingsTot = new ArrayList();
			
			OWLNamedIndividual[] ratings = {};
			
			for (OWLClass rank : rating)
			{	
				
				ratings = individualSelection(rank);
				for (OWLNamedIndividual mio : ratings)
					if (!ratingsTot.contains(mio))
						ratingsTot.add(mio);
			}
			
			System.out.println("- - - - - -" + ratingsTot.size());
			
			return ratingsTot.toArray(ratings);
		}

		
		
		public static OWLNamedIndividual[] individualSelection(OWLClass rating)
		{
			//System.out.println("individualSelection");
			OWLNamedIndividual[] ratings;
			
			Set<OWLIndividual> indList = rating.getIndividuals(ontology);
	        
			ratings = new OWLNamedIndividual[indList.size()];
	        System.out.println(indList.size() + " - " + rating.toString());
	        int i=0;
	        
	        for(OWLIndividual ind : indList) {
				if (!ind.isAnonymous()) {
					OWLNamedIndividual zsdfg = (OWLNamedIndividual) ind;
					
					ratings[i++] = zsdfg;
					System.out.println(ind);
				}	
			}
	        
			return ratings;
		}
		*/
	
		
		
		
		
		
		

		
		/**
		* 
		* @param concepts concetti da cui estrarre gli individui
		* @param ratingAnnProp AnnotationProperty del rating 
		* @param ratingValue AnnotationProperty dal valore del rating
		* 
		* @return  mappa con individual del film in dbpedia come chiave e media dei rating come valore
		*/
	/*	
	public Map<OWLNamedIndividual, Double> filmRating(OWLClass[] concepts, OWLAnnotationProperty ratingAnnProp,	OWLAnnotationProperty ratingValue)
	{
		System.out.println("filmRating");

		Map<OWLNamedIndividual, Double> mappa = new HashMap<OWLNamedIndividual, Double>();

		for (OWLClass concept : concepts)
		{
			NodeSet<OWLNamedIndividual> conceptIndividuals = reasoner.getInstances(concept, true);
			for (Node<OWLNamedIndividual> individual : conceptIndividuals)
			{
				Set<OWLLiteral> individualAnnotations = reasoner.getAnnotationPropertyValues(individual.getRepresentativeElement(),	ratingAnnProp);
				Set<OWLAnnotation> annotations = individual.getRepresentativeElement().getAnnotations(ontology);

				double sum = 0, count = 0;
				for (OWLAnnotation ann : annotations)
				{
					if (ontology.containsIndividualInSignature(IRI.create(ann.getValue().toString())))
					{
						OWLNamedIndividual rating = new OWLNamedIndividualImpl(dataFactory, IRI.create(ann.getValue().toString()));
						Set<OWLAnnotation> valore = rating.getAnnotations(ontology, ratingValue);
						for (OWLAnnotation val : valore)
						{
							String str = val.getValue().toString();
							str = str.substring(1, str.length() - 1);
							sum = sum + Integer.parseInt(str);
							count++;
						}
					}
				}
				// System.out.println(sum/count);
				if (count > 0)
					mappa.put(individual.getRepresentativeElement(), Double.valueOf(sum / count));
			}
		}
		return mappa;
	}
		
		*/

		
}
// ------------------------------- END OF FILE -------------------------------------------------