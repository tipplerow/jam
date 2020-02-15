
Hugo.dataVault <- function() {
    JamEnv.getRequired("CSB_DATA_VAULT")
}

Hugo.homeDir <- function() {
    file.path(Hugo.dataVault(), "Hugo")
}

## ---------------------------------------------------------------------

Hugo.loadRaw <- function() {
    read.table(Hugo.rawFile(), header = TRUE, sep = "\t", quote = "", comment.char = "", strip.white = TRUE)
}

Hugo.rawFile <- function() {
    gzfile(file.path(Hugo.homeDir(), "GeneNames_20200214.txt.gz"))
}

## ---------------------------------------------------------------------

Hugo.buildMaster <- function(rawFrame = NULL) {
    if (is.null(rawFrame))
        master <- Hugo.loadRaw()
    else
        master <- rawFrame

    colnames(master) <-
        c("HGNC", "Hugo_Symbol", "Name", "Aliases", "Ensembl_Gene")

    withdrawn      <- grep("symbol withdrawn", master$Name)
    antisenseRNA   <- grep("antisense RNA", master$Name)
    missingEnsembl <- which(is.na(master$Ensembl_Gene) | nchar(master$Ensembl_Gene) == 0)

    removeRows <- c(withdrawn, antisenseRNA, missingEnsembl)

    master <- master[-removeRows,]
    master <- master[,-1]
    master <- master[order(master$Hugo_Symbol),]
    master
}

Hugo.loadMaster <- function() {
    read.table(Hugo.masterFile(), sep = "\t", header = TRUE, quote = "", comment.char = "", strip.white = TRUE)
}

Hugo.masterFile <- function() {
    gzfile(Hugo.masterFileName())
}

Hugo.masterFileName <- function() {
    file.path(Hugo.homeDir(), "Hugo_Master.tsv.gz")
}

Hugo.writeMaster <- function(master = NULL) {
    if (is.null(master))
        master <- Hugo.buildMaster()

    write.table(master, Hugo.masterFile(),
                sep = "\t", quote = FALSE,
                row.names = FALSE, col.names = TRUE)
}
