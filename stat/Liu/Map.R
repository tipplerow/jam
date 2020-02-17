
Map.barcodeFile <- function() {
    file.path(Liu.homeDir(), "Liu_Barcode.txt")
}

Map.writeBarcodeFile <- function() {
    genoFrame <- HLA.loadGenotype()
    writeLines(genoFrame$Patient_ID, Map.barcodeFile())
}
