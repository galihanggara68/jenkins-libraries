def call(Map config = [:]){
    if(config.cloudType == "Alibaba Cloud"){
        docker.withRegistry("${config.registryURL}", "${config.credentialsId}") {
            dockerImageRemote = docker.build "${config.imageName}:build-${env.BUILD_ID}"
            dockerImageRemote.push()
            dockerImageRemote.push("cloud")
        }
    }else if(config.cloudType == "Google Cloud"){
        withCredentials([file(credentialsId: "${config.credentialsId}", variable: 'FILE')]) {
            sh "cat $FILE | docker login -u _json_key --password-stdin ${config.registryURL}"
        }
        dockerImageRemote = docker.build "${config.imageName}:build-${env.BUILD_ID}"
        dockerImageRemote.push()
        dockerImageRemote.push("cloud")
    }else if(config.cloudType == "AWS"){
        echo "AWS Provider is under maintenance or unavailable"
    }else if(config.cloudType == "Azure"){
        echo "Azure Provider is under maintenance or unavailable"
    }
}