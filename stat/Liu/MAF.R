
MAF.loadRaw <- function() {
    read.table(MAF.rawFile(), header = TRUE, sep = "\t", quote = "")
}

MAF.rawFile <- function() {
    gzfile(file.path(Liu.homeDir(), "raw", "all_muts_7_1_2018_vaf_added.maf.gz"))
}

## ---------------------------------------------------------------------

MAF.buildMissense <- function(mafRaw = NULL) {
    if (is.null(mafRaw))
        mafRaw <- MAF.loadRaw()

    ## Keep only relevant columns...
    mafRaw <- mafRaw[,c("Hugo_Symbol",
                        "Variant_Classification",
                        "Variant_Type",
                        "Tumor_Sample_Barcode",
                        "Protein_Change",
                        "ccf_hat")]

    ## Keep only SNPs...
    mafRaw <- subset(mafRaw, Variant_Classification == "Missense_Mutation" & Variant_Type == "SNP")

    ## RNA expression is keyed by patient identifier...
    mafRaw$Patient_ID <- Liu.patientID(mafRaw$Tumor_Sample_Barcode)

    ## Ensure that the barcode-to-patient mapping is unique...
    jam.assert(length(unique(mafRaw$Patient_ID)) == length(unique(mafRaw$Tumor_Sample_Barcode)))
    
    ## Rearrange...
    missenseFrame <-
        data.frame(Tumor_Barcode  = mafRaw$Patient_ID,
                   Hugo_Symbol    = mafRaw$Hugo_Symbol,
                   Protein_Change = mafRaw$Protein_Change,
                   CCF            = mafRaw$ccf_hat)

    missenseFrame <-
        missenseFrame[order(missenseFrame$Tumor_Barcode, missenseFrame$Hugo_Symbol),]

    JamLog.info("%d of %d missing CCF...", sum(is.na(missenseFrame$CCF)), nrow(missenseFrame))
    JamLog.info("Median CCF: [%.2f]...", median(missenseFrame$CCF, na.rm = TRUE))

    ## Replace missing CCFs...
    missenseFrame$CCF <-
        Filter.replaceNA(missenseFrame$CCF, median(missenseFrame$CCF, na.rm = TRUE))

    rownames(missenseFrame) <- NULL
    missenseFrame
}

MAF.loadMissense <- function() {
    read.table(MAF.missenseMAFFile(), sep = "\t", header = TRUE)
}

MAF.missenseMAFFile <- function() {
    gzfile(file.path(Liu.homeDir(), "Liu_Missense.maf.gz"))
}

MAF.writeMissense <- function(missenseFrame = NULL) {
    if (is.null(missenseFrame))
        missenseFrame <- MAF.buildMissense()

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
             xlim = c(0, 4),
             ylim = c(0, 60),
             xlab = "log10(Mutations per tumor)",
             ylab = "Count",
             cex.lab = 0.75)
    axis(1, cex = 0.75, cex.lab = 0.75, cex.axis = 0.75)
    axis(2, cex = 0.75, cex.lab = 0.75, cex.axis = 0.75)
    box()

    par(fig = c(1.0 - XWD, 1.0, 0.5 * (1.0 - YHT), 0.5 * (1.0 + YHT)), new = TRUE)
    qqnorm(Filter.demean(Filter.standardize(log10(perTumor))),
           ##main = "", xlim = c(-4, 4), ylim = c(-4, 4),
           main = "", xlim = c(-3, 3), ylim = c(-3, 3),
           cex = 0.75, cex.lab = 0.75, cex.axis = 0.75)
    lines(c(-10, 10), c(-10, 10))
}
