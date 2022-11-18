def call(Map config = [:]){
    if(config.cleanWorkspace){
        cleanWs()
    }
    withCredentials([usernamePassword(credentialsId: config.credentialsId, passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        if(config.branchName){
            checkout([$class: 'GitSCM', branches: [[name: config.branchName]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: config.credentialsId, url: config.url]]])
        }else{
            checkout([$class: 'TeamFoundationServerScm', projectPath: config.projectPath, serverUrl: config.serverUrl, useOverwrite: true, useUpdate: true, userName: "$USER", password: hudson.util.Secret.fromString("$PASS"), workspaceName: config.workspaceName ? config.workspaceName : 'Hudson-${JOB_NAME}-${NODE_NAME}-CLIENT'])
        }
    }
    if(!isUnix()){
        powershell "md -Force publish"
        dir("./publish"){
            if(!fileExists('.gitignore')){
                echo "Init Repo"
                bat "%SCRIPTS%\\psrun.bat engine-init-repo.ps1 ./"
            }
        }
    }
    
}