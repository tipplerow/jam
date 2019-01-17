
Chowell.zscore <- function(x) {
    (x - mean(x, na.rm = TRUE)) / sd(x, na.rm = TRUE)
}

Chowell.load1 <- function(dirName = "../data") {
    cohort1 <- read.table(file.path(dirName, "Chowell_Cohort1.txt"), sep = "\t", header = TRUE)
    cohort1 <- subset(cohort1, is.finite(OS_Months) & is.finite(Homozygous))

    cohort1$zOS  <- Chowell.zscore(log(cohort1$OS_Months))
    cohort1$zTMB <- Chowell.zscore(log(cohort1$MutCnt))

    cohort1$LowTMB <- as.numeric(cohort1$MutCnt < median(cohort1$MutCnt, na.rm = TRUE))
    cohort1
}

Chowell.load2 <- function(dirName = "../data") {
    cohort2 <- read.table(file.path(dirName, "Chowell_Cohort2.txt"), sep = "\t", header = TRUE)
    cohort2 <- subset(cohort2, is.finite(OS_Months) & is.finite(IMPACT_MutCnt) & is.finite(Homozygous))

    cohort2$zOS  <- Chowell.zscore(log(1 + cohort2$OS_Months))
    cohort2$zTMB <- Chowell.zscore(log(1 + cohort2$IMPACT_MutCnt))

    cohort2$LowTMB <- as.numeric(cohort2$IMPACT_MutCnt < median(cohort2$IMPACT_MutCnt, na.rm = TRUE))
    cohort2
}

Chowell.loadJoin <- function(dirName = "../data") {
    chow1 <- Chowell.load1(dirName)
    chow2 <- Chowell.load2(dirName)

    chow1 <- Chowell.expandHLA(chow1)
    chow2 <- Chowell.expandHLA(chow2)

    chow1 <- HLA.mergeRepertoire(chow1, HLA.master())
    chow2 <- HLA.mergeRepertoire(chow2, HLA.master())

    chow1 <- subset(chow1, hla.a1.repertoire > 0 &
                           hla.a2.repertoire > 0 &
                           hla.b1.repertoire > 0 &
                           hla.b2.repertoire > 0)

    chow2 <- subset(chow2, hla.a1.repertoire > 0 &
                           hla.a2.repertoire > 0 &
                           hla.b1.repertoire > 0 &
                           hla.b2.repertoire > 0)

    chow1$zOS  <- Chowell.zscore(log(chow1$OS_Months))
    chow1$zHLA <- Chowell.zscore(log(chow1$repertoireSize))
    chow1$zTMB <- Chowell.zscore(log(chow1$MutCnt))

    chow2$zOS  <- Chowell.zscore(log(1 + chow2$OS_Months))
    chow2$zHLA <- Chowell.zscore(log(chow2$repertoireSize))
    chow2$zTMB <- Chowell.zscore(log(1 + chow2$IMPACT_MutCnt))

    colnames <- c("OS_Months", "zOS", "OS_Event", "zHLA", "zTMB")

    chow1  <- chow1[,colnames]
    chow2  <- chow2[,colnames]
    joined <- rbind(chow1, chow2)

    list(chow1 = chow1, chow2 = chow2, joined = joined)
}

Chowell.expandHLA <- function(master) {
    master$hla.list <- strsplit(master$HLA_Class_I_Alleles, ",")

    master$hla.a1 <- lapply(master$hla.list, function(x) x[[1]])
    master$hla.a2 <- lapply(master$hla.list, function(x) x[[2]])
    master$hla.b1 <- lapply(master$hla.list, function(x) x[[3]])
    master$hla.b2 <- lapply(master$hla.list, function(x) x[[4]])
    master$hla.c1 <- lapply(master$hla.list, function(x) x[[5]])
    master$hla.c2 <- lapply(master$hla.list, function(x) x[[6]])

    hlaNames <- sprintf("hla.%s", c("a1", "a2", "b1", "b2", "c1", "c2"))

    for (hlaName in hlaNames)
        master[,hlaName] <-
            sprintf("HLA-%s:%s",
                    substr(master[,hlaName], 1, 3),
                    substr(master[,hlaName], 4, 5))

    master
}

Chowell.plotKR1 <- function() {
    require(survival)

    cohort1 <- Chowell.load1()

    xwd <- 0.57
    yht <- 0.55
    cex <- 0.7

    par(las = 1)
    par(fig = c(0, xwd, 0.5 - 0.5 * yht, 0.5 + 0.5 * yht))
    plot(survfit(Surv(OS_Months, OS_Event) ~ Homozygous, data = cohort1),
         col = 3:4,
         lwd = 2,
         xlim = c(-3, 83),
         xlab = "Months",
         ylab = "Survival probability",
         cex.lab  = cex,
         cex.axis = cex)
    legend("topright", bty = "n", col = 3:4, lwd = c(2, 2), lty = c(1, 1), cex = cex,
           legend = c("Homozygous", "Heterozygous"))
    legend(-2, 0.2, bty = "n", legend = "p = 0.036", cex = cex)

    par(fig = c(1 - xwd, 1, 0.5 - 0.5 * yht, 0.5 + 0.5 * yht), new = TRUE)
    plot(survfit(Surv(OS_Months, OS_Event) ~ LowTMB, data = cohort1),
         col = 3:4,
         lwd = 2,
         xlab = "Months",
         xlim = c(-3, 83),
         axes = FALSE,
         cex.lab  = cex,
         cex.axis = cex)
    axis(1, cex.axis = cex)
    axis(2, labels = FALSE)
    box()
    legend("topright", bty = "n", col = 3:4, lwd = c(2, 2), lty = c(1, 1), cex = cex,
           legend = c("High TMB", "Low TMB"))
    legend(-2, 0.2, bty = "n", legend = "p = 0.0013", cex = cex)
}

Chowell.plotKR2 <- function() {
    require(survival)

    cohort2 <- Chowell.load2()

    xwd <- 0.57
    yht <- 0.55
    cex <- 0.7

    par(las = 1)
    par(fig = c(0, xwd, 0.5 - 0.5 * yht, 0.5 + 0.5 * yht))
    plot(survfit(Surv(OS_Months, OS_Event) ~ Homozygous, data = cohort2),
         col = 3:4,
         lwd = 2,
         xlim = c(-3, 83),
         xlab = "Months",
         ylab = "Survival probability",
         cex.lab  = cex,
         cex.axis = cex)
    legend("topright", bty = "n", col = 3:4, lwd = c(2, 2), lty = c(1, 1), cex = cex,
           legend = c("Homozygous", "Heterozygous"))
    legend(-2, 0.2, bty = "n", legend = "p = 0.036", cex = cex)

    par(fig = c(1 - xwd, 1, 0.5 - 0.5 * yht, 0.5 + 0.5 * yht), new = TRUE)
    plot(survfit(Surv(OS_Months, OS_Event) ~ LowTMB, data = cohort2),
         col = 3:4,
         lwd = 2,
         xlab = "Months",
         xlim = c(-3, 83),
         axes = FALSE,
         cex.lab  = cex,
         cex.axis = cex)
    axis(1, cex.axis = cex)
    axis(2, labels = FALSE)
    box()
    legend("topright", bty = "n", col = 3:4, lwd = c(2, 2), lty = c(1, 1), cex = cex,
           legend = c("High TMB", "Low TMB"))
    legend(-2, 0.2, bty = "n", legend = "p = 0.0013", cex = cex)
}

Chowell.plotQQ <- function() {
    zQQ <- function(x, desc, xlab, ylab, pch = 1, cex = 0.80) {
        xy <- qqnorm(x, plot.it = FALSE)
        plot(xy$x,
             xy$y,
             cex  = cex,
             type = "p",
             xlab = "",
             ylab = "",
             xlim = c(-4, 4),
             ylim = c(-4, 4),
             axes = FALSE,
             cex.axis = cex)
        box()
        axis(1, labels = nchar(xlab) > 0, cex.axis = cex)
        axis(2, labels = nchar(ylab) > 0, cex.axis = cex)
        lines(c(-5, 5), c(-5, 5))
        legend(-4.7, 4.3, bty = "n", legend = desc, cex = cex)

        if (nchar(xlab) > 0)
            mtext(xlab, 1, line = 2.5, cex = cex)

        if (nchar(ylab) > 0)
            mtext(ylab, 2, line = 2, cex = cex, las = 0)
    }

    par(las = 1)
    xwd <- 0.57
    yht <- 0.61

    par(fig = c(0, xwd, 1 - yht, 1))
    zQQ(master$zOS, "Overall Survival (OS)", "", "Sample Quantiles")

    par(fig = c(1 - xwd, 1, 1 - yht, 1), new = TRUE)
    ##zQQ(master$zPFS, "Progression Free Survival (PFS)", "", "")
    zQQ(master$zTMB, "Tumor Mutational Burden (TMB)", "", "")

    par(fig = c(0, xwd, 0, yht), new = TRUE)
    zQQ(master$zNAB, "Neoantigen Load (NAL)", "Theoretical Quantiles", "Sample Quantiles")
    ##zQQ(master$zTMB, "Tumor Mutational Burden (TMB)", "Theoretical Quantiles", "Sample Quantiles")

    par(fig = c(1 - xwd, 1, 0, yht), new = TRUE)
    zQQ(master$zHLA, "HLA Presentation Rate (HLA)", "Theoretical Quantiles", "")
}

Chowell.writeGenotype <- function(dframe, fileName) {
    keys <- dframe$Sample
    alleles <- strsplit(dframe$HLA_Class_I_Alleles, ",")

    alleles <-
        lapply(alleles,
               function(x) sprintf("HLA-A*%s:%s HLA-A*%s:%s HLA-B*%s:%s HLA-B*%s:%s HLA-C*%s:%s HLA-C*%s:%s",
                                   substr(x[1], 2, 3), substr(x[1], 4, 5),
                                   substr(x[2], 2, 3), substr(x[2], 4, 5),
                                   substr(x[3], 2, 3), substr(x[3], 4, 5),
                                   substr(x[4], 2, 3), substr(x[4], 4, 5),
                                   substr(x[5], 2, 3), substr(x[5], 4, 5),
                                   substr(x[6], 2, 3), substr(x[6], 4, 5)))

    keys <- c("Sample", keys)
    alleles <- c("Alleles", as.character(alleles))

    writeLines(sprintf("%s,%s", keys, alleles), fileName)
}
