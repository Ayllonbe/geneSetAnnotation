package gsan.distribution.gsan_api.annotation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnnotationProperty {

	String id;
	String symbol;
	String name; // Creo que hay mas de un nombre pues esta relacionada a la proteina y no al gen
	public double idf; // Lo calculo en Annotation.java y ya normalizado. 
	public Set<String> proteins;
	public Set<String> terms;
	//List<String> evidence;
	public Set<String> bp;
	public Set<String> r;
	public Set<String> mf;
	public Set<String> cc;
	String BD = "GO";

	public AnnotationProperty(String id, String symbol, String name, List<String> terms){
		this.id = id;
		this.symbol = symbol;
		this.name = name;
		//this.evidence = new HashSet<String>(evidence);
	}
	public AnnotationProperty(String id){
		this.id = id;
		this.symbol = new String();
		this.name = new String();
		this.terms = new HashSet<String>();
		this.bp = new HashSet<String>();
		this.proteins = new HashSet<String>();
		this.mf = new HashSet<String>();
		this.cc = new HashSet<String>();
		this.r = new HashSet<String>();
		//this.evidence = new ArrayList<String>();
	}
	public String getSymbol(){
		return this.symbol;
	}
	public String getname(){
		return this.name;
	}
	public String toString(){
		return this.id;
	}
	
	public List<String> getTerms(String ontology){

		List<String> newlistterm = new ArrayList<String>();

	
		switch(ontology){
		case "GO:0008150" :
			newlistterm.addAll(this.bp);
			//System.out.println(this.bp.size());
			break;
		case "GO:0003674" :
			newlistterm.addAll(this.mf);
			break;
		case "GO:0005575" :
			newlistterm.addAll(this.cc);
			break;
		case "Reactome" :
			newlistterm.addAll(this.r);
			break;
		}



		return newlistterm;


	}
	public List<String> addAllTerms(Set<String> t,String ontology){

		List<String> newlistterm = new ArrayList<String>();

	
		switch(ontology){
		case "GO:0008150" :
			this.bp.addAll(t);
			this.terms.addAll(t);
			//System.out.println(this.bp.size());
			break;
		case "GO:0003674" :
			this.mf.addAll(t);
			this.terms.addAll(t);
			break;
		case "GO:0005575" :
			this.cc.addAll(t);
			this.terms.addAll(t);
			break;
		case "Reactome" :
			this.r.addAll(t);
			this.terms.addAll(t);
			break;
		}



		return newlistterm;


	}

	public Set<String> getTerms(){

		return this.terms;			

	}
	
	
	
	

}
