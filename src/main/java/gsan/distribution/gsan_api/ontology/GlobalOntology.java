package gsan.distribution.gsan_api.ontology;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import gsan.distribution.algorihms.astar.AstarSearchAlgo;
import gsan.distribution.algorihms.astar.Edges;
import gsan.distribution.gsan_api.annotation.Annotation;
import uk.ac.manchester.owl.owlapi.tutorial.LabelExtractor;

public class GlobalOntology {

	public Hashtable<String,InfoTerm> allStringtoInfoTerm = new Hashtable<String,InfoTerm>();
	public Hashtable<String, List<String>> obsolete2consORrepl = new Hashtable<String,List<String>>();
	public Map<String,OntoInfo> subontology = new HashMap<>();
	public final String owlprefix = "http://purl.obolibrary.org/obo/";
	private static  OWLReasonerFactory reasonerFactory;
	public static OWLOntology ontology;
	private OWLReasoner reasoner ;
	private PrintStream out;
	private static OWLOntologyManager manager;


	//private Map<String,Set<String>> go2descendantORG = new HashMap<>();

	@SuppressWarnings("static-access")
	public GlobalOntology(GlobalOntology go) {
		this.ontology = go.ontology;
		this.reasonerFactory = go.reasonerFactory;
		this.manager = go.manager;
		this.reasoner = go.reasoner;
		this.allStringtoInfoTerm = new Hashtable<>();
		for(String t : go.allStringtoInfoTerm.keySet()) {
			this.allStringtoInfoTerm.put(t,new InfoTerm(go.allStringtoInfoTerm.get(t)));
		}
		this.obsolete2consORrepl = new Hashtable<>(go.obsolete2consORrepl);
		
		for(Entry<String,OntoInfo> entry : go.subontology.entrySet()) {
			this.subontology.put(entry.getKey(), new OntoInfo(entry.getValue()));
		}
		
				
	}

	public GlobalOntology(OWLOntologyManager manager, OWLReasonerFactory reasonerFactory)
			throws OWLException, MalformedURLException {
		GlobalOntology.reasonerFactory = reasonerFactory;
		out = System.out;
	}

	public GlobalOntology()
			throws Exception {
		out = System.out;
	}
	public Hashtable<String,InfoTerm> GetInfoSubOnt(String Onto){

		Hashtable<String,InfoTerm> lo = new Hashtable<String,InfoTerm>();

		lo.put(Onto,this.allStringtoInfoTerm.get(Onto));
		for(String d : this.allStringtoInfoTerm.get(Onto).is_a.descendants) {
			lo.put(d, this.allStringtoInfoTerm.get(d));
		}


		return lo;


	}
	/*
	 * Print the class hierarchy for the given ontology from this class down, assuming this class is at
	 * the given level. Makes no attempt to deal sensibly with multiple
	 * inheritance.
	 */
	public void AddTermToGenome(Annotation GOA) {



		for(String g : GOA.annotation.keySet()) { // For each genes
			for(String sub :this.subontology.keySet()) { // For each sub-ontology
				for(String t: GOA.annotation.get(g).getTerms(sub)){//For each term in sub-ontology
					this.allStringtoInfoTerm.get(t).genome.add(g); // We add the gen to term
					for(String anc :this.allStringtoInfoTerm.get(t).is_a.ancestors) { //for each term ancestor 
						this.allStringtoInfoTerm.get(anc).genome.add(g); // We add too the gen
					}
					//				if(this.allStringtoInfoTerm.get(t).getRegulatesClass()!=null) {
					//					String r = this.allStringtoInfoTerm.get(t).getRegulatesClass();
					//					this.allStringtoInfoTerm.get(r).genome.add(g); // We add the gen to term
					//					for(String anc :this.allStringtoInfoTerm.get(r).is_a.ancestors) { //for each term ancestor 
					//						this.allStringtoInfoTerm.get(anc).genome.add(g); // We add too the gen
					//					}
					//					for(String anc :this.allStringtoInfoTerm.get(r).part_of.ancestors) { //for each term ancestor 
					//						this.allStringtoInfoTerm.get(anc).genome.add(g); // We add too the gen
					//					}
					//				}
				}
			}
		}



	}




	public void printHierarchy(OWLOntology ontology, OWLClass clazz) throws Exception {

		reasoner = reasonerFactory.createReasoner(ontology); // Creation of reasoner
		printHierarchy(reasoner, clazz); // Once reasoner charged, go to printHierarchy method
		/* Now print out any unsatisfiable classes */
		for (OWLClass cl: ontology.getClassesInSignature()) {
			if (!reasoner.isSatisfiable(cl)) {
				out.println("XXX: " + labelFor(cl));
			}
		}
	}

	static String labelFor( OWLClass clazz) {
		/*
		 * Use a visitor to extract label annotations
		 */
		LabelExtractor le = new LabelExtractor();
		Collection<OWLAnnotationAssertionAxiom> annotations = EntitySearcher.getAnnotationAssertionAxioms(clazz, ontology);
		for (OWLAnnotationAssertionAxiom anno : annotations) {


			anno.getAnnotation().accept(le);
		}
		/* Print out the label if there is one. If not, just use the class URI */
		if (le.getResult() != null) {
			return le.getResult().toString();
		} else {            
			return clazz.getIRI().toString();
		}
	}
	private static boolean IsDespreciated( OWLClass clazz) {
		/*
		 * Use a visitor to extract label annotations
		 */

		LabelExtractor le = new LabelExtractor();
		Collection<OWLAnnotationAssertionAxiom> annotations = EntitySearcher.getAnnotationAssertionAxioms(clazz, ontology);
		for (OWLAnnotationAssertionAxiom anno : annotations) {
			anno.getAnnotation().accept(le);
		}
		/* Print out the label if there is one. If not, just use the class URI */
		return le.getComment();
	}

	static String labelobjetFor( OWLObjectProperty clazz) {
		/*
		 * Use a visitor to extract label annotations
		 */


		LabelExtractor le = new LabelExtractor();
		Collection<OWLAnnotation> annotations = EntitySearcher.getAnnotations(clazz, ontology);
		for (OWLAnnotation anno : annotations) {
			anno.accept(le);
		}
		/* Print out the label if there is one. If not, just use the class URI */
		if (le.getResult() != null) {
			return le.getResult().toString();
		} else {            
			return clazz.asOWLClass().getIRI().toString();
		}
	}

	static String IDFor( OWLClass clazz) {
		/*
		 * Use a visitor to extract label annotations
		 */

		LabelExtractor le = new LabelExtractor();
		ArrayList<OWLAnnotation> annotations = new ArrayList<OWLAnnotation>(EntitySearcher.getAnnotations(clazz, ontology));

		for (OWLAnnotation anno : annotations) {
			anno.accept(le);

			// System.out.println(anno+" : " + anno.);
		}
		/* Print out the label if there is one. If not, just use the class URI */
		if (le.getID() != null) {
			return le.getID().toString();
		} else {            
			return clazz.getIRI().toString();
		}
	}
	static List<String> IDtFor( List<OWLClass> list) {
		/*
		 * Use a visitor to extract label annotations
		 */
		List<String> listID =  new ArrayList<String>();
		for(OWLClass clazz:list){
			if(!clazz.isTopEntity()){
				LabelExtractor le = new LabelExtractor();
				Collection<OWLAnnotation> annotations = EntitySearcher.getAnnotations(clazz, ontology);
				for (OWLAnnotation anno : annotations) {
					anno.accept(le);

				}
				/* Print out the label if there is one. If not, just use the class URI */
				if (le.getID() != null) {
					listID.add(le.getID().toString());
				} else {            
					listID.add(clazz.getIRI().toString());
				}}}
		return listID;
	}

	public void Leaves() { 

		// Esta function va a recorrer todos los terminos y va mirar si tiene o no hijos, si no tiene hijos todos sus ancestros lo incluyen en sus leaves

		for(String terms : this.allStringtoInfoTerm.keySet()) {

			InfoTerm iT = this.allStringtoInfoTerm.get(terms);
			if(iT.is_a.childrens.isEmpty()) {
				for(String anc : iT.is_a.ancestors) {
					this.allStringtoInfoTerm.get(anc).is_a.descLeaves.add(terms);
				}
			}

		}
		for(String terms : this.allStringtoInfoTerm.keySet()) {

			InfoTerm iT = this.allStringtoInfoTerm.get(terms);
			if(iT.part_of.childrens.isEmpty()) {
				for(String anc : iT.part_of.ancestors) {
					this.allStringtoInfoTerm.get(anc).part_of.descLeaves.add(terms);
				}
			}

		}
	}

	public void completingThePath() {

		for(String t : this.allStringtoInfoTerm.keySet()) { //for each term in GO
			if(this.allStringtoInfoTerm.get(t).is_a.childrens.size()==0) { // If term are no childs

				this.generatingPartOfPath(t); // method to generate part of path from leave. It is a recursive method
			}
		}


		for(String t : this.allStringtoInfoTerm.keySet()) { 
			Set<String> set = new HashSet<>();
			for(String ancPO : this.allStringtoInfoTerm.get(t).part_of.ancestors) {
				//set.add(ancPO);
				set.addAll(this.allStringtoInfoTerm.get(ancPO).is_a.ancestors);
			}
			set.addAll(this.allStringtoInfoTerm.get(t).part_of.ancestors);
			this.allStringtoInfoTerm.get(t).part_of.ancestors.clear();
			this.allStringtoInfoTerm.get(t).part_of.ancestors.addAll(set);
		}


		for(String t : this.allStringtoInfoTerm.keySet()) {

			InfoTerm iT = this.allStringtoInfoTerm.get(t);
			for(String po : iT.part_of.ancestors) {
				if(iT.part_of.parents.contains(po)) {
					this.allStringtoInfoTerm.get(po).part_of.childrens.add(iT.id);
				}
				this.allStringtoInfoTerm.get(po).part_of.descendants.add(iT.id);
			}


		}

	}
	public void generatingPartOfPath(String leave) { //depending of completingThePath()
		/*
		 * Este algo genera los ancestros, children y descendientes part of que con el razonador tardariamos siglos.
		 * Para ello hago un while (similar que para recuperar toda la informacion) haciendo etapas top-down.
		 * Luego para cada padre part of recuperamos todos los ancestros is a y part of de ese padre y lo incluimos en los
		 * ancestros del termino en question. Tambien hacemos para cada ancestro is a, incluir en ancestros part of todos los ancestros
		 * part of de cada ancestro. Para los descendientes lo calculamos a partir de cada elemento de la GO miramos sus ancestros part of y para cada 
		 * ancestro incluimos a ese ancestro los descendientes. 
		 */

		InfoTerm iT = this.allStringtoInfoTerm.get(leave);
		//if(  !iT.part_of.parents.isEmpty()) {
		Set<String> set = new HashSet<>();
		for(String anc : iT.is_a.parents) {
			set.addAll(this.completing(anc));
		}
		for(String anc : iT.part_of.parents) {
			set.add(anc);
			set.addAll(this.completing(anc));
		}
		set.addAll(iT.part_of.ancestors);
		iT.part_of.ancestors.clear();
		iT.part_of.ancestors.addAll(set);


	}
	private Set<String> completing(String leave) { // depending of generatingPartOfPath(). Is the recursive method
		/*
		 * Este algo genera los ancestros, children y descendientes part of que con el razonador tardariamos siglos.
		 * Para ello hago un while (similar que para recuperar toda la informacion) haciendo etapas top-down.
		 * Luego para cada padre part of recuperamos todos los ancestros is a y part of de ese padre y lo incluimos en los
		 * ancestros del termino en question. Tambien hacemos para cada ancestro is a, incluir en ancestros part of todos los ancestros
		 * part of de cada ancestro. Para los descendientes lo calculamos a partir de cada elemento de la GO miramos sus ancestros part of y para cada 
		 * ancestro incluimos a ese ancestro los descendientes. 
		 */

		InfoTerm iT = this.allStringtoInfoTerm.get(leave);
		//if(  !iT.part_of.parents.isEmpty()) {
		Set<String> set = new HashSet<>();
		for(String anc : iT.is_a.parents) {
			set.addAll(this.completing(anc));
		}
		for(String anc : iT.part_of.parents) {
			set.add(anc);
			set.addAll(this.completing(anc));
		}
		set.addAll(iT.part_of.ancestors);
		iT.part_of.ancestors.clear();
		iT.part_of.ancestors.addAll(set);
		return set;

	}
	//	
	//	
	/**
	 * Print the class hierarchy from this class down, assuming this class is at
	 * the given level. Makes no attempt to deal sensibly with multiple
	 * inheritance.
	 * @throws Exception 
	 */
	public void printHierarchy(OWLReasoner reasoner, OWLClass clazz)
			throws Exception {

		Map<String,OWLClass> id2owlclass = new HashMap<>(); // Map of GO id to owl Class
		Map<OWLClass,String> converter = new HashMap<OWLClass,String>(); // Inverse Map : owl Class to GO id

		for(OWLClass term :ontology.getClassesInSignature()){ // For each owl class of the charged ontology
			String id =  IDFor(term); // We recover the GO id
			// we add the information into the maps.
			converter.put(term, id); 
			id2owlclass.put(id, term);

		}

		Set<OWLClass> onto = reasoner.getSubClasses(clazz, true).getFlattened(); // Set of OWLClass childs of owl:Thing
		Set<OWLClass> GOThing = new HashSet<OWLClass>(); // Set of  subOntologies top owlclass
		Set<OWLClass> GOObsolete = new HashSet<OWLClass>(); // Set of obsolete terms

		for(OWLClass r:onto){ // For each owlclass child of owl:Thing

			boolean sino = IsDespreciated(r); // boolean to ask if the terms is despreciated 
			if(sino == false){ // False : subontology
				GOThing.add(r);}
			else { // True : Obsolete
				GOObsolete.add(r);


			}

		}



		for(OWLClass sub : GOThing){ // For each SubOntology
			int flag=1; // Parameters to break the follow while 
			int level = 0;  // integer showing the depth of a term
			String top  = converter.get(sub); // Recovering the GO id
			this.subontology.put(top,new OntoInfo()); // put the GO id in the global list 
			Hashtable<String,InfoTerm> info =new Hashtable<String, InfoTerm>(); // Creation of a HashTable of String to InfoTerm class
			TreeSet<OWLClass> list2 = new TreeSet<OWLClass>(); //Pivote set
			Set<OWLClass> list1=new HashSet<OWLClass>(); // Owl class set to get every information
			list1.add(sub); //we add the subontology owl class

			/*
			 * In this loop we explore every GO term step by step to compute the depth, the ID, name, the is a taxonomie, the direct part of and regulate.
			 * After that, the InfoTerm is created and put in the HashTable. The childs of the depth are pushed in list2 and when every terms in List1 is 
			 * visited, we clear list1 and pushing every terms in list2 to list1, after we clear list2. When list2 is empty we break the while. 
			 */
			Set<String> leavesISA = new HashSet<>();
			while(flag==1) 
			{
				for (OWLClass pere : list1) { // For each owl class in list1
					if (!pere.isBottomEntity() ) { // if that owl class is not a owl:Nothing
						String id = converter.get(pere); // get the GO ID
						InfoTerm iT = new InfoTerm(id,level); // Create InfoTerm class to this owl class
						iT.name = labelFor(pere); // get GO name and add in InfoTerm class
						iT.top = top; // get the top of subOntology and add in InfoTerm class
						/*
						 * The follow 4 lines is to add in a Link class the is a taxonomy
						 * The method changetype is to transform the owl class to GO id
						 */
						iT.is_a.ancestors.addAll(changetype(reasoner.getSuperClasses(pere,false).getFlattened(),converter));
						iT.is_a.parents.addAll(changetype(reasoner.getSuperClasses(pere,true).getFlattened(),converter));
						iT.is_a.descendants.addAll(changetype(reasoner.getSubClasses(pere,false).getFlattened(),converter));
						iT.is_a.childrens.addAll(changetype(reasoner.getSubClasses(pere,true).getFlattened(),converter));

						if(iT.is_a.childrens.isEmpty()) {
							leavesISA.add(iT.toString());
						}

						list2.addAll(reasoner.getSubClasses(pere,true).getFlattened()); // Add in list2 every child in list2
						/*
						 * The owl sub class of axiom allow get the information about partOf and regulates. 
						 */
						for(OWLSubClassOfAxiom subclsaxiom : ontology.getSubClassAxiomsForSubClass(pere) ){
							String namereg = new String();
							for(OWLObjectProperty reg:subclsaxiom.getSuperClass().getObjectPropertiesInSignature()){
								namereg = labelobjetFor(reg);
								switch(namereg){
								case "part of":
									for(OWLClass cpof : subclsaxiom.getSuperClass().getClassesInSignature()){

										iT.part_of.parents.add(converter.get(cpof));
										//										iT.part_of.ancestors.add(converter.get(cpof));

									}
									break;
								case "has_part":
									for(OWLClass cpof : subclsaxiom.getSubClass().getClassesInSignature()){

										iT.part_of.childrens.add(converter.get(cpof));
									}
									break;
								case "positively regulates":
									for(OWLClass cpof : subclsaxiom.getSuperClass().getClassesInSignature()){
										iT.positiveR = converter.get(cpof);
									}
									break;
								case "negatively regulates":
									for(OWLClass cpof : subclsaxiom.getSuperClass().getClassesInSignature()){
										iT.negativeR = converter.get(cpof);
									}
									break;
								case "regulates":
									for(OWLClass cpof : subclsaxiom.getSuperClass().getClassesInSignature()){
										iT.regulate = converter.get(cpof);
									}
									break;
								}
							}
						}


						info.put(id, iT);	// put all recovered information in the HashTable
					}
				}
				list1.clear(); // clear all information in List1


				if(!list2.isEmpty()){

					list1.addAll(list2); // we add all information in list1
					list2.clear(); // clear all information in list2
					level = level + 1; // increase the depth
				}
				else
					flag=0;
			}




			this.allStringtoInfoTerm.putAll(info); // global HashTable where we add the local HashTable
			this.subontology.get(top).maxdepth = (double)level-1; // get the maximal Depth to subOntology
			this.subontology.get(top).nNodes = this.allStringtoInfoTerm.get(top).is_a.descendants.size();

			int nisA = 0;
			int npartOf = 0;

			for(String t : this.allStringtoInfoTerm.get(top).is_a.descendants) {
				nisA = nisA + this.allStringtoInfoTerm.get(t).is_a.parents.size();
				npartOf = npartOf + this.allStringtoInfoTerm.get(t).part_of.parents.size();

			}
			this.subontology.get(top).nEdgeIsA = nisA;
			this.subontology.get(top).nEdgePOf = npartOf;
			this.subontology.get(top).addAllLeavesISA(leavesISA);


		} 


		/*
		 * The followed step get the obsolete set to try the recover the information. That is possible because there are an annotation providing a term 
		 * replacing the current obsolete term.
		 */


		for(OWLClass r : GOObsolete) { // For each obsolete owl class

			boolean isDesreciated = true;
			String idObs = r.toString().split("/")[r.toString().split("/").length-1].replace(">", "").replace("_", ":");
			String id = new String();//
			boolean isReplaced = true;
			while(isDesreciated == true) { // While boolean is true
				if(!EntitySearcher.getAnnotations(r.getIRI(), ontology).toString().contains("IAO_0100001")) { // If there are no term that it replace the obsolete term
					isReplaced = false;
					List<String> consider = new ArrayList<String>(); 
					consider.add("consider");
					/*
					 * We try search the condier annotation where several obsolete can be considered as other terms
					 */
					for(OWLAnnotation subclsaxiom : EntitySearcher.getAnnotations(r.getIRI(), ontology) ){
						String sbcl= subclsaxiom.getProperty().toStringID();
						String IAO = sbcl.split("/")[sbcl.split("/").length-1];
						if(IAO.equals("oboInOwl#consider")) {
							consider.add(((OWLLiteral)subclsaxiom.getValue()).getLiteral());
						}
					}
					if(consider.size()>1) {// If we have any element
						this.obsolete2consORrepl.put(idObs, consider);
					}
					isDesreciated = false;
				}else {

					for(OWLAnnotation subclsaxiom : EntitySearcher.getAnnotations(r.getIRI(), ontology) ){
						String sbcl= subclsaxiom.getProperty().toStringID();
						String IAO = sbcl.split("/")[sbcl.split("/").length-1];
						if(IAO.equals("IAO_0100001")) { // If we find IAO_0100001 == replaceBy
							IRI iri = IRI.create(subclsaxiom.getValue().toString());
							id = IDFor(manager.getOWLDataFactory().getOWLClass(iri)); // Recover id literal
							if(id.contains("^^")) { // if not a GO id
								OWLLiteral li = (OWLLiteral) subclsaxiom.getValue();
								id = li.getLiteral();
							}

							r = id2owlclass.get(id);
							if(r == null) { // if the literal don't provide a owl class
								IRI i = IRI.create(owlprefix + id.replace(":", "_"));
								r = manager.getOWLDataFactory().getOWLClass(i);
							}
							isDesreciated =IsDespreciated(r);

						}
					}
				}
			}	

			if(isReplaced==true) {
				List<String> replaced = new ArrayList<String>();
				replaced.add("replaced");
				replaced.add(id);
				this.obsolete2consORrepl.put(idObs,replaced);
			}
		}
	}

	/**
	 * To change owl class set to GO id set
	 */
	public static List<String> changetype(Set<OWLClass> set, Map<OWLClass, String> converter){
		set.remove(manager.getOWLDataFactory().getOWLThing()); // remove owl:Thing
		set.remove(manager.getOWLDataFactory().getOWLNothing()); // remove owl:Nothing
		List<String> s = new ArrayList<String>();
		for(OWLClass c : set){ // For each owl class
			s.add(converter.get(c)); // change object type
		}
		return s;


	}

	/**
	 * The informationOnt method create the GlobalOntology object to the GSAn program. 
	 * @param args
	 * @return GlobalOntology
	 */
	public static GlobalOntology informationOnt(String args) {

		try {

			IRI classIRI =  OWLRDFVocabulary.OWL_THING.getIRI(); // Get owl:Thing class


			/*
			 *  We first need to obtain a copy of an OWLOntologyManager, which, as the name
			 *  suggests, manages a set of ontologies. 
			 */
			manager = OWLManager.createOWLOntologyManager();


			//File file = new File(args);
			
			InputStream fileStream = new FileInputStream(args);
			InputStream gzipStream = new GZIPInputStream(fileStream);

			ontology = manager.loadOntologyFromOntologyDocument(gzipStream); //Charging Ontology


			System.out.println("[GlobalOntology] Charging reasoner...");
			OWLReasonerFactory reasonerFactoryin = new ElkReasonerFactory(); // Charging ELK reasoner and hierarchy

			GlobalOntology simpleHierarchy = new GlobalOntology(manager, reasonerFactoryin); // Create a new GlobalOntology object with the given reasoner.


			OWLClass clazz = manager.getOWLDataFactory().getOWLClass(classIRI);// Get owl class of owl:Thing
			/*
			 * Completing information to GlobalOntology object
			 */
			simpleHierarchy.printHierarchy(ontology, clazz ); // Create the Object of Term information
			simpleHierarchy.completingThePath(); // Get PartOf terms (method created for me)
			simpleHierarchy.Leaves(); // Get the leaves terms in is a taxonomy
			//			simpleHierarchy.Both(); // create
			simpleHierarchy.GetDistanceTerms(); // Recover all distance between terms in the taxonomy
			simpleHierarchy.GetICs(); // Add the Intrinsic IC for every GO term
			simpleHierarchy.goUniverseIC(); // Add the GO Universal IC Mazandu et al 2013.
			return simpleHierarchy;

		} catch (Exception e) {
			System.out.println("There are a big problem to create the GlobalOntology object");
			e.printStackTrace();
			System.exit(0);
			return null;
		}


	}

	/**
	 * This method recover the IC created by Nuno Seco, Zhou and David Sanchez
	 */

	public void GetICs() {
		for(Entry<String, OntoInfo> topInfo : this.subontology.entrySet()) {
			String topOnt = topInfo.getKey();
			Set<Double> nunoSet = new HashSet<Double>(); // Seco IC values set
			Set<Double> zhouSet = new HashSet<Double>(); // Zhou IC values set
			Set<Double> sanchezSet = new HashSet<Double>(); // Sanchez IC values set
			InfoTerm topTerm = this.allStringtoInfoTerm.get(topOnt);
			ArrayList<Double> lic = new ArrayList<Double>();
			ArrayList<Double> lpob = new ArrayList<Double>();
			/*
			 * The first values is to TopTerm in subOntology
			 */
			lic.add(0.);
			lic.add(0.);
			lic.add(0.);
			lpob.add(1.);
			lpob.add(1.);
			lpob.add(1.);
			topTerm.addICs(lic);

			for(String ter : topTerm.is_a.descendants){ // For each term descedant of Top of subOntology

				InfoTerm t = this.allStringtoInfoTerm.get(ter); // Get the InfoTerm object
				/*
				 * Compute the Seco and Sanchez probability 
				 */
				double probd = ((double) t.is_a.descendants.size()+1.)/ ((double) topTerm.is_a.descendants.size()+1.);
				double probsanchez = (double) ((double)t.is_a.descLeaves.size()/(double)t.is_a.ancestors.size() + 1.)/(double)topTerm.is_a.descLeaves.size(); 
				/*
				 * Compute the Seco, Zhou and Sanchez IC. Zhou IC is a improvement of Seco IC applying the Depth. 
				 */
				double icnuno = 1.-Math.log((double) t.is_a.descendants.size()+1.)/Math.log((double) topTerm.is_a.descendants.size()+1.);
				double iczhou = 0.5*(icnuno)+(1.-0.5)*(Math.log(t.depth())/Math.log(topInfo.getValue().maxdepth));
				double icsanchez = (double) - Math.log(probsanchez);///(double)-Math.log(1./(double)topTerm.is_a.descLeaves.size());
				/*
				 * Add the IC results in list to infoTerm
				 */
				lic = new ArrayList<Double>();
				lic.add(icnuno);	
				lic.add(iczhou);
				lic.add(icsanchez);
				/*
				 * Add the IC results in set of all values
				 */
				nunoSet.add(icnuno);
				zhouSet.add(iczhou);
				sanchezSet.add(icsanchez);
				/*
				 * Add the probability results in list to infoTerm
				 */
				lpob = new ArrayList<Double>();
				lpob.add(probd);
				lpob.add(probd);
				lpob.add(probsanchez);

				t.addICs(lic); // ADD IC and probability list in InfoTerm object






			}
			topInfo.getValue().maxIC.add(Collections.max(nunoSet));
			topInfo.getValue().maxIC.add(Collections.max(zhouSet));
			topInfo.getValue().maxIC.add(Collections.max(sanchezSet));
		}
	}
	/**
	 * That is a idea to create a new hybrid information content using the Seco IC but using the specific terms of a organism.	
	 * @param sub
	 * @param goa
	 */
	public void hybridIC(String sub, Annotation goa) {

		Set<String> terms = new HashSet<String>(); // Terms set of annotated a complete genome
		Map<String,Set<String>> go2descendantORG = new HashMap<>(); // Map if GO terms a the descedant most specific to a specific organism
		for(String gen : goa.annotation.keySet()) { // For each annotated gene
			for(String t : goa.annotation.get(gen).getTerms(sub)) { // For each term annotated to a given gene
				terms.add(t); // Add in terms set
				terms.addAll(this.allStringtoInfoTerm.get(t).is_a.ancestors); // Add all ancestors in terms set
			}
		}
		Set<String> desc = new HashSet<String>(this.allStringtoInfoTerm.get(sub).is_a.descendants); // Get its descendants
		desc.retainAll(terms);
		go2descendantORG.put(sub,desc); 
		this.allStringtoInfoTerm.get(sub).ICs.add(0.);
		Set<Double> icsTest = new HashSet<>();
		for(String t : terms) { // For each term in terms set
			desc = new HashSet<String>(this.allStringtoInfoTerm.get(t).is_a.descendants); // Get its descendants
			desc.retainAll(terms); // Intersection with terms set
			go2descendantORG.put(t,desc); // Push the information in the Map
			double icnuno = 1.-Math.log((double) go2descendantORG.get(t).size()+1.)/Math.log((double)go2descendantORG.get(sub).size()+1.); //compute IC

			this.allStringtoInfoTerm.get(t).ICs.add(icnuno);
			icsTest.add(icnuno);
		}
		this.subontology.get(sub).maxIC.add(Collections.max(icsTest)); // Get Max IC and add to Map top2MaxIC
		//go2descendantORG.putAll(go2descendantORG);

	}
	/**
	 * After get the GOA annotation, we compute the extrinsic IC. Also, we use a new hybrid IC using Annotation IC instad Seco IC to Zhou IC.
	 */
	/*
	 * The procedure is similar to GetICs method
	 */
	public void addAnnotationICs(){
		Set<Double> annotationSet = new HashSet<Double>(); // Annotation IC set
		Set<Double> annotationZhouSet = new HashSet<Double>(); // Annotation improve apply Zhou IC set

		//		Set<String> test = new HashSet<String>();
		//		for(String topOnt : this.subontology) { 
		//			test.addAll(this.allStringtoInfoTerm.get(topOnt).genome);
		//		}
		for(Entry<String, OntoInfo> topInfo : this.subontology.entrySet()) {
			String topOnt = topInfo.getKey(); // For each subOntology
			InfoTerm topTerm = this.allStringtoInfoTerm.get(topOnt);
			topTerm.ICs.add(0.);
			topTerm.ICs.add(0.);

			for(String ter : topTerm.is_a.descendants){ // For each terms descendant to top
				InfoTerm t = this.allStringtoInfoTerm.get(ter);
				double probg = ((double)t.genome.size())/((double)topTerm.genome.size());
				double icg = - Math.log(probg);///-Math.log10((1./((double)topTerm.genome.size()))) ;
				t.ICs.add(icg);

				double iczhou = 0.5*(icg/-Math.log(1./(double)topTerm.genome.size()))+(1.-0.5)*(Math.log(this.allStringtoInfoTerm.get(ter).depth())/Math.log(topInfo.getValue().maxdepth));	
				t.ICs.add(iczhou);

				if(probg>0.) {
					annotationSet.add(icg);
					annotationZhouSet.add(iczhou);
					//					annotationSetNN.add(icgnn);
				}



			}
			topInfo.getValue().maxIC.add(Collections.max(annotationSet));
			topInfo.getValue().maxIC.add(Collections.max(annotationZhouSet));



		}


	}
	public void getLocalInformation() {


		for(String t : this.allStringtoInfoTerm.keySet()) {
			InfoTerm it = this.allStringtoInfoTerm.get(t);
			it.sIC = -Math.log((double)it.geneSet.size()/(double) this.allStringtoInfoTerm.get(it.top).geneSet.size());
			it.ivalue = it.ICs.get(0)/it.sIC;

		}



	}




	/**
	 * Method to get the distance for every terms making easy recover the information after in others process.
	 */
	public void GetDistanceTerms(){
		/*
		 * Get distances between terms
		 */
		Set<Edges> edges = new HashSet<Edges>();
		for(String m : this.allStringtoInfoTerm.keySet()){
			Edges[] lien;
			ArrayList<Edges> e = new ArrayList<Edges>();
			InfoTerm it = this.allStringtoInfoTerm.get(m);
			for(String pere : it.is_a.parents){
				Edges ed = new Edges(this.allStringtoInfoTerm.get(m),this.allStringtoInfoTerm.get(pere),1.0);
				e.add(ed);
				edges.add(ed);
			}
			for(String pere : it.part_of.ancestors){
				Edges ed = new Edges(this.allStringtoInfoTerm.get(m),this.allStringtoInfoTerm.get(pere),1.0);
				e.add(ed);
				edges.add(ed);
			}
			// To save distances for each node
			lien =  new Edges[e.size()];
			for(int n = 0; n<e.size();n++){
				lien[n] = e.get(n);
			}
			this.allStringtoInfoTerm.get(m).adjacencies = lien;
		}
		//this.LienMax = edges.size();
		// Saving distance for each terms
		this.SaveDistance();
	}
	/**
	 * Method depends of GetDistanceTerms. That method compute A* algorithm to get the distance between two terms
	 */
	private void SaveDistance(){

		for(String ter: this.allStringtoInfoTerm.keySet()){
			InfoTerm t = this.allStringtoInfoTerm.get(ter);
			Hashtable<String,Double> distancias = new Hashtable<String,Double>();
			for(String p : t.is_a.ancestors){
				AstarSearchAlgo.AstarSearch(t,this.allStringtoInfoTerm.get(p));
				double valor = this.allStringtoInfoTerm.get(p).g_scores;// keep disance
				distancias.put(p, valor);
			}
			//			for(String p : t.part_of.ancestors){
			//				AstarSearchAlgo.AstarSearch(t,this.allStringtoInfoTerm.get(p));
			//				double valor = this.allStringtoInfoTerm.get(p).g_scores;// keep disance
			//				distancias.put(p, valor);
			//			}
			distancias.put(ter, 0.);
			t.SaveDistance(distancias);

		}

	}
	public void AggregateIC() {

		for(String t : this.allStringtoInfoTerm.keySet()) {
			InfoTerm it = this.allStringtoInfoTerm.get(t);

			if(!it.id.equals(it.top)) {
				it.sValueIC = 1./(1. + Math.exp(-1./it.ICs.get(3)));
			}else {
				it.sValueIC = 1;

			}






		}

		for(String t : this.allStringtoInfoTerm.keySet()) {
			InfoTerm it = this.allStringtoInfoTerm.get(t);
			double aic = it.sValueIC;
			for(String anc : it.is_a.ancestors) {
				aic = aic + this.allStringtoInfoTerm.get(anc).sValueIC;
			}
			it.aggregateIC = aic;
		}

		//		System.exit(0);

	}

	public void goUniverseIC() {
		
		 NumberFormat formatter = new DecimalFormat();
		 formatter = new DecimalFormat("0.###########E0");

		for(Entry<String,OntoInfo> sub : this.subontology.entrySet()) {
			OntoInfo oi = sub.getValue();
			this.allStringtoInfoTerm.get(sub.getKey()).ICs.add(0.);
			this.allStringtoInfoTerm.get(sub.getKey()).alphaBetaMazandu[0] = "1";
			this.allStringtoInfoTerm.get(sub.getKey()).alphaBetaMazandu[1] = "0";
			Set<Double> setMax = new HashSet<>();
			for(String lt : oi.getLeavesISA()) {
				String[] dd = this.goUniverseIC(this.allStringtoInfoTerm.get(lt),formatter);
				setMax.add(-Math.log(Double.parseDouble(dd[0]))-Double.parseDouble(dd[1])*Math.log(10));
				//System.out.println(-Math.log(Double.parseDouble(dd[0]))-Double.parseDouble(dd[1])*Math.log(10));
				
				//			if(dd == 0) {
				//				System.out.println(lt + " " + dd);
				//				System.exit(0);
				//			}
				//			probMax.add(dd);
			}

			Double max = Collections.max(setMax);

			oi.maxIC.add(max);

			System.out.println("GO Universel " + sub.getKey() + " value = " + max );
		}



	}
	public String[] goUniverseIC(InfoTerm it, NumberFormat nf) {

		Double alpha = 1.;
		Double beta = 0.;
		
		for(String par : it.is_a.parents) {

			
			if(!par.equals(it.top)) {
				String[] pereAlphaBeta = goUniverseIC(this.allStringtoInfoTerm.get(par), nf);
				double division = Double.parseDouble(pereAlphaBeta[0])/(double) this.allStringtoInfoTerm.get(par).is_a.childrens.size();
				String[] alphaBeta = nf.format(division).toString().split("E");
				alpha = alpha* Double.parseDouble(alphaBeta[0]);
				beta = beta + Double.parseDouble(pereAlphaBeta[1]) + Double.parseDouble(alphaBeta[1]);
				alphaBeta[1] = beta.toString();
				
				}
			else {
				double division = 1./(double) this.allStringtoInfoTerm.get(par).is_a.childrens.size();
				String[] alphaBeta = nf.format(division).toString().split("E");
				alpha = alpha* Double.parseDouble(alphaBeta[0]);
				beta = beta + Double.parseDouble(alphaBeta[1]);
			}
		}
		it.alphaBetaMazandu[0] = alpha.toString();
		it.alphaBetaMazandu[1] = beta.toString();
	
		if(it.ICs.size()==3) {
			it.ICs.add(-Math.log(Double.parseDouble(alpha.toString()))-Double.parseDouble(beta.toString())*Math.log(10));
		}
		//System.out.println(it.ICs.size());
		//System.out.println("\t"+it.id + " " +probX);
		return it.alphaBetaMazandu;
	}

//	public BigDecimal goUniverseICTest(InfoTerm it) {
//
//		BigDecimal probX = new BigDecimal(1);
//		if(it.probMazandu==0) {
//			
//		
//		
//		for(String par : it.is_a.parents) {
//
//			
//			if(!par.equals(it.top)) {
//				probX = probX.multiply(this.goUniverseIC(this.allStringtoInfoTerm.get(par)).divide(new BigDecimal(this.allStringtoInfoTerm.get(par).is_a.childrens.size()), 10, RoundingMode.UP));
//				}
//			else {
//				//System.out.println(it.top+":::" +it.top+ " : "+ this.allStringtoInfoTerm.get(par).is_a.childrens.size());
//				probX = probX.multiply(new BigDecimal(1).divide(new BigDecimal(this.allStringtoInfoTerm.get(par).is_a.childrens.size()),10, RoundingMode.UP));
//			}
//		}
//	
//		//System.out.println(it.top+":::" +it+ " : "+ it.is_a.childrens.size()+ "  :  " +probX);
//		//System.out.println(probX.doubleValue());
//		it.ICs.add(-BigDecimalMath.log(probX,new MathContext(20)).doubleValue());
//		it.probMazandu = probX.doubleValue();
//	}else {
//		probX = new BigDecimal(it.probMazandu);
//	}
//	
//		System.out.println(it.ICs.size());
//		//System.out.println("\t"+it.id + " " +probX);
//		return (probX);
//	}


}




