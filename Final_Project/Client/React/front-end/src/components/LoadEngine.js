import React, { Component } from "react";

class LoadEngine extends Component {

    state = {
        reload: false
    };

    constructor(props){
        super(props);
        this.fileInput = React.createRef();
        this.chosenFileList = React.createRef();
        this.triggerInput = this.triggerInput.bind(this);
        
        this.files = [];
        for (var i=0;i<props.files.length;i++){
            this.files.push(props.files[i].name);
        }
        console.log(this.files);

        this.getFiles = () => {
            props.setFiles(this.fileInput.current.files);
        }


        this.list = () => {

            if (this.files.length > 0){
                return (
                    <div>
                        {this.files.forEach((name) => (
                            <p>name</p>
                        ))}
                    </div>
                )
            }
            return;
        }

    }

    triggerInput() {
        this.fileInput.current.click();
    }

    


    render(){
        return(
            <div id="loadEngine">
    
                <h1>Load My Engine</h1>
                <input onChange={this.getFiles} ref={this.fileInput} id="fileSelect" type="file" multiple="multiple" hidden={true} />
                <button onClick={this.triggerInput}>Choose Files</button>
                <div ref={this.chosenFileList} id="chosenFiles">
                    {
                        this.list()
                    }
                </div>
    
            </div>
        );
    }

}

export default LoadEngine;