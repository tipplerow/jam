
CDR.homeDir <- function() {
    file.path(TCGA.homeDir(), "CDR")
}

CDR.loadCancerType <- function(homeDir = NULL) {
    if (is.null(homeDir))
        homeDir <- CDR.homeDir()

    read.table(file.path(homeDir, "CDR_Cancer_Type.tsv"), sep = "\t", header = TRUE)
}

CDR.loadSurvival <- function(homeDir = NULL) {
    if (is.null(homeDir))
        homeDir <- CDR.homeDir()

    master <- read.table(file.path(homeDir, "Survival.tsv"),
                         sep = "\t",
                         header = TRUE,
                         comment.char = "",
                         strip.white = TRUE)

    NA.codes <-
        c("#N/A",
          "[Discrepancy]",
          "[Not Applicable]",
          "[Not Available]",
          "[Not Evaluated]",
          "[Unknown]")

    for (colName in colnames(master)) {
        rowIndex <- which(master[,colName] %in% NA.codes)
        master[rowIndex, colName] <- NA
    }

    master$Redaction <-
        master$Redaction == "Redacted"

    numeric.cols <-
        c("age_at_initial_pathologic_diagnosis",
          "OS", "OS.time", "PFI", "PFI.time")

    for (colName in numeric.cols)
        master[,colName] <- as.numeric(master[,colName])

    hasStage <- function(keys) {
        ifelse(is.na(master$ajcc_pathologic_tumor_stage), 0, as.numeric(master$ajcc_pathologic_tumor_stage %in% keys))
    }

    master$Stage1 <- hasStage(c("Stage I",   "Stage IA",   "Stage IB",   "Stage IC"))
    master$Stage2 <- hasStage(c("Stage II",  "Stage IIA",  "Stage IIB",  "Stage IIC"))
    master$Stage3 <- hasStage(c("Stage III", "Stage IIIA", "Stage IIIB", "Stage IIIC"))
    master$Stage4 <- hasStage(c("Stage IV",  "Stage IVA",  "Stage IVB",  "Stage IVC"))

    master$StageNum <-
        ifelse(master$Stage1, 1,
        ifelse(master$Stage2, 2,
        ifelse(master$Stage3, 3,
        ifelse(master$Stage4, 4, NA))))

    data.frame(Patient_ID       = master$bcr_patient_barcode,
               Cancer_Type      = master$type,
               Diagnosis_Age    = master$age_at_initial_pathologic_diagnosis,
               Gender           = master$gender,
               Race             = master$race,
               Pathologic_Stage = master$ajcc_pathologic_tumor_stage,
               Clinical_Stage   = master$clinical_stage,
               Vital_Status     = master$vital_status,
               Tumor_Status     = master$tumor_status,
               Redaction        = master$Redaction,
               OS_Event         = master$OS,
               OS_Days          = master$OS.time,
               PFS_Event        = master$PFI,
               PFS_Days         = master$PFI.time,
               Stage1           = master$Stage1,
               Stage2           = master$Stage2,
               Stage3           = master$Stage3,
               Stage4           = master$Stage4,
               StageNum         = master$StageNum)
}

CDR.writeCancerType <- function(homeDir = NULL) {
    if (is.null(homeDir))
        homeDir <- CDR.homeDir()

    survFrame <- CDR.loadSurvival()
    survFrame <- survFrame[,c("bcr_patient_barcode", "type")]
    survFrame <- survFrame[order(survFrame$type, survFrame$bcr_patient_barcode),]

    names(survFrame) <-
        c("Patient_ID", "Cancer_Type")

    write.table(survFrame, file.path(homeDir, "CDR_Cancer_Type.tsv"),
                sep = "\t", quote = FALSE, col.names = TRUE, row.names = FALSE)
}
