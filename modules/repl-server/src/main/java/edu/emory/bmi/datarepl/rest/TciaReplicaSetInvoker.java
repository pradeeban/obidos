/*
 * Copyright (c) 2015-2016, Pradeeban Kathiravelu and others. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.bmi.datarepl.rest;

import edu.emory.bmi.datarepl.interfacing.TciaInvoker;
import edu.emory.bmi.datarepl.tcia.TciaLogInInitiator;
import edu.emory.bmi.datarepl.ui.DataRetriever;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static spark.Spark.*;

public class TciaReplicaSetInvoker {
    private static Map<String, Book> books = new HashMap<String, Book>();
    private static Logger logger = LogManager.getLogger(DataRetriever.class.getName());
    private static TciaInvoker tciaInvoker;

    public static void main(String[] args) {

        port(5678);

        TciaLogInInitiator logInInitiator = new TciaLogInInitiator();
        logInInitiator.init();

        tciaInvoker = logInInitiator.getTciaInvoker();

        /**
         * Create Replica Set:
         curl "http://lion.bmi.emory.edu:8080/mediator/createRs?iUserID=12&iCollection=TCGA-GBM&iPatientID=&iStudyInstanceUID=&iSeriesInstanceUID"

         Duplicate Replica Set:
         curl "http://lion.bmi.emory.edu:8080/mediator/duplicateRs?dUserID=123&replicaSetID=-8818562079351590113"

         List Replica Sets:
         curl "http://lion.bmi.emory.edu:8080/mediator/listRs?dUserID=12"

         Retrieve Replica Set:
         curl "http://lion.bmi.emory.edu:8080/mediator/retrieveRs?replicaSetID=-8818562079351590113"

         Update Replica Set:
         curl "http://lion.bmi.emory.edu:8080/mediator/createRs?iRsID=-8818562079351590113&iCollection=TCGA-GBM&iPatientID=TCGA-06-6701%2CTCGA-08-0831&iStudyInstanceUID=1.3.6.1.4.1.14519.5.2.1.4591.4001.151679082681232740021018262895&iSeriesInstanceUID=1.3.6.1.4.1.14519.5.2.1.4591.4001.179004339156422100336233996679"

         Append Replica Set:
         curl "http://lion.bmi.emory.edu:8080/mediator/appendRs?iRsID=-8818562079351590113&iCollection=&iPatientID=A111111111111&iStudyInstanceUID=CCCCC1111111111111&iSeriesInstanceUID="

         Delete Replica Set:
         curl "http://lion.bmi.emory.edu:8080/mediator/deleteRs?userID=123&replicaSetID=-5844980299248549862"

         Search Series (TCIA):
         curl "http://lion.bmi.emory.edu:8080/mediator/init?iCollection=TCGA-GBM&iPatientID=TCGA-06-6701&iStudyInstanceUID=1.3.6.1.4.1.14519.5.2.1.4591.4001.151679082681232740021018262895&iModality=MR"
         */

        final Random random = new Random();

        // Creates a new book resource, will return the ID to the created resource
        // author and title are sent in the post body as x-www-urlencoded values e.g. author=Foo&title=Bar
        // you get them by using request.queryParams("valuename")
        post("/replicasets", (request, response) -> {
            String author = request.queryParams("author");
            String title = request.queryParams("title");
            Book book = new Book(author, title);

            int id = random.nextInt(Integer.MAX_VALUE);
            books.put(String.valueOf(id), book);

            response.status(201); // 201 Created
            return id;
        });

        // Gets the book resource for the provided id
        get("/replicasets/:id", (request, response) -> {
            Book book = books.get(request.params(":id"));
            if (book != null) {
                return "Title: " + book.getTitle() + ", Author: " + book.getAuthor();
            } else {
                response.status(404); // 404 Not found
                return "Book not found";
            }
        });

        // Updates the book resource for the provided id with new information
        // author and title are sent in the request body as x-www-urlencoded values e.g. author=Foo&title=Bar
        // you get them by using request.queryParams("valuename")
        put("/replicasets/:id", (request, response) -> {
            String id = request.params(":id");
            Book book = books.get(id);
            if (book != null) {
                String newAuthor = request.queryParams("author");
                String newTitle = request.queryParams("title");
                if (newAuthor != null) {
                    book.setAuthor(newAuthor);
                }
                if (newTitle != null) {
                    book.setTitle(newTitle);
                }
                return "Book with id '" + id + "' updated";
            } else {
                response.status(404); // 404 Not found
                return "Book not found";
            }
        });

        // Deletes the book resource for the provided id
        delete("/replicasets/:id", (request, response) -> {
            String id = request.params(":id");
            Book book = books.remove(id);
            if (book != null) {
                return "Book with id '" + id + "' deleted";
            } else {
                response.status(404); // 404 Not found
                return "Book not found";
            }
        });

        // Gets all available book resources (ids)
        get("/replicasets", (request, response) -> {
            String ids = "";
            for (String id : books.keySet()) {
                ids += id + " ";
            }
            return ids;
        });
    }

    public static class Book {

        public String author, title;

        public Book(String author, String title) {
            this.author = author;
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
