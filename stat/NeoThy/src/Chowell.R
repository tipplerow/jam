
Chowell.zscore <- function(x) {
    (x - mean(x, na.rm = TRUE)) / sd(x, na.rm = TRUE)
}

Chowell.mergePresentRate <- function(master) {
}

Chowell.load1 <- function() {
    cohort1 <- read.table("Chowell_Cohort1.txt", sep = "\t", header = TRUE)
    cohort1 <- subset(cohort1, is.finite(OS_Months) & is.finite(Homozygous))

    cohort1$zOS  <- Chowell.zscore(log(cohort1$OS_Months))
    cohort1$zTMB <- Chowell.zscore(log(cohort1$MutCnt))

    cohort1$TMB_High <- as.numeric(cohort1$MutCnt > median(cohort1$MutCnt, na.rm = TRUE))
    cohort1
}

Chowell.load2 <- function() {
    cohort2 <- read.table("Chowell_Cohort2.txt", sep = "\t", header = TRUE)
    cohort2 <- subset(cohort2, is.finite(OS_Months) & is.finite(IMPACT_MutCnt) & is.finite(Homozygous))

    cohort2$zOS  <- Chowell.zscore(log(1 + cohort2$OS_Months))
    cohort2$zTMB <- Chowell.zscore(log(1 + cohort2$IMPACT_MutCnt))

    cohort2$HighLoad <- as.numeric(cohort2$IMPACT_MutCnt > 16.72)
    cohort2
}

Chowell.plotKR <- function() {
    require(survival)
    require(survminer)

    cohort1 <- read.table("Chowell_Cohort1.txt", sep = "\t", header = TRUE)
    cohort1 <- subset(cohort1, is.finite(OS_Months) & is.finite(Homozygous))

    cohort1$MutZyg <-
        ifelse(cohort1$MutCnt > 113 & cohort1$Homozygous > 0.5, "High_Homo",
        ifelse(cohort1$MutCnt < 113 & cohort1$Homozygous < 0.5, "Low_Hetero", NA))
    cohort1 <- subset(cohort1, !is.na(MutZyg))

    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))
    fit <- survfit(Surv(OS_Months, OS_Event) ~ MutZyg, data = cohort1)
    ggsurvplot(fit, data = cohort1, risk.table = TRUE, pval = TRUE)
    print(coxph(Surv(OS_Months, OS_Event) ~ MutZyg, data = cohort1))

    plot(fit,
         col  = c(3, 4),
         lwd  = 3,
         xlim = c(-2, 82),
         xlab = "Months",
         ylab = "Survival probability")

    legend("topright", bty = "n",
           legend = c("High TMB, Homozygous", "Low TMB, Heterozygous"),
           col = c(3, 4),
           lty = c(1, 1),
           lwd = c(3, 3))
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

Miao.vioplotBenefit <- function(master, colName, logy = FALSE, ...) {
    require(vioplot)

    kCR <- which(master$RECIST == "CR")
    kPR <- which(master$RECIST == "PR")
    kSD <- which(master$RECIST == "SD")
    kPD <- which(master$RECIST == "PD")

    yCR <- master[kCR, colName]
    yPR <- master[kPR, colName]
    ySD <- master[kSD, colName]
    yPD <- master[kPD, colName]

    if (logy) {
        yCR <- log10(yCR)
        yPR <- log10(yPR)
        ySD <- log10(ySD)
        yPD <- log10(yPD)
    }

    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))

    vioplot(c(yCR, yPR), yPD,
            col   = "cadetblue3",
            names = c("CR/PR", "PD"),
            ...)

    wilcox.test(c(yCR, yPR), yPD)
}

Miao.vioplot1 <- function(master, colName, ylim) {
    require(vioplot)

    kCR <- which(master$RECIST == "CR")
    kPR <- which(master$RECIST == "PR")
    kSD <- which(master$RECIST == "SD")
    kPD <- which(master$RECIST == "PD")

    xCR <- master[kCR, colName]
    xPR <- master[kPR, colName]
    xSD <- master[kSD, colName]
    xPD <- master[kPD, colName]

    par(las = 1)
    par(fig = c(0, 1, 0.1, 0.9))

    vioplot(xCR, xPR, xSD, xPD, ylim = ylim,
            names = c("CR", "PR", "SD", "PD"), col = "cadetblue3")
}

Miao.aggregateByAllele <- function(neoAg) {
    aggFunc <- function(slice) {
        data.frame(HLA   = slice$HLA[1],
                   total = nrow(slice),
                   nm50  = mean(slice$affinity_mut <  50),
                   nm100 = mean(slice$affinity_mut < 100),
                   nm250 = mean(slice$affinity_mut < 250),
                   nm500 = mean(slice$affinity_mut < 500))
    }
    
    result <- do.call(rbind, by(neoAg, neoAg$HLA, aggFunc))
    result
}

Miao.aggregateByCancerType <- function(master) {
    aggFunc <- function(slice) {
        slice$zOS  <- Miao.zscore(log(slice$os_days))
        slice$zPFS <- Miao.zscore(log(slice$pfs_days))

        slice$zMissense  <- Miao.zscore(log(slice$missenseCount))
        slice$zNonSilent <- Miao.zscore(log(slice$nonSilentCount))

        slice$zTotalNeo <- Miao.zscore(log(slice$totalNeo))
        slice$zCount50  <- Miao.zscore(log(slice$count50))
        slice$zCount100 <- Miao.zscore(log(slice$count100))
        slice$zCount250 <- Miao.zscore(log(slice$count250))
        slice$zCount500 <- Miao.zscore(log(slice$count500))

        slice$zFrac50  <- Miao.zscore(log(slice$count50  / slice$nonSilentCount))
        slice$zFrac100 <- Miao.zscore(log(slice$count100 / slice$nonSilentCount))
        slice$zFrac250 <- Miao.zscore(log(slice$count250 / slice$nonSilentCount))
        slice$zFrac500 <- Miao.zscore(log(slice$count500 / slice$nonSilentCount))

        slice
    }
    
    result <- do.call(rbind, by(master, master$cancer_type, aggFunc))
    rownames(result) <- NULL

    result
}

Miao.aggregateMutByTumor <- function(mutDetail) {
    aggFunc <- function(slice) {
        data.frame(pair_id        = slice$pair_id[1],
                   missenseCount  = sum(slice$Variant_Classification == "Missense_Mutation"),
                   nonSilentCount = sum(slice$Variant_Classification %in%
                                        c("Nonstop_Mutation",
                                          "In_Frame_Ins",
                                          "In_Frame_Del",
                                          "Frame_Shift_Ins",
                                          "Frame_Shift_Del",
                                          "Missense_Mutation")))
    }
    
    result <- do.call(rbind, by(mutDetail, mutDetail$pair_id, aggFunc))
    rownames(result) <- NULL

    result
}

Miao.aggregateNeoByTumor <- function(neoAg) {
    aggFunc <- function(slice) {
        ##
        ## Multiple alleles may present the same peptide, so order
        ## with strongest binders first and then eliminate
        ## duplicates...
        ##
        slice <- slice[order(slice$affinity_mut),]
        slice <- slice[!duplicated(slice$pep_mut),]

        data.frame(Tumor_Sample_Barcode = slice$Tumor_Sample_Barcode[1],
                   totalNeo = nrow(slice),
                   count50  = sum(slice$affinity_mut <  50),
                   count100 = sum(slice$affinity_mut < 100),
                   count250 = sum(slice$affinity_mut < 250),
                   count500 = sum(slice$affinity_mut < 500))
    }
    
    result <- do.call(rbind, by(neoAg, neoAg$Tumor_Sample_Barcode, aggFunc))
    rownames(result) <- NULL

    result
}

Miao.decile <- function(x) {
    ceiling(10 * rank(x, na.last = "keep") / sum(!is.na(x)))
}

Miao.quintile <- function(x) {
    ceiling(5 * rank(x, na.last = "keep") / sum(!is.na(x)))
}

Miao.zscore <- function(x) {
    (x - mean(x, na.rm = TRUE)) / sd(x, na.rm = TRUE)
}
