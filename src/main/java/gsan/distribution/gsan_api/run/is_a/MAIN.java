package gsan.distribution.gsan_api.run.is_a;

import java.io.File;
import java.io.IOException;
import java.util.List;

import gsan.distribution.gsan_api.annotation.Annotation;
import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.read_write.ReadFile;
import gsan.distribution.gsan_api.read_write.ReadOrganism;
import jxl.read.biff.BiffException;
import plos.one.Analysis_PlosOne;

public class MAIN {

	public static void main(String[] args) throws IOException, BiffException {

		File file = new File(".");
		
		System.out.println(file.getAbsolutePath());
		
		
		String ontologyfile = "resources/GODATA/go-07-05-2018.owl.gz";	
		GlobalOntology globalontology = GlobalOntology.informationOnt(ontologyfile); // Create GlobalOntology class

		String GOAFile=       "resources/GODATA/goa_human_21_05_18.gaf.gz";
		List<List<String>> goafile = ReadFile.ReadAnnotation(GOAFile); // Read goa file
		Annotation GOA = new Annotation(goafile,globalontology, 3,true); // To recovery Genome annotation and create Annotation class
		globalontology.AddTermToGenome(GOA); // Add information about the gene in Terms
		globalontology.addAnnotationICs();// Add information about extrinsic IC and Hybrid IC (Resnik and Zhou)
		globalontology.AggregateIC();

		for(String sub : globalontology.subontology.keySet()) // For each subOntology
			globalontology.hybridIC(sub,GOA); // Add information about new hybrid IC
		Annotation GOAred = Annotation.redondancyReduction(GOA,globalontology); // Remove redondancy
		Annotation GOAincom = Annotation.icIncompleteReduction(GOAred,globalontology,3);// remove incomplete annotation


		GSAN_is_a gsan = new GSAN_is_a(globalontology, GOAincom);

		Chaussabel(gsan);
		BTM(gsan);
		
		
	}
	
	public static void Chaussabel(GSAN_is_a gsan) {
		String protfile =     "resources/SYSTEMSDATA/V2_Trial_8_Modules.csv";
		try {
			ReadOrganism infoOrganism = ReadOrganism.chargeFile(protfile, "\t");
//			gsan.GenSetAnnotation(infoOrganism, "Chaussabel");
			Analysis_PlosOne.computeCovertureQuartiles(gsan.getGO(), gsan.getGOA(), infoOrganism, "Chaussabel");
//			Analysis_PlosOne.getTermSizeByModuleAndSS(gsan.getGO(), gsan.getGOA(),  infoOrganism, "Chaussabel");
			Analysis_PlosOne.getGenCoverture(gsan.getGO(), gsan.getGOA(), infoOrganism, "Chaussabel");
		} catch ( Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Read Chaussable Annotation
		
	}
	
	public static void BTM(GSAN_is_a gsan) {
		String protfile =     "resources/SYSTEMSDATA/btm_annotation_table.xls";
		try {
			ReadOrganism infoOrganism = ReadOrganism.chargeFileXLS(protfile);
//			gsan.GenSetAnnotation(infoOrganism, "BTM");
//			Analysis_PlosOne.computeCovertureQuartiles(gsan.getGO(), gsan.getGOA(), infoOrganism, "BTM");
//			Analysis_PlosOne.getTermSizeByModuleAndSS(gsan.getGO(), gsan.getGOA(),  infoOrganism, "BTM");
			Analysis_PlosOne.getGenCoverture(gsan.getGO(), gsan.getGOA(), infoOrganism, "BTM");	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Read Chaussable Annotation
		
	}

}
