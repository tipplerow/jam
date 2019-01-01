
HLA.merge <- function() {
    kosmr <- read.csv("Kosmrlj_TableS1.csv", comment.char = "#")
    sette <- read.csv("Sette_Table5.csv",    comment.char = "#")

    master <- merge(kosmr, sette, all = TRUE)
    master$kosmr.pct <- 100 * master$bindingFraction
    master$sette.pct <- master$repertoireSizePct

    master$meanPct <- apply(master[,c("kosmr.pct", "sette.pct")], 1, mean, na.rm = TRUE)
    master <- master[order(-master$meanPct),]
    master
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

HLA.sampleGenotype <- function(N = 100) {
    master <- HLA.merge()

    masterA <- master[grep("HLA-A", master$allele),]
    masterB <- master[grep("HLA-B", master$allele),]
    masterC <- masterA ## We do not have an HLA-C subset, so reuse A...

    genoPct <- numeric(N)

    for (k in 1:N) {
        kA1 <- sample.int(nrow(masterA), 1)
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

