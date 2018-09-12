#!/usr/bin/env Rscript

# test if there is at least one argument: if not, return an error
#args = commandArgs(trailingOnly=TRUE)

print(getwd())
library("optparse")
library(clValid)
library("fastcluster")

silClus <- function(hc.obj,dist.obj,nc){
  require(cluster)
  
  asw <-c()
  for( k in 2 : nc){
    sil <- silhouette(cutree(hc.obj,k = k), dist.obj)
    asw <- c(asw,summary(sil)$avg.width)
  }
  
  
  #  print(which.max(unlist(l))+1)
  #print(which.max(asw)+1)
  # return(which.max(unlist(l))+1)
  return(which.max(asw)+1)
}
 
option_list = list(
    
  make_option(c("-f", "--file"), type="character", default=NULL, 
              help="dataset file name", metavar="character"),  
  make_option("--outFolder", type="character", default="/tmp",
              help="output folder name [default= %default]", metavar="character"),
  make_option(c("-o", "--outFile"), type="character", default="out.txt", 
              help="output file name [default= %default]", metavar="character"),
  make_option(c("-mh", "--methodHclust"), type="character", default="average",
              help="method hclust name [default= %default]", metavar="character")
 #make_option(c("-nc", "--numberclust"), type="numeric", default=20,
  #            help="method hclust name [default= %default]", metavar="character")
 # make_option(c("-c", "--cutOff"), type="character", default="",
 #             help="Cut Off method name [default= %default]", metavar="character")


);
 
opt_parser = OptionParser(option_list=option_list);
opt = parse_args(opt_parser);


if (is.null(opt$file)){
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
require(vegan,warn.conflicts =FALSE,quietly=TRUE)


mydata <- read.csv(opt$file,header=T,sep=";",row.names=1)
#mydata[mydata<0.3] = 0
print(length(mydata))


K <- as.matrix(mydata)
kernelPCA <- function(K,r=3){
  us <- matrix(NA,ncol(K),r)
  row.names(us) <- row.names(K)
  X <- K
  for(comp in 1:r){
    us[,comp] <- svd(X,nu = 0,nv = 1)$v
    Xdef <- tcrossprod((X%*%us[,comp]),us[,comp])
    X <- X-Xdef
  }
  us
}

t <- kernelPCA(K = K,r=2)

t <- K%*%kernelPCA(K,r=2)
#dist.obj <- dist(cbind(t[,1],t[,2]))
#hclust.obj <- hclust(dist.obj, method = opt$methodHclust)


dist.obj <- as.dist(1-mydata)


hclust.obj <- hclust(dist.obj,method = opt$methodHclust)




cl = silClus(hclust.obj,dist.obj, nrow(mydata) -1)


cutree.obj <- cutree(hclust.obj,k=cl)

sil.obj <- summary(silhouette(cutree.obj, dist.obj))$clus.avg.widths

clusters <- c()

termCl <- c()

for(x in 1:cl){
    namesCluster <- names(cutree.obj[cutree.obj==x])
    termCl<-c(termCl,length(namesCluster))
    print(paste(x,paste(namesCluster,collapse=";"),sep="\t"))
    clusters <- c(clusters,paste(x,sil.obj[x],paste(namesCluster,collapse=";"),sep="\t"))
}
Tcl <- paste("#Cluster Number:",length(termCl))
Tmax <- paste("#Terms Number max in a cluster:",max(termCl))
Tmin <- paste("#Terms Number min in a cluster:",min(termCl))
Tmean <- paste("#Terms Number mean in a cluster:",mean(termCl))


res.clu <- paste(Tcl,Tmax,Tmin,Tmean,sep="\n")
res.clu <- c(res.clu,clusters)

write.table(res.clu,opt$outFile,row.names = FALSE, col.names=FALSE, quote=FALSE)
