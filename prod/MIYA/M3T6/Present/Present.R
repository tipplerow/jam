
Present.load <- function() {
    dframe <- read.csv("presentation-detail-report.csv")
    dframe <- subset(dframe, presentationRate > 0.001)
    dframe
}

Present.hist1 <- function(dframe) {
    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))

    truehist(dframe$presentationRate,
             h = 0.0125,
             prob = FALSE,
             xlab = "Presentation rate",
             ylab = "Allele count",
             xlim = c(0.0, 0.5))
    box()
}

Present.hist2 <- function(dframe) {
    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))

    truehist(log10(dframe$presentationRate),
             h = 0.1,
             prob = FALSE,
             xlab = expression(log[10]("Presentation rate")),
             ylab = "Allele count",
             xlim = c(-3, 0),
             ylim = c(0, 250))
    box()
}
