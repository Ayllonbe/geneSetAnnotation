package plos.one;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import gsan.distribution.gsan_api.ontology.GlobalOntology;
import gsan.distribution.gsan_api.ontology.InfoTerm;
import gsan.distribution.gsan_api.read_write.ReadFile;

public class Dist_GT {

	
	public static void main(String[] args) throws IOException {
		/*
		 * Files to use
		 */
		String ontologyfile = "resources/GODATA/go-07-05-2018.owl.gz";
		String GOAFile=       "resources/GODATA/goa_human_21_05_18.gaf.gz";


		List<List<String>> goafile = ReadFile.ReadAnnotation(GOAFile);
		System.out.println(goafile.get(0));
		Map<String,Set<String>> ont2TGpair = new HashMap<>();
		ont2TGpair.put("P", new HashSet<String>());
		ont2TGpair.put("F", new HashSet<String>());
		ont2TGpair.put("C", new HashSet<String>());
		
		GlobalOntology go = GlobalOntology.informationOnt(ontologyfile);
		
		Map<String,DescriptiveStatistics> ont2ds = new HashMap<>();
		ont2ds.put("P",new DescriptiveStatistics());
		ont2ds.put("F", new DescriptiveStatistics());
		ont2ds.put("C", new DescriptiveStatistics());
		
		
		for(List<String> list : goafile) {
			if(!(list.get(3).equals("NOT")||list.get(6).equals("ND"))) {
				if(go.allStringtoInfoTerm.containsKey(list.get(4))) {
					String asso = list.get(2) +"__"+list.get(4);
					ont2TGpair.get(list.get(8)).add(asso);
				}
			}
		}
		
		
		StringBuffer sb = new StringBuffer();
		sb.append("onto\tdepth\tquartile\tvalue\n");
		
		for(String o : ont2TGpair.keySet()) {
			String p= "";
			switch (o) {
			case "P":
				p = "Biological Process";
				break;
			case "F":
				p="Molecular Function";
				break;
			case "C":
				p="Cellular Component";
				break;
			}
			
			
			Map<Integer,Set<String>> mp = new HashMap<>();
			for(int i=0; i<17;i++) {
				mp.put(i, new HashSet<String>());
			}
			for(String asso:ont2TGpair.get(o)) {
				String[] array = asso.split("__");
				
					mp.get(go.allStringtoInfoTerm.get(array[1]).depth().intValue()).add(asso);
					ont2ds.get(o).
					addValue(go.allStringtoInfoTerm.get(array[1]).ICs.get(3));
				
			}
			for(int i=1; i<17;i++) {
				sb.append(p +"\t" + i +"\tQ0\t" + mp.get(i).size()+"\n");
			}
			
			
			
		}
//
//		System.out.println("BP "+ont2ds.get("P").getPercentile(25));
//		System.out.println("BP "+ont2ds.get("P").getPercentile(50));
//		System.out.println("BP "+ont2ds.get("P").getPercentile(75));
//		System.out.println("MF "+ont2ds.get("F").getPercentile(25));
//		System.out.println("MF "+ont2ds.get("F").getPercentile(50));
//		System.out.println("MF "+ont2ds.get("F").getPercentile(75));
//		System.out.println("CC "+ont2ds.get("C").getPercentile(25));
//		System.out.println("CC "+ont2ds.get("C").getPercentile(50));
//		System.out.println("CC "+ont2ds.get("C").getPercentile(75));
//	
//		
		for(String o : ont2TGpair.keySet()) {
			String p= "";
			switch (o) {
			case "P":
				p = "Biological Process";
				break;
			case "F":
				p="Molecular Function";
				break;
			case "C":
				p="Cellular Component";
				break;
			}
			
			double q1 = ont2ds.get(o).getPercentile(25);
			double q2 = ont2ds.get(o).getPercentile(50);
			double q3 = ont2ds.get(o).getPercentile(75);
			Map<Integer,Set<String>> mp25 = new HashMap<>();
			Map<Integer,Set<String>> mp50 = new HashMap<>();
			Map<Integer,Set<String>> mp75 = new HashMap<>();
			for(int i=0; i<17;i++) {
				mp25.put(i, new HashSet<String>());
				mp50.put(i, new HashSet<String>());
				mp75.put(i, new HashSet<String>());
				
			}
			for(String asso:ont2TGpair.get(o)) {
				String[] array = asso.split("__");
				InfoTerm it = go.allStringtoInfoTerm.get(array[1]);
				if(it.ICs.get(3)>q1)
					mp25.get(it.depth().intValue()).add(asso);
				if(it.ICs.get(3)>q2)
					mp50.get(it.depth().intValue()).add(asso);
				if(it.ICs.get(3)>q3)
					mp75.get(it.depth().intValue()).add(asso);
				
			}
			for(int i=1; i<17;i++) {
				sb.append(p +"\t" + i +"\tQ1"+"\t" + mp25.get(i).size()+"\n");
				sb.append(p +"\t" + i +"\tQ2"+"\t" + mp50.get(i).size()+"\n");
				sb.append(p +"\t" + i +"\tQ3"+"\t" + mp75.get(i).size()+"\n");
			}
			
			
			
		}
		
		PrintWriter pw = new PrintWriter("results/DistributionAssociation_GT_Depth_IC_Q.csv");
		pw.print(sb);
		pw.close();
	}
	
}
