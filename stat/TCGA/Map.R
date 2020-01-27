
Map.homeDir <- function() {
    file.path(TCGA.homeDir(), "Map")
}

## ---------------------------------------------------------------------

Map.buildTumorPatientMap <- function() {
    fileConn <- RNA.rawFile()
    barcodes <- readLines(fileConn, n = 1L)
    close(fileConn)

    ## Parse barcodes...
    barcodes <- unlist(strsplit(barcodes, "\t"))

    ## Remove the "gene_id" in the first element...
    barcodes <- barcodes[-1]

    ## Remove the surrounding quotation marks...
    barcodes <- gsub("\"", "", barcodes)

    ## Truncate the barcodes to standard length...
    barcodes <- TCGA.truncateBarcode(barcodes)

    ## Derive patient keys by rule...
    patients <- TCGA.derivePatient(barcodes)

    tumorMap <- data.frame(Tumor_Barcode = barcodes, Patient_ID = patients)
    tumorMap <- tumorMap[order(tumorMap$Tumor_Barcode),]

    ## There are two duplicate barcodes; their RNA expression data
    ## will be averaged to preserve a unique
    dupCodes <- which(duplicated(tumorMap$Tumor_Barcode))

    if (length(dupCodes) > 0)
        tumorMap <- tumorMap[-dupCodes,]

    jam.assert(!any(duplicated(tumorMap$Tumor_Barcode)))
    tumorMap
}

Map.loadTumorPatientMap <- function() {
    read.table(Map.tumorPatientMapFile(), sep = "\t", header = TRUE)
}

Map.tumorPatientMapFile <- function() {
    file.path(Map.homeDir(), "TCGA_Tumor_Patient_Map.tsv")
}

Map.writeTumorPatientMap <- function() {
    write.table(Map.buildTumorPatientMap(),
                file      = Map.tumorPatientMapFile(),
                sep       = "\t",
                quote     = FALSE,
                col.names = TRUE,
                row.names = FALSE)
}

## ---------------------------------------------------------------------

Map.buildTumorCancerTypeMap <- function() {
    patientTypeMap  <- CDR.loadCancerType()
    tumorPatientMap <- Map.loadTumorPatientMap()

    tumorTypeMap <- merge(patientTypeMap, tumorPatientMap, by = "Patient_ID")
    tumorTypeMap <- tumorTypeMap[,c("Tumor_Barcode", "Cancer_Type")]
    tumorTypeMap <- tumorTypeMap[order(tumorTypeMap$Cancer_Type, tumorTypeMap$Tumor_Barcode),]
    tumorTypeMap
}

Map.loadTumorCancerTypeMap <- function() {
    read.table(Map.tumorCancerTypeMapFile(), sep = "\t", header = TRUE)
}

Map.tumorCancerTypeMapFile <- function() {
    file.path(Map.homeDir(), "TCGA_Tumor_Cancer_Type_Map.tsv")
}

Map.writeTumorCancerTypeMap <- function() {
    write.table(Map.buildTumorCancerTypeMap(),
                file      = Map.tumorCancerTypeMapFile(),
                sep       = "\t",
                quote     = FALSE,
                col.names = TRUE,
                row.names = FALSE)
}

## ---------------------------------------------------------------------

Map.writeBarcodeByCancerType <- function(format = "Tumor_Barcode_%s.txt") {
    typeMap <- Map.buildTumorCancerTypeMap()
    types   <- sort(unique(typeMap$Cancer_Type))

    for (type in types)
        writeLines(typeMap[which(typeMap$Cancer_Type == type), "Tumor_Barcode"], sprintf(format, type))
}
