var express = require('express');
const { exec, execSync } = require('child_process');
const fs = require('fs');
var server = express();
const port = 8238;

//Change these variables to what you need them to be
const bucketName = 'gs://'+'BUCKETNAME';
const cluster = 'CLUSTERNAME';
const region = 'REGION';
const project = 'PROJECT';

//--------------------------------------------------------


console.log("Loading Inverted Indices & Top-N");
try{
 execSync('gcloud auth activate-service-account --key-file=gcloud-keys.json');
 console.log('Running MapReduce jobs');
 execSync('gcloud dataproc jobs submit hadoop --region='+region+
   ' --cluster='+cluster+' --project='+project+' --jar='+bucketName+
   '/wc.jar -- WordCount hdfs://'+cluster+'-m/MapReduce/uploads/ '+bucketName+
   '/InvertedResults hdfs://'+cluster+'-m/Output2 '+bucketName+'/TopNResults');
 console.log("Loaded.");
 console.log("Downloading results");
 execSync('gsutil cp -r '+bucketName+'/InvertedResults .');
 console.log("InvertedResults downloaded.");
 execSync('gsutil cp -r '+bucketName+'/TopNResults .');
 console.log("TopNResults downloaded.");

}
catch(err){
 console.log(err);
}

var topN = [];
var inverted = {};

const TopNResults = fs.readdirSync('TopNResults').sort();
for (let i=0; i<TopNResults.length;i++){
  if (TopNResults[i].includes('_SUCCESS') == false){
    let lines = fs.readFileSync('TopNResults/'+TopNResults[i], 'utf-8').split('\n');
    for (let j=0; j<lines.length; j++){
      if (lines[j] !== "" && lines[j] !== undefined && lines[j] !== null && lines[j] !== "\n"){
        topN.push(lines[j]);
      }
    }

  }
}
console.log("TopNResults loaded");

const InvertedResults = fs.readdirSync('InvertedResults').sort();
for (let i=0; i<InvertedResults.length;i++){
  if (InvertedResults[i].includes('_SUCCESS') == false){
    let lines = fs.readFileSync('InvertedResults/'+InvertedResults[i], 'utf-8').split('\n');
    for (let j=0; j<lines.length; j++){
      var split1 = lines[j].split(String.fromCharCode(9));
      var split2 = split1[0].split('|');
      var word = split2[1];
      var path = split2[0];
      if (path.includes('hdfs://'+cluster+'-m/MapReduce/uploads/')){
        path = path.substr(String('hdfs://'+cluster+'-m/MapReduce/uploads/').length)
      }
      var count = split1[1];
      if (inverted[word] === undefined) {
        inverted[word] = {};
        inverted[word].data = [];
        inverted[word].data.push([path,count]);
      }
      else{
        if (inverted[word].data === undefined){
          inverted[word].data = [];
          inverted[word].data.push([path,count]);
        }
        else{
          inverted[word].data.push([path,count]);
        }
      }
      
    }

  }
}
console.log('InvertedResults loaded');

server.get('/requestTopN', function (req, res) {
  res.sendFile(__dirname +'/requestTopN.html')
})

server.get('/requestIndex', function (req, res) {
  res.sendFile(__dirname +'/requestIndex.html')
})

server.get('/topN', function (req, res) {
  let now = process.hrtime()[1];
  var output = "<table><tr><th>Position</th><th>Word</th><th>Count</th></tr>";
  var n = req.query.n;
  
  for (let i=1; i<=n && i<topN.length; i++){
    output = output+"<tr><td>"+i+"</td><td>"+topN[i].split(String.fromCharCode(9))[0]+"</td><td>"+topN[i].split(String.fromCharCode(9))[1]+"</td></tr>";
  }
  output = output+'</table> <form action="/requestTopN" method="get"><input type="submit" value="Return to topN search"></form>';
  output = "<h1>Top "+n+" results</h1><p>Search executed in "+((process.hrtime()[1])-now)+" ns</p>"+output;
  res.send(output);
})

server.get('/index', function (req, res) {
  let now = process.hrtime()[1];
  var output = "<table><tr><th>Path</th><th>Count</th></tr>";
  var word = req.query.word;
  var ind = inverted[word.toLowerCase()];
  if (ind === undefined){
    res.send("<h1>Search results for "+word+"</h1><p>Search executed in "+((new Date().getTime())-now)+" ms</p><p>Term not found.</p>"+'<form action="/requestIndex" method="get"><input type="submit" value="Return to index search"></form>')
  }
  var data = ind.data;
  for (let i=0; i<data.length; i++){
    output = output+"<tr><td>"+data[i][0]+"</td><td>"+data[i][1]+"</td></tr>";
  }
  output = output+'</table> <form action="/requestIndex" method="get"><input type="submit" value="Return to index search"></form>';
  output = "<h1>Search Results for "+word+"</h1><p>Search executed in "+((process.hrtime()[1])-now)+" ns</p>"+output;
  res.send(output);
})

server.get('/', function (req, res) {
  res.sendFile(__dirname+'/menu.html');
})


 var server = server.listen(port, function () {
     console.log(`Listening at http://localhost:${port}`)
 })

