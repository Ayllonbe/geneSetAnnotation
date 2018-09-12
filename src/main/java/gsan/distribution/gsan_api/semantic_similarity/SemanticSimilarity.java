package gsan.distribution.gsan_api.semantic_similarity;

import java.util.*;

import gsan.distribution.algorihms.astar.AstarSearchAlgo;
import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;

//import org.apache.poi.poifs.crypt.standard.StandardEncryptionInfoBuilder;

public class SemanticSimilarity {
	
	
	public static Double DistanceFunction(String ti, String tj,GlobalOntology go){
		InfoTerm termino1 = go.allStringtoInfoTerm.get(ti);
		InfoTerm termino2 = go.allStringtoInfoTerm.get(tj);
		Set<String> ancu = new HashSet<String>(termino1.is_a.ancestors);
	//	ancu.addAll(termino1.pOfancestor);
		Set<String> anci = new HashSet<String>(ancu);
		Set<String> anc2 = new HashSet<String>(termino2.is_a.ancestors);
		//anc2.addAll(termino2.pOfancestor);
//		List<String> union = new ArrayList<String>(SetTheory.union(anc1, anc2));
//		List<String> intersection = new ArrayList<String>(SetTheory.intersection(anc1, anc2));
		ancu.addAll(anc2);
		anci.retainAll(anc2);
		double distancefuntion = 1.0 - ((double)(ancu.size()-anci.size())/ancu.size()) ;
	//	double distancefuntion = 1.0 - ((double)((termino1.ancestor.size() + termino2.ancestor.size())-anci.size())/(double)(termino1.ancestor.size() + termino2.ancestor.size())) ;
		return distancefuntion;
	}

//	public static Double Zhou(String ti, String tj, String MICA,double spl,double index,int ic ,LocalOntology lo){
//		
//		Double deepMax = lo.getMaxDept();
//				
//		Double zhou = 1.-(index*(Math.log10(spl+1.)/Math.log10(2.*(deepMax-1.))))-(1.-index)*(jiangEquationdistance(ti,tj,MICA,lo,ic)/2.);
//		
//		return zhou;
//	}
//	
//	
//	
//
//	
//	
//	
//	
//	public static Double Lin(String ti, String tj,String tc,LocalOntology lo,int position){
//		
//		
//				
//			return ((2.0*((double)lo.stringtoInfoTerm.get(tc).ICs.get(position)))/((double)(lo.stringtoInfoTerm.get(ti).ICs.get(position)+lo.stringtoInfoTerm.get(tj).ICs.get(position)))) ;}
//	
//	public static Double ganesan(String ti, String tj,String tc,LocalOntology lo){
//
//		//String tc = ti;
//		Double ganesan = 2.*(lo.stringtoInfoTerm.get(tc).depth()+1.)/(lo.stringtoInfoTerm.get(ti).depth()+1.+lo.stringtoInfoTerm.get(tj).depth()+1.); 
//		return ganesan;
//		
//	}
//
//	public static Double schlicker(String ti, String tj,String tc, LocalOntology lo, int position){
//		
//		if(!ti.equals(tj)){
//		//	System.out.println(Lin(ti, tj, tc,lo,position)*(1.-lo.stringtoInfoTerm.get(tc).probability.get(position)));
//		return	Lin(ti, tj, tc,lo,position)*(1.-lo.stringtoInfoTerm.get(tc).probability.get(position));}
//		
//		else{
//			return 1.0;
//		}
//	
//}
//	public static Double WuPalmer(String ti, String tj, String lca,LocalOntology lo){
//
//		double spl = lo.shortestPathLengths(ti, tj);
//		double wu = 2.*(lo.stringtoInfoTerm.get(lca).depth()+1.)/(spl+2.*( lo.stringtoInfoTerm.get(lca).depth()+1.));
//		return wu;
//
//	}
//	public static Double leacock(String ti, String tj,Double distance, LocalOntology lo){
//
//		if(distance!=0){
//		double leacock = 1. - Math.log10((double) distance)/Math.log10(2.*lo.getMaxDept());
//		return leacock;}
//		else{ 
//			return 1.;
//		}
//	}
//	public static Double LCH(String ti, String tj, String tc,LocalOntology lo , double icmax ,int position){
//
//		Double lch = 1.-(Math.log10(jiangEquationdistance(ti, tj,tc,lo, position)+1.)/Math.log10(2.*icmax+1.));
//		return lch;
//	
//}
//	public static Double Resnick(String ti,String tj,String MICA, LocalOntology lo,Integer position){		
//		
//		if(!ti.equals(tj)){
//			//	System.out.println(Lin(ti, tj, tc,lo,position)*(1.-lo.stringtoInfoTerm.get(tc).probability.get(position)));
//			return lo.stringtoInfoTerm.get(MICA).ICs.get(position);}
//			
//			else{
//				return 1.0;
//			}
//	
//}
//	public static Double pathIC(String ti, String tj, String tc,LocalOntology lo , int position){
//
//		Double path = 1./(jiangEquationdistance(ti, tj,tc,lo, position)+1.);
//		return path;
//	
//}
//	
//	public static Double jiangEquationdistance(String ti,String tj,String lca,LocalOntology lo ,int ic){
//		
//		Double jiangEquationdistance = lo.stringtoInfoTerm.get(ti).ICs.get(ic)+ lo.stringtoInfoTerm.get(tj).ICs.get(ic) - 2.*lo.stringtoInfoTerm.get(lca).ICs.get(ic);
//		return jiangEquationdistance;
//	}
public static Double Shen(InfoTerm it1, InfoTerm it2, InfoTerm mica) {
		
		Set<InfoTerm> testIT = new HashSet<>();
		
		AstarSearchAlgo.AstarSearch(it1,mica);
		testIT.addAll(it1.printPath(mica));
		AstarSearchAlgo.AstarSearch(it2,mica);
		testIT.addAll(it2.printPath(mica));
		double summ = 0.;
		for(InfoTerm it : testIT) {
			summ += 1./it.ICs.get(3);
		}
		return 1.-(Math.atan(summ)/(Math.PI/2));
	}

}
