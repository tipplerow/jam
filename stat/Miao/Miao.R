
Miao.loadGlobal <- function() {
    master   <- read.csv("Miao_SupTable2.csv")
    keyFrame <- master[,c("pair_id", "Tumor_Sample_Barcode")]

    mutDetail  <- read.table("Miao_SupTable5.tsv", header = TRUE, sep = "\t")
    mutDetail  <- subset(mutDetail, nchar(Variant_Classification) < 20)
    mutByTumor <- Miao.aggregateMutByTumor(mutDetail)
    mutByTumor <- merge(keyFrame, mutByTumor)
    
    neoDetail  <- read.table("Miao_SupTable10.tsv", header = TRUE, sep = "\t")
    neoDetail  <- neoDetail[!duplicated(neoDetail[,c("Tumor_Sample_Barcode", "HLA", "pep_mut")]),]
    neoByTumor <- Miao.aggregateNeoByTumor(neoDetail)
    neoByTumor <- merge(keyFrame, neoByTumor)

    master <- merge(master, mutByTumor, all = TRUE)
    master <- merge(master, neoByTumor, all = TRUE)
    master <- subset(master, is.finite(nonSilentCount) & is.finite(count500))

    master$zOS  <- Miao.zscore(log(master$os_days))
    master$zPFS <- Miao.zscore(log(master$pfs_days))

    master$zTMB <- Miao.zscore(log(master$nonSilentCount))
    master$zNAB <- Miao.zscore(log(master$count500))
    master$zHLA <- Miao.zscore(log(master$count500 / master$nonSilentCount))

    master$lowTMB <- as.numeric(master$zTMB < median(master$zTMB, na.rm = TRUE))
    master$lowNAB <- as.numeric(master$zNAB < median(master$zNAB, na.rm = TRUE))
    master$lowHLA <- as.numeric(master$zHLA < median(master$zHLA, na.rm = TRUE))

    master
}

Miao.plotKR <- function(master) {
    require(survival)

    xwd <- 0.435
    yht <- 0.45
    cex <- 0.7

    par(las = 1)
    par(fig = c(0, xwd, 0.5 - 0.5 * yht, 0.5 + 0.5 * yht))
    plot(survfit(Surv(os_days, os_censor) ~ lowTMB, data = master),
         col = 3:4,
         lwd = 2,
         xlim = c(-50, 2050),
         ylab = "Survival probability",
         cex.lab  = cex,
         cex.axis = 0.7)
    legend("topright", bty = "n", col = 3:4, lwd = c(2, 2), lty = c(1, 1), cex = cex,
           legend = c("High TMB", "Low TMB"))
    legend(-200, 0.2, bty = "n", legend = "p = 0.00013", cex = cex)
    
    par(fig = c(0.5 - 0.5 * xwd, 0.5 + 0.5 * xwd, 0.5 - 0.5 * yht, 0.5 + 0.5 * yht), new = TRUE)
    plot(survfit(Surv(os_days, os_censor) ~ lowNAB, data = master),
         col = 3:4,
         lwd = 2,
         xlim = c(-50, 2050),
         axes = FALSE,
         cex.lab  = cex,
         cex.axis = cex)
    axis(1, cex.axis = cex)
    axis(2, labels = FALSE)
    box()
    legend("topright", bty = "n", col = 3:4, lwd = c(2, 2), lty = c(1, 1), cex = cex,
           legend = c("High NAL", "Low NAL"))
    legend(-200, 0.2, bty = "n", legend = "p = 0.011", cex = cex)
    
    
    par(fig = c(1.0 - xwd, 1.0, 0.5 - 0.5 * yht, 0.5 + 0.5 * yht), new = TRUE)
    plot(survfit(Surv(os_days, os_censor) ~ lowHLA, data = master),
         col = 3:4,
         lwd = 2,
         xlim = c(-50, 2050),
         axes = FALSE,
         cex.lab  = cex,
         cex.axis = cex)
    axis(1, cex.axis = cex)
    axis(2, labels = FALSE)
    box()
    legend("topright", bty = "n", col = 3:4, lwd = c(2, 2), lty = c(1, 1), cex = cex,
           legend = c("High HLA", "Low HLA"))
    legend(-200, 0.2, bty = "n", legend = "p = 0.19", cex = cex)
}

Miao.plotQQ <- function(master) {
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


Miao.loadByType <- function() {
    master   <- read.csv("Miao_SupTable2.csv")
    keyFrame <- master[,c("pair_id", "Tumor_Sample_Barcode")]

    mutDetail  <- read.table("Miao_SupTable5.tsv", header = TRUE, sep = "\t")
    mutDetail  <- subset(mutDetail, nchar(Variant_Classification) < 20)
    mutByTumor <- Miao.aggregateMutByTumor(mutDetail)
    mutByTumor <- merge(keyFrame, mutByTumor)
    
    neoDetail  <- read.table("Miao_SupTable10.tsv", header = TRUE, sep = "\t")
    neoDetail  <- neoAg1[!duplicated(neoAg1[,c("Tumor_Sample_Barcode", "HLA", "pep_mut")]),]
    neoByTumor <- Miao.aggregateNeoByTumor(neoDetail)
    neoByTumor <- merge(keyFrame, neoByTumor)

    master <- merge(master, mutByTumor, all = TRUE)
    master <- merge(master, neoByTumor, all = TRUE)
    master <- Miao.aggregateByCancerType(master)
    master
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
        data.frame(cancer_type = slice$cancer_type,
                   os_censor   = slice$os_censor,
                   zOS  = Miao.zscore(log(slice$os_days)),
                   zPFS = Miao.zscore(log(slice$pfs_days)),
                   zTMB = Miao.zscore(log(slice$nonSilentCount)),
                   zNAB = Miao.zscore(log(slice$count500)),
                   zHLA = Miao.zscore(log(slice$count500 / slice$nonSilentCount)))
    }

    result <- do.call(rbind, by(master, list(master$cancer_type, master$os_censor), aggFunc))
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
