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
        withCredentials([file(credentialsId: "${config.credentialsId}", variable: 'FILE')]) {
            sh "cat $FILE | docker login --username AWS --password-stdin ${config.registryURL}"
        }
        dockerImageRemote = docker.build "${config.imageName}:build-${env.BUILD_ID}"
        dockerImageRemote.push()
        dockerImageRemote.push("cloud")
    }else if(config.cloudType == "AWS CLI"){
        if(!env.AWS_DEFAULT_REGION.notBlank){
            env.AWS_DEFAULT_REGION = config.regionId
        }
        withCredentials([usernamePassword(credentialsId: "${config.credentialsId}", passwordVariable: 'SECRET', usernameVariable: 'KEY')]) {
            sh "aws configure set aws_access_key_id $KEY"
            sh "aws configure set aws_secret_access_key $SECRET"
            sh "aws ecr get-login-password > ~/aws_creds.txt"
            sh "cat ~/aws_creds.txt | docker login --username AWS --password-stdin ${config.registryURL}"
        }
        dockerImageRemote = docker.build "${config.imageName}:build-${env.BUILD_ID}"
        dockerImageRemote.push()
        dockerImageRemote.push("cloud")
    }else if(config.cloudType == "Azure"){
        echo "Azure Provider is under maintenance or unavailable"
    }
}