#CS 1660 Project Option-II

# Project Checkpoint:
## Source code for the client-side application (terminal or GUI-based).
It is a React App located in:
/Client/React/front-end/
Most specifically:
/Client/React/front-end/src/components
/Client/React/front-end/src/App.js

## Source code for the Dockerfile used to run the client-side application.
/Client/Dockerfile

## In your ReadMe file, list the steps you will use to connect to GCP.
I am still waiting on a response from the professor on how I could go about doing this, but I have multiple ideas that could potentially work.
1. Create the authentication JSON from https://cloud.google.com/docs/authentication/getting-started#cloud-console
2. Add the GOOGLE_APPLICATION_CREDENTIALS environment variable to the docker container.
3. This is where my ideas could vary:
   1. Use REST api commands if available from GCP to run the commands needed in the HDSF directly from the React app.
   2. Create a local server docker container that the React App container would connect to (that server container would have the GOOGLE_APPLICATION_CREDENTIALS environment variable) and use that to create processes that can potentially run gcloud to connect to the HDFS
   3. Create the server from part 2 on the GCP cluster to possibly get better access to what is going on.
   4. Use some APIs that GCP offers to run MapReduce and retrieve the data. I saw some tutorials that mentioned something about BigTable, so I feel I could possibly use something like that.

## In your ReadMe file, list all the build/run commands you used to run the client-side application
To build:
In /Client/React/front-end/ run ``` docker build -t client-app . ```

To run:
(Dockerfile docker run command made with help from (this article)[https://typeofnan.dev/how-to-serve-a-react-app-with-nginx-in-docker/])
Either:
From the docker image: ``` docker pull lch43/cs1660-project-front-end ```
Run: ``` docker run --rm -it -p 8080:80 lch43/cs1660-project-front-end ```

Or:
    After building from the above step:
    Run: ``` docker run --rm -it -p 8080:80 client-app ```

---

# Grading:
## First Java Application Implementation and Execution on Docker: 40% of the total project grade:

## Docker to Local (or GCP) Cluster Communication: 20% of the total project grade:

## Inverted Indexing MapReduce Implementation and Execution on the Cluster (GCP) (with stop-list): 20% of the total project grade:

## Term Search and Top-N Search (including execution time): 20% of the total project grade:

## Extra-credit: building Graphical User Interface for this application: +20% of the total project grade:
(Dockerfile docker run command made with help from (this article)[https://typeofnan.dev/how-to-serve-a-react-app-with-nginx-in-docker/])
From the docker image: ``` docker pull lch43/cs1660-project-front-end ```
Run: ``` docker run --rm -it -p 8080:80 lch43/cs1660-project-front-end ```
React code is in: React/front-end/