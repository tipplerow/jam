
TCGA.dataVault <- function() {
    dataVault <- Sys.getenv("CSB_DATA_VAULT", unset = NA)

    if (is.na(dataVault))
        JamLog.error("Environment variable CSB_DATA_VAULT is not set.");

    dataVault
}

TCGA.homeDir <- function() {
    file.path(TCGA.dataVault(), "TCGA")
}

## ---------------------------------------------------------------------

TCGA.derivePatient <- function(barcode) {
    ##
    ## The patient identifier is the first 12 characters...
    ##
    substr(barcode, 1, 12)
}

TCGA.truncateBarcode <- function(barcode) {
    ##
    ## To match across data sets, our canonical tumor barcode keeps
    ## the sample and vial information but discards the remainder...
    ##
    substr(barcode, 1, 16)
}
