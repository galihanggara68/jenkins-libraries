def call(Map config = [:]){
    if(config.cloudType == "Google Cloud"){
        withCredentials([file(credentialsId: "${config.credentialsId}", variable: 'FILE')]) {
            bat "gcloud auth activate-service-account ${config.serviceAccountName} --key-file=%FILE%"
            bat "gcloud container clusters get-credentials ${config.clusterName} --zone ${config.zone}"

            bat "kubectl apply -f namespace.yaml"

            bat "kubectl apply -f deployment.yaml"
            bat "kubectl apply -f service.yaml"

            if(config.resetConfigmap){
                bat "kubectl apply -f configmap.yaml"
            }

            if(config.autoRestart){
                def deployment = readYaml(file: "deployment.yaml")
                bat "kubectl rollout restart deployments/${deployment.metadata.labels.app}"
            }
        }
    }
}