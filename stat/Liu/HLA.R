
HLA.buildGenotype <- function() {
    rawFrame  <- HLA.rawFrame()
    patientID <- rawFrame[,1]

    typeCode <- function(slice, code, index) {
        sprintf("%s%s%s", code, substr(slice[,index], 7, 8), substr(slice[,index], 10, 11))
    }

    processRow <- function(slice) {
        hlaA1 <- typeCode(slice, "A", 2)
        hlaA2 <- typeCode(slice, "A", 3)

        hlaB1 <- typeCode(slice, "B", 4)
        hlaB2 <- typeCode(slice, "B", 5)

        hlaC1 <- typeCode(slice, "C", 6)
        hlaC2 <- typeCode(slice, "C", 7)

        genotype <- c(hlaA1, hlaA2, hlaB1, hlaB2, hlaC1, hlaC2)
        genotype <- sort(genotype)
        genotype <- genotype[!duplicated(genotype)]
        genotype <- paste(genotype, sep = " ", collapse = " ")

        data.frame(Patient_ID = slice[,1], Genotype = genotype)
    }

    genoFrame <- do.call(rbind, by(rawFrame, rawFrame[,1], processRow))
    rownames(genoFrame) <- NULL

    genoFrame
}

HLA.genotypeFile <- function() {
    file.path(Liu.homeDir(), "Liu_Patient_Genotype.csv")
}

HLA.loadGenotype <- function() {
    read.csv(HLA.genotypeFile())
}

HLA.writeGenotype <- function(genoFrame = NULL) {
    if (is.null(genoFrame))
        genoFrame <- HLA.buildGenotype()

    write.csv(genoFrame, HLA.genotypeFile(), row.names = FALSE, quote = FALSE)
}

## ---------------------------------------------------------------------

HLA.rawFrame <- function() {
    rawFile  <- file.path(Liu.homeDir(), "raw", "combinedHLAs.txt")
    rawFrame <- read.table(rawFile, header = TRUE, sep = "\t")
    rawFrame
}
