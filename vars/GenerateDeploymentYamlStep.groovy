def call(Map config = [:]){
    configFileProvider([configFile(fileId: 'kube-deployment-yaml', targetLocation: './deployment.yaml', variable: 'deployment'), configFile(fileId: 'kube-service-yaml', targetLocation: './service.yaml', variable: 'service'), configFile(fileId: 'kube-configmap-yaml', targetLocation: './configmap.yaml', variable: 'configmap')]) {
        def matchers = ~/.*-(frontend|fe)/
        config.type = config.type ? config.type : (matchers.matcher(config.deploymentName).matches() ? "fe" : "be")
        
        // Namespace
        Map namespace = [apiVersion: "v1", kind: "Namespace", metadata: [name: config.namespace]]
        writeYaml(data: namespace, file: "namespace.yaml")
        
        // Deployment
        def deployment = readYaml(file: 'deployment.yaml')
        deployment.metadata.name = config.deploymentName
        deployment.metadata.namespace = config.namespace
        deployment.metadata.labels.app = config.deploymentName
        deployment.spec.selector.matchLabels.app = config.deploymentName
        deployment.spec.template.metadata.labels.app = config.deploymentName
        deployment.spec.template.spec.volumes[0].name = config.deploymentName+"-volume"
        deployment.spec.template.spec.volumes[0].configMap.name = config.deploymentName+"-appsettings"
        deployment.spec.template.spec.containers[0].name = config.deploymentName
        deployment.spec.template.spec.containers[0].image = config.imageName
        deployment.spec.template.spec.containers[0].volumeMounts[0].name = config.deploymentName+"-volume"
        deployment.spec.template.spec.containers[0].volumeMounts[0].mountPath = (config.configContainerPath ? config.configContainerPath : (config.type == 'fe' ? "/usr/share/nginx/html/assets/config/${config.configMapFileName}" : "/app/${config.configMapFileName}"))
        deployment.spec.template.spec.containers[0].volumeMounts[0].subPath = config.configMapFileName
        
        bat "del deployment.yaml"
        writeYaml(data: deployment, file: "deployment.yaml")

        // Service
        def service = readYaml(file: 'service.yaml')
        service.metadata.name = config.deploymentName
        service.metadata.namespace = config.namespace
        service.spec.selector.app = config.deploymentName
        service.spec.ports[0].port = config.port
        service.spec.ports[0].targetPort = config.targetPort
        service.spec.type = config.serviceType
        
        bat "del service.yaml"
        writeYaml(data: service, file: "service.yaml")

        // ConfigMap
        def configmap = readYaml(file: 'configmap.yaml')
        configmap.metadata.name = config.deploymentName+"-appsettings"
        configmap.metadata.namespace = config.namespace
        def data = config.configPath ? readFile(config.configPath) : "{}"
        Map configData = [(config.configMapFileName): data]
        configmap.data = configData
        
        
        bat "del configmap.yaml"
        writeYaml(data: configmap, file: "configmap.yaml")

        bat "type deployment.yaml"
        bat "type service.yaml"
        bat "type configmap.yaml"
    }
}