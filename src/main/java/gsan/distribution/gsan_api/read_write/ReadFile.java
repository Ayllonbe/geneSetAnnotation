package gsan.distribution.gsan_api.read_write;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


public class ReadFile {
	
	

	public static List<String> readTextFileByLines(String fileName) throws IOException {
		Charset charset = Charset.forName("UTF-8");
	    List<String> lines = Files.readAllLines(Paths.get(fileName),charset);
	    return lines;
	  }
	
	
	
	@SuppressWarnings({ "resource", "rawtypes", "deprecation" })
	public static List<List<String>> readXLS(String fileName) throws IOException {
		InputStream ExcelFileToRead = new FileInputStream(fileName);

		HSSFWorkbook wb = new HSSFWorkbook(ExcelFileToRead);

		HSSFSheet sheet=wb.getSheetAt(0);
		HSSFRow row; 
		HSSFCell cell;

		Iterator rows = sheet.rowIterator();
		List<List<String>> importxml = new ArrayList<List<String>>();
		while (rows.hasNext())
		{
			row=(HSSFRow) rows.next();
			Iterator cells = row.cellIterator();
			List<String> list = new ArrayList<String>();
			while (cells.hasNext())
			{
				cell=(HSSFCell) cells.next();
		
				if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
				{
//					System.out.print(cell.getStringCellValue()+" ");
					list.add(cell.getStringCellValue());
				}
				else if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC)
				{
					list.add("" + cell.getNumericCellValue());
//					System.out.print(cell.getNumericCellValue()+" ");
				}
				else
				{
					list.add("");//U Can Handel Boolean, Formula, Errors
				}
			}
			importxml.add(list);
//			System.out.println();
		}
	return importxml;
	}
	
	

	
	@SuppressWarnings("resource")
	public static List<List<String>> ReadAnnotation(String goa) throws IOException{
		
		Charset charset = Charset.forName("UTF-8");	
	  //  List<String> lines = Files.readAllLines(Paths.get(goa),charset);
	    InputStream fileStream = new FileInputStream(goa);
	    InputStream gzipStream = new GZIPInputStream(fileStream);
	    Reader decoder = new InputStreamReader(gzipStream, charset);
	    BufferedReader buffered = new BufferedReader(decoder);
	    StringBuilder textBuilder = new StringBuilder();
	    int value = 0;
	    while((value = buffered.read()) != -1) {
	         
            // converts int to character
            char c = (char)value;
            
            // prints character
           textBuilder.append(c);
         }
	    List<List<String>> list = new ArrayList<List<String>>();
	    String[] lines = textBuilder.toString().split("\n"); 
	    for(String l : lines){
	    	if(!l.contains("!")) {
	    		list.add(Arrays.asList(l.split("\t"))); // !! 3.6 sec. le procesus pour human	
	    		
	    		
	    	}else {
	    		System.out.println(l);
	    	}
	    	
	    }
	 
		return list;
		}
	
	
public static List<List<String>> Readcsv(String goa,String sep) throws IOException{
		
		Charset charset = Charset.forName("UTF-8");
		
		
	    List<String> lines = Files.readAllLines(Paths.get(goa),charset);
	    
	    
	    List<List<String>> list = new ArrayList<List<String>>();
	    
	    for(String l : lines){
	    	list.add(Arrays.asList(l.replace("\"", "").split(sep))); // !! 3.6 sec. le procesus pour human
	    }
	 
	   
		
		
		return list;
		}
	
	
	
	
	
	
}
