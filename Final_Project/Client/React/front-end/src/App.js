import React, { useState } from "react";
import axios from 'axios';
import './App.css';
import ChooseAction from "./components/ChooseAction";
import ConstructInvertedButton from "./components/ConstructInvertedButton";

import LoadEngine from "./components/LoadEngine";
import RequestSearchTerm from "./components/RequestSearchTerm";
import RequestTopNValue from "./components/RequestTopNValue";
import SearchTermResults from "./components/SearchTermResults";

//Change this to where you are hosting the backend server
const backendURL = "http://localhost:8238";


function App() {
  const [ files, setFiles ] = useState({});
  const [ appState, setAppState ] = useState(0);
  const [ searchTerm, setSearchTerm] = useState("");
  const [ requestType, setRequestType ] = useState("");
  const [ requestHandled, setRequestHandled ] = useState(true);
  const [ requestResponse, setRequestResponse ] = useState({});

  var fnames = [];

  try{
    if (appState === 0){
      for (var i=0; i<files.length; i++){
        console.log(files[i].name);
        fnames.push(files[i].name);
      }
    }
  }
  catch(e){}

  if (requestHandled === false){

    if (requestType === "TopN"){
      if (searchTerm !== ""){
        setRequestResponse();
      }
      else{
        setAppState(2);
      }
    }
    else if(requestType === "Term"){
      if (searchTerm !== ""){
        setRequestResponse();
      }
      else{
        setAppState(3);
      }
    }
    else if(requestType === "Upload"){
      if (files.length > 0){

        //Assisted by https://stackoverflow.com/questions/58381990/react-axios-multiple-files-upload
        const formData = new FormData();
        for (let filePos = 0; filePos<files.length; filePos++){
          formData.append("fileArray", files[filePos]);
        }
        // files.forEach(file=>{
        //   formData.append("fileArray", file);
        // });

        axios({
          method: "POST",
          url: backendURL + "/uploadFiles",
          data: formData,
          headers: {
            "Content-Type": "multipart/form-data"
          }
        }).then(function (response) {
          console.log(response);
        })
      }
    }

    setRequestHandled(true);
  }

  function revertToSearch(){
    setAppState(1);
  }


  //Doing what I am doing here probably defeats the whole purpose of React, but I am just trying to use this to advance on my project
  //If I were to do this in a professional setting I would make sure I know what I am doing before I do it lol.
  if (appState === 0){ //Home screen. Ask for the files.
    return (
      <div>
        <LoadEngine setFiles={setFiles} files={files} setAppState={setAppState} refresh={Math.random}/>
        {fnames.length > 0 &&
          <h2>Files selected:</h2>
        }
        {
          fnames.map((function(name){
            return <p key={name}>{name}</p>;
          }))
        }
        
        <ConstructInvertedButton setAppState={setAppState} fileList={files} setRequestType={setRequestType} setRequestHandled={setRequestHandled} />
      </div>
    );
  }
  else if(appState === 1){ //Choose the action you want to perform
    return (
      <ChooseAction setAppState={setAppState} />
    );
  }
  else if(appState === 2){ //Search for term
    return (
      <RequestSearchTerm setRequestHandled={setRequestHandled} setAppState={setAppState} setSearchTerm={setSearchTerm} setRequestType={setRequestType}/>
    );
  }
  else if(appState === 3){ //Search Top-N
    return (
      <RequestTopNValue setRequestHandled={setRequestHandled} setAppState={setAppState} setSearchTerm={setSearchTerm} setRequestType={setRequestType}/>
    );
  }
  else if(appState === 4){ //Term search results
    return (
      <SearchTermResults setAppState={setAppState} searchTerm={searchTerm} requestResponse={requestResponse}/>
    );
  }
  else if(appState === 5){ //Top-N search results
    return (
      <div id="topNResults">
          <h1>Top {searchTerm} Frequent Terms</h1>
          <table>
              <tbody>
                  <tr>
                      <th>Term</th>
                      <th>Total Frequencies</th>
                  </tr>
                  
              </tbody>
          </table>

          <button onClick={revertToSearch}>Return to Search</button>
      </div>
    );
  }
  else{
    return(
      <div className="App">
        <h1>Something went wrong!</h1>
      </div>
    )
  }
  
}

export default App;
