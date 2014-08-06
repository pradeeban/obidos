/*
 * Title:        Data Replication Server
 * Description:  Data Replication / Synchronization Tools.
 * Licence:      Apache License Version 2.0 - http://www.apache.org/licenses/
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */

package edu.emory.bmi.datarepl.datasources;

import edu.emory.bmi.datarepl.constants.CommonConstants;
import edu.emory.bmi.datarepl.core.InfDataAccessIntegration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Initiates Infinispan instance with the meta data read from the csv file.
 */
public class CSVInitiator {
    private static Logger logger = LogManager.getLogger(CSVInitiator.class.getName());

    public static void main(String[] args) {
        InfDataAccessIntegration infDataAccessIntegration = CSVInfDai.getInfiniCore();
        logger.info("Infinispan Initiator instance started..");

        CSVParser.parseCSV(CommonConstants.META_CSV_FILE, ParsingConstants.CSV_META_POSITION);
    }
}