package gsan.distribution.gsan_api.run.representative;

import java.io.*;
import java.util.*;

import gsan.distribution.gsan_api.read_write.Format;



public class Communication {
	public Hashtable<Integer,List<String>> clusters = new Hashtable<Integer,List<String>>();
	Hashtable<String,Double> statTable = new Hashtable<String,Double>();
	public Hashtable<Integer,Double> qualityTable = new Hashtable<Integer,Double>();
	static Format format = new Format(3);
	
	
	public Communication(Hashtable<Integer,List<String>> cl, Hashtable<String,Double> stat ,Hashtable<Integer,Double> quality){
		clusters.putAll(cl);
		statTable.putAll(stat);
		qualityTable.putAll(quality);
	}
	

	
	public static  Communication  run(String file, String method) throws IOException, InterruptedException
	{  
		
		File Rfile = new File("resources/Rscript/clusteranalisis.R");
		File SSfile = new File(file);
		
		Process p = Runtime.getRuntime().exec("Rscript "+Rfile.getAbsolutePath()+" --file " + SSfile.getAbsolutePath() + " -m "+method);
		
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		p.waitFor();
		
		File path = new File("/tmp/out.txt");
		Hashtable<Integer,List<String>> clusters = new Hashtable<Integer,List<String>>();
		Hashtable<Integer,Double> quality = new Hashtable<Integer,Double>();
		Hashtable<String,Double> statTable = new Hashtable<String,Double>();
		if(!path.exists()){
			
		System.err.println("Error");	
		}else{
		
		FileReader f = new FileReader(path);
	
		stdInput = new BufferedReader(f);
		String s = null;
		String info = new String();
		while ((s = stdInput.readLine()) != null) {

			if(!s.contains("#")){
				String[] A = s.split("\t");
				int n = Integer.parseInt(A[0]);
				List<String> B = Arrays.asList(A[2].split(";"));
				clusters.put(n, B);
				quality.put(n, Double.parseDouble(A[1]));
			}
			else{
				info = info + s + "\n";
				
			}

		}
		info = info.replace("#" ,"");
		path.delete();
		for(String i : info.split("\n")){
			String[] sp = i.split(":");
			Double v = format.round(Double.parseDouble(sp[1]));
			statTable.put(sp[0],v);
		}
		}
		
		Communication com = new Communication(clusters,statTable,quality);
		return com;

	}
	
	
	public static  Communication  runONE(String file, String method, String rf) throws IOException, InterruptedException
	{  
		
		File Rfile = new File(rf);
		File SSfile = new File(file);
		
		Process p = Runtime.getRuntime().exec("Rscript "+Rfile.getAbsolutePath()+" --file " + SSfile.getAbsolutePath() + " -m "+method);
		
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		p.waitFor();
		
		File path = new File("/tmp/out.txt");
		Hashtable<Integer,List<String>> clusters = new Hashtable<Integer,List<String>>();
		Hashtable<Integer,Double> quality = new Hashtable<Integer,Double>();
		Hashtable<String,Double> statTable = new Hashtable<String,Double>();
		if(!path.exists()){
			
		System.err.println("Error");	
		}else{
		
		FileReader f = new FileReader(path);
	
		stdInput = new BufferedReader(f);
		String s = null;
		String info = new String();
		while ((s = stdInput.readLine()) != null) {

			if(!s.contains("#")){
				String[] A = s.split("\t");
				int n = Integer.parseInt(A[0]);
				List<String> B = Arrays.asList(A[2].split(";"));
				clusters.put(n, B);
				quality.put(n, Double.parseDouble(A[1]));
			}
			else{
				info = info + s + "\n";
				
			}

		}
		info = info.replace("#" ,"");
		path.delete();
		for(String i : info.split("\n")){
			String[] sp = i.split(":");
			Double v = format.round(Double.parseDouble(sp[1]));
			statTable.put(sp[0],v);
		}
		}
		
		Communication com = new Communication(clusters,statTable,quality);
		return com;

	}
	
	}
    