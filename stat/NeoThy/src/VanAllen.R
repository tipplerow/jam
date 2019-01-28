
VanAllen.colorMap <- function() {
    data.frame(RECIST = c("CR",    "PR",     "SD",  "PD",    "X"),
               col    = c("green", "yellow", "red", "black", "cyan"),
               pch15  = c(15,      15,       15,    15,      4),
               pch16  = c(16,      16,       16,    16,      4))
}

VanAllen.merge <- function() {
    sette <- read.csv("Sette_Table5.csv", comment.char = "#")

    kosmrlj <- read.csv("Kosmrlj_TableS1.csv", comment.char = "#")
    kosmrlj$repertoireSizePct <- 100.0 * kosmrlj$bindingFraction
    
    alleles <- merge(sette, kosmrlj, by = "allele", all = TRUE, suffixes = c(".sette", ".kosmrlj"))
    alleles$repertoireSizePct <- NA

    for (k in 1:nrow(alleles))
        alleles$repertoireSizePct[k] <-
            mean(c(alleles$repertoireSizePct.sette[k],
                   alleles$repertoireSizePct.kosmrlj[k]), na.rm = TRUE)

    alleles <-
        data.frame(allele = alleles$allele,
                   repertoireSizePct = alleles$repertoireSizePct)

    vanAllen <- read.csv("VanAllen_TableS2_Revised.csv")
    vanAllenCols <- colnames(vanAllen)

    vanAllen <- merge(vanAllen, alleles, by.x = "hla.a1", by.y = "allele")

    vanAllen$hla.a1.present    <- vanAllen$repertoireSizePct
    vanAllen$repertoireSizePct <- NULL
    
    vanAllen <- merge(vanAllen, alleles, by.x = "hla.a2", by.y = "allele")

    vanAllen$hla.a2.present    <- ifelse(vanAllen$hla.a1 != vanAllen$hla.a2, vanAllen$repertoireSizePct, 0.0)
    vanAllen$repertoireSizePct <- NULL
    
    vanAllen <- merge(vanAllen, alleles, by.x = "hla.b1", by.y = "allele")

    vanAllen$hla.b1.present    <- vanAllen$repertoireSizePct
    vanAllen$repertoireSizePct <- NULL
    
    vanAllen <- merge(vanAllen, alleles, by.x = "hla.b2", by.y = "allele")

    vanAllen$hla.b2.present    <- ifelse(vanAllen$hla.b1 != vanAllen$hla.b2, vanAllen$repertoireSizePct, 0.0)
    vanAllen$repertoireSizePct <- NULL
    
    vanAllen <- vanAllen[, c(vanAllenCols,
                             "hla.a1.present", 
                             "hla.a2.present", 
                             "hla.b1.present", 
                             "hla.b2.present")]

    vanAllen$presentPct <-
        vanAllen$hla.a1.present +
        vanAllen$hla.a2.present +
        vanAllen$hla.b1.present +
        vanAllen$hla.b2.present

    vanAllen <- merge(vanAllen, VanAllen.colorMap(), by = "RECIST")
    vanAllen <- vanAllen[order(vanAllen$presentPct),]
    vanAllen
}

VanAllen.barRECIST <- function(vanAllen) {
    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))

    colors   <- VanAllen.colorMap()
    colors   <- subset(colors, RECIST != "X")
    vanAllen <- subset(vanAllen, RECIST != "X")

    barplot(vanAllen$presentPct,
            ylab = "HLA presentation rate [%]",
            ylim = c(-0.5, 20.5),
            col  = vanAllen$col,
            density = 40)
    box()

    legend("topleft",
           bty = "n",
           legend = colors$RECIST,
           col    = colors$col,
           pch    = 15)
}

VanAllen.plotSurvival <- function(vanAllen) {
    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))

    plot(vanAllen$presentPct,
         vanAllen$overall_survival,
         log = "y",
         pch = 16,
         xlab = "HLA presentation rate [%]",
         xlim = c(0, 20),
         ylab = "Days",
         ylim = c(10, 2200))
    points(vanAllen$presentPct, vanAllen$progression_free, pch = 1)

    for (k in 1:nrow(vanAllen))
        lines(c(vanAllen$presentPct[k],
                vanAllen$presentPct[k]),
              c(vanAllen$progression_free[k],
                vanAllen$overall_survival[k]))

    legend("topleft", bty = "n",
           legend = c("Progression-free", "Overall survival"), col = c(1, 1), pch = c(1, 16), cex = 0.9)
}

VanAllen.correlation <- function(vanAllen) {
    colkeys <- c("progression_free", "overall_survival", "mutations", "neos500", "presentPct")

    corrP <- cor(vanAllen[,colkeys], method = "p")
    corrS <- cor(vanAllen[,colkeys], method = "s")

    cormat <- corrP

    for (ii in 2:5)
        for (jj in 1:(ii - 1))
            cormat[ii,jj] <- corrS[ii,jj]

    cormat
}

VanAllen.writeGenotype <- function(master, genoFile) {
    lines <- character(nrow(master) + 1)
    lines[1] <- c("patient,genotype")

    for (k in 1:nrow(master))
        lines[k + 1] <-
            sprintf("%s,HLA-A*%s HLA-A*%s HLA-B*%s HLA-B*%s HLA-C*%s HLA-C*%s",
                    x$patient[k],
                    substr(x$hla.a1[k], 6, 10),
                    substr(x$hla.a2[k], 6, 10),
                    substr(x$hla.b1[k], 6, 10),
                    substr(x$hla.b2[k], 6, 10),
                    substr(x$hla.c1[k], 6, 10),
                    substr(x$hla.c2[k], 6, 10))
    
    writeLines(lines, genoFile)
}



