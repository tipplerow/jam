
HLA.computeDiversity <- function(hla) {
    nA <- 2 - hla$HomoA
    nB <- 2 - hla$HomoB
    nC <- 2 - hla$HomoC
    N  <- nA + nB + nC

    herf <- (hla$normA * hla$normA / nA +
             hla$normB * hla$normB / nB +
             hla$normC * hla$normC / nC)

    diversity <- (1.0 - herf) / (1.0 - 1.0 / N)
    diversity
}

HLA.normalize <- function(rna) {
    hla <- rna[,c("HLA-A", "HLA-B", "HLA-C")]
    hla <- cbind(hla, HLA = rowSums(hla))
    hla <- cbind(hla, normA = hla[,1] / hla[,4])
    hla <- cbind(hla, normB = hla[,2] / hla[,4])
    hla <- cbind(hla, normC = hla[,3] / hla[,4])
    hla <- as.data.frame(hla)

    hla$Tumor_Barcode <- rownames(hla)
    hla
}

HLA.hist <- function(hla) {
    XWD <- 0.44
    YHT <- 0.46
    cex <- 0.80

    par(las = 1)

    plotHist <- function(x, xlab) {
        truehist(x, xlab = xlab, xlim = c(0.0, 0.8), ylim = c(0.0, 6.5), axes = FALSE, cex = cex, cex.axis = cex)
        axis(1, cex.axis = cex)
        box()
    }

    par(fig = c(0.0, XWD, 0.5 * (1.0 - YHT), 0.5 * (1.0 + YHT)))
    plotHist(hla$normA, "HLA-A")

    par(fig = c(0.5 * (1.0 - XWD), 0.5 * (1.0 + XWD), 0.5 * (1.0 - YHT), 0.5 * (1.0 + YHT)), new = TRUE)
    plotHist(hla$normB, "HLA-B")

    par(fig = c(1.0 - XWD, 1.0, 0.5 * (1.0 - YHT), 0.5 * (1.0 + YHT)), new = TRUE)
    plotHist(hla$normC, "HLA-C")
}

HLA.mergeGeno <- function(hla) {
    hla <- merge(hla, ImmuneLandscape.loadGenotypeDetail())
    hla
}


