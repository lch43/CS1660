# CS 1660 Project Option-II

# Project Checkpoint:
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

In [/Client/React/front-end/](https://github.com/lch43/CS1660/tree/main/Final_Project/Client/React/front-end) run ``` docker build -t client-app . ```

To run:

(Dockerfile docker run command made with help from [this article](https://typeofnan.dev/how-to-serve-a-react-app-with-nginx-in-docker/))

Either:

From the docker image: ``` docker pull lch43/cs1660-project-front-end ```

Run: ``` docker run --rm -it -p 8080:80 lch43/cs1660-project-front-end ```

Or after building from the above step:

Run: ``` docker run --rm -it -p 8080:80 client-app ```

---

# Grading:
## First Java Application Implementation and Execution on Docker: 40% of the total project grade:
React app in [/Client/React/front-end/](https://github.com/lch43/CS1660/tree/main/Final_Project/Client/React/front-end)

## Docker to Local (or GCP) Cluster Communication: 20% of the total project grade:

## Inverted Indexing MapReduce Implementation and Execution on the Cluster (GCP) (with stop-list): 20% of the total project grade:

## Term Search and Top-N Search (including execution time): 20% of the total project grade:

## Extra-credit: building Graphical User Interface for this application: +20% of the total project grade:
(Dockerfile docker run command made with help from [this article](https://typeofnan.dev/how-to-serve-a-react-app-with-nginx-in-docker/))

From the docker image: ``` docker pull lch43/cs1660-project-front-end ```

Run: ``` docker run --rm -it -p 8080:80 lch43/cs1660-project-front-end ```

React code is in: [/Client/React/front-end/](https://github.com/lch43/CS1660/tree/main/Final_Project/Client/React/front-end)