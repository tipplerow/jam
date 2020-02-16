
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

Hugo.buildBackup <- function(mafRaw = NULL) {
    if (is.null(mafRaw)) {
        jamr.loadDir(file.path(Sys.getenv("JAM_HOME"), "stat", "TCGA"))
        mafRaw <- MAF.loadRaw()
    }

    backupFrame <-
        data.frame(Hugo_Symbol = mafRaw$Hugo_Symbol,
                   Gene_ID     = mafRaw$Gene)

    backupFrame <-
        backupFrame[!duplicated(backupFrame[,c("Hugo_Symbol", "Gene_ID")]),]

    backupFrame <-
        backupFrame[order(backupFrame$Hugo_Symbol),]

    backupFrame
}

## ---------------------------------------------------------------------

Hugo.buildMaster <- function(rawFrame = NULL, backupFrame = NULL) {
    if (is.null(rawFrame))
        master <- Hugo.loadRaw()
    else
        master <- rawFrame

    if (is.null(backupFrame))
        backupFrame <- Hugo.buildBackup()

    colnames(master) <-
        c("HGNC", "Hugo_Symbol", "Name", "Aliases", "Gene_ID")

    withdrawn      <- grep("symbol withdrawn", master$Name)
    antisenseRNA   <- grep("antisense RNA", master$Name)
    missingEnsembl <- which(is.na(master$Gene_ID) | nchar(master$Gene_ID) == 0)

    removeRows <- c(withdrawn, antisenseRNA, missingEnsembl)

    master <- master[-removeRows,]
    master <- master[,c("Hugo_Symbol", "Gene_ID")]
    master <- rbind(master, backupFrame)
    master <- master[order(master$Hugo_Symbol, master$Gene_ID),]
    master <- master[!duplicated(master[,c("Hugo_Symbol", "Gene_ID")]),]

    rownames(master) <- NULL
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
