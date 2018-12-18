
ResidueMaster.find <- function() {
    file.path(Sys.getenv("JAM_HOME", names = FALSE), "data", "residue_master.csv")
}

ResidueMaster.load <- function() {
    read.csv(ResidueMaster.find(), strip.white = TRUE)
}





