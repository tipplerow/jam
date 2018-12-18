
Hugo.load <- function() {
    hugo <- read.csv("Hugo_S1B.csv")

    hugo$ResponseCode <-
        ifelse(hugo$Response == "NR", 0, 1)

    hugo$Binder_Frac        <- hugo$Binder       / hugo$TotalNonSyn
    hugo$Strong_Binder_Frac <- hugo$StrongBinder / hugo$TotalNonSyn

    hugo$PresentRate <- 50 * (hugo$Binder_Frac + hugo$Strong_Binder_Frac)

    hugo <- subset(hugo, is.finite(PresentRate))
    hugo <- hugo[order(hugo$PresentRate),]
    hugo
}

Hugo.barplot <- function() {
    hugo <- Hugo.load()

    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))

    barplot(hugo$PresentRate,
            ylab = "HLA presentation rate [%]",
            ylim = c(-2, 62),
            col  = ifelse(hugo$Response == "NR", 2, 3),
            density = 40)
    box()
    legend("topleft", bty = "n",
           legend = c("Responders", "Non-responders"),
           col = c(3, 2), pch = c(15, 15), cex = 0.9)
}

Hugo.vioplot <- function() {
    hugo <- Hugo.load()

    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))

    xR  <- hugo$PresentRate[which(hugo$Response == "R")]
    xNR <- hugo$PresentRate[which(hugo$Response == "NR")]

    vioplot(xR, xNR, names = c("R", "NR"), col = "cadetblue3", ylim = c(0, 60))
    text(x = 0, y = 0.5, labels = "HLA presentation rate [%]", srt = 90)
}




