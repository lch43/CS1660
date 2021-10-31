import React from 'react';

function RequestSearchTerm(props) {

    const inputText = React.createRef();

    function printText(){
        props.setSearchTerm(inputText.current.value)
        props.setRequestType("Term");
        props.setRequestHandled(false);
        props.setAppState(4);
    }

    return (
        <div id="requestSearchTerm">
            <h1>Enter Your Search Term</h1>
            <input type="text" id="word" name="word" ref={inputText}></input>
            <button onClick={printText}>Search</button>
        </div>
    )

}

export default RequestSearchTerm;