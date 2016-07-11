/*
 * Title:        Data Replication Server
 * Description:  Data Replication / Synchronization Tools.
 * Licence:      Apache License Version 2.0 - http://www.apache.org/licenses/
 *
 * Copyright (c) 2014, Pradeeban Kathiravelu <pradeeban.kathiravelu@tecnico.ulisboa.pt>
 */

package edu.emory.bmi.datarepl.tcia;

import edu.emory.bmi.datarepl.interfacing.TciaInvoker;
import edu.emory.bmi.datarepl.ui.LogInInitiator;
import edu.emory.bmi.datarepl.ui.UIGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The log in class for TCIA
 */
public class TciaLogInInitiator extends LogInInitiator {
    private static Logger logger = LogManager.getLogger(TciaLogInInitiator.class.getName());
    private static TciaReplicaSetAPI tciaReplicaSetAPI;

    /**
     * When the user logs in, retrieve the stored replica sets
     *
     * @param userId Id of the user.
     */
    public void login(String userId) {
        tciaReplicaSetAPI = (TciaReplicaSetAPI) TciaReplicaSetAPI.getInfiniCore();

        tciaInvoker = new TciaInvoker();

        Long[] replicaSetIDs = tciaReplicaSetAPI.getUserReplicaSets(userId);

        //currently getting all the replicaSets.
        if (replicaSetIDs != null) {
            logger.info("Retrieving the replica sets");
            UIGenerator.printReplicaSetList(replicaSetIDs);

            for (Long aReplicaSetID : replicaSetIDs) {
                tciaReplicaSetAPI.getReplicaSet(aReplicaSetID);
            }
        }
    }

    /**
     * Initiate replication and synchronization tool at the start time, without the user id.
     */
    public void init() {
        tciaReplicaSetAPI = (TciaReplicaSetAPI) TciaReplicaSetAPI.getInfiniCore();
        tciaInvoker = new TciaInvoker();
    }

    public static TciaReplicaSetAPI getTciaReplicaSetAPI() {
        return tciaReplicaSetAPI;
    }
}
