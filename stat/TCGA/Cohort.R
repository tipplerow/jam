
Cohort.homeDir <- function(cancerType) {
    file.path(TCGA.homeDir(), "Cohort", cancerType)
}

## ---------------------------------------------------------------------

Cohort.barcodeFile <- function(cancerType) {
    file.path(Cohort.homeDir(cancerType), sprintf("%s_Barcode.txt", cancerType))
}

Cohort.findBarcodes <- function(cancerType) {
    cancerTypeFrame <- Map.loadTumorCancerTypeMap()
    cancerTypeFrame <- subset(cancerTypeFrame, Cancer_Type == cancerType)

    if (nrow(cancerTypeFrame) < 1)
        JamLog.error("No barcodes with cancer type [%s].", cancerType)

    tumorPatientFrame    <- Map.loadTumorPatientMap()
    patientGenotypeFrame <- ImmuneLandscape.loadPatientGenotype()

    tumorGenoFrame  <- merge(tumorPatientFrame, patientGenotypeFrame)
    cancerTypeFrame <- merge(cancerTypeFrame, tumorGenoFrame)

    barcodes <- sort(cancerTypeFrame$Tumor_Barcode)
    barcodes
}

Cohort.loadBarcodeFile <- function(cancerType) {
    barcodeFile <- Cohort.barcodeFile(cancerType)

    JamLog.info("Reading [%s]...", barcodeFile)
    barcodeList <- writeLines(barcodeList, barcodeFile)

    barcodeList
}

Cohort.writeBarcodeFile <- function(cancerType) {
    barcodeList <- Cohort.findBarcodes(cancerType)
    barcodeFile <- Cohort.barcodeFile(cancerType)

    JamLog.info("Writing [%s]...", barcodeFile)
    writeLines(barcodeList, barcodeFile)
}

