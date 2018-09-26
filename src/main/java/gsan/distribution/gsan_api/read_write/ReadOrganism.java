package gsan.distribution.gsan_api.read_write;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


public class ReadOrganism {


	public List<String> symbolTotal;
	public Hashtable<String,Set<String>> module2symbols;

	public ReadOrganism(List<String> symbolTotal,Hashtable<String,Set<String>> module2symbols) {
		this.symbolTotal = new ArrayList<String>(symbolTotal);
		this.module2symbols = new Hashtable<String,Set<String>>(module2symbols);
	}
	public ReadOrganism(List<String> symbolTotal) {
		this.symbolTotal = new ArrayList<String>(symbolTotal);
	}

	public static ReadOrganism chargeFile(String fileInput, String split) throws IOException {

		List<String> file = ReadFile.readTextFileByLines(fileInput);
		List<String> head = Arrays.asList(file.get(0).split(split));
		file.remove(0);

		Set<String> sym = new HashSet<String>();
		Hashtable<String,Set<String>> mod2sym  = new Hashtable<String,Set<String>>();

		for(String line :file) {
			String[] l = line.split(split);
			if(l.length>2) {
				if(!mod2sym.containsKey(l[head.indexOf("Module")])) {
					Set<String> set = new HashSet<String>();
					set.add(l[head.indexOf("Symbol")]);
					sym.add(l[head.indexOf("Symbol")]);
					mod2sym.put(l[head.indexOf("Module")], set);
				}
				else {
					mod2sym.get(l[head.indexOf("Module")]).add(l[head.indexOf("Symbol")]);
				}
			}

		}

		ReadOrganism ro = new ReadOrganism(new ArrayList<String>(sym),mod2sym);
		return ro;


	}
	
	
	
	public static ReadOrganism chargeFileXLS(String fileInput) throws IOException {

		List<List<String>> file = ReadFile.readXLS(fileInput);

		List<String> head = file.get(0);

		file.remove(0);

		Set<String> sym = new HashSet<String>();
		Hashtable<String,Set<String>> mod2sym  = new Hashtable<String,Set<String>>();

		for(List<String> line :file) {
			List<String> genes = Arrays.asList(line.get(head.indexOf("Module member genes")).split(","));
			mod2sym.put(line.get(head.indexOf("ID")), new HashSet<String>(genes));
			sym.addAll(genes);


		}

		ReadOrganism ro = new ReadOrganism(new ArrayList<String>(sym),mod2sym);
		return ro;


	}
	public static ReadOrganism Ecoli(String fileInput, String split) throws IOException {

		List<String> file = ReadFile.readTextFileByLines(fileInput);
		List<String> head = Arrays.asList(file.get(0).split(split));
		file.remove(0);

		Set<String> sym = new HashSet<String>();

		for(String line :file) {

			String[] l = line.split(split);
			if(!l[head.indexOf("Gene")].equals("")) {
				sym.add(l[head.indexOf("Gene")]);
			}


		}

		ReadOrganism ro = new ReadOrganism(new ArrayList<String>(sym));
		return ro;


	}


}
