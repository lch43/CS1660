import React from 'react';

function RequestSearchTerm(props) {

    function revertToSearch(){
        props.setAppState(1);
    }

    return (
        <div id="searchTermResults">
            <h1>You searched for the term: {props.searchTerm}</h1>
            <h1>Your search was executed in XXX ms</h1>
            <table>
                <tbody>
                    <tr>
                        <th>Doc ID</th>
                        <th>Doc Folder</th>
                        <th>Doc Name</th>
                        <th>Frequencies</th>
                    </tr>
                </tbody>
            </table>

            <button onClick={revertToSearch}>Return to Search</button>
        </div>
    )

}

export default RequestSearchTerm;