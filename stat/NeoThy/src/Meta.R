
Meta.load <- function() {
    vanAllen <- VanAllen.merge()
    hugo     <- Hugo.load()
    riaz     <- Riaz.load()

    z.score <- function(x) {
        (x - mean(x)) / sd(x)
    }

    recist <- data.frame(RECIST = c("CR", "PR", "SD", "PD"),
                         R_NR   = c( "R",  "R",  "R", "NR"))

    vanAllen <- merge(vanAllen, recist, by = "RECIST")
    vanAllen$presentZ <- z.score(vanAllen$presentPct)
    vanAllen$survivalZ <- z.score(log(vanAllen$overall_survival))
    vanAllen <- vanAllen[,c("presentZ", "survivalZ",  "R_NR")]

    hugo <- Hugo.load()
    hugo$R_NR <- hugo$Response
    hugo$presentZ <- z.score(hugo$PresentRate)
    hugo$survivalZ <- NA
    hugo <- hugo[,c("presentZ", "survivalZ",  "R_NR")]

    riaz <- Riaz.load()
    riaz$presentZ <- z.score(riaz$NeoPepRatio)
    riaz$survivalZ <- z.score(log(riaz$TimeToDeathWeeks))
    riaz <- riaz[,c("presentZ", "survivalZ",  "R_NR")]

    vanAllen$Author <- "VanAllen"
    hugo$Author <- "Hugo"
    riaz$Author <- "Riaz"

    master <- do.call(rbind, list(vanAllen, hugo, riaz))
    master
}

Meta.scatterSurvival <- function() {
    master <- Meta.load()

    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))

    plot(master$presentZ,
         master$survivalZ,
         col = ifelse(master$Author == "VanAllen", 1, 2),
         pch = ifelse(master$Author == "VanAllen", 16, 16),
         xlim = c(-3, 3),
         ylim = c(-4, 3),
         xlab = "Presentation score",
         ylab = "Survival score")

    print(cor(master$presentZ, master$survivalZ, use = "c", method = "s"))

    legend("topleft", bty = "n",
           legend = c("Van Allen", "Riaz"),
           col = c(1, 2), pch = c(16, 16))

    print(summary(lm(survivalZ ~ presentZ, data = master)))
}

Meta.violinResponse <- function() {
    master <- Meta.load()

    kR  <- which(master$R_NR == "R")
    kNR <- which(master$R_NR == "NR")

    xR  <- master$presentZ[kR]
    xNR <- master$presentZ[kNR]

    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))

    vioplot(xR, xNR, names = c("R", "NR"), col = "cadetblue3", ylim = c(-4.0, 4.2))
    print(summary(xR))
    print(summary(xNR))
    print(wilcox.test(xR, xNR))
}
