#!/usr/bin/env groovy

package scope;

class ManageGit implements Serializable {

    def steps
    ManageGit(steps) {this.steps = steps}

    def copyGit(Map git){
        String source = git.source
        String destination = git.destination
        String credential = git.credential
        String branch = git.branch
        
        steps.stage ('Get files from GitLab') {        
            this.executeRemoteCopy(source, destination, credential, branch)
        }

    }

    def executeRemoteCopy(source, destination, credential, branch){
        steps.checkout(
        [$class: 'GitSCM',
        branches: [[name: "*/${branch}"]],
        doGenerateSubmoduleConfigurations: false,
        extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: "${destination}"]],
        submoduleCfg: [],
        userRemoteConfigs: [[credentialsId: "${credential}", url: "${source}"]]])
    }


}