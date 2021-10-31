import React from 'react';


//import FileListEntry from "./FileListEntry";

function ConstructInvertedButton(props) {

    function constructInvertedIndicies(){
        if (props.fileList.length > 0){
            props.setAppState(1);
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