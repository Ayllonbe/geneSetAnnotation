#setwd(dirname(rstudioapi::getSourceEditorContext()$path))
setwd("../../results/")


  
### Creation of cophenetic and Silhouette matrix for a given hierarchical clustering methods
  silAvgMax <- function(hc.obj,dist.obj,nc){
    require(cluster)
    asw <-c()
    for( k in 2 : (nc-1)){
      sil <- silhouette(cutree(hc.obj,k = k), dist.obj)
      asw <- c(asw,summary(sil)$avg.width)
    }
    return(max(asw))
  }
  
  ResMatrix <- function(modules, ssVector,file,methods){
    res <- list()
    
    for(m in 1:length(methods)){
    method = methods[m]
    cophresult <- c()
    silresult <- c()
    for(x in 1:length(modules)){
      print(paste(method," ",x,"/",length(modules),": ",modules[x],sep = ""))
      copheneticVector <- c()
      silhouetteVector <- c()
      for(y in 1:length(ssVector)){
    print(paste("      ",ssVector[y]))
        finf <- file.info(paste(file,modules[x],"/is_a/biological_process/SemanticMatrix/",ssVector[y],".csv",sep=""), extra_cols = FALSE)
        if(finf$size>1){
        semsimMatrix <- read.csv(paste(file,modules[x],"/is_a/biological_process/SemanticMatrix/",ssVector[y],".csv",sep=""), row.names = 1, sep= ";", head=T)
        if(length(semsimMatrix[,1])<3){
        
          copheneticVector <- c(copheneticVector,NA)
          silhouetteVector <- c(silhouetteVector,NA)
        }else{
          dist.obj <- as.dist(1-semsimMatrix) # change SS to dist
          hclust.obj <- hclust(dist.obj,method)
          cph <- round(cor(dist.obj,cophenetic(hclust.obj)),3) # compute cophenetic value
          copheneticVector <- c(copheneticVector,cph)
        sil <-  round(silAvgMax(hclust.obj,dist.obj,length(semsimMatrix[,1])-1),3) 
        silhouetteVector <- c(silhouetteVector,sil)
        }
        } 
        else{
          copheneticVector <- c(copheneticVector,NA)
          silhouetteVector <- c(silhouetteVector,NA)
        }
      }
      
      cophresult <- rbind(cophresult,copheneticVector)
      silresult <- rbind(silresult,silhouetteVector)

          }
    row.names(cophresult) = modules
    colnames(cophresult) = ssVector
    row.names(silresult) = modules
    colnames(silresult) = ssVector
    
    result <- list()
    result[["cophenetic"]] = cophresult
    result[["silhouette"]] = silresult
    res[[method]] = result
    
    
    }
    return(res)
  }
  
  
#Analyses 
  ss <- c("DF","Ganesan","LC","PS",
          "Zhou","Resnik","Lin","NUnivers","AIC")
  dataset = "Chaussabel"
  file <- paste("newBriefings_Incomplete/",dataset,"_3/",sep="")

  mod <-list.files(path = file )[-1]
  methods <- c("single","complete","average")
  
  res = ResMatrix(mod,ss,file,methods )
resFile = "rdata/"
if(!file.exists(resFile)){
  file.create(resFile)
}

 save(res,file = paste(resFile,"Sil_",dataset,"_3_SS_Incomplete.RData",sep=""))
 
 dataset = "BTM"
 file <- paste("newBriefings_Incomplete/",dataset,"_3/",sep="")
 
 mod <-list.files(path = file )[-1]
 methods <- c("single","complete","average")
 res = ResMatrix(mod,ss,file,methods )
 
 save(res,file = paste(resFile,"Sil_",dataset,"_3_SS_Incomplete.RData",sep=""))

  
 
  