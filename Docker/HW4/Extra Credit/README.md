To run this first you must ensure that you have Node.js installed.
Install node.js from https://nodejs.org/en/

Then set your environment variable "GOOGLE_APPLICATION_CREDENTIALS" to the JSON gcloud keys obtained from https://cloud.google.com/docs/authentication/getting-started#cloud-console . I named my JSON key file "gcloud-keys.json" to simplify it. You must change it to that name too because the scripts reference that file name. Put that .json file in this same directory.

For windows in this directory:
set GOOGLE_APPLICATION_CREDENTIALS=gcloud-keys.json

Ensure you have an empty folder in the same directory named "uploads"

Then run the following in the command line:
npm install express
npm install cors
npm install --save multer
npm install --save @google-cloud/storage
node server.js
