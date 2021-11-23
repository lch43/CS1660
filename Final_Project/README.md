# CS 1660 Project Option-II

# Final Walkthrough:
1. Create a new project on GCP. You may use an already existing one if you like, however, please follow the rest of the steps.
2. Enable billing, and enable the 'Cloud Dataproc API'.
3. Navigate to dataproc clusters.
4. Click create a cluster.
   1. Name is your choice
   2. Region is your choice
   3. Cluster type: Standard
   4. Autoscaling policy: none
   5. Click create.
5. Update the variables in [/Client/Full/server.js](https://github.com/lch43/CS1660/tree/main/Final_Project/Client/Full/server.js)
   1. const bucketName = 'gs://'+THE NAME OF YOUR CLOUD STORAGE STAGING BUCKET FOR THE CLUSTER
   2. const cluster = CLUSTER NAME
   3. const region = CLUSTER REGION;
   4. const project = PROJECT ID;
6. Create service account keys by following the steps listed [here](https://cloud.google.com/docs/authentication/getting-started#cloud-console).
   1. Use the cloud console option and click the button "Go to Create service account"
   2. Select your project if asked.
   3. Create a service account name and service account id.
   4. Click to add roles, and select Basic>Owner
   5. Press continue
   6. Press done
   7. Click on the three dots under actions next to the new account.
   8. Click manage keys
   9. Click add key
   10. Create new key
   11. Choose JSON
   12. A json file will be downloaded with the project name and some characters. Rename that to gcloud-keys.json and place it in [/Client/Full/](https://github.com/lch43/CS1660/tree/main/Final_Project/Client/Full)
7.  Enter into [/CloudPrep/](https://github.com/lch43/CS1660/tree/main/Final_Project/CloudPrep)
    1. Upload /uploads folder with contents into your cluster's dataproc staging bucket.
    2. Upload WordCount.java to your cluster's dataproc staging bucket.
    3. Ensure that the folders InvertedResults and TopNResults do not exist in your bucket.
    4. Open a browser window of your cluster using the master node.
    5. Run the following commands. Replace BUCKETNAME with the name of your staging bucket:
       1. export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar;
       2. gsutil cp -r gs://BUCKETNAME/uploads .;
       3. cd uploads;
       4. for f in *.tar.gz; do tar xf "$f"; rm -rf "$f"; done;
       5. cd ..;
       6. hadoop fs -mkdir /MapReduce
       7. hadoop fs -put uploads /MapReduce/uploads;
       8. gsutil cp gs://BUCKETNAME/WordCount.java .;
       9. hadoop com.sun.tools.javac.Main WordCount.java;
       10. jar cf  wc.jar  WordCount*.class;
       11. gsutil cp wc.jar gs://BUCKETNAME/;
8.  In [/Client/Full/](https://github.com/lch43/CS1660/tree/main/Final_Project/Client/Full) run ``` docker build -t cs1660-final-client . ```
9.  Run ``` docker run -p 8238:8238 cs1660-final-client ```
10. It will take around three and a half minutes for the job created by the client to complete on the cloud, but once it is complete you will be able to access the search engine at http://localhost:8238/
    1.  If you kill the container and want to run it again, you must make sure you delete the InvertedResults and TopNResults folder from your staging bucket, as well as /Output2 from the cluster-m's hdfs. This is due to MapReduce needing to output to a directory that does not exist.
11. On the browser click the buttons to navigate to windows where you can enter an integer to get the top-n words, or a word to get the count and sources of that word in the files.
12. To kill the container you can open the docker desktop app and under Containers/Apps you can find the running container and stop it.
13. Ensure you turn off billing once you are finished with the project.

# Project Checkpoint:
## **THIS PROJECT HAD A MAJOR CHANGE SINCE THE CHECKPOINT. IT NO LONGER IS RUN OFF OF A REACT FRONT-END AND EXPRESS BACKEND. IT IS NOW AN EXPRESS FULL-STACK. THE PROJECT CHECKPOINT INFORMATION IS NOW OUTDATED**
## Source code for the client-side application (terminal or GUI-based).
It is a React App located in:

[/Client/React/front-end/](https://github.com/lch43/CS1660/tree/main/Final_Project/Client/React/front-end)

Most specifically I worked mainly in:

[/Client/React/front-end/src/components](https://github.com/lch43/CS1660/tree/main/Final_Project/Client/React/front-end/src/components)

[/Client/React/front-end/src/App.js](https://github.com/lch43/CS1660/blob/main/Final_Project/Client/React/front-end/src/App.js)

---

## Source code for the Dockerfile used to run the client-side application.
[With help from https://typeofnan.dev/how-to-serve-a-react-app-with-nginx-in-docker/](https://typeofnan.dev/how-to-serve-a-react-app-with-nginx-in-docker/)

[/Client/React/front-end/Dockerfile](https://github.com/lch43/CS1660/blob/main/Final_Project/Client/React/front-end/Dockerfile)

```
FROM node:10 AS app_builder
WORKDIR /app
COPY . .
RUN yarn install && yarn build

FROM nginx:alpine
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
COPY --from=app_builder /app/build .
ENTRYPOINT ["nginx", "-g", "daemon off;"]
```

---

## In your ReadMe file, list the steps you will use to connect to GCP.
I am still working out which way would be the best approach, but here are my plans:
1. Create the authentication JSON from https://cloud.google.com/docs/authentication/getting-started#cloud-console
2. Add the GOOGLE_APPLICATION_CREDENTIALS environment variable to the docker container.
3. Add the mapreduce java jar or class files to the bucket to be able to run MapReduce when called.
4. This is where my ideas could vary:
   1. Use REST api commands if available from GCP to run the commands needed in the HDSF directly from the React app.
   2. Create a local server docker container that the React App container would connect to (that server container would have the GOOGLE_APPLICATION_CREDENTIALS environment variable) and use that to create processes that can potentially run gcloud to connect to the HDFS
   3. Create the server from part 2 on the GCP cluster to possibly get better access to what is going on.
   4. Use some APIs that GCP offers to run MapReduce and retrieve the data. I saw some tutorials that mentioned something about BigTable, so I feel I could possibly use something like that.
   5. Use the jar files on the GCP bucket to also act as an intermediary between Hadoop and the client

---

## In your ReadMe file, list all the build/run commands you used to run the client-side application
To build:

In [/Client/Full/](https://github.com/lch43/CS1660/tree/main/Final_Project/Client/Full) run ``` docker build -t cs1660-final-client . ```

To run:

``` docker run -p 8238:8238 cs1660-final-client ```

---

# Grading:
## First Java Application Implementation and Execution on Docker: 40% of the total project grade:
Express app in [/Client/Full/](https://github.com/lch43/CS1660/tree/main/Final_Project/Client/Full). Folder contains the dockerfile and to build and run:

To build:

In [/Client/Full/](https://github.com/lch43/CS1660/tree/main/Final_Project/Client/Full) run ``` docker build -t cs1660-final-client . ```

To run:

``` docker run -p 8238:8238 cs1660-final-client ```

## Docker to Local (or GCP) Cluster Communication: 20% of the total project grade:
[/Client/Full/](https://github.com/lch43/CS1660/tree/main/Final_Project/Client/Full) contains the docker file and the source code needed to create the docker image. GCP must already be set up before running the container. Project steps are listed near top of the document.

To build:

In [/Client/Full/](https://github.com/lch43/CS1660/tree/main/Final_Project/Client/Full) run ``` docker build -t cs1660-final-client . ```

To run:

``` docker run -p 8238:8238 cs1660-final-client ```

## Inverted Indexing MapReduce Implementation and Execution on the Cluster (GCP) (with stop-list): 20% of the total project grade:
Implementation can be found at [/CloudPrep/WordCount.java](https://github.com/lch43/CS1660/tree/main/Final_Project/CloudPrep/WordCount.java)

## Term Search and Top-N Search (including execution time): 20% of the total project grade:
Search results generated via MapReduce job done using [/CloudPrep/WordCount.java](https://github.com/lch43/CS1660/tree/main/Final_Project/CloudPrep/WordCount.java) on GCP, which is triggered to run by the client. This assumes the files are already placed where they need to be, which was allowed by Dr. Farag in class. Also allowed by Dr. Farag in class was the implementation where the results can be downloaded from the bucket to the client app and used for the searching.

## Extra-credit: building Graphical User Interface for this application: +20% of the total project grade:
The client express app provides a graphical user interface for the application. It is accessed via web browser.

## Video Walkthrough
[Youtube link](https://youtu.be/6-ZP4D1ly90)