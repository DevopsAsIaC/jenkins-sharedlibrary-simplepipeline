#!/usr/bin/env groovy

package scope;

class PublishFiles implements Serializable {
 
  def steps
  Map conf
  String host
  String jenkins_source
  String server_directory_temporary_destination
  String server_directory_destination
  String server_directory_destination_permission
  String remove_old_directory_destination
  int timeout

  PublishFiles(steps, conf) {
    this.steps = steps
    this.host = conf.host
    this.jenkins_source = conf.jenkins_source
    this.server_directory_temporary_destination = conf.server_directory_temporary_destination
    this.server_directory_destination = conf.server_directory_destination
    this.server_directory_destination_permission = conf.server_directory_destination_permission
    this.remove_old_directory_destination = conf.remove_old_directory_destination
    this.timeout = 120000
  }

  def removeOldFilesFromServer(){
    steps.stage('Remove Old Files From Server'){
      this.executeRemoteCommand("rm -rf ${this.server_directory_temporary_destination}")
    }
  }

  def copyFromJenkinsToServer(){
    steps.stage('Copy From Jenkins to Server'){
      this.executeRemoteCopy(this.jenkins_source, this.server_directory_temporary_destination)
    }
  }

  def copyFromSeverToDestination(){
    steps.stage('Copy From Server to Destination'){
      if (this.remove_old_directory_destination == 'yes') {
        def delete_old_files = "sudo rm -rf ${this.server_directory_destination}"
        this.executeRemoteCommand(delete_old_files)
      }

      def copy_to_destination = "sudo cp -ar ${this.server_directory_temporary_destination} ${this.server_directory_destination}"
      def change_owner = "sudo chown -R ${server_directory_destination_permission} ${this.server_directory_destination}"
      this.executeRemoteCommand(copy_to_destination)
      this.executeRemoteCommand(change_owner)
    }
  }

  def executeRemoteCopy(source, destination){
       steps.sshPublisher(
        publishers: [steps.sshPublisherDesc
                        (configName: "${this.host}",
                          transfers: [steps.sshTransfer
                                        (excludes: '',
                                        execCommand: '',
                                        execTimeout: this.timeout,
                                        flatten: false,
                                        makeEmptyDirs: false,
                                        noDefaultExcludes: false,
                                        patternSeparator: '[, ]+',
                                        remoteDirectory: "${destination}/",
                                        remoteDirectorySDF: false,
                                        removePrefix: "${source}",
                                        sourceFiles: "${source}/**")
                                        ],
                          usePromotionTimestamp: false,
                          useWorkspaceInPromotion: false,
                          verbose: false)
                          ]) 
  }

   def executeRemoteCommand(command){
       steps.sshPublisher(
        publishers: [steps.sshPublisherDesc
                        (configName: "${this.host}",
                          transfers: [steps.sshTransfer
                                        (excludes: '',
                                        execCommand: "${command}",
                                        execTimeout: this.timeout,
                                        flatten: false,
                                        makeEmptyDirs: false,
                                        noDefaultExcludes: false,
                                        patternSeparator: '[, ]+',
                                        remoteDirectory: '',
                                        remoteDirectorySDF: false,
                                        removePrefix: '',
                                        sourceFiles: '')
                                        ],
                          usePromotionTimestamp: false,
                          useWorkspaceInPromotion: false,
                          verbose: true)
                          ]) 

  }

}

