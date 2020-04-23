#!/usr/bin/env groovy

package scope
class ManageDir implements Serializable {
    def steps
    ManageDir(steps) {this.steps = steps}
 
    def deleteDirectory(){
        steps.stage('Clean WORKSPACE') {
            steps.deleteDir()
        }
    }

}
