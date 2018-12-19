
Target2.load <- function() {
    dframe <- read.csv("summary.csv")
}

Target2.plotRecogFrac <- function(dframe) {
    x <- dframe$presentationRate

    y1 <- dframe$sharedNeo.PR  / (1.0 - dframe$sharedNeo.XX)
    y2 <- dframe$medullaNeo.PR / (1.0 - dframe$medullaNeo.XX)
    y3 <- dframe$pathogen.PR   / (1.0 - dframe$pathogen.XX)

    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))

    plot(x, y1,
         log = "xy",
         axes = FALSE,
         type = "n",
         xlab = "HLA presentation rate",
         ylab = "Recognition/presentation ratio",
         xlim = c(0.01, 0.50),
         ylim = c(1E-6, 1E-1))

    axis(1, at = c(0.01, 0.02, 0.05, 0.1, 0.2, 0.5))
    .jamPlot.logAxis(2, -6:-1, TRUE, FALSE)
    box()

    points(x, y2, col = 2, pch = 1, cex = 0.8)
    points(x, y1, col = 1, pch = 0, cex = 0.8)

    legend("topright", bty = "n",
           legend = c("Shared", "Medulla-private"),
           col = c(1, 2), pch = c(0, 1), cex = 0.8)

    yy <- 0.5 * (y1 + y2)
    lmobj <- rlm(log(yy) ~ log(x))

    xx <- 10 ^ seq(-3, 0, 0.01)
    yy <- exp(lmobj$coeff[1]) * xx ^ lmobj$coeff[2]
    lines(xx, yy, lwd = 2, lty = 1)

    print(lmobj$coeff[1])
    print(lmobj$coeff[2])
    print(head(xx))
    print(head(yy))
    print(summary(lmobj))

    yy <- 0.5 * (y1 + y2)
    
}


Target2.plotPR <- function(dframe) {
    x <- dframe$presentationRate

    y1 <- dframe$sharedNeo.PR
    y2 <- dframe$medullaNeo.PR

    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))

    plot(x, y1,
         log = "xy",
         axes = FALSE,
         type = "n",
         xlab = "HLA presentation rate",
         ylab = "Immunogenic neo-peptide fraction ",
         xlim = c(0.01, 0.50),
         ylim = c(1E-6, 1E-3))

    axis(1, at = c(0.01, 0.02, 0.05, 0.1, 0.2, 0.5))
    .jamPlot.logAxis(2, -6:-1, TRUE, FALSE)
    box()

    points(x, y2, col = 2, pch = 1, cex = 0.8)
    points(x, y1, col = 1, pch = 0, cex = 0.8)

    legend("topleft", bty = "n",
           legend = c("Shared", "Medulla-private"),
           col = c(1, 2), pch = c(0, 1), cex = 0.8)


    yy <- 0.5 * (y1 + y2)

    print(cor(x, yy, method = "p"))
    print(cor(x, yy, method = "s"))
}
