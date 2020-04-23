#!/usr/bin/env groovy

/* Example of use:
@Library('common-pipelines')_

git_url = "http://myowngitlab.local"

Map git = [ 
    source: "${git_url}/infrastructure/automation/myprogram/here.git",
    branch: "master",
    destination: "MyOwnDST",
    credential: "1"
]

Map publish_files = [
    host: 'Bender',
    jenkins_source: "${git.destination}/code/WhereIsMyCode",
    server_directory_temporary_destination: 'tmp/WhereIsMyCode',
    server_directory_destination: '/var/mydestination/WhereIsMyCode',
    remove_old_directory_destination: 'yes',
    server_directory_destination_permission: 'userapp:groupapp'
]

Map user_conf = [ git: git,
                publish_files: publish_files ]

copyFromJenkinsToServer user_conf 
 */


import scope.*

def call(Map pipeline_config) {
    node() {
        clean_workdir = new scope.ManageDir(this).deleteDirectory()
        copy_git = new scope.ManageGit(this).copyGit(pipeline_config.git)
        publish_files = new scope.PublishFiles(this, pipeline_config.publish_files)
        publish_files.removeOldFilesFromServer()
        publish_files.copyFromJenkinsToServer()
        publish_files.copyFromSeverToDestination()
    }
}