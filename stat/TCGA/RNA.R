
RNA.loadRaw <- function() {
    read.table(RNA.rawFile(),
               sep = "\t",
               quote = "\"",
               header = TRUE,
               comment.char = "",
               strip.white = TRUE)
}

RNA.rawFile <- function() {
    gzfile(file.path(CellOrigin.homeDir(), "raw", "EBPlusPlusAdjustPANCAN_IlluminaHiSeq_RNASeqV2-v2.geneExp.tsv.gz"))
}

## ---------------------------------------------------------------------

RNA.rnaFrame <- function(geneList, zscore = FALSE, rna = NULL) {
    if (is.null(rna))
        rna <- RNA.loadRNA()

    geneList <- intersect(geneList, colnames(rna))

    if (length(geneList) < 1)
        JamLog.error("No matching genes.")

    rna <- rna[,geneList]

    if (zscore) {
        rna <- log(1 + rna)
        rna <- apply(rna, 2, Filter.zscore)
    }

    rnaFrame <- data.frame(Tumor_Barcode = rownames(rna))
    rnaFrame <- cbind(rnaFrame, rna)

    colnames(rnaFrame) <-
        c("Tumor_Barcode", geneList)

    rnaFrame
}

RNA.mergeRNA <- function(master, geneList, zscore = FALSE, rna = NULL) {
    rnaFrame <- RNA.rnaFrame(geneList, zscore, rna)
    master   <- merge(master, rnaFrame, by = "Tumor_Barcode")
    master
}

## ---------------------------------------------------------------------

RNA.formatRNA <- function(rnaRaw = NULL) {
    if (is.null(rnaRaw))
        rnaRaw <- RNA.loadRaw()

    hugoSymbol <- unlist(lapply(strsplit(rnaRaw$gene_id, "\\|"), function(x) x[[1]]))
    rnaMatrix  <- as.matrix(rnaRaw[,2:ncol(rnaRaw)])

    rownames(rnaMatrix) <- hugoSymbol
    colnames(rnaMatrix) <- gsub("\\.", "-", colnames(rnaMatrix))

    missingGene <- which(rownames(rnaMatrix) == "?")

    if (length(missingGene) > 0)
        rnaMatrix <- rnaMatrix[-missingGene,]

    ## Now transpose to have tumors as the row variables, genes as the
    ## columns...
    rnaMatrix <- t(rnaMatrix)

    ## Convert the full barcodes into canonical form...
    rownames(rnaMatrix) <-
        RNA.truncateBarcode(rownames(rnaMatrix))

    ## Several tumors have multiple samples in the matrix, so compute
    ## the geometric mean of their expression in order to collapse to
    ## a single value for the canonical tumor barcode...
    dupRows <- which(duplicated(rownames(rnaMatrix)))

    for (dupRow in dupRows) {
        dupSlice <- which(rownames(rnaMatrix) == rownames(rnaMatrix)[dupRow])
        geomMean <- apply(rnaMatrix[dupSlice,], 2, function(x) exp(mean(log(x), na.rm = TRUE)))

        for (sliceRow in dupSlice)
            rnaMatrix[sliceRow,] <- geomMean
    }

    dupRows <- which(duplicated(rownames(rnaMatrix)))
    dupCols <- which(duplicated(colnames(rnaMatrix)))

    if (length(dupRows) > 0) {
        JamLog.info("Collapsing duplicate barcodes: [%s].", paste(rownames(rnaMatrix)[dupRows], collapse = ", "))
        rnaMatrix <- rnaMatrix[-dupRows,]
    }

    if (length(dupCols) > 0) {
        JamLog.info("Removing duplicate gene symbols: [%s].", paste(colnames(rnaMatrix)[dupCols], collapse = ", "))
        rnaMatrix <- rnaMatrix[,-dupCols]
    }

    jam.assert(!any(duplicated(rownames(rnaMatrix))))
    jam.assert(!any(duplicated(colnames(rnaMatrix))))

    ## Remove missing...
    rnaMatrix <- Filter.replaceNA(rnaMatrix, 0.0)

    ## Remove negative...
    rnaMatrix <- pmax(rnaMatrix, 0.0)

    ## No use keeping more than two decimal places...
    rnaMatrix <- round(rnaMatrix, 2)
    rnaMatrix
}

RNA.loadRData <- function() {
    JamIO.load(RNA.RDataFile())
}

RNA.RDataFile <- function() {
    file.path(CellOrigin.homeDir(), "TCGA_RNA.RData")
}

RNA.CSVFile <- function() {
    gzfile(file.path(.homeDir(), "TCGA_RNA.csv.gz"))
}

RNA.writeRNA <- function(rnaMatrix = NULL) {
    if (is.null(rnaMatrix))
        rnaMatrix <- RNA.formatRNA()

    JamIO.save(rnaMatrix, RNA.rnaRDataFile())
    write.csv(rnaMatrix, RNA.rnaCSVFile(), quote = FALSE, row.names = TRUE)
}

## ---------------------------------------------------------------------

RNA.hist <- function(rna, N = 1E6) {
    if (is.matrix(rna))
        rna <- as.vector(rna)

    if (length(rna) > N)
        rna <- rna[sample.int(length(rna), N)]

    rna <- rna[rna > 1.0E-12]

    par(las = 1)
    par(fig = c(0.05, 1.0, 0.15, 0.85))
    truehist(log10(rna),
             xlab = expression("log"[10] * "(Expression [FPKM])"),
             ylab = "Density")
}

RNA.log1P.hist <- function(rna, xmax, N = 1E6) {
    if (is.matrix(rna))
        rna <- as.vector(rna)

    if (length(rna) > N)
        rna <- rna[sample.int(length(rna), N)]

    rna <- rna[rna > -1.0E-12]
    x   <- pmin(xmax, log(1 + rna))

    par(las = 1)
    par(fig = c(0.05, 1.0, 0.15, 0.85))
    truehist(x,
             xlab = "log(1 + EX)",
             ylab = "Density")
}

RNA.log1P.alpha.hist <- function(rna, alpha, xmax, N = 1E6) {
    if (is.matrix(rna))
        rna <- as.vector(rna)

    if (length(rna) > N)
        rna <- rna[sample.int(length(rna), N)]

    rna <- rna[rna > -1.0E-12]
    x   <- pmin(xmax, log(1 + alpha * rna))

    par(las = 1)
    par(fig = c(0.05, 1.0, 0.15, 0.85))
    truehist(x,
             xlab = sprintf("log(1 + %s * EX)", as.character(alpha)),
             ylab = "Density")
}
