/*
 * Title:        Data Replication Server
 * Description:  Data Replication / Synchronization Tools.
 * Licence:      Apache License Version 2.0 - http://www.apache.org/licenses/
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */

package edu.emory.bmi.datarepl.datasources;

/**
 * Retrieve data from S3
 */
public class S3Retriever {
    /**
     * Gets the S3 storage url for the given patient
     * @param patientID, the patient ID
     * @return url.
     */
    public static String retrieveUrl(String patientID) {
        String fileName = CSVInfDai.getS3MetaMap().get(patientID);
        String url;
        if (fileName.contains(ParsingConstants.S3_LEVEL2)) {
            url = ParsingConstants.S3_BASE_URL + ParsingConstants.S3_LEVEL2 + ParsingConstants.URL_SEPARATOR + fileName;
        } else if (fileName.contains(ParsingConstants.S3_LEVEL3)) {
            url = ParsingConstants.S3_BASE_URL + ParsingConstants.S3_LEVEL3 + ParsingConstants.URL_SEPARATOR + fileName;
        } else {
            url = ParsingConstants.S3_BASE_URL + ParsingConstants.S3_LEVEL1 + ParsingConstants.URL_SEPARATOR + fileName;
        }
        return url;
    }
}
