# Assignment 1

Complete the implementation of a Java RMI based exam system for the university.  The server allows clients to authenticate and download an Assessment object.  The Assessment object implements an interface that provides methods to retrieve and answer a list of multiple-choice questions. The Assessment is completed on the client and the updated Assessment object can then be submitted back to the server for correction. The following interfaces are provided and you should not need to change these interface definitions:

ExamServer - this (remote) interface provides methods for user authentication, download of assessments and the submission of completed assessments.  Assessments can only be downloaded and submitted during certain time intervals while they are available on the server.

Assessment - this (serializable) interface provides methods for the retrieval of information about the assessment, and the retrieval / answering of questions.  It also has a method to output the selected answer to each question - the answer provided to a question can then be changed, if desired, prior to submission of the assessment. A completed assignment may be submitted multiple time to the server up to the closing time. The last version submitted would then be corrected.

A starting template for the mainline server code is also provided for your convenience and the following command line options will the server code provided in a Unix / Linux environment. This command line parameters will obviously have to be changed as required for a Windows environment:

    $ java -cp /Users/macbook/rmidemo -Djava.rmi.server.codebase=file:/Users/macbook/rmidemo/ -Djava.rmi.server.hostname=localhost -Djava.security.policy=server.policy ct414/ExamEngine

There are two main parts to this assignment:

1: Complete the implementation of the server side application. You will need to define at least one implementation class for the Assessment interface that you can use for testing. You will also have to then implement the remote methods that are currently empty in the mainline server code.

2: Implement a client application. This can be done as a simple GUI or as a command line application. The main point of the client application is that it provides a means of testing the functionality of the server and it also provides some way of downloading, answering and submitting an assessment object.

The assignment should be done in groups of two students. Where the assignment is submitted by one student then don't forget to mention the name and ID number of the other person in your group so that they will also be credited for the assignment. All submissions should be done via Blackboard and please do not email the submissions. If you submit more than one attempt via Blackboard then only the final attempt will be marked. A description of the system implementation and testing, and related screen shots of the applications running, should be included in a single Word or PDF file. This Word or PDF file should then be included in a standard ZIP file along with all the related Java source code. No other file formats will be accepted for correction. So you should be uploading a single ZIP file for this assignment. Please do not include any of the Java class files in the submitted ZIP file.