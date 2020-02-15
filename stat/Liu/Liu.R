
Liu.dataVault <- function() {
    dataVault <- Sys.getenv("CSB_DATA_VAULT")

    if (nchar(dataVault) == 0)
        JamLog.stop("CSB_DATA_VAULT is not set.")

    dataVault
    
}

Liu.homeDir <- function() {
    file.path(Liu.dataVault(), "Liu")
}

## ---------------------------------------------------------------------

Liu.patientID <- function(barcode) {
    unlist(lapply(strsplit(barcode, "-"), function(x) x[2]))
}
