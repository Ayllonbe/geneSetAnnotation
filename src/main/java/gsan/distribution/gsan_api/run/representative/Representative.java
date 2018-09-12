package gsan.distribution.gsan_api.run.representative;


import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import gsan.distribution.gsan_api.annotation.Annotation;
import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;

public class Representative {
	static DecimalFormat df = new DecimalFormat("0.00");

	public String id;
	public InfoTerm repesentative;
	public Set<InfoTerm> terms;
	public double locality ; 
	public Representative(String id, InfoTerm r, Set<InfoTerm> t, double loc) {
		this.id = id;
		this.repesentative = r;
		this.terms = t;
		this.locality = loc;
	}
	public Representative(String id, InfoTerm r, Set<InfoTerm> t) {
		this.id = id;
		this.repesentative = r;
		this.terms = t;
	}

	/**
	 * Allow get the set of representative of each cluster 
	 * @param ic
	 * @param sub
	 * @param mSS
	 * @param mhcl
	 * @param geneset
	 * @param resfolder
	 * @param statfolder
	 * @param go
	 * @param terminos
	 * @param tailmin
	 * @param filtre
	 * @param precision
	 * @return
	 * @throws Exception
	 */

	public static void onemetric(String module, int ic,String sub,String[] methodSS, String mhcl,Set<String> geneset,
			String resfolder, String statfolder, GlobalOntology go, List<String>terminos, Annotation ann,
			double tailmin,double RepCombinedSimilarity, double precision,int compare) throws Exception{

		for(String mSS : methodSS) {

			System.out.println("### Method : " + mSS);
			String fileSS = resfolder+"/" + mSS+".csv";

			Communication communication = Communication.run(fileSS, mhcl);

			Hashtable<Integer,List<Representative>> cl2rep = Representative.getRepresentative(go,sub,ic,  communication.clusters, 
					tailmin, RepCombinedSimilarity, precision,compare);


			Hashtable<Integer,Integer> cl2nbg = new Hashtable<Integer,Integer>();
			for(Integer i : cl2rep.keySet()){

				for(Representative rep : cl2rep.get(i)){

					InfoTerm it = rep.repesentative;


					cl2nbg.put(i, it.geneSet.size());

				}
			}

			//Transfer as List and sort it

			ArrayList<Map.Entry<Integer, Integer>> listCluster = new ArrayList<>(cl2nbg.entrySet());
			Collections.sort(listCluster, new Comparator<Map.Entry<?, Integer>>(){

				public int compare(Map.Entry<?, Integer> o1, Map.Entry<?, Integer> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}});

			StringBuffer infoRepSB = new StringBuffer();
			infoRepSB.append("id\tname\tnbGenes\tICSeco\tICZhou\tICSanchez\tICMazandu\n");

			for(Entry<Integer, Integer> entry : listCluster){
				int i = entry.getKey();
				for(Representative rep : cl2rep.get(i)){

					InfoTerm it = rep.repesentative;
					Set<String> gen = new HashSet<>();
					for(InfoTerm s : rep.terms) {
						gen.addAll(s.geneSet);	
					}
					//if(gen.size()>=3) {
					if(!it.termcombi.isEmpty()) {
						for(String ti : it.termcombi) {
							InfoTerm term = go.allStringtoInfoTerm.get(ti);
							infoRepSB.append(term.id+"\t"+term.name+"\t"+term.geneSet.size()+"\t"+df.format(term.ICs.get(0))+"\t"+df.format(term.ICs.get(1))+"\t"+df.format(term.ICs.get(2))+"\t"+df.format(term.ICs.get(3))+"\n");

						}
					}else {
						infoRepSB.append(it.id+"\t"+it.name+"\t"+it.geneSet.size()+"\t"+df.format(it.ICs.get(0))+"\t"+df.format(it.ICs.get(1))+"\t"+df.format(it.ICs.get(2))+"\t"+df.format(it.ICs.get(3))+"\n");



						//}
					}

				}

			}
			PrintWriter pw = new PrintWriter(statfolder+"/representatives_"+mSS+".csv");
			pw.print(infoRepSB);
			pw.close();

		}

	}

	public static  Hashtable<Integer, List<Representative>> getRepresentative(GlobalOntology go,String sub,int ic,
			Hashtable<Integer,List<String>> clusters,double tailmin,double RepCombinedSimilarity, double precision,double compare) throws Exception
	{
		// Default

		/*
		 * Al final de este procedimiento, obtendremos un hashtable donde tenemos para cada custer una lista de terminos
		 * representativos para ese cluster.
		 *  
		 */

		Hashtable<Integer, List<Representative>> clu2rep = new Hashtable<Integer, List<Representative> >();

		int MaxCombination = 0;
		int termClusterMax = 0;
		for(Integer j : clusters.keySet()){ // Para cada cluster
			List<String> termCluster = clusters.get(j);
			int ncombi = termCluster.size()>5?((int) Math.floor(Math.sqrt(Math.abs(termCluster.size()/10)-1))+2):1 ; // numero que indica el numero de combinaciones deseadas
			MaxCombination = Math.max(MaxCombination, ncombi);
			termClusterMax = Math.max(termClusterMax, termCluster.size());
			ncombi = ncombi >6? 6:ncombi;
			Set<InfoTerm> manyRep = new HashSet<InfoTerm>(getManyRep(termCluster,sub,go,ncombi,tailmin,RepCombinedSimilarity,precision,ic));

			Set<InfoTerm> infoterm = new HashSet<InfoTerm>();
			for(String t : termCluster){
				infoterm.add(go.allStringtoInfoTerm.get(t));
			}

			List<Double> ics = new ArrayList<Double>();
			for(InfoTerm it : manyRep){
				ics.add(it.ICs.get(ic));
			}
			double icMAX = Collections.max(ics);
			List<Representative> listrep = new ArrayList<Representative>();

			for(InfoTerm it : manyRep){
				if(it.ICs.get(ic)==icMAX) {

					listrep.add( new Representative(it.toString(),it,infoterm));
				}
			}




			clu2rep.put(j, listrep);




		}
		
		System.out.println("Max combination obtained is: " + MaxCombination);
		System.out.println("Max number of terms in a cluster is: " + termClusterMax);

		return clu2rep;
	}

	/**
	 * 
	 * @param term
	 * @param sub
	 * @param go
	 * @param ncombi
	 * @param tailmin
	 * @param filtre
	 * @param precision
	 * @param ic
	 * @return
	 * @throws Exception
	 * 
	 * Objective: compute the representative algorithm exploiting the graph (using only the "is a" link.
	 * 
	 * Output: List of representative candidates.
	 * 
	 */

	public static Set<InfoTerm> getManyRep(List<String> termList,String topSubOnt,GlobalOntology go,int ncombi,double tailmin,double RepCombinedSimilarity,double precision,int ic) throws Exception{

		Stack<String> stack = new Stack<String>();
		/*
		 *  Initialize stack and bitset.
		 */
		for(int i = 0; i<termList.size();i++){
			String St = termList.get(i);
			stack.push(St);
			InfoTerm term = go.allStringtoInfoTerm.get(St); 
			term.bits.set(i);
		}
		/*
		 * Charge the set of terms and their ancestors.
		 */
		Set<String> termSubGraph = new HashSet<String>(termList);

		for(String t : termList){
			termSubGraph.addAll(go.allStringtoInfoTerm.get(t).is_a.ancestors);


		}

		/*
		 * Using the true path to heritage the bitset information from children to parent.
		 */
		while(stack.size()>0){
			String x = (String) stack.pop();
			InfoTerm term = go.allStringtoInfoTerm.get(x);
			for(String p : term.is_a.parents){
				InfoTerm parent = go.allStringtoInfoTerm.get(p);
				BitSet pivot = new BitSet();
				pivot.or(term.bits);
				pivot.andNot(parent.bits);
				if(!pivot.isEmpty()){
					parent.bits.or(term.bits);
					stack.push(p);
				}
			}


		}

		/*
		 * Get the most specific representative terms ancestor of every term in a given cluster
		 */
		Set<InfoTerm> candidates =  getOneRep(termSubGraph,topSubOnt, go,termList.size(),precision); 
		Set<InfoTerm> candidateManyRep = new HashSet<InfoTerm>();

		int limit = 2;
		while(limit<=ncombi){

			
			for(InfoTerm repCan : candidates){
				List<String> consideredChildren = new ArrayList<String>();

				for(String d : repCan.is_a.childrens){ /// ATENTION CHANGEMENT
					if(termSubGraph.contains(d)){
						if((double)go.allStringtoInfoTerm.get(d).bits.cardinality()/(double)termList.size()>=tailmin)

							consideredChildren.add(d);
					}
				}
				candidateManyRep = new HashSet<InfoTerm>();
				
				Set<Set<String>> dC  = doCombine(repCan, consideredChildren,termList.size(), go,   RepCombinedSimilarity,limit,precision); // TEST				// Get the most specific terms of the combination
				//System.out.println(dC);
				for(Set<String> sdc : dC){
					
					List<List<InfoTerm>> li = new ArrayList<List<InfoTerm>>();
					for(String t : sdc){
						InfoTerm term = go.allStringtoInfoTerm.get(t);
						Set<InfoTerm> X = getSpeficicTerm(term.toString(),termSubGraph,go);
						li.add(new ArrayList<InfoTerm>(X));
					}
					Set<InfoTerm> result = new HashSet<InfoTerm>();
					List<InfoTerm> current = new ArrayList<InfoTerm>();
					candidateManyRep.addAll(GeneratePermutations(li, result, 0, current,go));
				}

			}
			if(!candidateManyRep.isEmpty()){
				candidates.clear();

				List<Double> list_ic = new ArrayList<Double>();
				for(InfoTerm t : candidateManyRep ){
					list_ic.add( t.ICs.get(ic));


				}
				double ICmax = Collections.max(list_ic);

				for(InfoTerm t : candidateManyRep){
					if(t.ICs.get(ic) == ICmax){
						candidates.add(t);
					}
				}
				candidateManyRep.clear();
			}
			limit++;

		}

		for(String d : termSubGraph) go.allStringtoInfoTerm.get(d).bits.clear();
		return candidates;
	}

	public static BitSet compare(BitSet lhs, BitSet rhs){
		if(lhs.equals(rhs)) return lhs;
		return lhs.cardinality()<rhs.cardinality()?lhs:rhs;

	}

	public static Set<InfoTerm> getOneRep(Set<String> termSubGraph,String topSubOnt,GlobalOntology go, int termsize,double precision){
		Stack<String> stack = new Stack<String>(); 
		stack.push(topSubOnt);
		Set<InfoTerm> candidateSet = new HashSet<InfoTerm>();
		while(stack.size()>0){
			String t = stack.pop();
			InfoTerm term = go.allStringtoInfoTerm.get(t); 
			int count = 0;
			for(String e :  term.is_a.childrens){ // Pour chaque fils de ta
				if(termSubGraph.contains(e)){
					InfoTerm termChildren = go.allStringtoInfoTerm.get(e);
					//		if(ta.bits.equals(tx.bits)){
					if((double)termChildren.bits.cardinality()/(double)termsize>=precision){
						stack.push(e);
						count++;
					}
				}}
			if(count==0){
				candidateSet.add(term);
			}
		}
		return candidateSet;

	}

	public static Set<InfoTerm> getSpeficicTerm(String top,Set<String> termSubGraph,GlobalOntology go){
		Stack<String> stack = new Stack<String>(); 
		stack.push(top);
		Set<InfoTerm> candidateSet = new HashSet<InfoTerm>();
		while(stack.size()>0){
			String t = stack.pop();
			InfoTerm term = go.allStringtoInfoTerm.get(t); 
			int count = 0;
			for(String e :  term.is_a.childrens){ // Pour chaque fils de ta
				if(termSubGraph.contains(e)){
					InfoTerm termChildren = go.allStringtoInfoTerm.get(e);
					if(term.bits.equals(termChildren.bits)){ // Filtro invariable! 
						stack.push(e);
						count++;
					}
				}}
			if(count==0){

				candidateSet.add(term);
			}
		}
		return candidateSet;

	}

	private static Set<Set<String>> doCombine(InfoTerm ta,List<String> children, int termsize,GlobalOntology go, double RepCombinedSimilarity,int ncombi,double precision) {


		Set<List<String>> combinationSet = Combination.ncombination(children ,ncombi);
		//System.out.println(combinationSet);
		Set<Set<String>> goodCombinedSet = new HashSet<Set<String>>();
		for(List<String> ns : combinationSet) {

			BitSet bitset = new BitSet();
			int control = 0;
			for(int i = 0;i<ns.size();i++){
				InfoTerm tx = go.allStringtoInfoTerm.get(ns.get(i));

				for(int j = i+1;j<ns.size();j++){
					InfoTerm ty = go.allStringtoInfoTerm.get(ns.get(j));
					BitSet inter = new BitSet();
					inter.or(tx.bits);
					inter.and(ty.bits);

					BitSet union = new BitSet();
					union.or(tx.bits);
					union.or(ty.bits);

					double jaccard = ((double)inter.cardinality())/(double)union.cardinality();
					
					if(jaccard>RepCombinedSimilarity){ // el simbolo ">" es porque vamos a romper el bucle si se da el caso. 
						//					// El RepCombinedSimilarity es para evitar tener una combinacion de dos elementos similares.
						control++;	
						break;	
					}	

				}
				if(control!=0){ break;}
				else{
					bitset.or(tx.bits);
				}
			}

			if(control==0&&((double)bitset.cardinality()/(double)termsize)>=precision){ // Elegir cuanta precision queremos en el resultado.

				Collections.sort(ns);
				goodCombinedSet.add(new HashSet<String>(ns));
			}



		}
		return goodCombinedSet;
	}

	public static  Set<InfoTerm> GeneratePermutations(List<List<InfoTerm>> Lists, Set<InfoTerm> result, int depth, List<InfoTerm> current, GlobalOntology go) throws Exception
	{
		current = new ArrayList<InfoTerm>(current);


		if(depth == Lists.size())
		{
			Collections.sort(current);


			InfoTerm tf = new InfoTerm(current);
			if(!result.contains(tf)){
				result.add(tf);}
			return result;
		}

		for(int i = 0; i < Lists.get(depth).size(); ++i)
		{
			current.add(Lists.get(depth).get(i));
			GeneratePermutations(Lists, result, depth + 1, current,go);
			current.remove(current.size()-1);
		}


		return result;
	}
}

