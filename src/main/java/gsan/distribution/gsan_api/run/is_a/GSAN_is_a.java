
package gsan.distribution.gsan_api.run.is_a;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import gsan.distribution.gsan_api.annotation.Annotation;
import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.read_write.ReadOrganism;
import gsan.distribution.gsan_api.read_write.Write;
import gsan.distribution.gsan_api.run.representative.Representative;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class GSAN_is_a {

	private double RepCombinedSimilarity ;
	private double precision;
	private double tailmin;
	private int nbGeneMin;
	private int ic;
	private GlobalOntology go;
	private Annotation goa;
	static DecimalFormat df = new DecimalFormat("0.00");

	
	public GSAN_is_a(GlobalOntology go, Annotation goa) throws BiffException, IOException {
		this.RepCombinedSimilarity = 0.3; // term similarity filter between combined GO terms (Useful in the representative step)
		this.precision = 1; // Percentage get a representative that cover this probability in a cluster (if = 1, Representative cover 100% of cluster terms)
		this.tailmin = 0.0; // Filter to despreciate terms.
		this.nbGeneMin = 3; // Cluster filter. Number of genes covered by the cluster
		this.ic = 3;
		this.go = new GlobalOntology(go);
		this.goa = new Annotation(goa);
	}
	
	public GlobalOntology getGO() {
		return this.go;
	}
	
	public Annotation getGOA() {
		return this.goa;
	}

	public void GenSetAnnotation(ReadOrganism infoOrganism, String author) throws Exception {
		/*
		 * File to export
		 */		

		String workspace = "results/";

		File dossier = new File(workspace);
		if(!dossier.exists()){
			dossier.mkdir();
		}

		
		StringBuffer plossb = new StringBuffer();

		plossb.append("SS\tModule\tCoverGenes\tNumTerms\tGNTotal\n");
		int countMod = 1;
		for(String module :infoOrganism.module2symbols.keySet()) {
		
			System.out.println("### Module : "+module+ " - " + countMod +"/"+infoOrganism.module2symbols.keySet().size());
			countMod++;
			List<String> symb = new ArrayList<String>( infoOrganism.module2symbols.get(module)); // Get the gene to a precise module

			String sub = "GO:0008150";
//			for( String sub: this.go.subontology.keySet()) {
				System.out.println("####### SubOntology : " +sub  );
				String statfolder = workspace+"newBriefings_Incomplete/"+author+"_"+ic+"/"+module+"/is_a/";

				dossier = new File(statfolder);
				if(!dossier.exists()){
					dossier.mkdir();
				}

				Set<String> terminosinc = new HashSet<String>(this.goa.getTerms(symb, sub,go)); // Get terms to GOA with removed incomplete terms 

				String export = statfolder+ this.go.allStringtoInfoTerm.get(sub).toName();  // url folder to save the information

				ArrayList<String> listTerm = new ArrayList<String>(terminosinc); // change to list the terms set

				Write.exportSSM(go, sub, this.goa,listTerm, symb,export+"/SemanticMatrix"); //  computed and export the semantic similarity

				String[] methods = {"DF","Ganesan","LC","PS","Zhou","Resnik","Lin","NUnivers","AIC"};
				
				Representative.onemetric(module, ic,sub,methods, "average", new HashSet<String>(symb), export+"/SemanticMatrix",  export,  go, listTerm,this.goa,
						tailmin,RepCombinedSimilarity,precision,nbGeneMin);

			}	
			for(String t : this.go.allStringtoInfoTerm.keySet()) {
				this.go.allStringtoInfoTerm.get(t).geneSet.clear();
			}
//		}
		
		
	}	
}

