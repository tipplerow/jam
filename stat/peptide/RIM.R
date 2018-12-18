
RIM.findMJ <- function(fileName) {
    file.path(Sys.getenv("JAM_HOME", names = FALSE), "data", "MJ_upper.csv")
}

RIM.loadMJ <- function(fileName) {
    RIM.loadSparse(RIM.findMJ())
}

RIM.loadSparse <- function(fileName) {
    master <- ResidueMaster.load()
    rowmap <- data.frame(V1 = master$code3, rowkey = master$code1)
    colmap <- data.frame(V2 = master$code3, colkey = master$code1)

    dframe <- read.table(fileName, header = FALSE, sep = ",", strip.white = TRUE, blank.lines.skip = TRUE)
    dframe <- merge(dframe, rowmap, by = "V1")
    dframe <- merge(dframe, colmap, by = "V2")

    rowkeys <- sort(unique(dframe$rowkey))
    colkeys <- sort(unique(dframe$colkey))

    stopifnot(identical(rowkeys, colkeys))

    dense <- matrix(nrow = length(rowkeys),
                    ncol = length(colkeys),
                    dimnames = list(rowkeys, colkeys))

    for (k in 1:nrow(dframe)) {
        dense[dframe$rowkey[k], dframe$colkey[k]] <- dframe$V3[k]
        dense[dframe$colkey[k], dframe$rowkey[k]] <- dframe$V3[k]
    }

    dense
}

RIM.barMean <- function(rim) {
    master <- ResidueMaster.load()
    colors <- data.frame(family = c("NONPOLAR", "UNCHARGED_POLAR", "ACIDIC", "BASIC"), color = 1:4)
    master <- merge(master, colors, by = "family")
    
    means <- rowMeans(rim)
    means <- data.frame(code1 = names(means), meanE = means)
    means <- merge(means, master, by = "code1")
    means <- means[order(-means$meanE),]

    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))
    barplot(height    = abs(means$meanE),
            names.arg = means$code1,
            cex.names = 0.9,
            density   = 40,
            col       = means$color,
            ylab      = "Mean absolute free energy [kT]",
            ylim      = c(-0.1, 5.2))
    box()
    legend("topleft", bty = "n",
           legend = c("Nonpolar", "Uncharged polar", "Acidic", "Basic"),
           col = colors$color, pch = 15, cex = 0.9)
}
