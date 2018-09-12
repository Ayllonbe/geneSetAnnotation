setwd(dirname(rstudioapi::getSourceEditorContext()$path))
setwd("../../results/")

resFile = "visualization/"
if(!dir.exists(resFile)){
  dir.create(resFile)
}


##########################################################################################
#Chau
dataset = "Chaussabel"
t<- read.table(paste("newBriefings_Incomplete/",dataset,"_3/Analysis_Rep/genCoverture.csv",sep=""),header = T)

tiff(filename = "visualization/Figure_Chaussabel_Sup.tiff",pointsize = 12,
     width = 8250,height=3250,res=400)
lineplots(t)
dev.off()

dataset = "BTM"
t<- read.table(paste("newBriefings_Incomplete/",dataset,"_3/Analysis_Rep/genCoverture.csv",sep=""),header = T)

tiff(filename = "visualization/Figure_BTM_Sup.tiff",pointsize = 12,
     width = 8250,height=3250,res=400)
lineplots(t)
dev.off()

s# Order by 
t<- t[order(t$NbGenesBP),]
t$Module<- factor(t$Module, levels = unique(t$Module))
lineplots(t)


lineplots <- function(t){
  
  require(ggplot2)
  k=1
  l <- list()
  count = 0
  n = length(t$Module)/5
  for(x in 1:5){
    
    a <- 1 + count
    if(x == 5){
      b <- length(t$Module)
    }else{
      b <- n+ count
    }
    ccc <- t[a:b,] 
    
    count = count +n
    
    l[[x]] <- ggplot(ccc, aes(x = Module, y=value,group = SS))+
      geom_line(aes(linetype=SS, color = SS))+
      ylim(0,100)+
      scale_x_discrete("Chaussabel modules") +
      #scale_colour_grey( start = 0.2, end = 0.8, na.value = "red")+
      #scale_y_continuous("Percentage of covered genes")+
      theme(axis.text.x = element_text(hjust = 0.5),
            axis.line.x=element_line(size=0.5*k, colour="black"),
            axis.line.y = element_line(size=0.5*k,colour="black"),
            axis.line = element_line(size=1,colour="black"),
            axis.title = element_text(size=10*k),
            axis.title.y = element_blank(), #element_text(margin = margin(0,20,0,0)),
            axis.title.x =element_blank(), #element_text(margin = margin(20,0,0,0)),
            axis.text = element_text(size=8*k),
            #panel.grid.major = element_blank(),
            # panel.grid.minor = element_blank(),
            # panel.border = element_blank(),
            legend.key = element_rect(fill = "white", colour = "white"),
            legend.title = element_text(face = "bold"),
            legend.title.align=0.5,
            panel.background = element_blank(), 
            plot.margin = margin(t = 15, r = 15, b = 15, l = 15, unit = "pt"),
            plot.title = element_text(size=25*k,hjust = 0.5,face="bold",margin = margin(5,0,40,0)))
    
  }
  library(gtable)
  legend = gtable_filter(ggplotGrob(l[[1]]), "guide-box") 
  library(grid)
  library(gridExtra)
  grid.arrange(arrangeGrob(l[[1]]+ theme(legend.position="none"),
                           l[[2]] + theme(legend.position="none"),
                           l[[3]] + theme(legend.position="none"),
                           l[[4]]+ theme(legend.position="none"),
                           l[[5]] + theme(legend.position="none"),
                           nrow=5, ncol = 1),
               left = textGrob("\nPercentage of genes", rot = 90, vjust = 1),
               legend, 
               bottom = textGrob("Chaussabel modules"),
               
               widths=unit.c(unit(0.95, "npc") - legend$width, legend$width ))
}
