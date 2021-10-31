import React from 'react';

function RequestTopNValue(props) {

    const inputText = React.createRef();

    function printText(){
        props.setSearchTerm(inputText.current.value)
        props.setRequestType("TopN");
        props.setRequestHandled(false);
        props.setAppState(5);
    }

    return (
        <div id="requestTopNValue">
            <h1>Enter Top-N Value (Integer only)</h1>
            <input type="text" id="topN" name="topN" ref={inputText}></input>
            <button onClick={printText}>Search</button>
        </div>
    )

}

export default RequestTopNValue;