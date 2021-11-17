var express = require('express');
var cors = require('cors');
const fs = require('fs');
var path = require('path');
var server = express();

//Change these variables to what you need them to be
const port = 8238;
const bucketName = "PUT YOUR BUCKET NAME HERE";
//

server.use(cors());


// Code below with help from https://medium.com/@henslejoseph/multiple-file-upload-with-node-js-9b6215f6b8f1 //
var bodyParser =    require("body-parser");
var multer  =   require('multer');
const { Console } = require('console');
server.use(bodyParser.json());
var storage =   multer.diskStorage({
  destination: function (req, file, callback) {
    callback(null, './uploads');
  },
  filename: function (req, file, callback) {
    callback(null, file.originalname);
  }
});
var upload = multer({ storage : storage }).array('fileArray',100);

//From https://github.com/googleapis/nodejs-storage/blob/main/samples/uploadFile.js

const {Storage} = require('@google-cloud/storage');

  // Creates a client
  const gstorage = new Storage();

  async function uploadFile(filePath, destFileName) {
    await gstorage.bucket(bucketName).upload(filePath, {
      destination: destFileName,
    });

    console.log(`${filePath} uploaded to ${bucketName}`);
  }

// End from https://github.com/googleapis/nodejs-storage/blob/main/samples/uploadFile.js

server.post('/uploadFiles',function(req,res){
    upload(req,res,function(err) {
        console.log("A");
        console.log(req.files);
        if(err) {
            console.log(err);
            return res.sendStatus(500);
        }
        console.log("B");
        
        fs.readdir('./uploads/', (err, files) => {
            files.forEach(file => {
                console.log("C");
              if (fs.lstatSync(path.resolve('./uploads/', file)).isDirectory()) {
                console.log('Directory: ' + file);
              } else {
                console.log('File: ' + file);
                console.log("D");
                uploadFile("uploads/"+file, 'HW4ExtraCredit/'+file).catch(console.error);
              }
            });
          });

        res.sendStatus(200);
    });
});

// End help from https://medium.com/@henslejoseph/multiple-file-upload-with-node-js-9b6215f6b8f1 //

server.get('/', function (req, res) {
    res.sendFile(path.join(__dirname, '/index.html'));
})

var server = server.listen(port, function () {
    console.log(`Listening at http://localhost:${port}`)
})