


# genSetAnnotation


## genSetAnnotation main repository

The genSetAnnotation is a pipeline (written in Java) dedicated to the annotation of human gene sets using the Gene Ontology (GO). The pipeline combines ontology methods, data mining approaches and combinatory algorithms to provide a representative annotation for a given gene set.  

The following components are included in the repository:

-   Maven source 
- **[resources]** Two lists of gene modules: **BTM** and **V2_Trial_8**
- **[resources]** OWL of Gene Ontology: version 07/05/2018
- **[resources]** Gene Ontology Annotation is provided in gaf 2.0 format: version 21/05/2018
- **[resources]** R scripts
- **[results]** All analysis results
-   Runnable jars necessary to execute the pipeline

## Running analysis

First of all, you need to run the Rscript  **DavidAnalysis.R** to get the enrichment annotations for the BTM and V2_Trial_8 modules.
> Rscript resources/Rscript/DavidAnalysis.R
> **Note:** Verify if the first line of the script is uncommented

To run the analysis using the BTM and V2_Trial_8 modules, you have to launch in your terminal the following command:
> java -jar -Xmx20g -Xms10g geneSetAnnotationPipeline.jar

 The -Xmx and -Xms are adapted to a computer with at least 25 Go of RAM.

 Then the R script can be run from the terminal, but before doing this, you need to uncomment the first line (corresponding to *setwd(dirname(rstudioapi::getSourceEditorContext()$path))*). The recommended order to run the different R scripts is:

- ClusterQuality.R
- zindex.R
- Visualization.R
- supplementaryVis.R

> Rscript resources/Rscript/<file>.R


## Analysis of a gene set

To analyse exclusively one gene set (to recover its representative annotation), you need to rxecute the following line command:

> java -jar geneSetAnnotationAnalysis.jar <list_genes>

The list of genes must be provided as symbol ids and each id have to be separated by coma (without space). An example is given:

    java -jar geneSetAnnotationAnalysis.jar LY6E,IFIT1,OAS1,IFIT1,IFIT3,OAS3,IFIT3,OAS1,OASL,LOC129607,ISG15,HERC5,OAS1,MX1,BATF2,LAMP3,IFI44L,XAF1,OASL,IFI44,OAS2,TRIM6,HES4,OTOF,FLJ20035,IFITM3,IFIT3,CXCL10,EPSTI1,SERPING1,LOC26010,OAS2,RSAD2,RTP4


