# Help from https://nodejs.org/en/docs/guides/nodejs-docker-webapp/
# Help from https://stackoverflow.com/questions/28372328/how-to-install-the-google-cloud-sdk-in-a-docker-image
FROM node:14
WORKDIR /usr/src/app
COPY gcloud-keys.json .
RUN curl -sSL https://sdk.cloud.google.com | bash
ENV PATH $PATH:/root/google-cloud-sdk/bin
ENV GOOGLE_APPLICATION_CREDENTIALS=gcloud-keys.json
RUN npm install
RUN npm install express
COPY . .
CMD [ "node", "server.js"]