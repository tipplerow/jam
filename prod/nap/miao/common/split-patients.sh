#!/bin/sh
MASTER_PATIENT_LIST=${CSB_DATA_VAULT}/Miao/Miao_Patient_List.txt

head  -31 $MASTER_PATIENT_LIST            > miao_patient_list_1.txt
head  -62 $MASTER_PATIENT_LIST | tail -31 > miao_patient_list_2.txt
head  -93 $MASTER_PATIENT_LIST | tail -31 > miao_patient_list_3.txt
head -124 $MASTER_PATIENT_LIST | tail -31 > miao_patient_list_4.txt
head -155 $MASTER_PATIENT_LIST | tail -31 > miao_patient_list_5.txt
head -186 $MASTER_PATIENT_LIST | tail -31 > miao_patient_list_6.txt
head -217 $MASTER_PATIENT_LIST | tail -31 > miao_patient_list_7.txt
tail  -30 $MASTER_PATIENT_LIST            > miao_patient_list_8.txt
