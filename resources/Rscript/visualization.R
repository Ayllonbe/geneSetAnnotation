setwd(dirname(rstudioapi::getSourceEditorContext()$path))
setwd("../../results/")

resFile = "visualization/"
if(!dir.exists(resFile)){
  dir.create(resFile)
}


library(ggplot2)
library(grid)
##################################################
# First image paper

table <- read.csv("DistributionAssociation_GT_Depth_IC_Q.csv",sep="\t",header= T)
k =1
t0 <- table[table$quartile%in%"Q0",c(1,2,4)]

tiff(filename = paste(resFile,"Fig1",".tiff",sep=""),pointsize = 8,
     width = 2250,height=1125,res=400,compression = "lzw")
ggplot(t0, aes(x=depth,y=value,fill=onto))+
  geom_bar(stat="identity")+
  xlab("Depth")+ylim(0,60000)+ylab("Number of distinct (gene, GO term)\n pairs in GOA human")+
  scale_x_continuous(limits=c(0,16),breaks =seq(1,16))+
  # scale_fill_grey( start = 0.2, end = 0.8, na.value = "red",name="GO\ncategories")+
  scale_fill_brewer(palette="Paired",name="GO\ncategories")+
  theme( text = element_text(family = "DejaVu Serif"),
         axis.line.x=element_line(size=0.5*k, colour="black"),
         axis.line.y = element_line(size=0.5*k,colour="black"),
         axis.line = element_line(size=1,colour="black"),
         axis.title = element_text(size=10),
         axis.title.y = element_text(margin = margin(0,20,0,0)),
         axis.title.x = element_text(margin = margin(20,0,0,0)),
         axis.text = element_text(size=8),
         strip.text.x = element_text(face="bold", size=8,lineheight=5.0),
         strip.text.y = element_text(face="bold", size=10,lineheight=5.0),
         strip.background = element_rect(fill="lightgray", colour="black",size=1),
         panel.border = element_rect(colour = "black", fill=NA, size=1),
         legend.title = element_text(face="bold",size=8),
         legend.text = element_text(size=8),
         legend.title.align = 0.5,
         panel.background = element_blank(), 
         plot.title = element_text(size=8,hjust = 0.5,face="bold",margin = margin(5,0,40,0)))

dev.off()
png(filename = paste(resFile,"Fig1",".png",sep=""),pointsize = 12,
    width = 2250,height=1125,res=400)
ggplot(t0, aes(x=depth,y=value,fill=onto))+
  geom_bar(stat="identity")+
  xlab("Depth")+ylim(0,60000)+ylab("Number of distinct (gene, GO term)\n pairs in GOA human")+
  scale_x_continuous(limits=c(0,16),breaks =seq(1,16))+
  # scale_fill_grey( start = 0.2, end = 0.8, na.value = "red",name="GO\ncategories")+
  scale_fill_brewer(palette="Paired",name="GO\ncategories")+
  theme( axis.line.x=element_line(size=0.5*k, colour="black"),
         axis.line.y = element_line(size=0.5*k,colour="black"),
         axis.line = element_line(size=1,colour="black"),
         axis.title = element_text(size=10),
         axis.title.y = element_text(margin = margin(0,20,0,0)),
         axis.title.x = element_text(margin = margin(20,0,0,0)),
         axis.text = element_text(size=8),
         strip.text.x = element_text(face="bold", size=8,lineheight=5.0),
         strip.text.y = element_text(face="bold", size=10,lineheight=5.0),
         strip.background = element_rect(fill="lightgray", colour="black",size=1),
         panel.border = element_rect(colour = "black", fill=NA, size=1),
         legend.title = element_text(face="bold",size=8),
         legend.text = element_text(size=8),
         legend.title.align = 0.5,
         panel.background = element_blank(), 
         plot.title = element_text(size=8,hjust = 0.5,face="bold",margin = margin(5,0,40,0)))

dev.off()

res = ggplot(t0, aes(x=depth,y=value,fill=onto))+
  geom_bar(stat="identity")+
  xlab("Depth")+ylim(0,60000)+ylab("Number of distinct (gene, GO term)\n pairs in GOA human")+
  scale_x_continuous(limits=c(0,16),breaks =seq(1,16))+
  # scale_fill_grey( start = 0.2, end = 0.8, na.value = "red",name="GO\ncategories")+
  scale_fill_brewer(palette="Paired",name="GO\ncategories")+
  theme( text = element_text(family = "DejaVu Serif", size = 18),
         axis.line.x=element_line(size=0.5*k, colour="black"),
         axis.line.y = element_line(size=0.5*k,colour="black"),
         axis.line = element_line(size=1,colour="black"),
         axis.title = element_text(size=18),
         axis.title.y = element_text(margin = margin(0,20,0,0)),
         axis.title.x = element_text(margin = margin(20,0,0,0)),
         axis.text = element_text(size=18),
         strip.text.x = element_text(face="bold", size=18,lineheight=5.0),
         strip.text.y = element_text(face="bold", size=18,lineheight=5.0),
         strip.background = element_rect(fill="lightgray", colour="black",size=1),
         panel.border = element_rect(colour = "black", fill=NA, size=1),
         legend.title = element_text(face="bold",size=18),
         legend.text = element_text(size=18),
         legend.title.align = 0.5,
         panel.background = element_blank(), 
         plot.title = element_text(size=18,hjust = 0.5,face="bold",margin = margin(5,0,40,0)))



ggsave(filename = paste(resFile,"Fig1.svg",sep=""), width=12, 
       plot=res)



grid.arrange(arrangeGrob(chau.sil.sin.bp, chau.sil.com.bp,chau.sil.avg.bp, top=grid::textGrob("(A)",x=0.01,hjust=0,gp=gpar(fontfamily="DejaVu Serif",fontsize=18,lineheight=1)), ncol=3,nrow = 1),
             arrangeGrob(btm.sil.sin.bp,btm.sil.com.bp,btm.sil.avg.bp, top=grid::textGrob("(B)",x=0.01,hjust=0,gp=gpar(fontfamily="DejaVu Serif",fontsize=18,lineheight=1)), ncol=3,nrow = 1),
             ncol=1, nrow = 2)
####################################################

boxplot_graphics<-function(visites,title = "",xlab ="",ylab,ylim=c(0,1), k=1,cond=0){
  require(ggplot2)
  require(reshape2)
  mdf <- melt(visites)
  print(head(mdf))
  lines = "#1F3552"
  #Fill pour changer le coulours
  fill=c("#DCDCDC")
  ggplot(mdf, aes(x = Var2, y=value))+
    stat_boxplot(geom="errorbar")+
    geom_boxplot(colour = "black",fill=fill,alpha=1,outlier.color = "black", outlier.shape = 20)+
    geom_hline(aes(yintercept=0.25), linetype="dashed", color="red",size=1*k)  +
    ylab(ylab)+ylim(ylim[1],ylim[2])+
    scale_x_discrete(name=xlab) +theme(text = element_text(family = "DejaVu Serif", size = 18),
                                       axis.text.x = element_text(hjust = 1,angle = 45),
                                       axis.line.x=element_line(size=0.5*k, colour="black"),
                                       axis.line.y = element_line(size=0.5*k,colour="black"),
                                       axis.line = element_line(size=1,colour="black"),
                                       axis.title = element_text(size=18),
                                       axis.title.y = element_text(margin = margin(0,20,0,0)),
                                       axis.title.x = element_blank(),
                                       #panel.grid.major = element_blank(),
                                       # panel.grid.minor = element_blank(),
                                       # panel.border = element_blank(),
                                       panel.background = element_blank(), 
                                       plot.title = element_blank())
  
}


dataset = "Chaussabel"
load(paste("rdata/Sil_",dataset,"_3_SS_Incomplete.RData",sep=""))
load(paste("rdata/Zindex_",dataset,"_3_incomplete.RData",sep=""))
chau.res = res
chau.zindex = zindex
dataset = "BTM"
load(paste("rdata/Sil_",dataset,"_3_SS_Incomplete.RData",sep=""))
load(paste("rdata/Zindex_",dataset,"_3_incomplete.RData",sep=""))
btm.res = res
btm.zindex =zindex
rm(res,zindex)

library(reshape2)

ssname <- c("Ganesan","LC","PS",
            "Zhou","Resnik","Lin","Nunivers","DF","AIC")

chau.despreciate = names(which(is.na(chau.res$average$silhouette[,1])==T))
btm.despreciate = names(which(is.na(btm.res$average$silhouette[,1])==T))


chau.zindex= chau.zindex[!(row.names(chau.zindex) %in% chau.despreciate),]
chau.zindex = chau.zindex[,c(2,3,4,5,6,7,8,1,9)]
btm.zindex= btm.zindex[!(row.names(btm.zindex) %in% btm.despreciate),]
btm.zindex  = btm.zindex[,c(2,3,4,5,6,7,8,1,9)]

chau.bp <- boxplot_graphics(chau.zindex,ylab="Z-index",k=1,cond = 1,ylim = c(0,0.8))
chau.bp
btm.bp <-  boxplot_graphics(btm.zindex,ylab="Z-index",k=1,cond = 1,ylim = c(0,0.8))
library(gridExtra)

tiff(filename = paste(resFile,"Fig6.tiff",sep=""),pointsize = 12,
     width = 2250,height=1125,res=300,compression = "lzw")
grid.arrange(arrangeGrob(chau.bp, top=grid::textGrob("(A)",x=0.01,hjust=0)), arrangeGrob(btm.bp, top=grid::textGrob("(B)",x=0.01,hjust=0)), ncol=2, nrow = 1)
dev.off()

png(filename = paste(resFile,"Fig6.png",sep=""),pointsize = 12,
    width = 2250,height=1125,res=300)
grid.arrange(arrangeGrob(chau.bp, top=grid::textGrob("(A)",x=0.01,hjust=0)), arrangeGrob(btm.bp, top=grid::textGrob("(B)",x=0.01,hjust=0)), ncol=2, nrow = 1)
dev.off()



ggsave(filename = paste(resFile,"Fig6.svg",sep=""), 
       plot=grid.arrange(arrangeGrob(chau.bp, top=grid::textGrob("(A)",x=0.01,hjust=0,gp=gpar(fontfamily="DejaVu Serif",fontsize=18,lineheight=1))), arrangeGrob(btm.bp, top=grid::textGrob("(B)",x=0.01,hjust=0,gp=gpar(fontfamily="DejaVu Serif",fontsize=18,lineheight=1))), ncol=2, nrow = 1))
#

###################################################################################
boxplot_graphics<-function(visites,title = "",xlab ="",ylab,ylim=c(0,1), k=1,cond=10){
  require(ggplot2)
  require(reshape2)
  mdf <- melt(visites)
  print(head(mdf))
  lines = "#1F3552"
  #Fill pour changer le coulours
  fill=c("#DCDCDC")
  ggplot(mdf, aes(x = Var2, y=value))+
    stat_boxplot(geom="errorbar")+
    geom_boxplot(colour = "black",fill=fill,alpha=1,outlier.color = "black", outlier.shape = 20)+
    geom_hline(aes(yintercept=0.25), linetype="dashed", color="red",size=1*k)  +
    ylab(ylab)+ylim(ylim[1],ylim[2])+
    scale_x_discrete(name=xlab) +
    ggtitle(title)+theme(text = element_text(family = "DejaVu Serif",size=18),
                         axis.text.x = element_text(hjust = 1,angle = 45),
                         axis.line.x=element_line(size=0.5*k, colour="black"),
                         axis.line.y = element_line(size=0.5*k,colour="black"),
                         axis.line = element_line(size=1,colour="black"),
                         axis.title = element_text(size=18),
                         axis.title.y = element_text(margin = margin(0,20,0,0)),
                         axis.title.x = element_blank(),
                         axis.text = element_text(size=18),
                         #panel.grid.major = element_blank(),
                         # panel.grid.minor = element_blank(),
                         # panel.border = element_blank(),
                         panel.background = element_blank(), 
                         
                         plot.title = element_text(size=18,hjust = 0.5, face="bold",margin = margin(5,0,40,0)))
  
}

chau.SilAvg = chau.res$average$silhouette
chau.SilAvg = chau.SilAvg[!(row.names(chau.SilAvg) %in% chau.despreciate),]
chau.SilAvg = chau.SilAvg[,c(2,3,4,5,6,7,8,1,9)]

chau.sil.avg.bp <- boxplot_graphics(chau.SilAvg,ylab="ASW",title = "ALHM",k=1,cond=0) 


chau.SilCom = chau.res$complete$silhouette
chau.SilCom = chau.SilCom[!(row.names(chau.SilCom) %in% chau.despreciate),]
chau.SilCom = chau.SilCom[,c(2,3,4,5,6,7,8,1,9)]

chau.sil.com.bp <- boxplot_graphics(chau.SilCom,ylab="ASW",title = "CLHM",k=1,cond=0) 

chau.SilSin = chau.res$single$silhouette
chau.SilSin = chau.SilSin[!(row.names(chau.SilSin) %in% chau.despreciate),]
chau.SilSin = chau.SilSin[,c(2,3,4,5,6,7,8,1,9)]

chau.sil.sin.bp <- boxplot_graphics(chau.SilSin,ylab="ASW",title = "SLHM",k=1,cond=0) 



btm.SilAvg = btm.res$average$silhouette
btm.SilAvg = btm.SilAvg[!(row.names(btm.SilAvg) %in% btm.despreciate),]
btm.SilAvg  = btm.SilAvg[,c(2,3,4,5,6,7,8,1,9)]

btm.sil.avg.bp <- boxplot_graphics(btm.SilAvg,ylab="ASW",title = "ALHM",k=1,cond=0) 

btm.SilCom = btm.res$complete$silhouette
btm.SilCom = btm.SilCom[!(row.names(btm.SilCom) %in% btm.despreciate),]
btm.SilCom  = btm.SilCom[,c(2,3,4,5,6,7,8,1,9)]

btm.sil.com.bp <- boxplot_graphics(btm.SilCom,ylab="ASW",title = "CLHM",k=1,cond=0) 

btm.SilSin = btm.res$single$silhouette
btm.SilSin = btm.SilSin[!(row.names(btm.SilSin) %in% btm.despreciate),]
btm.SilSin  = btm.SilSin[,c(2,3,4,5,6,7,8,1,9)]

btm.sil.sin.bp <- boxplot_graphics(btm.SilSin,ylab="ASW",title = "SLHM",k=1,cond=0) 





tiff(filename = paste(resFile,"Fig7.tiff",sep=""),pointsize = 12,
     2250,height=2250,res=300,compression = "lzw")
#grid.arrange(chau.sil.bp,btm.sil.bp, ncol=2, nrow = 1)
grid.arrange(arrangeGrob(chau.sil.sin.bp, chau.sil.com.bp,chau.sil.avg.bp, top=grid::textGrob("(A)",x=0.01,hjust=0), ncol=3,nrow = 1),
             arrangeGrob(btm.sil.sin.bp,btm.sil.com.bp,btm.sil.avg.bp, top=grid::textGrob("(B)",x=0.01,hjust=0), ncol=3,nrow = 1),
             ncol=1, nrow = 2)
dev.off()

png(filename = paste(resFile,"Fig7.png",sep=""),pointsize = 12,
    2250,height=2250,res=300)
#grid.arrange(chau.sil.bp,btm.sil.bp, ncol=2, nrow = 1)
grid.arrange(arrangeGrob(chau.sil.sin.bp, chau.sil.com.bp,chau.sil.avg.bp, top=grid::textGrob("(A)",x=0.01,hjust=0), ncol=3,nrow = 1),
             arrangeGrob(btm.sil.sin.bp,btm.sil.com.bp,btm.sil.avg.bp, top=grid::textGrob("(B)",x=0.01,hjust=0), ncol=3,nrow = 1),
             ncol=1, nrow = 2)
dev.off()

ggsave(filename = paste(resFile,"Fig7.svg",sep=""),height = 11,width = 14, 
       plot=grid.arrange(arrangeGrob(chau.sil.sin.bp, chau.sil.com.bp,chau.sil.avg.bp, top=grid::textGrob("(A)",x=0.01,hjust=0,gp=gpar(fontfamily="DejaVu Serif",fontsize=18,lineheight=1)), ncol=3,nrow = 1),
                         arrangeGrob(btm.sil.sin.bp,btm.sil.com.bp,btm.sil.avg.bp, top=grid::textGrob("(B)",x=0.01,hjust=0,gp=gpar(fontfamily="DejaVu Serif",fontsize=18,lineheight=1)), ncol=3,nrow = 1),
                         ncol=1, nrow = 2)
)   

#########################################################################################################################

boxplot_graphics<-function(visites,title = "",xlab ="",ylab,ylim=c(0,1), k=1,cond=0){
  require(ggplot2)
  require(reshape2)
  mdf <- melt(visites)
  print(head(mdf))
  lines = "#1F3552"
  #Fill pour changer le coulours
  fill=c("#DCDCDC")
  ggplot(mdf, aes(x = Var2, y=value))+
    stat_boxplot(geom="errorbar")+
    geom_boxplot(colour = "black",fill=fill,alpha=1,outlier.color = "black", outlier.shape = 20)+
    geom_hline(aes(yintercept=0.25), linetype="dashed",size=cond*k)  +
    ylab(ylab)+ylim(ylim[1],ylim[2])+
    scale_x_discrete(name=xlab) +
    ggtitle(title)+theme(text = element_text(family = "DejaVu Serif",size=18),
                         axis.text.x = element_text(hjust = 1,angle = 45),
                         axis.line.x=element_line(size=0.5*k, colour="black"),
                         axis.line.y = element_line(size=0.5*k,colour="black"),
                         axis.line = element_line(size=1,colour="black"),
                         axis.title = element_text(size=18),
                         axis.title.y = element_text(margin = margin(0,20,0,0)),
                         axis.title.x = element_blank(),
                         axis.text = element_text(size=18),
                         #panel.grid.major = element_blank(),
                         # panel.grid.minor = element_blank(),
                         # panel.border = element_blank(),
                         panel.background = element_blank(), 
                         
                         plot.title = element_text(size=18,hjust = 0.5, face="bold",margin = margin(5,0,40,0)))
  
  
}

chau.cophCom <- chau.res$complete$cophenetic
chau.cophCom <- chau.cophCom[!(row.names(chau.cophCom) %in% chau.despreciate),]
chau.cophCom = chau.cophCom[,c(2:8,1,9)]
chau.cophSin <- chau.res$single$cophenetic
chau.cophSin <- chau.cophSin[!(row.names(chau.cophSin) %in% chau.despreciate),]
chau.cophSin = chau.cophSin[,c(2:8,1,9)]
chau.cophAvg <- chau.res$average$cophenetic
chau.cophAvg <- chau.cophAvg[!(row.names(chau.cophAvg) %in% chau.despreciate),]
chau.cophAvg = chau.cophAvg[,c(2:8,1,9)]
colnames(chau.cophCom) = colnames(chau.cophSin) = colnames(chau.cophAvg)  = ssname
chau.cophCom[is.na(chau.cophCom)==TRUE] = chau.cophSin[is.na(chau.cophSin)==TRUE] = chau.cophAvg[is.na(chau.cophAvg)==TRUE]  = 0
chau.bxCom <- boxplot_graphics(chau.cophCom,title="CLHM",ylab="CCC",k=1)  
chau.bxSin <- boxplot_graphics(chau.cophSin,title="SLHM",ylab="CCC",k=1)  
chau.bxAvg <- boxplot_graphics(chau.cophAvg,title="ALHM",ylab="CCC",k=1)  


btm.cophCom <- btm.res$complete$cophenetic
btm.cophCom <- btm.cophCom[!(row.names(btm.cophCom) %in% btm.despreciate),]
btm.cophCom = btm.cophCom[,c(2:8,1,9)]
btm.cophSin <- btm.res$single$cophenetic
btm.cophSin <- btm.cophSin[!(row.names(btm.cophSin) %in% btm.despreciate),]
btm.cophSin = btm.cophSin[,c(2:8,1,9)]
btm.cophAvg <- btm.res$average$cophenetic
btm.cophAvg <- btm.cophAvg[!(row.names(btm.cophAvg) %in% btm.despreciate),]
btm.cophAvg = btm.cophAvg[,c(2:8,1,9)]
colnames(btm.cophCom) = colnames(btm.cophSin) = colnames(btm.cophAvg)  = ssname
btm.cophCom[is.na(btm.cophCom)==TRUE] = btm.cophSin[is.na(btm.cophSin)==TRUE] = btm.cophAvg[is.na(btm.cophAvg)==TRUE]  = 0
btm.bxCom <- boxplot_graphics(btm.cophCom,title="CLHM",ylab="CCC",k=1)  
btm.bxSin <- boxplot_graphics(btm.cophSin,title="SLHM",ylab="CCC",k=1)  
btm.bxAvg <- boxplot_graphics(btm.cophAvg,title="ALHM",ylab="CCC",k=1)  

tiff(filename = paste(resFile,"Fig5.tiff",sep=""),pointsize = 12,
     width = 2250,height=2250,res=300,compression = "lzw")
grid.arrange(arrangeGrob(chau.bxSin,chau.bxCom,chau.bxAvg, top=grid::textGrob("(A)",x=0.01,hjust=0), ncol=3,nrow = 1),
             arrangeGrob(btm.bxSin, btm.bxCom, btm.bxAvg, top=grid::textGrob("(B)",x=0.01,hjust=0), ncol=3,nrow = 1),
             ncol=1, nrow = 2)
dev.off()
png(filename = paste(resFile,"Fig5.png",sep=""),pointsize = 12,
    width = 2250,height=2250,res=300)
grid.arrange(arrangeGrob(chau.bxSin,chau.bxCom,chau.bxAvg, top=grid::textGrob("(A)",x=0.01,hjust=0), ncol=3,nrow = 1),
             arrangeGrob(btm.bxSin, btm.bxCom, btm.bxAvg, top=grid::textGrob("(B)",x=0.01,hjust=0), ncol=3,nrow = 1),
             ncol=1, nrow = 2)
dev.off()



ggsave(filename = paste(resFile,"Fig5.svg",sep=""),height = 11,width = 14, 
       plot=grid.arrange(arrangeGrob(chau.bxSin,chau.bxCom,chau.bxAvg, top=grid::textGrob("(A)",x=0.01,hjust=0,gp=gpar(fontfamily="DejaVu Serif",fontsize=18,lineheight=1)), ncol=3,nrow = 1),
                         arrangeGrob(btm.bxSin, btm.bxCom, btm.bxAvg, top=grid::textGrob("(B)",x=0.01,hjust=0,gp=gpar(fontfamily="DejaVu Serif",fontsize=18,lineheight=1)), ncol=3,nrow = 1),
                         ncol=1, nrow = 2)
)   
         
         
         
#####################################################################

quartBoxPlotFunction <- function(stat){
  k=1
  fill <- c("#ebebeb","#c0c0c0","#929292","#5f605f")
  br <- c("Q0","Q1","Q2","Q3")
  ggplot(stat, aes(x =Quartile , y=value,fill=Quartile)) + 
    stat_boxplot(geom="errorbar")+
    geom_boxplot(colour = "black",alpha=1,outlier.color = "black", outlier.shape = 20)+
    scale_fill_manual(breaks =br ,values=fill)+ylab("Percentage %")+xlab("")+
    facet_grid(type ~ SS) + ylim(c(0,100))+
    theme(text = element_text(family = "DejaVu Serif"),
          axis.text.x = element_text(hjust = 1,angle = 45),
          axis.line.x=element_line(size=0.5*k, colour="black"),
          axis.line.y = element_line(size=0.5*k,colour="black"),
          strip.text.x = element_text(face="bold", size=8,lineheight=5.0),
          strip.text.y = element_text(face="bold", size=10,lineheight=5.0),
          strip.background = element_rect(fill="lightgray", colour="black",size=1),
          axis.line = element_line(size=1,colour="black"),
          axis.text = element_text(size=8.5*k),
          legend.position = "none",
          panel.border = element_rect(colour = "black", fill=NA, size=1),
          panel.background = element_blank())
} 

quartBoxPlotFunctionUni <- function(stat,ylab="Percentage %"){
  require(ggplot2)
  k=1
  fill <- c("#ebebeb","#c0c0c0","#929292","#5f605f")
  br <- c("Q0","Q1","Q2","Q3")
  ggplot(stat, aes(x =Quartile , y=value,fill=Quartile)) +
    stat_boxplot(geom="errorbar")+
    geom_boxplot(colour = "black",alpha=1,outlier.color = "black", outlier.shape = 20)+
    scale_fill_manual(breaks =br ,values=fill)+ylab(ylab)+xlab("IC distribution")+
    facet_grid(~SS) + scale_y_continuous(limits = c(0, 100), breaks = seq(0,100,5), labels = c(0,"","","",20,"","","",40,"","","",60,"","","",80,"","","",100))+
    theme(
      text = element_text(family = "DejaVu Serif",size=18),
      #axis.text.x = element_text(hjust = 1,angle = 0),
      axis.line.x=element_line(size=0.5*k, colour="black"),
      axis.line.y = element_line(size=0.5*k,colour="black"),
      strip.text.x = element_text(face="bold", size=18,lineheight=5.0),
      strip.text.y = element_text(face="bold", size=18,lineheight=5.0),
      strip.background = element_rect(fill="lightgray", colour="black",size=1),
      axis.line = element_line(size=1,colour="black"),
      axis.text = element_text(size=18),
      axis.title.y = element_text(size=18),
      axis.title.x = element_text(size=18),
      axis.ticks.length=unit(.25, "cm"),
      legend.position = "none",
      panel.border = element_rect(colour = "black", fill=NA, size=1),
      panel.background = element_blank())
}

dataset = "Chaussabel"
stat <- read.table(paste("newBriefings_Incomplete/",dataset,"_3/Analysis_Rep/CovertureQuartiles.csv",sep=""),header = T,sep="\t")

ssname <- c("Ganesan","LC","PS",
            "Zhou","Resnik","Lin","Nunivers","DF","AIC","DAVID")

stat <- stat[stat$SS%in%ssname,]
stat$SS<- factor(stat$SS, levels = unique(ssname))
stat$type<- factor(stat$type, levels = c("terms","genes"))


tiff(filename =paste(resFile,"Fig9.tiff",sep=""),pointsize = 8,
     width = 2250,height=1125,res=300,compression = "lzw")
quartBoxPlotFunction(stat)
dev.off()


png(filename =paste(resFile,"Fig9Terms.png",sep=""), pointsize = 12,
    width = 2800,height=900,res=300)

quartBoxPlotFunctionUni(stat[stat$type%in%"terms",],ylab="Percentage of synthetic terms (%)")
dev.off()

ggsave(filename = paste(resFile,"Fig9Termss.svg",sep=""),width = 19, 
       plot=quartBoxPlotFunctionUni(stat[stat$type%in%"terms",],ylab="Percentage of synthetic terms (%)"))

png(filename =paste(resFile,"Fig9Genes.png",sep=""), pointsize = 12,
    width = 2800,height=900,res=300)

quartBoxPlotFunctionUni(stat[stat$type%in%"genes",],ylab="Percentage of covered genes (%)")
dev.off()

ggsave(filename = paste(resFile,"Fig9Genes.svg",sep=""),width = 19, 
       plot=quartBoxPlotFunctionUni(stat[stat$type%in%"genes",],ylab="Percentage of covered genes (%)"))

dataset = "BTM"
stat <- read.table(paste("newBriefings_Incomplete/",dataset,"_3/Analysis_Rep/CovertureQuartiles.csv",sep=""),header = T,sep="\t")

ssname <- c("Ganesan","LC","PS",
            "Zhou","Resnik","Lin","Nunivers","DF","AIC","DAVID")

stat <- stat[stat$SS%in%ssname,]
stat$SS<- factor(stat$SS, levels = unique(ssname))
stat$type<- factor(stat$type, levels = c("terms","genes"))


tiff(filename = paste(resFile,"Fig10.tiff",sep=""),pointsize = 8,
     width = 2250,height=1125,res=300,compression = "lzw")
quartBoxPlotFunction(stat)
dev.off()

png(filename =paste(resFile,"Fig10Terms.png",sep=""), pointsize = 12,
    width = 2800,height=900,res=300)

quartBoxPlotFunctionUni(stat[stat$type%in%"terms",],ylab="Percentage of synthetic terms (%)")
dev.off()
ggsave(filename = paste(resFile,"Fig10Termss.svg",sep=""),width = 19, 
       plot=quartBoxPlotFunctionUni(stat[stat$type%in%"terms",],ylab="Percentage of synthetic terms (%)"))

png(filename =paste(resFile,"Fig10Genes.png",sep=""), pointsize = 12,
    width = 2800,height=900,res=300)

quartBoxPlotFunctionUni(stat[stat$type%in%"genes",],ylab="Percentage of covered genes (%)")
dev.off()
ggsave(filename = paste(resFile,"Fig10Genes.svg",sep=""),width = 19, 
       plot=quartBoxPlotFunctionUni(stat[stat$type%in%"genes",],ylab="Percentage of covered genes (%)"))

#########################################################################################################
# Figure intermediary


boxPlotFunction <- function(stat, ylim, title){
  ggplot(stat, aes(x =SS , y=value)) + 
    stat_boxplot(geom="errorbar")+
    geom_boxplot(colour = "black",fill= "#DCDCDC",alpha=1,outlier.color = "black", outlier.shape = 20)+
    ylab("Number of terms")+xlab("")+ ylim(ylim[1],ylim[2])+
    ggtitle(title)+theme(text = element_text(family = "DejaVu Serif",size=18),
                         axis.text.x = element_text(hjust = 1,angle = 45),
                         axis.line.x=element_line(size=0.5*k, colour="black"),
                         axis.line.y = element_line(size=0.5*k,colour="black"),
                         axis.line = element_line(size=1,colour="black"),
                         axis.title = element_text(size=18),
                         axis.title.y = element_text(margin = margin(0,20,0,0)),
                         axis.title.x = element_text(margin = margin(20,0,0,0)),
                         axis.text = element_text(size=18),
                         panel.background = element_blank(), 
                         plot.title = element_text(size=18,hjust = -0.25, vjust=2.12,margin = margin(5,0,40,0)))
  
}


dataset = "Chaussabel"
chau.stat <- read.table(paste("newBriefings_Incomplete/",dataset,"_3/Analysis_Rep/TermsVSRep.csv",sep=""),header = T,sep="\t")

ssname <- c("Annotation","Ganesan","LC","PS", "Zhou","Resnik","Lin","Nunivers","DF","AIC")

chau.stat <- chau.stat[chau.stat$SS%in%ssname,]
chau.stat$SS<- factor(chau.stat$SS, levels = unique(ssname))

library(ggplot2)
k=1


ChaussabelPlot <- boxPlotFunction(chau.stat, ylim = c(0,500),"") 
dataset = "BTM"
stat <- read.table(paste("newBriefings_Incomplete/",dataset,"_3/Analysis_Rep/TermsVSRep.csv",sep=""),header = T,sep="\t")

ssname <- c("Annotation","Ganesan","LC","PS","Zhou","Resnik","Lin","Nunivers","DF","AIC")
stat <- stat[stat$SS%in%ssname,]
stat$SS<- factor(stat$SS, levels = unique(ssname))

library(ggplot2)
library(gridExtra)
k=1

BTMPlot <- boxPlotFunction(stat, ylim = c(0,500),"")

tiff(filename = paste(resFile,"Fig8.tiff",sep=""),pointsize = 8,
     width =2243,height=1121,res=300, compression = "lzw")
grid.arrange(ChaussabelPlot,BTMPlot, ncol=2, nrow = 1)
dev.off()

ggsave(filename = paste(resFile,"Fig8.svg",sep=""), 
       plot=grid.arrange(arrangeGrob(ChaussabelPlot, top=grid::textGrob("(A)",x=0.01,hjust=0,gp=gpar(fontfamily="DejaVu Serif",fontsize=18,lineheight=1))), arrangeGrob(BTMPlot, top=grid::textGrob("(B)",x=0.01,hjust=0,gp=gpar(fontfamily="DejaVu Serif",fontsize=18,lineheight=1))), ncol=2, nrow = 1))
