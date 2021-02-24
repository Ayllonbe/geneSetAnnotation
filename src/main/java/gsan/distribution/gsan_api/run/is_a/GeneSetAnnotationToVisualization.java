package gsan.distribution.gsan_api.run.is_a;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import gsan.distribution.gsan_api.annotation.Annotation;
import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.read_write.ReadFile;
import jxl.read.biff.BiffException;

public class GeneSetAnnotationToVisualization {


	public static void main(String[] args) throws IOException, BiffException {
	
		
        List<String> file = ReadFile.readTextFileByLines(args[0]);
		
		String[] arg = file.get(1).split("=")[1].split(";");
		List<String> symb = Arrays.asList(arg);
		String simsem = file.get(2).split("=")[1];
		int ic = Integer.parseInt(file.get(4).split("=")[1]);
		double length_min = Double.parseDouble(file.get(8).split("=")[1]);
		double ncombinationSim = Double.parseDouble(file.get(9).split("=")[1]);
		double precision = Double.parseDouble(file.get(7).split("=")[1]);
		int geneSupport =  Integer.parseInt(file.get(6).split("=")[1]);
		String ontology = file.get(5).split("=")[1];
		String gaf = file.get(11).split("=")[1];
		String HCL = file.get(3).split("=")[1];
		String ontologyfile = file.get(10).split("=")[1];
		
		String exportfolder =  file.get(13).split("=")[1];
		String Rfile =  file.get(12).split("=")[1];	
		
		GlobalOntology globalontology = GlobalOntology.informationOnt(ontologyfile); // Create GlobalOntology class

		String GOAFile=     gaf;
		List<List<String>> goafile = ReadFile.ReadAnnotation(GOAFile); // Read goa file
		Annotation GOA = new Annotation(goafile,globalontology, ic,true); // To recovery Genome annotation and create Annotation class
		globalontology.AddTermToGenome(GOA); // Add information about the gene in Terms
		globalontology.addAnnotationICs();// Add information about extrinsic IC and Hybrid IC (Resnik and Zhou)
		globalontology.AggregateIC();

		for(String sub : globalontology.subontology.keySet()) // For each subOntology
			globalontology.hybridIC(sub,GOA); // Add information about new hybrid IC
		Annotation GOAred = Annotation.redondancyReduction(GOA,globalontology); // Remove redondancy
		Annotation GOAincom = Annotation.icIncompleteReduction(GOAred,globalontology,3);// remove incomplete annotation


		GSAN_is_a gsan = new GSAN_is_a(globalontology, GOAincom,ncombinationSim,precision,length_min,geneSupport,ic);
		
		
		
		
		
		try {
			gsan.GenSetAnnotationONE(symb,simsem,ontology,HCL,exportfolder,Rfile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
