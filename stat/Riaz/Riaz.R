
Riaz.colorMap <- function() {
    data.frame(Response = c("CR",    "PR",     "SD",  "PD",    "NE"),
               col      = c("green", "yellow", "red", "black", "cyan"),
               pch15    = c(15,      15,       15,    15,      4),
               pch16    = c(16,      16,       16,    16,      4))
}

Riaz.load <- function() {
    riaz <- read.csv("Riaz_S2.csv")

    riaz$NeoPepRatio <-
        riaz$NeoPeptideLoad / riaz$MutationLoad

    riaz <- subset(riaz, is.finite(NeoPepRatio))
    riaz <- merge(riaz, Riaz.colorMap(), by = "Response")
    riaz <- riaz[order(riaz$NeoPepRatio),]

    riaz$R_NR <- ifelse(riaz$Response == "PD", "NR",
                        ifelse(riaz$Response == "NE", NA, "R"))
    riaz
}

Riaz.barResponse <- function(riaz) {
    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))

    colors <- Riaz.colorMap()
    colors <- subset(colors, Response != "NE")
    riaz   <- subset(riaz, Response != "NE")

    barplot(riaz$NeoPepRatio,
            ylab = "Neopeptide ratio",
            ylim = c(-0.1, 4.1),
            col  = riaz$col,
            density = 40)
    box()

    legend("topleft",
           bty = "n",
           legend = colors$Response,
           col    = colors$col,
           pch    = 15)
}

Riaz.plotTimeToDeath <- function(riaz) {
    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))

    plot(riaz$NeoPepRatio,
         riaz$TimeToDeathWeeks,
         log = "y",
         pch = 16,
         xlab = "Neopeptide ratio",
         xlim = c(0, 4),
         ylab = "Time to death [weeks]",
         ylim = c(1, 200))
}

Riaz.correlation <- function(riaz) {
    colkeys <- c("progression_free", "overall_survival", "mutations", "neos500", "NeoPepRatio")

    corrP <- cor(riaz[,colkeys], method = "p")
    corrS <- cor(riaz[,colkeys], method = "s")

    cormat <- corrP

    for (ii in 2:5)
        for (jj in 1:(ii - 1))
            cormat[ii,jj] <- corrS[ii,jj]

    cormat
}

