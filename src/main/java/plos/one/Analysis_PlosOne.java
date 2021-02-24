package plos.one;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


import gsan.distribution.gsan_api.annotation.Annotation;
import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;
import gsan.distribution.gsan_api.read_write.ReadFile;
import gsan.distribution.gsan_api.read_write.ReadOrganism;
import jxl.read.biff.BiffException;

public class Analysis_PlosOne {

	public static void computeCovertureQuartiles(GlobalOntology go, Annotation GOA, ReadOrganism infoOrganism, String author)  throws IOException, BiffException {




		String biologicalprocess = "GO:0008150";


		int ic_inc = 3;
		String workspace = "results/newBriefings_Incomplete/"+author+"_" +ic_inc ;
		String res = workspace +"/Analysis_Rep/";

		File dossier = new File(res);
		if(!dossier.exists()){
			dossier.mkdir();
		}

		StringBuffer covertureQuart = new StringBuffer();
		covertureQuart.append("Module\tSS\tQuartile\ttype\tvariable\tvalue\n");
		System.out.println("Percentile :" + GOA.percentileOnt.get(biologicalprocess).get(25));

		for(String module :infoOrganism.module2symbols.keySet()) {

			List<String> symb = new ArrayList<String>( infoOrganism.module2symbols.get(module)); // Get the gene to a precise module
			Set<String> terminosinc = new HashSet<String>(GOA.getTerms(symb, biologicalprocess,go)); // Get terms to GOA with removed incomplete terms 
			Set<String> geneSet = new HashSet<>();

			for(String t : terminosinc) {
				geneSet.addAll(go.allStringtoInfoTerm.get(t).geneSet);
			}


			String[] methods = {"DF","Ganesan","LC","PS","Zhou","Resnik","Lin","NUnivers","AIC"};


			String statfolder = workspace+"/"+module+"/is_a/biological_process/"; 

			for(String m : methods){

				String n;

				if(m.equals("NUnivers")){
					n = "Nunivers";
				}else {
					n=m;
				}

				String filename = statfolder+"representatives_"+m+".csv";
				List<String> lines = ReadFile.readTextFileByLines(filename);	
				lines.remove(0);
				List<String> termRep = new ArrayList<String>();
				for(String l : lines) {
					termRep.add(l.split("\t")[0]);
				}
				DescriptiveStatistics ds = new DescriptiveStatistics();
				Set<String> genesRep = new HashSet<String>();
				Set<String> remove = new HashSet<>();
				int sizeTerm = termRep.size();
				System.out.println(module +" sizeTerm " +sizeTerm);
				for(String t:termRep) {
					InfoTerm it = go.allStringtoInfoTerm.get(t);
					ds.addValue(it.ICs.get(ic_inc));
					if(it.geneSet.size()>=3)
						genesRep.addAll(it.geneSet);
					else {
						remove.add(it.id);
					}
				}

				termRep.removeAll(remove);

				covertureQuart.append(module+"\t"+n+"\tQ0"+"\t"+"terms"+"\t"+"rep"+"\t"+(100.*(double) termRep.size()/(double)sizeTerm)+"\n");
				covertureQuart.append(module+"\t"+n+"\tQ0"+"\t"+"genes"+"\t"+"rep"+"\t"+(100.*(double) genesRep.size()/(double)geneSet.size())+"\n");
				for(int q=1; q<4;q++) {

					Set<String> genesRepInc = new HashSet<String>();
					Set<String> termRepInc = new HashSet<String>();
					for(String t:termRep) {
						InfoTerm it = go.allStringtoInfoTerm.get(t);
						if(it.ICs.get(ic_inc)>GOA.percentileOnt.get(biologicalprocess).get(25*q)&&it.geneSet.size()>=3) {
							genesRepInc.addAll(it.geneSet);
							termRepInc.add(it.id);
						}
					}

					covertureQuart.append(module+"\t"+n+"\tQ"+q+"\t"+"terms"+"\t"+"rep_inc"+"\t"+(100.*(double) termRepInc.size()/(double)sizeTerm)+"\n");
					covertureQuart.append(module+"\t"+n+"\tQ"+q+"\t"+"genes"+"\t"+"rep_inc"+"\t"+(100.*(double) genesRepInc.size()/(double)geneSet.size())+"\n");	
				}

			}

			List<String> list = ReadFile.readTextFileByLines("results/DAVID/"+author+"/"+module+".csv");
			list.remove(0);
			List<String> termRep = new ArrayList<String>();
			for(String l : list) {
				termRep.add(l.split("\t")[1].split("~")[0].replaceAll("\"", ""));
			}
			DescriptiveStatistics ds = new DescriptiveStatistics();
			Set<String> genesRep = new HashSet<String>();
			Set<String> remove = new HashSet<>();
			int sizeTerm = termRep.size();
			for(String t:termRep) {

				InfoTerm it = go.allStringtoInfoTerm.get(t);
				if(it!=null) {
					if(it.geneSet.size()>=3) {
						ds.addValue(it.ICs.get(ic_inc));
						genesRep.addAll(it.geneSet);
					}else {
						remove.add(t);
					}

				}else {
					remove.add(t);
				}
			}
			termRep.removeAll(remove);
			String m ="DAVID";
			covertureQuart.append(module+"\t"+m+"\tQ0"+"\t"+"terms"+"\t"+"rep"+"\t"+(100.*(double) termRep.size()/(double)sizeTerm)+"\n");
			covertureQuart.append(module+"\t"+m+"\tQ0"+"\t"+"genes"+"\t"+"rep"+"\t"+(100.*(double) genesRep.size()/(double)geneSet.size())+"\n");

			for(int i =1;i<4;i++) {
				Set<String> genesRepInc = new HashSet<String>();
				List<String> termRepInc = new ArrayList<String>();

				for(String t:termRep) {

					InfoTerm it = go.allStringtoInfoTerm.get(t);

					if(it.ICs.get(ic_inc)>GOA.percentileOnt.get(biologicalprocess).get(25*i)&&it.geneSet.size()>=3) {
						genesRepInc.addAll(it.geneSet);
						termRepInc.add(it.id);
					}
				}
				genesRepInc.retainAll(geneSet);
				covertureQuart.append(module+"\t"+m+"\tQ"+i+"\t"+"terms"+"\t"+"rep_inc"+"\t"+(100.*(double) termRepInc.size()/(double)sizeTerm)+"\n");
				covertureQuart.append(module+"\t"+m+"\tQ"+i+"\t"+"genes"+"\t"+"rep_inc"+"\t"+(100.*(double) genesRepInc.size()/(double)geneSet.size())+"\n");

			}

			for(String t : go.allStringtoInfoTerm.keySet()) {
				go.allStringtoInfoTerm.get(t).geneSet.clear();
			}	
		}
		PrintWriter pw= new PrintWriter(res+"CovertureQuartiles.csv");
		pw.print(covertureQuart);
		pw.close();


	}



	public static void  getTermSizeByModuleAndSS(GlobalOntology go, Annotation GOA, ReadOrganism infoOrganism, String author)  throws IOException, BiffException {

		String biologicalprocess = "GO:0008150";


		int ic_inc = 3;
		String workspace = "results/newBriefings_Incomplete/"+author+"_" +ic_inc;
		String res = workspace +"/Analysis_Rep/";

		StringBuffer termSizeByModuleAndSS = new StringBuffer();
		termSizeByModuleAndSS.append("Module\tSS\tvalue\n");
		System.out.println("Percentile :" + GOA.percentileOnt.get(biologicalprocess).get(25));

		for(String module :infoOrganism.module2symbols.keySet()) {

			List<String> symb = new ArrayList<String>( infoOrganism.module2symbols.get(module)); // Get the gene to a precise module
			Set<String> terminosinc = new HashSet<String>(GOA.getTerms(symb, biologicalprocess,go)); // Get terms to GOA with removed incomplete terms 
			Set<String> geneSet = new HashSet<>();

			for(String t : terminosinc) {
				geneSet.addAll(go.allStringtoInfoTerm.get(t).geneSet);
			}


			String[] methods = {"DF","Ganesan","LC","PS","Zhou","Resnik","Lin","NUnivers","AIC"};


			String statfolder = workspace+"/"+module+"/is_a/biological_process/"; 

			termSizeByModuleAndSS.append(module+"\tAnnotation"+"\t"+terminosinc.size()+"\n");
			for(String n : methods){
				String filename = statfolder+"representatives_"+n+".csv";
				String m;

				if(n.equals("NUnivers")){
					m = "Nunivers";
				}else {
					m=n;
				}


				List<String> lines = ReadFile.readTextFileByLines(filename);	
				lines.remove(0);
				List<String> termRep = new ArrayList<String>();
				for(String l : lines) {
					termRep.add(l.split("\t")[0]);
				}
				int sizeTerm = termRep.size();
				termSizeByModuleAndSS.append(module+"\t"+m+"\t"+sizeTerm+"\n");


			}

			for(String t : go.allStringtoInfoTerm.keySet()) {
				go.allStringtoInfoTerm.get(t).geneSet.clear();
			}	
		}
		PrintWriter pw= new PrintWriter(res+"TermsVSRep.csv");
		pw.print(termSizeByModuleAndSS);
		pw.close();


	}

	public static void  getGenCoverture(GlobalOntology go, Annotation GOA, ReadOrganism infoOrganism, String author)  throws IOException, BiffException {

		DecimalFormat df = new DecimalFormat("0.00");
		String biologicalprocess = "GO:0008150";


		int ic_inc = 3;
		String workspace = "results/newBriefings_Incomplete/"+author+"_" +ic_inc;
		String res = workspace +"/Analysis_Rep/";

		StringBuffer genCoverture = new StringBuffer();
		genCoverture.append("Module\tSS\tvalue\n");
		System.out.println("Percentile :" + GOA.percentileOnt.get(biologicalprocess).get(25));

		for(String module :infoOrganism.module2symbols.keySet()) {

			List<String> symb = new ArrayList<String>( infoOrganism.module2symbols.get(module)); // Get the gene to a precise module
			Set<String> terminosinc = new HashSet<String>(GOA.getTerms(symb, biologicalprocess,go)); // Get terms to GOA with removed incomplete terms 
			Set<String> geneSet = new HashSet<>();

			for(String t : terminosinc) {
				geneSet.addAll(go.allStringtoInfoTerm.get(t).geneSet);
			}


			String[] methods = {"DF","Ganesan","LC","PS","Zhou","Resnik","Lin","NUnivers","AIC"};


			String statfolder = workspace+"/"+module+"/is_a/biological_process/"; 

			//genCoverture.append(module+"\tAnnotation"+"\t"+terminosinc.size()+"\n");
			for(String n : methods){
				String filename = statfolder+"representatives_"+n+".csv";
				String m;

				if(n.equals("NUnivers")){
					m = "Nunivers";
				}else {
					m=n;
				}


				List<String> lines = ReadFile.readTextFileByLines(filename);	
				lines.remove(0);

				Set<String> geneModules = new HashSet<String>();
				for(String l : lines) {
					String term  = l.split("\t")[0];
					
					if(go.allStringtoInfoTerm.get(term).geneSet.size()>3&&
							go.allStringtoInfoTerm.get(term).ICs.get(3)>GOA.percentileOnt.get(biologicalprocess).get(25))
						geneModules.addAll(go.allStringtoInfoTerm.get(term).geneSet);
				}
				double coverture = (double)geneModules.size()/(double)symb.size() *100.;
				genCoverture.append(module+"\t"+m+"\t"+df.format(coverture)+"\n");


			}
			double coverture = 0;
			List<String> lines = ReadFile.readTextFileByLines("results/DAVID/"+author+"/"+module+".csv");
			lines.remove(0);
			String m ="DAVID";
			if(lines.size()>0) {
				Set<String> geneModules = new HashSet<String>();
				for(String l : lines) {
					String term = l.split("\t")[1].split("~")[0].replaceAll("\"", "");
					if(go.allStringtoInfoTerm.containsKey(term)) {
						
						geneModules.addAll(go.allStringtoInfoTerm.get(term).geneSet);
					}
				}
				coverture = (double)geneModules.size()/(double)symb.size() *100.;
			}
			genCoverture.append(module+"\t"+m+"\t"+coverture+"\n");


			for(String t : go.allStringtoInfoTerm.keySet()) {
				go.allStringtoInfoTerm.get(t).geneSet.clear();
			}	
		}
		PrintWriter pw= new PrintWriter(res+"genCoverture.csv");
		pw.print(genCoverture);
		pw.close();


	}


}
