import React from 'react';

function ChooseAction(props) {

    function setState2(){
        props.setAppState(2);
    }

    function setState3(){
        props.setAppState(3);
    }

    return (
        <div id="chooseAction">
            <h1>Engine was loaded</h1>
            <h1>&amp;</h1>
            <h1>Inverted indicies were constructed successfully!</h1>
            <h1>Please Select Action</h1>
            <button onClick={setState2}>Search for  Term</button>
            <button onClick={setState3}>Top-N</button>
        </div>
    )

}

export default ChooseAction;