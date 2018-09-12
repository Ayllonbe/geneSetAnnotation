#library(rstudioapi) 
#setwd(dirname(getSourceEditorContext()$path))
setwd("../../results/")

Z_index <- function(hclust.obj1, hclust.obj2,N,K){
  n1 = as.character(N[,1])
  n2 = as.character(N[,2])
  AB <- c()
  A <- c()
  B <- c()
  for(y in 1:length(K)){
    ct.obj1 = cutree( hclust.obj1 ,K[y]) 
    ct.obj1 = as.data.frame(ct.obj1)
    pf1 <- ct.obj1[n1,]
    pf2 <- ct.obj1[n2,]
    ct.obj2 = cutree( hclust.obj2 ,K[y]) 
    ct.obj2 = as.data.frame(ct.obj2)
    pf3 <- ct.obj2[n1,]
    pf4 <- ct.obj2[n2,]

    AB <- c(AB,sum(abs( (pf1 == pf2) - (pf3==pf4) )))
    A <- c(A,sum(abs( (pf1 == pf2) )))
    B <- c(B,sum(abs((pf3==pf4) )))
    
    
   # l[[y]] <- pf1 == pf2 
  }
 
  
  return(sum(AB)/(sum(A)+sum(B)))
}



ZindexComAvg <- function(modules, ssVector,file){
  zindexRes <- c()
  for(x in 1:length(modules)){
    print(paste("zindex ",x,"/",length(modules),": ",modules[x],sep = ""))
    zindexVector <- c()
    for(y in 1:length(ssVector)){
      finf <- file.info(paste(file,modules[x],"/is_a/biological_process/SemanticMatrix/",ssVector[y],".csv",sep=""), extra_cols = FALSE)
      if(finf$size>1){
      semsimMatrix <- read.csv(paste(file,modules[x],"/is_a/biological_process/SemanticMatrix/",ssVector[y],".csv",sep=""), row.names = 1, sep= ";", head=T)
      if(length(semsimMatrix[,1])<3){
        
       
        zindexVector <- c(zindexVector,NA)
      }else{
      n <- length(names(semsimMatrix))
      N = as.data.frame(t(combn(row.names(semsimMatrix),2))) # Function making the pairs n(n-1)/2
      K = seq(2,(n-1)) # All K-values from 2 to n-1
      
      dist.obj <- as.dist(1-semsimMatrix) # change SS to dist
      hclust.comp.obj <- hclust(dist.obj,"complete")
      hclust.avg.obj <- hclust(dist.obj,"average")
      
      zindexVector <- c(zindexVector,round(Z_index(hclust.avg.obj,hclust.comp.obj,N,K),3))
      }
      } else{
      zindexVector <- c(zindexVector,NA)
    
    }
    }
   
    print(length(zindexVector))
    zindexRes <- rbind(zindexRes,zindexVector)
  }
  row.names(zindexRes) = modules
  colnames(zindexRes) = ssVector
  return(zindexRes)
}




#Analyses 
dataset= "Chaussabel"
ss <- c("DF","Ganesan","LC","PS",
        "Zhou","Resnik","Lin","NUnivers","AIC")
ssname <- c("DF","Ganesan","LC","PS",
            "Zhou","Resnik","Lin","Nunivers","AIC")
file <- paste("newBriefings_Incomplete/",dataset,"_3/",sep="")
mod <-list.files(path = file )[-1]

resFile = "rdata/"
if(!dir.exists(resFile)){
  dir.create(resFile)
}
zindex = ZindexComAvg(mod,ss,file)
save(zindex,file = paste(resFile,"Zindex_",dataset,"_3_incomplete.RData",sep=""))


dataset= "BTM"
ssname <- c("DF","Ganesan","LC","PS",
            "Zhou","Resnik","Lin","Nunivers","AIC")
file <- paste("newBriefings_Incomplete/",dataset,"_3/",sep="")
mod <-list.files(path = file )[-1]


zindex = ZindexComAvg(mod,ss,file)
save(zindex,file = paste(resFile,"Zindex_",dataset,"_3_incomplete.RData",sep=""))




