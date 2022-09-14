def call(Map config = [:]){
    if(config.CleanWorkspace == true){
        cleanWs()
    }
    withCredentials([usernamePassword(credentialsId: 'af058150-c046-4012-9739-2a21198727b4', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        if(config.git == true){
            checkout([$class: 'GitSCM', branches: [[name: config.branchName]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'af058150-c046-4012-9739-2a21198727b4', url: config.url]]])
        }else{
            checkout([$class: 'TeamFoundationServerScm', projectPath: config.projectPath, serverUrl: config.serverUrl, useOverwrite: true, useUpdate: true, userName: "$USER", password: hudson.util.Secret.fromString("$PASS"), workspaceName: 'Hudson-${JOB_NAME}-${NODE_NAME}-CLIENT'])
        }
    }
    powershell "md -Force publish"
    dir("./publish"){
        if(!fileExists('.gitignore')){
            echo "Init Repo"
            bat "C:\\Users\\Administrator\\Documents\\scripts\\psrun.bat engine-init-repo.ps1 ./"
        }
    }
}