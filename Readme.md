


# genSetAnnotation


## genSetAnnotation main repository

The genSetAnnotation is a pipeline (wrote in Java) for annotating human gene set with the Gene Ontology (GO). The pipeline combine ontology methods, data mining and combinatory algorithm to provide a representative annotation for a given gene set.  

The following components are included in the repository:

-   Maven source 
- **[resources]** Two list of gene modules: **BTM** and **V2_Trial_8**
- **[resources]** OWL of Gene Ontology: version 07/05/2018
- **[resources]** Gene Ontology Annotation in gaf 2.0 format: version 21/05/2018
- **[resources]** R script 
- **[results]** All analysis results
-   Runnable jars to execute the pipeline

## Running analysis

First at all, we run the Rscript  **DavidAnalysis.R** to get the enrichment annotation for the BTM and V2_Trial_8 modules.
> Rscript resources/Rscript/DavidAnalysis.R
> **Note:** Verify if the first line of script is commented

To run the analysis using the BTM and V2_Trial_8 modules, you have to launch in your terminal this command:
> java -jar -Xmx20g -Xms10g geneSetAnnotationPipeline.jar

The -Xmx and -Xms are adapted to use for a compute with at least 25 Go of RAM.

Then the R script could be running using the terminal, but before they must to have the first line commented (corresponding to *setwd(dirname(rstudioapi::getSourceEditorContext()$path))*). The recomended order to run is:

- ClusterQuality.R
- zindex.R
- Visualization.R
- supplementaryVis.R




## Analysis of a gene set

To run only a gene set to recove their representative annotation, You need only to run that:

> java -jar geneSetAnnotationAnalysis.jar <list_genes>

The list of genes must to be with symbol id and separate by coma (without space). An example is:

    java -jar geneSetAnnotationAnalysis.jar LY6E,IFIT1,OAS1,IFIT1,IFIT3,OAS3,IFIT3,OAS1,OASL,LOC129607,ISG15,HERC5,OAS1,MX1,BATF2,LAMP3,IFI44L,XAF1,OASL,IFI44,OAS2,TRIM6,HES4,OTOF,FLJ20035,IFITM3,IFIT3,CXCL10,EPSTI1,SERPING1,LOC26010,OAS2,RSAD2,RTP4


