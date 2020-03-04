
ImmuneLandscape.dataVault <- function() {
    dataVault <- Sys.getenv("CSB_DATA_VAULT", unset = NA)

    if (is.na(dataVault))
        JamLog.error("Environment variable CSB_DATA_VAULT is not set.");

    dataVault
}

ImmuneLandscape.homeDir <- function() {
    file.path(ImmuneLandscape.dataVault(), "TCGA", "ImmuneLandscape")
}

ImmuneLandscape.rawDir <- function() {
    file.path(ImmuneLandscape.homeDir(), "raw")
}

## ---------------------------------------------------------------------

ImmuneLandscape.loadHLARaw <- function() {
    read.csv(gzfile(file.path(ImmuneLandscape.rawDir(), "OptiTypeCallsHLA_20171207.tsv.gz")))
}

## ---------------------------------------------------------------------

ImmuneLandscape.buildGenotypeDetail <- function(hlaRaw = NULL) {
    if (is.null(hlaRaw))
        hlaRaw <- ImmuneLandscape.loadHLARaw()

    source(file.path(ImmuneLandscape.dataVault(), "TCGA", "CellOrigin", "CellOrigin.R"))

    ## Remove missing alleles...
    hlaRaw <- subset(hlaRaw, nchar(A1) >= 7)
    hlaRaw <- subset(hlaRaw, nchar(A2) >= 7)
    hlaRaw <- subset(hlaRaw, nchar(B1) >= 7)
    hlaRaw <- subset(hlaRaw, nchar(B2) >= 7)
    hlaRaw <- subset(hlaRaw, nchar(C1) >= 7)
    hlaRaw <- subset(hlaRaw, nchar(C2) >= 7)

    ## Remove mal-formed identifiers...
    hlaRaw <- subset(hlaRaw, nchar(aliquot_id) == 28)

    ## Place alleles in canonical order...
    A1 <- ifelse(hlaRaw$A1 < hlaRaw$A2, hlaRaw$A1, hlaRaw$A2)
    A2 <- ifelse(hlaRaw$A1 > hlaRaw$A2, hlaRaw$A1, hlaRaw$A2)

    B1 <- ifelse(hlaRaw$B1 < hlaRaw$B2, hlaRaw$B1, hlaRaw$B2)
    B2 <- ifelse(hlaRaw$B1 > hlaRaw$B2, hlaRaw$B1, hlaRaw$B2)

    C1 <- ifelse(hlaRaw$C1 < hlaRaw$C2, hlaRaw$C1, hlaRaw$C2)
    C2 <- ifelse(hlaRaw$C1 > hlaRaw$C2, hlaRaw$C1, hlaRaw$C2)

    Patient_ID    <- substr(hlaRaw$aliquot_id, 1, 12)
    Tumor_Barcode <- CellOrigin.truncateBarcode(hlaRaw$aliquot_id)

    Genotype <- paste(A1, A2, B1, B2, C1, C2, sep = " ")

    HomoA <- as.numeric(A1 == A2)
    HomoB <- as.numeric(B1 == B2)
    HomoC <- as.numeric(C1 == C2)

    Homozygosity <- HomoA + HomoB + HomoC

    detailFrame <-
        data.frame(Patient_ID    = Patient_ID,
                   Tumor_Barcode = Tumor_Barcode,
                   Genotype      = Genotype,
                   A1            = A1,
                   A2            = A2,
                   B1            = B1,
                   B2            = B2,
                   C1            = C1,
                   C2            = C2,
                   HomoA         = HomoA,
                   HomoB         = HomoB,
                   HomoC         = HomoC,
                   Homozygosity  = Homozygosity)

    detailFrame <-
        detailFrame[order(detailFrame$Tumor_Barcode),]

    ## Remove all duplicate tumors with inconsistent genotypes...
    dupRows  <- which(duplicated(detailFrame$Tumor_Barcode))
    badCodes <- character(0)

    for (dupRow in dupRows) {
        dupCode   <- detailFrame$Tumor_Barcode[dupRow]
        dupSlice  <- subset(detailFrame, Tumor_Barcode == dupCode)
        isBadCode <- length(unique(dupSlice$Genotype)) > 1

        if (isBadCode) {
            badCodes <- c(badCodes, dupCode)
            JamLog.info("Inconsistent duplicate barcode: [%s]...", dupCode)
        }
    }

    detailFrame <- subset(detailFrame, !(Tumor_Barcode %in% badCodes))
    detailFrame <- detailFrame[!duplicated(detailFrame$Tumor_Barcode),]

    detailFrame
}

ImmuneLandscape.genotypeDetailFile <- function() {
    file.path(ImmuneLandscape.homeDir(), "TCGA_Genotype_Detail.csv")
}

ImmuneLandscape.loadGenotypeDetail <- function() {
    read.csv(ImmuneLandscape.genotypeDetailFile())
}

ImmuneLandscape.writeGenotypeDetail <- function() {
    write.csv(ImmuneLandscape.buildGenotypeDetail(),
              ImmuneLandscape.genotypeDetailFile(),
              quote = FALSE, row.names = FALSE)
}

## ---------------------------------------------------------------------

ImmuneLandscape.buildPatientGenotype <- function(hlaRaw = NULL) {
    if (is.null(hlaRaw))
        hlaRaw <- ImmuneLandscape.loadHLARaw()

    ## Remove missing alleles...
    hlaRaw <- subset(hlaRaw, nchar(A1) >= 7)
    hlaRaw <- subset(hlaRaw, nchar(A2) >= 7)
    hlaRaw <- subset(hlaRaw, nchar(B1) >= 7)
    hlaRaw <- subset(hlaRaw, nchar(B2) >= 7)
    hlaRaw <- subset(hlaRaw, nchar(C1) >= 7)
    hlaRaw <- subset(hlaRaw, nchar(C2) >= 7)

    ## Remove mal-formed identifiers...
    hlaRaw <- subset(hlaRaw, nchar(aliquot_id) == 28)

    ## Extract identifiers...
    hlaRaw$Patient_ID    <- substr(hlaRaw$aliquot_id, 1, 12)
    hlaRaw$Tumor_Barcode <- substr(hlaRaw$aliquot_id, 1, 16)

    shortKey <- function(hlaCode) {
        gsub(":", "", gsub("\\*", "", hlaCode))
    }

    processBarcode <- function(slice) {
        A1 <- shortKey(slice$A1)
        A2 <- shortKey(slice$A2)

        B1 <- shortKey(slice$B1)
        B2 <- shortKey(slice$B2)

        C1 <- shortKey(slice$C1)
        C2 <- shortKey(slice$C2)

        genotype <- c(A1, A2, B1, B2, C1, C2)
        genotype <- sort(genotype)
        genotype <- genotype[!duplicated(genotype)]
        genotype <- paste(genotype, sep = " ", collapse = " ")

        genoFrame <- data.frame(Patient_ID    = slice$Patient_ID,
                                Tumor_Barcode = slice$Tumor_Barcode,
                                Genotype      = genotype)
    }

    barcodeFrame <- do.call(rbind, by(hlaRaw, hlaRaw$Tumor_Barcode, processBarcode))
    rownames(barcodeFrame) <- NULL

    processPatient <- function(slice) {
        if (length(unique(slice$Genotype)) == 1)
            return(data.frame(Patient_ID = slice$Patient_ID[1],
                              Genotype   = slice$Genotype[1]))
        else
            return(NULL)
    }

    patientFrame <- do.call(rbind, by(barcodeFrame, barcodeFrame$Patient_ID, processPatient))
    rownames(patientFrame) <- NULL

    patientFrame
}

ImmuneLandscape.patientGenotypeFile <- function() {
    file.path(ImmuneLandscape.homeDir(), "TCGA_Patient_Genotype.csv")
}

ImmuneLandscape.loadPatientGenotype <- function() {
    read.csv(ImmuneLandscape.patientGenotypeFile())
}

ImmuneLandscape.writePatientGenotype <- function() {
    write.csv(ImmuneLandscape.buildPatientGenotype(),
              ImmuneLandscape.patientGenotypeFile(),
              quote = FALSE, row.names = FALSE)
}

## ---------------------------------------------------------------------

ImmuneLandscape.commonAlleles <- function(N = 10) {
    genoFrame <- ImmuneLandscape.loadGenotypeDetail()

    hlaA <- c(genoFrame$A1, genoFrame$A2)
    hlaB <- c(genoFrame$B1, genoFrame$B2)
    hlaC <- c(genoFrame$C1, genoFrame$C2)

    countA <- rev(sort(by(hlaA, hlaA, length)))
    countB <- rev(sort(by(hlaB, hlaB, length)))
    countC <- rev(sort(by(hlaC, hlaC, length)))

    commonA <- head(names(countA), N)
    commonB <- head(names(countB), N)
    commonC <- head(names(countC), N)

    c(commonA, commonB, commonC)
}

## ---------------------------------------------------------------------

ImmuneLandscape.findUniqueAlleles <- function(genoFrame) {
    alleles <- sort(unique(unlist(strsplit(genoFrame$Genotype, " "))))
    alleles <- gsub("\\*", "", alleles)
    alleles <- gsub(":", "", alleles)
    alleles
}

ImmuneLandscape.splitAlleles <- function(alleles, batchSize = 25) {
    index <- 1

    alleleFile <- function() {
        sprintf("HLA_%02d", index)
    }

    while (length(alleles) >= batchSize) {
        slice <- alleles[1:batchSize]
        writeLines(slice, con = alleleFile())

        index   <- index + 1
        alleles <- alleles[-(1:batchSize)]
    }

    writeLines(alleles, con = alleleFile())
}
