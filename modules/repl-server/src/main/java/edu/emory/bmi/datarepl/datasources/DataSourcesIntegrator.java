/*
 * Title:        Data Replication Server
 * Description:  Data Replication / Synchronization Tools.
 * Licence:      Apache License Version 2.0 - http://www.apache.org/licenses/
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */

package edu.emory.bmi.datarepl.datasources;

import com.mashape.unirest.http.exceptions.UnirestException;
import edu.emory.bmi.datarepl.ds_impl.DataSourcesConstants;
import edu.emory.bmi.datarepl.interfacing.TciaInvoker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Integrate with the data sources
 */
public abstract class DataSourcesIntegrator implements Integrator {
    private static TciaInvoker tciaInvoker = new TciaInvoker();
    private static Logger logger = LogManager.getLogger(DataSourcesIntegrator.class.getName());

    /**
     * Integrate with TCIA
     *
     * @param patientID, id of the patient
     */
    public static String getPatientStudiesFromTCIA(String patientID) {
        String output = "";
        try {
            tciaInvoker.getPatientStudy("json", null, patientID, null);
            output = tciaInvoker.getStudiesOfThePatientString("json", null, patientID, null);
            S3Integrator.updateExistenceInDataSource(patientID, edu.emory.bmi.datarepl.ds_impl.DataSourcesConstants.TCIA_META_POSITION, true);
        } catch (UnirestException e) {
            logger.info("UniRest Exception while invoking the patient study retrieval", e);
        } catch (IOException e) {
            logger.info("IO Exception while invoking the patient study retrieval", e);
        }
        return output;
    }

    /**
     * Integrate with CA Microscope
     *
     * @param patientID, id of the patient
     */
    public static String getPatientStudiesFromCAMicroscope(String patientID) {
        if (edu.emory.bmi.datarepl.ds_impl.CSVInfDai.getCaMetaMap().get(patientID)!=null) {
            return edu.emory.bmi.datarepl.ds_impl.CSVInfDai.getCaMetaMap().get(patientID);
        } else {
            String url = edu.emory.bmi.datarepl.ds_impl.DataSourcesConstants.CA_MICROSCOPE_BASE_URL + edu.emory.bmi.datarepl.ds_impl.DataSourcesConstants.CA_MICROSCOPE_QUERY_URL +
                    patientID;
            edu.emory.bmi.datarepl.ds_impl.CSVInfDai.getCaMetaMap().put(patientID, url);
            S3Integrator.updateExistenceInDataSource(patientID, edu.emory.bmi.datarepl.ds_impl.DataSourcesConstants.CA_META_POSITION, true);
            return url;
        }
    }


    /**
     * Does the data source exists.
     *
     * @param key,            patientId
     * @param metaArrayIndex, index in the meta array
     * @return if exists.
     */
    public static boolean doesExistInDataSource(String key, int metaArrayIndex) {
        if (edu.emory.bmi.datarepl.ds_impl.CSVInfDai.getMetaMap().get(key) == null) {
            return false;
        } else {
            Boolean[] temp = edu.emory.bmi.datarepl.ds_impl.CSVInfDai.getMetaMap().get(key);
            return temp[metaArrayIndex];
        }
    }

    /**
     * Update meta data
     *
     * @param key,       patient ID
     * @param metaArray, meta array to be stored
     * @param meta,      location in the meta array
     */
    public static void updateMetaData(String key, String[] metaArray, int meta) {
        if (meta == edu.emory.bmi.datarepl.ds_impl.DataSourcesConstants.CSV_META_POSITION) {
            S3Integrator.updateMetaDataWithCSV(key, metaArray);
        } else if (meta == DataSourcesConstants.S3_META_POSITION) {
            S3Integrator.updateMetaDataWithS3Entry(key, metaArray);
        }
    }
}
