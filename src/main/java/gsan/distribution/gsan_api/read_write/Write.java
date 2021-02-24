package gsan.distribution.gsan_api.read_write;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import gsan.distribution.gsan_api.annotation.Annotation;
import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.INFOComparator;
import gsan.distribution.gsan_api.ontology.InfoTerm;
import gsan.distribution.gsan_api.semantic_similarity.SemanticSimilarity;


public class Write{
	
	static Double[][] lin ;
	static Double[][] ganesan ;
	static Double[][] df ;
	static Double[][] leacock ;
	static Double[][] zhou ;
	static Double[][] resnik ;
	static Double[][] pekar;
	static Double[][] aggregateIC;
	static Double[][] mazandu ;
	
private static	 Hashtable<String,Double[]> terAnnotationsgenbinary = new Hashtable<String,Double[]>();


	
	/**
	 * Export the representative information. 
	 * @param rep
	 * @param export
	 * @throws FileNotFoundException
	 */
	
	public static void ExportRepresentativeInformation(Set<InfoTerm> rep,String itemexport) throws FileNotFoundException {
		StringBuffer infoRep = new StringBuffer();
		NumberFormat formatter = new DecimalFormat("#0.00");
		infoRep.append("ID;NAME;DEPTH;ICNUNO;ICZHOU;ICSANCHEZ;ICresnik;ICRZ;geneSize;genesSet\n"); // Create head

		for(InfoTerm it : rep) {
			/*
			 * Creation of every line to file
			 */
			infoRep.append(it.id + ";"+ it.name + ";"+formatter.format(it.depth())+ ";"+
					formatter.format(it.ICs.get(0)) + ";"+ formatter.format(it.ICs.get(1))+ ";"+
					formatter.format(it.ICs.get(2))+ ";"+formatter.format(it.ICs.get(3))+
					";"+formatter.format(it.ICs.get(4))
					+ ";"+it.geneSet.size()+ ";"+it.geneSet+"\n");

		}

		if(!new File(itemexport).exists()) {
			new File(itemexport).mkdirs();
		}
		PrintWriter pwTest = new PrintWriter(itemexport +"/InfoRep.csv");
		pwTest.print(infoRep);
		pwTest.close();
	}
	/**
	 * 
	 * @param GOAincom
	 * @param symb
	 * @param gen2rep
	 * @param globalontology
	 * @param GOstr2int
	 * @param rep
	 * @throws FileNotFoundException
	 */
	public static void ExportTransactions(Annotation GOAincom, List<String> symb, Hashtable<String,Set<String>> gen2rep,
			GlobalOntology globalontology,Hashtable<String,Integer> GOstr2int, Set<InfoTerm> rep, String input ) throws FileNotFoundException {
		StringBuffer transaction = new StringBuffer();
		for(String s : symb) {
			if(gen2rep.containsKey(s)) {
				List<String> bpt = new ArrayList<String>(gen2rep.get(s));
				for(String b : bpt) {

					transaction.append(GOstr2int.get(b) + " ");
				}
				transaction.deleteCharAt(transaction.length()-1);
				transaction.append("\n");

			}
		}
		PrintWriter pw = new PrintWriter(input);
		pw.print(transaction);
		pw.close();

	}
	/**
	 * Creation of Annotation binary table
	 * @param GOA
	 * @param go
	 * @param top
	 * @param terminos
	 * @param file
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	
	public static void TableAnnotation(Annotation GOA,GlobalOntology go,String top ,List<String> terminos, String file ) throws FileNotFoundException, UnsupportedEncodingException {
		Set<String> genes = new HashSet<String>();
		
		for(String t :terminos) {// For each term
			genes.addAll(go.allStringtoInfoTerm.get(t).genome); // Get annotated gene in gene set
		}
		List<String >genesList = new ArrayList<String>(genes);
		
		for(String t : terminos) {
			int i =0;
			InfoTerm it = go.allStringtoInfoTerm.get(t);
			terAnnotationsgenbinary.put(t, new Double[genes.size()]);
			for(String g : genesList) {
				if(it.genome.contains(g)) { // To create a binary table
					terAnnotationsgenbinary.get(t)[i] = GOA.annotation.get(g).idf;//1.;
					i++;
				}else {
					terAnnotationsgenbinary.get(t)[i] = 0.;
				i++;
				}
			}
		}
		exportcsv(terminos, genes, terAnnotationsgenbinary, file +"/AnnotationTable.csv");
	}
	/**
	 * Method to export the semantic similarity matrix to a precise term list
	 * @param go
	 * @param sub
	 * @param GOAincom
	 * @param terminos
	 * @param genes
	 * @param ic
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	
	public static void exportSSM(GlobalOntology go, String sub, Annotation GOAincom, List<String> terminos,List<String> genes, String file) throws FileNotFoundException, IOException
	{
		
	     //Double[][] random = generator.nextDouble();
		File f = new File(file);
		
		if(!f.exists()) {//Verify if the folders exists.
			f.mkdirs();
		}
		
		TableAnnotation(GOAincom,go, sub,terminos,file ) ; // Creation to annotation binary table
	
		/*
		 * Information All Term
		 */
		StringBuffer inf = new StringBuffer();
		inf.append("ID;Term;Depth;IC Nuno;IC zhou;IC sanchez;IC resnik;IC RZ;IC Rnormalized;IC Nuno org;Annotated Genes Set number\n");// Create head


		for(String t : terminos){
			/*
			 * Creating line where there is the information of every GO term
			 */
			inf.append(t+";"+go.allStringtoInfoTerm.get(t).toName()+";"+go.allStringtoInfoTerm.get(t).depth()+";"+go.allStringtoInfoTerm.get(t).ICs.get(0)+";"+
					go.allStringtoInfoTerm.get(t).ICs.get(1)+";"+go.allStringtoInfoTerm.get(t).ICs.get(2)+";"+go.allStringtoInfoTerm.get(t).ICs.get(3)+";"
					+go.allStringtoInfoTerm.get(t).ICs.get(4)+";"+(go.allStringtoInfoTerm.get(t).ICs.get(3)/go.subontology.get(sub).maxIC(3))+
					";"+ go.allStringtoInfoTerm.get(t).ICs.get(5)+";" + go.allStringtoInfoTerm.get(t).geneSet +"\n"); 
			
		}	
		inf.deleteCharAt(inf.length()-1);
/*
 * Export in folder
 */
		String ruta = file + "/infoTerm.csv";
		PrintWriter writer = new PrintWriter( ruta, "UTF-8");
		writer.println(inf);
		writer.close();
		
		
		
		/*
		 * Init evey semantic method matrix
		 */

		double icMax = go.subontology.get(sub).maxIC(4); // IC max of IC annotation of Resnik
		double depthMax = go.subontology.get(sub).maxDepth();
		lin = new Double[terminos.size()][terminos.size()];
		ganesan = new Double[terminos.size()][terminos.size()];
		df = new Double[terminos.size()][terminos.size()];
		pekar =new Double[terminos.size()][terminos.size()];
		leacock = new Double[terminos.size()][terminos.size()];
		mazandu = new Double[terminos.size()][terminos.size()];
		zhou = new Double[terminos.size()][terminos.size()];
		resnik = new Double[terminos.size()][terminos.size()];
		aggregateIC= new Double[terminos.size()][terminos.size()];
		inf = new StringBuffer();
		for(int i = 0; i<terminos.size();i++){
			/*
			 * One because is the similarity of a term and itself
			 */
			String ti = terminos.get(i);
			InfoTerm term1 = go.allStringtoInfoTerm.get(ti) ;//Get the first term
			df[i][i]=1.;
			lin[i][i] = 1.;
			ganesan[i][i]=1.;
			leacock[i][i]=1.;
			zhou[i][i] = 1.;
			resnik[i][i] = 1.;
			mazandu[i][i] = 1.;			
			pekar[i][i] = 1.;
			aggregateIC[i][i] = 1.;
			
			for(int j = i+1; j<terminos.size();j++){
				String tj =terminos.get(j); 
				InfoTerm term2 = go.allStringtoInfoTerm.get(tj) ; //Get second term where term1 != term2
				
				
				
				String[] ancestors = interstingAncestor(ti, tj, go); // Get the best common ancestor
					InfoTerm lca = go.allStringtoInfoTerm.get((String) ancestors[0]);
					InfoTerm mica = go.allStringtoInfoTerm.get((String) ancestors[1]);
					InfoTerm micaSec = go.allStringtoInfoTerm.get((String) ancestors[2]);
					InfoTerm micaMaz = go.allStringtoInfoTerm.get((String) ancestors[3]);
					
					Double spl = go.allStringtoInfoTerm.get(ti).distancias.get(lca.toString()) + go.allStringtoInfoTerm.get(tj).distancias.get(lca.toString());
				
					if(mica.depth()>0) {
					aggregateIC[i][j] =2*SVALUEIC(ti, tj, go)/(term1.aggregateIC+term2.aggregateIC);
					aggregateIC[j][i] = aggregateIC[i][j];
					df[i][j]=SemanticSimilarity.DistanceFunction(ti, tj, go);
					df[j][i]=df[i][j];
					lin[i][j] = (2.0*((double)mica.ICs.get(4)))/((double)(term1.ICs.get(4)+term2.ICs.get(4)));
					lin[j][i] = lin[i][j];
					if(lin[i][j] >0) {
						inf.append(term1.id + ";"+term2.id +";"+lin[i][j]+"\n");
					}
					
					ganesan[i][j]=2.*(lca.depth()+1.)/(term1.depth()+1.+term2.depth()+1.);
					ganesan[j][i]=ganesan[i][j];
					pekar[i][j] =(double) lca.distancias.get(lca.top)/(double)(lca.distancias.get(lca.top)+spl);//(1 - Math.log10(((double)spl-1.) * (depthMax-lca.depth())+1))/test;
					pekar[j][i] = pekar[i][j];
					leacock[i][j]=1. - Math.log10((double) spl)/Math.log10(2.*depthMax);
					leacock[j][i]=leacock[i][j];
					zhou[i][j] = 1.-(0.5*(Math.log10(spl+1.)/Math.log10(2.*(depthMax-1.))))-(1.-0.5)*((term1.ICs.get(0)+ term2.ICs.get(0) - 2.*micaSec.ICs.get(0))/2.);
					zhou[j][i]=zhou[i][j];
					resnik[i][j] = mica.ICs.get(4)/icMax;
					resnik[j][i]=resnik[i][j];
					mazandu[i][j] = micaMaz.ICs.get(3)/Math.max(term1.ICs.get(3), term2.ICs.get(3));
					mazandu[j][i]=mazandu[i][j];
					}else    {
						aggregateIC[i][j] =0.;
						aggregateIC[j][i] = aggregateIC[i][j];
						df[i][j]=0.;
						df[j][i]=df[i][j];
						lin[i][j] =0.;
						lin[j][i] = lin[i][j];
						if(lin[i][j] >0) {
							inf.append(term1.id + ";"+term2.id +";"+lin[i][j]+"\n");
						}
						
						ganesan[i][j]=0.;
						ganesan[j][i]=ganesan[i][j];
						pekar[i][j] =0.;
						pekar[j][i] = pekar[i][j];
						leacock[i][j]=0.;
						leacock[j][i]=leacock[i][j];
						zhou[i][j] =0.;
						zhou[j][i]=zhou[i][j];
						resnik[i][j] =0.;
						resnik[j][i]=resnik[i][j];
						mazandu[i][j] =0.;
						mazandu[j][i]=mazandu[i][j];
					}
	
						
			}

		}
        ruta = file + "/infoEdge.csv";
		
		/*
		 * End Information All Term 
		 */
		writer = new PrintWriter( ruta, "UTF-8");
		writer.println(inf);
		writer.close();
		

exportcsv(terminos,file,go);


	}
	/**
	 * Method to find the best common ancestor to two terms
	 * @param t1
	 * @param t2
	 * @param go
	 * @param position
	 * @return
	 */
	public static String[] interstingAncestor(String t1, String t2,GlobalOntology go){

		InfoTerm term1 = go.allStringtoInfoTerm.get(t1);
		InfoTerm term2 = go.allStringtoInfoTerm.get(t2);
		Set<String> anc1 = new HashSet<String>(term1.is_a.ancestors);
//		anc1.addAll(term1.part_of.ancestors);
		anc1.add(term1.toString());
		Set<String> anc2 =  new HashSet<String>(term2.is_a.ancestors);
//		anc2.addAll(term2.part_of.ancestors);
		anc2.add(term2.toString());
		String[] ancestor = new String[4]; // 0 String LCA, 1 String MICA, 2 Double SPL

		
		if(!t1.equals(t2)){
				anc1.retainAll(anc2);
				List<String> ancs = new ArrayList<String>();
				List<Double> ds = new ArrayList<Double>();
				List<Double> icRes = new ArrayList<Double>();
				List<Double> icSec = new ArrayList<Double>();
				List<Double> icMaz = new ArrayList<Double>();
			
				for(String a1 : anc1){ds.add(term1.distancias.get(a1) + term2.distancias.get(a1));
					icRes.add(go.allStringtoInfoTerm.get(a1).ICs.get(4));
					icSec.add(go.allStringtoInfoTerm.get(a1).ICs.get(0));
					icMaz.add(go.allStringtoInfoTerm.get(a1).ICs.get(3));
					ancs.add(a1);
				}
			if(!ancs.isEmpty()) {
				ancestor[0] = ancs.get(ds.indexOf((Collections.min(ds))));
			    ancestor[1] = ancs.get(icRes.indexOf((Collections.max(icRes))));
			    ancestor[2] = ancs.get(icSec.indexOf((Collections.max(icSec))));
			    ancestor[3] = ancs.get(icMaz.indexOf((Collections.max(icMaz))));
				
			
			return ancestor;}
			else {
				ancestor[0] = null;
			    ancestor[1] = null;
				return ancestor;
			}
		}
		else{
			ancestor[0] = t2;
		    ancestor[1] = t2;
		    ancestor[2] = t2;
		    ancestor[3] = t2;

		    
			return ancestor;
		}



	}
	
	
	
	
	public static double SVALUE(String t1, String t2,GlobalOntology go){

		InfoTerm term1 = go.allStringtoInfoTerm.get(t1);
		InfoTerm term2 = go.allStringtoInfoTerm.get(t2);
		Set<String> anc1 = new HashSet<String>(term1.is_a.ancestors);
		anc1.addAll(term1.part_of.ancestors);
		anc1.add(term1.toString());
		Set<String> anc2 =  new HashSet<String>(term2.is_a.ancestors);
		anc2.addAll(term2.part_of.ancestors);
		anc2.add(term2.toString());
		double ssSvalue = 0.;
		
				anc1.retainAll(anc2);
			
				for(String a1 : anc1){
					InfoTerm ianc = go.allStringtoInfoTerm.get(a1);
					ssSvalue += (ianc.sValue.get(term1.toString()) +ianc.sValue.get(term2.toString())); 


		}
			return ssSvalue;


	}
	
	public static double SVALUEIC(String t1, String t2,GlobalOntology go){

		InfoTerm term1 = go.allStringtoInfoTerm.get(t1);
		InfoTerm term2 = go.allStringtoInfoTerm.get(t2);
		Set<String> anc1 = new HashSet<String>(term1.is_a.ancestors);
		anc1.add(term1.toString());
		Set<String> anc2 =  new HashSet<String>(term2.is_a.ancestors);
		anc2.add(term2.toString());
		double ssSvalue = 0.;
		
				anc1.retainAll(anc2);
			
				for(String a1 : anc1){
					InfoTerm ianc = go.allStringtoInfoTerm.get(a1);
					ssSvalue += ianc.sValueIC;


		}
			return ssSvalue;


	}
	
	/**
	 * Export in csv format the matrices
	 * @param terms
	 * @param file
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static void exportcsv(List<String> terms, String file,GlobalOntology go) throws FileNotFoundException, UnsupportedEncodingException{
		
		StringBuffer lin_sb = new StringBuffer();
		StringBuffer ganesan_sb = new StringBuffer();
		StringBuffer df_sb = new StringBuffer();
		StringBuffer mazandu_sb = new StringBuffer();
		StringBuffer leacock_sb = new StringBuffer();
		StringBuffer zhou_sb = new StringBuffer();
		StringBuffer resnik_sb= new StringBuffer();
		StringBuffer pekar_sb = new StringBuffer();
		StringBuffer agregateIC_sb = new StringBuffer();
		
		mazandu_sb.append(";");
		lin_sb.append(";");
		ganesan_sb.append(";");
		df_sb.append(";");
		pekar_sb.append(";");
		leacock_sb.append(";");
		zhou_sb.append(";");
		resnik_sb.append(";");
		agregateIC_sb.append(";");
		
		

		
		
		
		for(String t : terms){
			mazandu_sb.append(t+";");
			lin_sb.append(t+";");
			ganesan_sb.append(t+";");
			df_sb.append(t+";");
			pekar_sb.append(t+";");
			leacock_sb.append(t+";");
			zhou_sb.append(t+";");
			resnik_sb.append(t+";");
			agregateIC_sb.append(t + ";");
		}
	mazandu_sb.deleteCharAt(lin_sb.length()-1);
	lin_sb.deleteCharAt(lin_sb.length()-1);
	ganesan_sb.deleteCharAt(ganesan_sb.length()-1);
	df_sb.deleteCharAt(df_sb.length()-1);
	pekar_sb.deleteCharAt(pekar_sb.length()-1);
	leacock_sb.deleteCharAt(leacock_sb.length()-1);
	zhou_sb.deleteCharAt(zhou_sb.length()-1);
	resnik_sb.deleteCharAt(resnik_sb.length()-1);
	agregateIC_sb.deleteCharAt(agregateIC_sb.length()-1);
	
	mazandu_sb.append("\n");
	lin_sb.append("\n");
	ganesan_sb.append("\n");
	df_sb.append("\n");
	pekar_sb.append("\n");
	leacock_sb.append("\n");
	zhou_sb.append("\n");
	resnik_sb.append("\n");
	agregateIC_sb.append("\n");
	
	for(int i=0; i<terms.size();i++){
		mazandu_sb.append(terms.get(i)+";");
		lin_sb.append(terms.get(i)+";");
		ganesan_sb.append(terms.get(i)+";");
		df_sb.append(terms.get(i)+";");
		pekar_sb.append(terms.get(i)+";");
		leacock_sb.append(terms.get(i)+";");
		zhou_sb.append(terms.get(i)+";");
		resnik_sb.append(terms.get(i)+";");
		agregateIC_sb.append(terms.get(i)+";");
		
		for(int j=0; j<terms.size();j++){
			mazandu_sb.append(mazandu[i][j]+";");
			lin_sb.append(lin[i][j]+";");
			ganesan_sb.append(ganesan[i][j]+";");
			df_sb.append(df[i][j]+";");
			pekar_sb.append(pekar[i][j]+";");
			leacock_sb.append(leacock[i][j]+";");
			zhou_sb.append(zhou[i][j]+";");
			resnik_sb.append(resnik[i][j]+";");
			agregateIC_sb.append(aggregateIC[i][j] + ";");
		
		}
		mazandu_sb.deleteCharAt(mazandu_sb.length()-1);
		lin_sb.deleteCharAt(lin_sb.length()-1);
		ganesan_sb.deleteCharAt(ganesan_sb.length()-1);
		df_sb.deleteCharAt(df_sb.length()-1);
		pekar_sb.deleteCharAt(pekar_sb.length()-1);
		leacock_sb.deleteCharAt(leacock_sb.length()-1);
		zhou_sb.deleteCharAt(zhou_sb.length()-1);
		resnik_sb.deleteCharAt(resnik_sb.length()-1);
		agregateIC_sb.deleteCharAt(agregateIC_sb.length()-1);
		mazandu_sb.append("\n");
		lin_sb.append("\n");
		ganesan_sb.append("\n");
		df_sb.append("\n");
		pekar_sb.append("\n");
		leacock_sb.append("\n");
		zhou_sb.append("\n");
		resnik_sb.append("\n");
		agregateIC_sb.append("\n");
	}
	    mazandu_sb.deleteCharAt(mazandu_sb.length()-1);
		lin_sb.deleteCharAt(lin_sb.length()-1);
		ganesan_sb.deleteCharAt(ganesan_sb.length()-1);
		df_sb.deleteCharAt(df_sb.length()-1);
		pekar_sb.deleteCharAt(pekar_sb.length()-1);
		leacock_sb.deleteCharAt(leacock_sb.length()-1);
		zhou_sb.deleteCharAt(zhou_sb.length()-1);
		resnik_sb.deleteCharAt(resnik_sb.length()-1);
		agregateIC_sb.deleteCharAt(agregateIC_sb.length()-1);
		
		PrintWriter writer = new PrintWriter( file+"/Lin.csv", "UTF-8");
		writer.println(lin_sb);
		writer.close();
		writer = new PrintWriter( file+"/NUnivers.csv", "UTF-8");
		writer.println(mazandu_sb);
		writer.close();
		writer = new PrintWriter( file+"/Ganesan.csv", "UTF-8");
		writer.println(ganesan_sb);
		writer.close();
		

		writer = new PrintWriter( file+"/DF.csv", "UTF-8");
		writer.println(df_sb);
		writer.close();

		writer = new PrintWriter( file+"/LC.csv", "UTF-8");
		writer.println(leacock_sb);
		writer.close();
		writer = new PrintWriter( file+"/Zhou.csv", "UTF-8");
		writer.println(zhou_sb);
		writer.close();

		writer = new PrintWriter( file+"/Resnik.csv", "UTF-8");
		writer.println(resnik_sb);
		writer.close();
		
		
		writer = new PrintWriter( file+"/AIC.csv", "UTF-8");
		writer.println(agregateIC_sb);
		writer.close();
		writer = new PrintWriter( file+"/PS.csv", "UTF-8");
		writer.println(pekar_sb);
		writer.close();


		
		
	}
public static void exportcsvONE(String[] terms, Double[][] table, String file) throws FileNotFoundException, UnsupportedEncodingException{
		
		StringBuffer table_sb = new StringBuffer();
		
		
		table_sb.append(";");
		
		
		

		
		
		
		for(String t : terms){
			table_sb.append(t+";");
			
		}
	table_sb.deleteCharAt(table_sb.length()-1);
	
	
	table_sb.append("\n");
	

	
	for(int i=0; i<terms.length;i++){
		table_sb.append(terms[i]+";");
		
		
		for(int j=0; j<terms.length;j++){
			table_sb.append(table[i][j]+";");
			
		
		}
		table_sb.deleteCharAt(table_sb.length()-1);
		
		table_sb.append("\n");
		
	}
	    table_sb.deleteCharAt(table_sb.length()-1);
		
		PrintWriter writer = new PrintWriter( file, "UTF-8");
		writer.print(table_sb);
		writer.close();


		
		
	}
	/**
	 * Export in csv format the matrices
	 * @param terms
	 * @param genes
	 * @param lin
	 * @param file
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static void exportcsv(List<String> terms,List<String> genes, Hashtable<String,Double[]>  lin, String file) throws FileNotFoundException, UnsupportedEncodingException{
		
		StringBuffer csv = new StringBuffer();
		csv.append(";");
		for(String t : genes){
			csv.append(t+";");
		}
	csv.deleteCharAt(csv.length()-1);
	csv.append("\n");

	for(int i=0; i<terms.size();i++){
		csv.append(terms.get(i)+";");
		for(int j=0; j<genes.size();j++){
			csv.append(lin.get(terms.get(i))[j]+";");
		}
		csv.deleteCharAt(csv.length()-1);
		csv.append("\n");
	}
		csv.deleteCharAt(csv.length()-1);
		PrintWriter writer = new PrintWriter( file, "UTF-8");
		writer.println(csv);
		writer.close();
		
	}
	/**
	 * Export in csv format the matrices
	 * @param terms
	 * @param genes
	 * @param lin
	 * @param file
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
public static void exportcsv(List<String> terms,Set<String> genes, Hashtable<String,Double[]>  lin, String file) throws FileNotFoundException, UnsupportedEncodingException{
		
		StringBuffer csv = new StringBuffer();
		csv.append(";");
		for(String t : genes){
			csv.append(t+";");
		}
	csv.deleteCharAt(csv.length()-1);
	csv.append("\n");

	for(int i=0; i<terms.size();i++){
		csv.append(terms.get(i)+";");
		for(int j=0; j<genes.size();j++){
			csv.append(lin.get(terms.get(i))[j]+";");
		}
		csv.deleteCharAt(csv.length()-1);
		csv.append("\n");
	}
		csv.deleteCharAt(csv.length()-1);
		PrintWriter writer = new PrintWriter( file, "UTF-8");
		writer.println(csv);
		writer.close();
		
	}

/**
 * Crutial semantic similarity using disjoint ancestor (similar to IC)
 * @param DCA
 * @param go
 * @param ic
 * @return
 */
	
	public static double share(Set<String> DCA, GlobalOntology go, int ic) {
		double sh = 0.;
		for(String d : DCA) {
			sh = sh + go.allStringtoInfoTerm.get(d).ICs.get(ic);
		}
		
		return sh/(double)DCA.size();
		
	}
	
	/**
	 * Recover the best disjoint common ancestor
	 * @param ti
	 * @param tj
	 * @param go
	 * @param ic
	 * @return
	 */
	public static Set<String> GetDisjointCommonAncestor(InfoTerm ti, InfoTerm tj,GlobalOntology go, int ic){
		
		List<String> commonAncestor = new ArrayList<String>(ti.is_a.ancestors);
		commonAncestor.retainAll(tj.is_a.ancestors);
		
		Collections.sort(commonAncestor, new INFOComparator(ic,go));
		
		Set<String> disjointCommonAncestor = new HashSet<String>();
		
		for(String a : commonAncestor) {
			boolean isDisj = true;
			for( String cda:disjointCommonAncestor) {
				
				isDisj = isDisj & (DisjAnc(ti,a,cda,go,ic) | DisjAnc(tj,a,cda,go,ic)  );
			}
			if(isDisj) {
				disjointCommonAncestor.add(a);
			}	
		}
		
		
		
		
		return disjointCommonAncestor;
	}
	
	
	
	public static boolean DisjAnc(InfoTerm c, String a, String cda, GlobalOntology go, int ic) {
		// Require IC(a) <= IC(cda)
		double npath2 = c.distancias.get(cda);
		Object[] ancestors = interstingAncestor(cda,a, go);
		String lca = (String) ancestors[0];
		
		double npath =  go.allStringtoInfoTerm.get(a).distancias.get(lca.toString()) + go.allStringtoInfoTerm.get(cda).distancias.get(lca.toString());
		
		return npath >= (npath*npath2);
	}
	
	
	
}
