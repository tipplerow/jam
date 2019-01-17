
HLA.master <- function(dirName = "../data") {
    kosmr <- read.csv(file.path(dirName, "Kosmrlj_TableS1.csv"), comment.char = "#")
    sette <- read.csv(file.path(dirName, "Sette_Table5.csv"),    comment.char = "#")

    master <- merge(kosmr, sette, all = TRUE)
    master$kosmr.pct <- 100 * master$bindingFraction
    master$sette.pct <- master$repertoireSizePct

    master$meanPct <- apply(master[,c("kosmr.pct", "sette.pct")], 1, mean, na.rm = TRUE)
    master <- master[order(-master$meanPct),]
    master
}

HLA.mergeRepertoire <- function(target,
                                repertoire,
                                hlaNames = c("hla.a1", "hla.a2",
                                             "hla.b1", "hla.b2",
                                             "hla.c1", "hla.c2")) {

    ## Eliminate extraneous columns from the repertoire...
    repertoire <- repertoire[,c("allele", "meanPct")]

    ## Restore the original column order afterward...
    targetCols     <- colnames(target)
    repertoireCols <- sprintf("%s.repertoire", hlaNames)

    for (k in seq_along(hlaNames)) {
        hlaName <- hlaNames[k]
        repCol  <- repertoireCols[k]

        target <- merge(target, repertoire, by.x = hlaName, by.y = "allele", all.x = TRUE)

        target[,repCol] <- target$meanPct
        target <- target[,c(targetCols, repertoireCols[1:k])]
    }

    for (repertoireCol in repertoireCols)
        target[,repertoireCol] <-
            ifelse(is.na(target[,repertoireCol]), 0, target[,repertoireCol])

    hlaA1 <- hlaNames[1]
    hlaA2 <- hlaNames[2]
    hlaB1 <- hlaNames[3]
    hlaB2 <- hlaNames[4]
    hlaC1 <- hlaNames[5]
    hlaC2 <- hlaNames[6]

    repA1 <- repertoireCols[1]
    repA2 <- repertoireCols[2]
    repB1 <- repertoireCols[3]
    repB2 <- repertoireCols[4]
    repC1 <- repertoireCols[5]
    repC2 <- repertoireCols[6]

    target$repertoireSize <-
        target[,repA1] + target[,repB1] + target[,repC1]

    target$repertoireSize <-
        ifelse(target[,hlaA1] == target[,hlaA2], target$repertoireSize, target$repertoireSize + target[,repA2])

    target$repertoireSize <-
        ifelse(target[,hlaB1] == target[,hlaB2], target$repertoireSize, target$repertoireSize + target[,repB2])

    target$repertoireSize <-
        ifelse(target[,hlaC1] == target[,hlaC2], target$repertoireSize, target$repertoireSize + target[,repC2])

    target
}

HLA.barplotAllele <- function() {
    kosmr <- read.csv("Kosmrlj_TableS1.csv", comment.char = "#")
    sette <- read.csv("Sette_Table5.csv",    comment.char = "#")

    master <- merge(kosmr, sette, all = TRUE)
    master$kosmr.pct <- 100 * master$bindingFraction
    master$sette.pct <- master$repertoireSizePct

    master$meanPct <- apply(master[,c("kosmr.pct", "sette.pct")], 1, mean, na.rm = TRUE)
    master <- master[order(-master$meanPct),]

    par(las = 1)
    par(fig = c(0, 1, 0.15, 0.85))

    barplot(master$meanPct,
            las = 2,
            ylab = "Presentation rate [%]",
            ylim = c(-1, 21),
            names.arg = substr(master$allele, 5, 11),
            cex.names = 0.7)
}

HLA.histGenotype <- function(genoPct) {
    par(las = 1)
    par(fig = c(0, 1, 0.15, 0.85))

    truehist(genoPct,
             col  = "grey",
             xlim = c(0, 60),
             ylim = c(0, 0.06),
             xlab = "Presentation rate [%]",
             ylab = "Density")
}

HLA.sampleGenotype <- function(N = 100, homozygous = FALSE, dirName = "../data") {
    master <- HLA.master(dirName)

    masterA <- master[grep("HLA-A", master$allele),]
    masterB <- master[grep("HLA-B", master$allele),]
    masterC <- masterA ## We do not have an HLA-C subset, so reuse A...

    genoPct <- numeric(N)

    for (k in 1:N) {
        kA1 <- sample.int(nrow(masterA), 1)

        if (homozygous)
            kA2 <- kA1
        else
            kA2 <- sample.int(nrow(masterA), 1)

        kB1 <- sample.int(nrow(masterB), 1)
        kB2 <- sample.int(nrow(masterB), 1)

        kC1 <- sample.int(nrow(masterC), 1)
        kC2 <- sample.int(nrow(masterC), 1)

        pct <- masterA$meanPct[kA1] + masterB$meanPct[kB1] + masterC$meanPct[kC1]

        if (kA2 != kA1)
            pct <- pct + masterA$meanPct[kA2]

        if (kB2 != kB1)
            pct <- pct + masterB$meanPct[kB2]

        if (kC2 != kC1)
            pct <- pct + masterC$meanPct[kC2]

        genoPct[k] <- pct
    }

    genoPct
}

