
Map.barcodeFile <- function() {
    file.path(Liu.homeDir(), "Liu_Barcode.txt")
}

Map.loadBarcodes <- function() {
    scan(Map.barcodeFile(), what = character())
}

Map.writeBarcodes <- function() {
    ##
    ## Only process patients with HLA information...
    ## 
    genoFrame <- HLA.loadGenotype()
    writeLines(genoFrame$Patient_ID, Map.barcodeFile())
}

## ---------------------------------------------------------------------

Map.buildTumorPatientMap <- function() {
    ##
    ## Patients and barcodes are identical...
    ##
    barcodes <- Map.loadBarcodes()
    patients <- barcodes

    data.frame(Tumor_Barcode = barcodes, Patient_ID = patients)
}

Map.tumorPatientFile <- function() {
    file.path(Liu.homeDir(), "Liu_Tumor_Patient_Map.tsv")
}

Map.writeTumorPatientMap <- function(mapFrame = NULL) {
    if (is.null(mapFrame))
        mapFrame <- Map.buildTumorPatientMap()
    
    write.table(mapFrame, Map.tumorPatientFile(),
                sep = "\t", quote = FALSE, col.names = TRUE, row.names = FALSE)
}
