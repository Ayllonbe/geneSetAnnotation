#!/usr/bin/env Rscript

# test if there is at least one argument: if not, return an error
#args = commandArgs(trailingOnly=TRUE)


library("optparse")


option_list = list(
  
  make_option(c("-s", "--semantic"), type="character", default=NULL, 
              help="Semantic Similarity method", metavar="character"),  
  make_option("--outFolder", type="character", default="/tmp",
              help="output folder name [default= %default]", metavar="character"),
  make_option(c("-o", "--outFile"), type="character", default="out.txt", 
              help="output file name [default= %default]", metavar="character"),
  make_option(c("-mh", "--methodHclust"), type="character", default="complete",
              help="method hclust name [default= %default]", metavar="character"),
  make_option(c("-c", "--cutOff"), type="character", default="",
             help="Cut Off method number [default= %default]", metavar="character")
  
  
);

opt_parser = OptionParser(option_list=option_list);
opt = parse_args(opt_parser);


if (is.null(opt$semantic)){
  print_help(opt_parser)
  stop("At least one argument must be supplied (input file).n", call.=FALSE)
}

# Create the working path
time.obj =paste(gsub(" ","_", as.character(Sys.time())))
main = opt$outFolder 
path = main #file.path(main,time.obj)
#dir.create(path,showWarnings=FALSE)
setwd(path)
require(GMD,warn.conflicts =FALSE,quietly=TRUE)
require(Hmisc,warn.conflicts =FALSE,quietly=TRUE)
require(cluster,warn.conflicts =FALSE,quietly=TRUE)
require(NbClust,warn.conflicts =FALSE,quietly=TRUE)
require(vegan,warn.conflicts =FALSE,quietly=TRUE)

# File <- "/home/aaron/Desktop/Thesis_Project/M1.1_Platelet/DistanceMatrix/WuPalmer.csv"
# mydata <- read.csv(File,header=T,sep=";",row.names=1)

mydata <- read.csv(paste('/home/aaron/Documents/Thesis_Project/Interferon/biological_process/SemanticMatrix/',
                         opt$semantic,".csv",sep=""),header=T,sep=";",row.names=1)


dist.obj <- as.dist(1-mydata)

if(opt$methodHclust=="ward.D"){
  hclust.obj <- hclust(dist.obj^2,method = opt$methodHclust)
}else{
hclust.obj <- hclust(dist.obj,method = opt$methodHclust)}

cl <- as.numeric(opt$cutOff)
cutree.obj <- cutree(hclust.obj,k=cl)

clusters <- c()

termCl <- c()

for(x in 1:cl){
  namesCluster <- names(cutree.obj[cutree.obj==x])
  termCl<-c(termCl,length(namesCluster))
  print(paste(x,paste(namesCluster,collapse=";"),sep="\t"))
  clusters <- c(clusters,paste(x,paste(namesCluster,collapse=";"),sep="\t"))
}


write.table(clusters,opt$outFile,row.names = FALSE, col.names=FALSE, quote=FALSE)

