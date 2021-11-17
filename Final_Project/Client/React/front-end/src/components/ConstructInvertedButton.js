import React from 'react';


//import FileListEntry from "./FileListEntry";

function ConstructInvertedButton(props) {

    function constructInvertedIndicies(){
        if (props.fileList.length > 0){
            console.log("A");
            props.setRequestType("Upload");
            console.log("B");
            props.setRequestHandled(false);
            console.log("C");
            props.setAppState(1);
            console.log("D");
        }
        else {
            console.log("No files have been selected.")
        }
    }

    return (
        <button onClick={constructInvertedIndicies}>Construct Inverted Indicies</button>
    )

}

export default ConstructInvertedButton;