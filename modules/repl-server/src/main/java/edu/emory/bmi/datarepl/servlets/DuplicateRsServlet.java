package edu.emory.bmi.datarepl.servlets;

import edu.emory.bmi.datarepl.tcia.TciaReplicaSetInterface;
import edu.emory.bmi.datarepl.ui.UIGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet to duplicate the replica set to the specified user
 */
public class DuplicateRsServlet extends HttpServlet {
    private static Logger logger = LogManager.getLogger(DuplicateRsServlet.class.getName());


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String dUserId = request.getParameter("dUserID");
        long replicaSetID;
        String output = "";


        if (((request.getParameter("replicaSetID") != null) && ((!request.getParameter("replicaSetID").equals(""))))&& (!dUserId.equals(""))) {
            replicaSetID = Long.parseLong(request.getParameter("replicaSetID"));

            TciaReplicaSetInterface tciaReplicaSetInterface = (TciaReplicaSetInterface) TciaReplicaSetInterface.getInfiniCore();

            logger.info("Duplicating the replica set of the user..");

            if (dUserId != null) {
                tciaReplicaSetInterface.duplicateReplicaSet(replicaSetID, dUserId);

                Long[] replicaSets = tciaReplicaSetInterface.getUserReplicaSets(dUserId);


                output = UIGenerator.returnReplicaSetOutput(replicaSets);

            }
        } else {
            output = "Empty values entered for the replica set ID or user ID";
        }
        logger.info("Listing the Replica Sets of the Receiving User");
        out.println(output);
    }

}
