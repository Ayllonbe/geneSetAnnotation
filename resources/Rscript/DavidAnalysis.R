setwd(dirname(rstudioapi::getSourceEditorContext()$path))
setwd("../../results/")

yourmail <- "aaron.ayllon-benitez@u-bordeaux.fr" # Add please your mail, you need subscribe in DAVID website first.

# Convert the row names to entrez ids
library(RDAVIDWebService)
library("AnnotationDbi")
library("org.Hs.eg.db")
library(annotate)
columns(org.Hs.eg.db)

library("illuminaHumanv2.db") 

load("../resources/SYSTEMSDATA/ModulesChaussabelV2_FormatGMT.Rdata")

for(x in  1 : length(gmt_modulesV2$genesets)){
  print(paste(x,"/", length(gmt_modulesV2$genesets)))
module = gmt_modulesV2$genesets[[x]]
GEmapping =as.character(unlist(mget(x = module,envir =  illuminaHumanv2ENTREZID)))
GEmapping = GEmapping[!is.na(GEmapping)]
#David annotation chart

david<-DAVIDWebService(email=yourmail, url="https://david.ncifcrf.gov/webservice/services/DAVIDWebService.DAVIDWebServiceHttpSoap12Endpoint/")
listGene <- addList(david,GEmapping,idType = "ENTREZ_GENE_ID",listName="test",listType = "Gene")

setAnnotationCategories(david,"GOTERM_BP_DIRECT")
res = getFunctionalAnnotationChart(david)
if(length(res)>1){
for(y in  1:length(res$Term)){
  line <- res[y,]
 
  genes <- strsplit(as.character(line$Genes),", ")[[1]]
  genes
  
 
  genes <-  getSYMBOL(genes, data='org.Hs.eg')
  genes <- paste(genes,collapse = ", ")
  res$Genes[y] =  genes
  
}
}
write.table(res,paste("DAVID/Chaussabel/",gmt_modulesV2$geneset.names[x],".csv",sep=""),sep="\t",row.names = FALSE)
}



##################################################################################################

# For the reverse map:
x <- org.Hs.egSYMBOL2EG
# Get the entrez gene identifiers that are mapped to a gene symbol
mapped_genes <- mappedkeys(x)
# Convert to a list
mapping <- as.data.frame(x[mapped_genes])

table <- read.csv("../resources/SYSTEMSDATA/btm_annotation_table.csv",sep="\t")

for(x in  1 : length(table$ID)){
  print(paste(x,"/", length(table$ID)))
  module = table$ID[[x]]
  genes = strsplit(as.character(table$Module.member.genes[x]),",")[[1]]
  GEmapping <- mapping[mapping[,2]%in%genes,1]
  GEmapping = GEmapping[!is.na(GEmapping)]
  
  #David annotation chart
  david<-DAVIDWebService(email=yourmail, url="https://david.ncifcrf.gov/webservice/services/DAVIDWebService.DAVIDWebServiceHttpSoap12Endpoint/")
  listGene <- addList(david,GEmapping,idType = "ENTREZ_GENE_ID",listName="test",listType = "Gene")
  
  setAnnotationCategories(david,"GOTERM_BP_DIRECT")
  res = getFunctionalAnnotationChart(david)
  if(length(res)>1){
    for(y in  1:length(res$Term)){
      line <- res[y,]
      
      genes <- strsplit(as.character(line$Genes),", ")[[1]]
      genes
      
      
      genes <-  getSYMBOL(genes, data='org.Hs.eg')
      genes <- paste(genes,collapse = ", ")
      res$Genes[y] =  genes
      
    }
  }
  write.table(res,paste("DAVID/BTM/",module,".csv",sep=""),sep="\t",row.names = FALSE)
  
  
  
}
