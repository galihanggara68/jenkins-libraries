def call(Map config = [:]){
    if(config.cloudType == "Google Cloud"){
        withCredentials([file(credentialsId: "${config.credentialsId}", variable: 'FILE')]) {
            bat "gcloud auth activate-service-account ${config.serviceAccountName} --key-file=%FILE%"
            bat "gcloud container clusters get-credentials ${config.clusterName} --zone ${config.zone}"

            bat "kubectl apply -f deployment.yaml"
            bat "kubectl apply -f service.yaml"
            bat "kubectl apply -f configmap.yaml"
        }
    }
}