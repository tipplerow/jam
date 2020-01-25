
MAF.loadRaw <- function() {
    read.table(MAF.rawFile(), sep = "\t", header = TRUE, quote = "")
}

MAF.rawFile <- function() {
    gzfile(file.path(CellOrigin.homeDir(), "raw", "mc3.v0.2.8.PUBLIC.maf.gz"))
}

## ---------------------------------------------------------------------

MAF.buildMissense <- function(mafRaw = NULL) {
    if (is.null(mafRaw))
        mafRaw <- MAF.loadRaw()

    ## Keep only relevant columns...
    missenseFrame <-
        mafRaw[,c("Hugo_Symbol",
                  "Variant_Classification",
                  "Variant_Type",
                  "Tumor_Sample_Barcode",
                  "Transcript_ID",
                  "Protein_position",
                  "Amino_acids")]

    ## Keep only SNPs...
    missenseFrame <-
        subset(missenseFrame,
               Variant_Classification == "Missense_Mutation" & Variant_Type == "SNP")

    ## Keep only valid protein change indicators...
    missenseFrame <-
        subset(missenseFrame, nchar(Amino_acids) == 3)

    missenseFrame <-
        subset(missenseFrame, substr(Amino_acids, 1, 1) != "U")

    missenseFrame <-
        subset(missenseFrame, substr(Amino_acids, 2, 2) == "/")

    missenseFrame <-
        subset(missenseFrame, substr(Amino_acids, 3, 3) != "U")

    changeFrom <- substr(missenseFrame$Amino_acids, 1, 1)
    changeTo   <- substr(missenseFrame$Amino_acids, 3, 3)

    missenseFrame$Protein_position <-
        as.integer(missenseFrame$Protein_position)

    missenseFrame <-
        subset(missenseFrame, is.finite(Protein_position))

    missenseFrame$Protein_Change <-
        sprintf("%s%d%s", changeFrom, missenseFrame$Protein_position, changeTo)

    missenseFrame$Tumor_Barcode <-
        CellOrigin.truncateBarcode(missenseFrame$Tumor_Sample_Barcode)

    missenseFrame <-
        missenseFrame[,c("Tumor_Barcode",
                         "Hugo_Symbol",
                         "Transcript_ID",
                         "Protein_Change")]

    missenseFrame <-
        missenseFrame[order(missenseFrame$Tumor_Barcode, missenseFrame$Hugo_Symbol),]

    ## Some mutations are duplicated in the MAF file, possibly called
    ## by two independent methods...
    missenseFrame <-
        missenseFrame[!duplicated(missenseFrame[,c("Tumor_Barcode",
                                                   "Hugo_Symbol",
                                                   "Protein_Change")]),]

    missenseFrame
}

MAF.loadMissense <- function() {
    read.table(MAF.missenseMAFFile(), sep = "\t", header = TRUE)
}

MAF.missenseMAFFile <- function() {
    gzfile(file.path(CellOrigin.homeDir(), "TCGA_Missense.maf.gz"))
}

MAF.writeMissense <- function(missenseFrame = NULL) {
    if (is.null(missenseFrame))
        missenseFrame <- MAF.buildMissense(MAF.loadMAFRaw())

    write.table(missenseFrame,
                file      = MAF.missenseMAFFile(),
                sep       = "\t",
                quote     = FALSE,
                col.names = TRUE,
                row.names = FALSE)
}

## ---------------------------------------------------------------------

MAF.perTumor <- function(maf) {
    byResult <- by(maf, maf$Tumor_Barcode, nrow)

    perTumor <- as.numeric(byResult)
    names(perTumor) <- names(byResult)

    perTumor
}

MAF.logNormal <- function(perTumor) {
    XWD <- 0.5
    YHT <- 0.55

    par(las = 1)
    par(fig = c(0.0, XWD, 0.5 * (1.0 - YHT), 0.5 * (1.0 + YHT)))
    truehist(log10(perTumor),
             prob = FALSE,
             axes = FALSE,
             xlim = c(0, 5),
             ylim = c(0, 1000),
             xlab = "log10(Mutations per tumor)",
             ylab = "Count",
             cex.lab = 0.75)
    axis(1, cex = 0.75, cex.lab = 0.75, cex.axis = 0.75)
    axis(2, cex = 0.75, cex.lab = 0.75, cex.axis = 0.75)
    box()

    par(fig = c(1.0 - XWD, 1.0, 0.5 * (1.0 - YHT), 0.5 * (1.0 + YHT)), new = TRUE)
    qqnorm(Filter.demean(Filter.standardize(log10(perTumor))),
           main = "", xlim = c(-5, 5), ylim = c(-5, 5),
           cex = 0.75, cex.lab = 0.75, cex.axis = 0.75)
    lines(c(-10, 10), c(-10, 10))
}
