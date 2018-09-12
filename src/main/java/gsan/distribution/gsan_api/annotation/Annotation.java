package gsan.distribution.gsan_api.annotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;
import jxl.read.biff.BiffException;
public class Annotation {
	static long time_start;
	static long time_end;


	public Hashtable<String,AnnotationProperty> annotation;
	//String organism = "N/A";
	List<String> proteins;
	Set<String> genes;
	public Map<String,Map<Integer,Double>> percentileOnt;

	public Annotation() throws BiffException, IOException {
		this.annotation = new Hashtable<String,AnnotationProperty>();
		this.proteins = new ArrayList<String>();
		this.genes = new HashSet<String>();
		this.percentileOnt = new HashMap<String,Map<Integer,Double>>();
	}
	public Annotation(Annotation goa) throws BiffException, IOException {
		this.annotation = new Hashtable<String,AnnotationProperty>();
		this.annotation.putAll(goa.annotation);
		this.proteins = new ArrayList<String>(goa.proteins);
		this.genes = new HashSet<String>(goa.genes);
		this.percentileOnt = new HashMap<String,Map<Integer,Double>>();
		this.percentileOnt.putAll(goa.percentileOnt);
	}
	public Annotation(List<List<String>> table, GlobalOntology gont, int ic,boolean bol) throws BiffException, IOException {
		this.annotation = new Hashtable<String, AnnotationProperty>();
		//this.organism = organisme;
		// To get all proteins
		this.proteins = new ArrayList<String>();
		this.genes = new HashSet<String>();
		int ndNot = 0;
		int obs = 0;
		List<String> avoidedEC = new LinkedList<String>();
		avoidedEC.add("NOT");
		avoidedEC.add("ND");
		if(!bol) {
			avoidedEC.add("IEA");
		}

		
		DescriptiveStatistics icBpOrg = new DescriptiveStatistics();
		DescriptiveStatistics icMfOrg = new DescriptiveStatistics();
		DescriptiveStatistics icCcOrg = new DescriptiveStatistics();
		DescriptiveStatistics icROrg = new DescriptiveStatistics();
		// Recupera toda la anotacion a partir de su fichero
		for(List<String> line:table){
//			System.out.println(line);
			if(!(avoidedEC.contains(line.get(3))||avoidedEC.contains(line.get(6)))){  // 
			//if(!(line.get(3).equals("NOT")|| line.get(6).equals("ND")  || line.get(6).equals("IEA") )){
			
				String gene = line.get(2);
				String prot = line.get(1);
				String go = line.get(4);
				String ont = line.get(8);
			//	String name = line.get(6);
				if(!this.annotation.containsKey(gene)) {
					this.proteins.add(prot);
					this.annotation.put(gene, new AnnotationProperty(gene));
					this.annotation.get(gene).symbol = gene;
					//this.annotation.get(gene).name = name;	
					
				}
				this.annotation.get(gene).proteins.add(prot);
				if(gont.allStringtoInfoTerm.containsKey(go)) {
					switch(ont){
					case "P" :
						
							this.annotation.get(gene).terms.add(go);
							this.annotation.get(gene).bp.add(go);
					
					
						break;
					case "F":
						this.annotation.get(gene).terms.add(go);
						this.annotation.get(gene).mf.add(go);
						
						break;
					case "C" :
						this.annotation.get(gene).terms.add(go);
						this.annotation.get(gene).cc.add(go);
						
						break;
					case "R" :
						this.annotation.get(gene).terms.add(go);
						this.annotation.get(gene).r.add(go);
						
						break;
					default :
						this.annotation.get(gene).terms.add(go);

					}
				}
				
				
				
				
			
			}
			else { ndNot ++;}

		}

		
for(String gene : this.annotation.keySet()) {
	
	for(String t : this.annotation.get(gene).bp) {
		icBpOrg.addValue(gont.allStringtoInfoTerm.get(t).ICs.get(ic));
	}
	for(String t : this.annotation.get(gene).mf) {
		icMfOrg.addValue(gont.allStringtoInfoTerm.get(t).ICs.get(ic));
	}
	for(String t : this.annotation.get(gene).cc) {
		icCcOrg.addValue(gont.allStringtoInfoTerm.get(t).ICs.get(ic));
	}
	
}

Set<String> rem = new HashSet<String>(this.proteins);
this.proteins.clear();
this.proteins.addAll(rem);
this.genes.addAll(this.annotation.keySet());
System.out.println("NDNOT number " + ndNot);
System.out.println("Obsolete number " + obs);

this.percentileOnt = new HashMap<>();
Map<Integer,Double> stats = new HashMap<>();  
    stats.put(0, 0.);
	stats.put(25, icBpOrg.getPercentile(25));
	stats.put(50, icBpOrg.getPercentile(50));
	stats.put(75, icBpOrg.getPercentile(75));
this.percentileOnt.put("GO:0008150", stats);
stats = new HashMap<>();  
stats.put(0, 0.);
stats.put(25, icMfOrg.getPercentile(25));
stats.put(50, icMfOrg.getPercentile(50));
stats.put(75, icMfOrg.getPercentile(75));
this.percentileOnt.put("GO:0003674", stats);
stats = new HashMap<>();  
stats.put(0, 0.);
stats.put(25, icCcOrg.getPercentile(25));
stats.put(50, icCcOrg.getPercentile(50));
stats.put(75, icCcOrg.getPercentile(75));
this.percentileOnt.put("GO:0005575",stats);
stats = new HashMap<>();  
stats.put(0, 0.);
stats.put(25, icROrg.getPercentile(25));
stats.put(50, icROrg.getPercentile(50));
stats.put(75, icROrg.getPercentile(75));
//this.percentileOnt.put("Reactome", (double)Math.round(per.evaluate(vector,25)*100)/100.);
this.percentileOnt.put("Reactome",stats );


for(String gen : this.genes) {
	this.annotation.get(gen).idf = Math.log10((double)genes.size()/(double)this.annotation.get(gen).terms.size())/ Math.log10((double)genes.size());
}
	}

	public static Annotation EcoliAnnotation(List<List<String>> table, GlobalOntology go) throws BiffException, IOException {
		int NumberOfGenesWithOutAnnotation = 0;
		List<String> head = table.get(0);
		Set<String> ObsoleteTerms = new HashSet<String>();
		Set<String> Terms = new HashSet<String>();
		List<String> ObsoleteTermsList = new ArrayList<String>();
		List<String> AnnotationList = new ArrayList<String>();
		table.remove(0);
		Annotation annot = new Annotation();
		// Recupera toda la anotacion a partir de su fichero
		for(List<String> line:table){
			String gene = line.get(head.indexOf("Gene Name"));
			List<String> bp = new ArrayList<String>();
			List<String> mf = new ArrayList<String>();
			List<String> cc = new ArrayList<String>();
			
		
			if(line.size()<5) {
				NumberOfGenesWithOutAnnotation++;
			}else {
				if(line.size()==7) {
					bp = new LinkedList<String>(Arrays.asList(line.get(head.indexOf("GO Process")).split(";")));
					if(!line.get(head.indexOf("GO Function")).isEmpty())
						mf = new LinkedList<String>(Arrays.asList(line.get(head.indexOf("GO Function")).split(";")));
					if(!line.get(head.indexOf("GO Component")).isEmpty())
						cc = new LinkedList<String>(Arrays.asList(line.get(head.indexOf("GO Component")).split(";")));
				}
				else if(line.size()==6) {
					mf = new LinkedList<String>( Arrays.asList(line.get(head.indexOf("GO Function")).split(";")));
					if(!line.get(head.indexOf("GO Component")).isEmpty())
						cc = new LinkedList<String>(Arrays.asList(line.get(head.indexOf("GO Component")).split(";")));
				}else if(line.size() == 5) {
					cc = new LinkedList<String>(Arrays.asList(line.get(head.indexOf("GO Component")).split(";")));
				}
				
				Iterator<String> iter = bp.iterator();
				
				while(iter.hasNext()) {
					
					String b = iter.next();
					
					AnnotationList.add(b);
					Terms.add(b);
					if(!go.allStringtoInfoTerm.containsKey(b)) {
						ObsoleteTerms.add(b);
						
						ObsoleteTermsList.add(b);
						
						iter.remove();
					}
				}
				
				iter = mf.iterator();
				
				while(iter.hasNext()) {
					String b = iter.next();
					AnnotationList.add(b);
					Terms.add(b);
					if(!go.allStringtoInfoTerm.containsKey(b)) {
						ObsoleteTerms.add(b);
						ObsoleteTermsList.add(b);
						iter.remove();
					}
				}
				iter = cc.iterator();
				
				while(iter.hasNext()) {
					String b = iter.next();
					AnnotationList.add(b);
					Terms.add(b);
					if(!go.allStringtoInfoTerm.containsKey(b)) {
						ObsoleteTerms.add(b);
						ObsoleteTermsList.add(b);
						iter.remove();
					}
				}
				
				if(bp.size() == 0 &&mf.size() == 0 &&cc.size() == 0) {
					NumberOfGenesWithOutAnnotation++;
				}else {
				annot.annotation.put(gene, new AnnotationProperty(gene));
				annot.annotation.get(gene).symbol = gene;
				annot.annotation.get(gene).terms.addAll(bp);
				annot.annotation.get(gene).bp.addAll(bp);
				annot.annotation.get(gene).terms.addAll(mf);
				annot.annotation.get(gene).mf.addAll(mf);
				annot.annotation.get(gene).terms.addAll(cc);
				annot.annotation.get(gene).cc.addAll(cc);
				}

			}


		}

		annot.genes.addAll(annot.annotation.keySet());
		System.out.println("\nNDNOT number : There are not that number because the information source is other than Gene Ontology or UniProt.\n"
				+ "However, there are "+NumberOfGenesWithOutAnnotation+" genes without any annotation and so they are not considered in the analysis.\n"
						+ "Also, There are "+ObsoleteTerms.size() +" of "+ Terms.size()+" terms obsolete losing "+ObsoleteTermsList.size() + " relationships of " + AnnotationList.size()+".\n");


		return annot;
	}
	
	
	public static Annotation ManualUniprotAnnotation(List<List<String>> table, GlobalOntology go) throws BiffException, IOException {
		int NumberOfGenesWithOutAnnotation = 0;
		List<String> head = table.get(0);
		Set<String> ObsoleteTerms = new HashSet<String>();
		Set<String> Terms = new HashSet<String>();
		List<String> ObsoleteTermsList = new ArrayList<String>();
		List<String> AnnotationList = new ArrayList<String>();
		table.remove(0);
		Annotation annot = new Annotation();
		// Recupera toda la anotacion a partir de su fichero
		for(List<String> line:table){
			if(line.size()>5) {
			String gene = line.get(head.indexOf("Gene names  (primary )"));
			Queue<String> q = new LinkedList<String>();
			List<String> bp = new ArrayList<String>();
			List<String> mf = new ArrayList<String>();
			List<String> cc = new ArrayList<String>();
			
			
		
			if(line.size()<6) {
				NumberOfGenesWithOutAnnotation++;
			}else {
				q = new LinkedList<String>(Arrays.asList(line.get(head.indexOf("Gene ontology IDs")).replaceAll(" ", "").split(";")));
			
				while(!q.isEmpty()) {
					String t = q.remove();
					
					if(go.allStringtoInfoTerm.containsKey(t)) {
						//System.out.println(t + " " +go.allStringtoInfoTerm.get(t).name );
						//System.out.println(go.allStringtoInfoTerm.get(t).top);
					switch(go.allStringtoInfoTerm.get(t).top) {
					case "GO:0008150":
						bp.add(t);
						break;
					case "GO:0003674":
						mf.add(t);
						break;
					case "GO:0005575":
						cc.add(t);
						break;
					
					}
					
					}else {
						ObsoleteTermsList.add(t);
						ObsoleteTerms.add(t);
					}
				}
				
				
				if(bp.size() == 0 &&mf.size() == 0 &&cc.size() == 0) {
					NumberOfGenesWithOutAnnotation++;
				}else {
				annot.annotation.put(gene, new AnnotationProperty(gene));
				annot.annotation.get(gene).symbol = gene;
				annot.annotation.get(gene).terms.addAll(bp);
				annot.annotation.get(gene).bp.addAll(bp);
				annot.annotation.get(gene).terms.addAll(mf);
				annot.annotation.get(gene).mf.addAll(mf);
				annot.annotation.get(gene).terms.addAll(cc);
				annot.annotation.get(gene).cc.addAll(cc);
				}
				
//				if(gene.equals("IFITM3")) {
//					System.out.println(line);
//					System.out.println(annot.annotation.get(gene).bp);
//					System.exit(0);
//				}

			}


		}else {
			//NumberOfGenesWithOutAnnotation++;
		}
		}
		annot.genes.addAll(annot.annotation.keySet());
		System.out.println("\nNDNOT number : There are not that number because the information source is other than Gene Ontology or UniProt.\n"
				+ "However, there are "+NumberOfGenesWithOutAnnotation+" genes without any annotation and so they are not considered in the analysis.\n"
						+ "Also, There are "+ObsoleteTerms.size() +" of "+ Terms.size()+" terms obsolete losing "+ObsoleteTermsList.size() + " relationships of " + AnnotationList.size()+".\n");


		return annot;
	}
	
	public static Annotation redondancyReduction(Annotation goa,GlobalOntology go) throws BiffException, IOException {


		Annotation GOAred = new Annotation();

		Hashtable<String, AnnotationProperty> newgoa = new Hashtable<String,AnnotationProperty>(); 

		for(String g : goa.annotation.keySet()) {
			newgoa.put(g, new AnnotationProperty(g));
			for(String s : go.subontology.keySet()) {
				List <String> termOnt = new ArrayList<String>(goa.annotation.get(g).getTerms(s));
				for(int i = 0; i<termOnt.size();i ++) {
					InfoTerm t1 = go.allStringtoInfoTerm.get(termOnt.get(i));

					for(int j= i+1; j<termOnt.size();j++) {
						InfoTerm t2 = go.allStringtoInfoTerm.get(termOnt.get(j));
						if(t1.is_a.descendants.contains(t2.toString())){
							termOnt.remove(i);
							i--;
							break;
						}else if(t2.is_a.descendants.contains(t1.toString())){
							termOnt.remove(j);
							j--;

						}

					}

				}
				newgoa.get(g).addAllTerms(new HashSet<String>(termOnt),s);


			}

			newgoa.get(g).proteins.addAll(goa.annotation.get(g).proteins);

			newgoa.get(g).name=goa.annotation.get(g).name;
			newgoa.get(g).symbol=goa.annotation.get(g).symbol;
			newgoa.get(g).idf=goa.annotation.get(g).idf;
		}
		GOAred.proteins.addAll(goa.proteins);
		GOAred.genes.addAll(goa.genes);
		GOAred.annotation.putAll(newgoa);
		GOAred.percentileOnt.putAll(goa.percentileOnt);





		return GOAred;






	}


public static Annotation icIncompleteReduction(Annotation goa,GlobalOntology go, int ic) throws BiffException, IOException {
	

	Annotation GOAred = new Annotation();
	
	Hashtable<String, AnnotationProperty> newgoa = new Hashtable<String,AnnotationProperty>(); 
	
	for(String g : goa.annotation.keySet()) {
		newgoa.put(g, new AnnotationProperty(g));
		for(String s : go.subontology.keySet()) {
			List <String> termOnt = new ArrayList<String>(goa.annotation.get(g).getTerms(s));
			for(int i = 0; i<termOnt.size();i ++) {
				InfoTerm t1 = go.allStringtoInfoTerm.get(termOnt.get(i));
				//System.out.println(t1.top +" " +t1.ICs.get(ic) + " " +ic);
				if(t1.ICs.get(ic)<goa.percentileOnt.get(t1.top).get(25)) {
//					if((t1.ICs.get(ic)/go.top2MaxIC.get(t1.top).get(ic))<goa.percentileOnt.get(t1.top)) {
						termOnt.remove(i);
						i--;
					}
					
				
			}
			newgoa.get(g).addAllTerms(new HashSet<String>(termOnt),s);
			
	
		}
		
		newgoa.get(g).proteins.addAll(goa.annotation.get(g).proteins);
		GOAred.proteins.addAll(goa.annotation.get(g).proteins);
		GOAred.genes.add(g);
		newgoa.get(g).name=goa.annotation.get(g).name;
		newgoa.get(g).symbol=goa.annotation.get(g).symbol;
		newgoa.get(g).idf=goa.annotation.get(g).idf;
		
	}
	
	GOAred.annotation.putAll(newgoa);
	GOAred.percentileOnt.putAll(goa.percentileOnt);
	
	List<String> gremove = new ArrayList<>();
	for(String g : GOAred.annotation.keySet()) {
		if(GOAred.annotation.get(g).terms.isEmpty()) {
			gremove.add(g);
		}
	}
	
	for(String g : gremove )GOAred.annotation.remove(g);
	
	GOAred.genes.removeAll(gremove);
	
	
	return GOAred;
	
	
	
	
	
	
}


public Set<String> getProt(List<String> genes){
	
	Set<String> p = new HashSet<String>();
	
	for(String g: genes) {
		if(this.annotation.containsKey(g)) {
		p.addAll(this.annotation.get(g).proteins);}
		
	}
	

	return p;			

}


	
	public  List<String> getTermsRR(Hashtable<String,List<String>> g2term){

		Set<String> newlistterm = new HashSet<String>();
		for(String p:g2term.keySet()){
			newlistterm.addAll(g2term.get(p));
		}
		return new ArrayList<String>(newlistterm);
	}
	
	public  List<String> getTerms(String ontology){

		Set<String> newlistterm = new HashSet<String>();
		for(String p:this.annotation.keySet()){
			newlistterm.addAll(this.annotation.get(p).getTerms(ontology));
		}
		return new ArrayList<String>(newlistterm);
	}
	
	public  List<String> getAnnotation(String ontology){ // Sans filtrer

		List<String> newlistterm = new ArrayList<String>();
		for(String p:this.annotation.keySet()){
			newlistterm.addAll(this.annotation.get(p).getTerms(ontology));
		}
		return newlistterm;
	}
	public  List<String> getAnnotation(List<String> genes,String ontology){ // Sans filtrer

		List<String> newlistterm = new ArrayList<String>();
		for(String p:new HashSet<String>(genes)){
			if(this.annotation.containsKey(p)) {
			newlistterm.addAll(this.annotation.get(p).getTerms(ontology));
		}
			}
		return newlistterm;
	}
public  List<String> getTerms(List<String> genes, String top, GlobalOntology go){
		
		Set<String> newlistterm = new HashSet<String>();
		Set<String> genesNoNoted = new HashSet<>();
		
		for(String p:new HashSet<String>(genes)){
			if(!this.annotation.containsKey(p)) {
				genesNoNoted.add(p);
				//System.out.println(p);
			}else {
			
			List<String> termes = this.annotation.get(p).getTerms(top);

			if(termes.isEmpty()) {
				genesNoNoted.add(p);
			}
				//System.out.println(p + " " + ontology.top);
			newlistterm.addAll(termes);
//			for(String nelt : termes) {
//				sb.append(p + "\t" +ontology.stringtoInfoTerm.get(nelt).toString()+"\t"+ ontology.stringtoInfoTerm.get(nelt).toName()+"\n");
//			}
			//System.out.println(this.annotation.get(p).bp);
			for(String t : termes) {
				InfoTerm it = go.allStringtoInfoTerm.get(t);
				it.addGen(p,go);
//				if(it.getRegulatesClass()!=null) {
//					go.allStringtoInfoTerm.get(it.getRegulatesClass()).addGen(p, go);
//				}
			}
			}
		}
		
//		try {
//			PrintWriter pw = new PrintWriter("/home/aaron/Bureau/"+ontology.top+".csv");
//			pw.println(sb);
//			pw.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//System.out.println(new HashSet<String>(genes).size());
		System.out.println("There are "+genesNoNoted.size()+" genes no registred in GOA for " + go.allStringtoInfoTerm.get(top).toName());
		System.out.println("There are "+(genes.size()-genesNoNoted.size())+" genes registred in GOA for " + go.allStringtoInfoTerm.get(top).toName());
	
		
		return new ArrayList<String>(newlistterm);
	}
public  List<String> getTermsSansPrint(List<String> genes, String top, GlobalOntology go){
	
	Set<String> newlistterm = new HashSet<String>();
	Set<String> genesNoNoted = new HashSet<>();
	
	for(String p:new HashSet<String>(genes)){
		if(!this.annotation.containsKey(p)) {
			genesNoNoted.add(p);
			//System.out.println(p);
		}else {
		
		List<String> termes = this.annotation.get(p).getTerms(top);

		if(termes.isEmpty()) {
			genesNoNoted.add(p);
		}
			//System.out.println(p + " " + ontology.top);
		newlistterm.addAll(termes);
//		for(String nelt : termes) {
//			sb.append(p + "\t" +ontology.stringtoInfoTerm.get(nelt).toString()+"\t"+ ontology.stringtoInfoTerm.get(nelt).toName()+"\n");
//		}
		//System.out.println(this.annotation.get(p).bp);
		for(String t : termes) {
			InfoTerm it = go.allStringtoInfoTerm.get(t);
			it.addGen(p,go);
//			if(it.getRegulatesClass()!=null) {
//				go.allStringtoInfoTerm.get(it.getRegulatesClass()).addGen(p, go);
//			}
		}
		}
	}
	
//	try {
//		PrintWriter pw = new PrintWriter("/home/aaron/Bureau/"+ontology.top+".csv");
//		pw.println(sb);
//		pw.close();
//	} catch (FileNotFoundException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
	//System.out.println(new HashSet<String>(genes).size());

	return new ArrayList<String>(newlistterm);
}
	public  List<String> getTerms(List<String> genes, String ontology){

		Set<String> newlistterm = new HashSet<String>();
		int count = 0;
		for(String p:genes){
			if(!this.annotation.containsKey(p)) {
				count ++ ;
			}else {
			newlistterm.addAll(this.annotation.get(p).getTerms(ontology));
			
			}
		}
		System.out.println("There are "+count+" genes no registred in GOA for " + ontology);
		return new ArrayList<String>(newlistterm);
	}

	
	
	


	public List<String> proteins(){
		return this.proteins;
	}
	public AnnotationProperty get(String prote){
		return this.annotation.get(prote);

	}
	public boolean containsKey(String prote){
		return this.annotation.containsKey(prote);

	}
	public Integer size(){
		return this.annotation.size();

	}
}

