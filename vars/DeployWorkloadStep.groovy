def call(Map config = [:]){
    if(config.cloudType.notBlank){
        if(config.cloudType == "Google Cloud"){
            withCredentials([file(credentialsId: "${config.credentialsId}", variable: 'FILE')]) {
                bat "gcloud auth activate-service-account ${config.serviceAccountName} --key-file=%FILE%"
                bat "gcloud container clusters get-credentials ${config.clusterName} --zone ${config.zone}"
            }
        }else if(config.cloudType == "AWS CLI"){
            withCredentials([usernamePassword(credentialsId: "${config.credentialsId}", passwordVariable: 'SECRET', usernameVariable: 'KEY')]) {
                sh "aws configure set aws_access_key_id $KEY"
                sh "aws configure set aws_secret_access_key $SECRET"
                sh "aws ecr get-login-password > ~/aws_creds.txt"
                sh "cat ~/aws_creds.txt | docker login --username AWS --password-stdin ${config.registryURL}"
            }
        }

        bat "kubectl apply -f namespace.yaml"

        bat "kubectl apply -f deployment.yaml"
        bat "kubectl apply -f service.yaml"

        if(config.resetConfigmap){
            bat "kubectl apply -f configmap.yaml"
        }

        if(config.autoRestart){
            def deployment = readYaml(file: "deployment.yaml")
            bat "kubectl rollout restart deployments/${deployment.metadata.labels.app} -n ${deployment.metadata.namespace}"
        }
    }
    
}