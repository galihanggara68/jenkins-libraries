def call(Map config = [:]){
    withSonarQubeEnv(config.sonarQubeEnv){
        withCredentials([string(credentialsId: config.credentialsId, variable: 'TOKEN')]) {
            if(config.type == "be"){
                bat "dotnet C:/Users/Administrator/Documents/tools/hudson.plugins.sonar.MsBuildSQRunnerInstallation/SonarScanner_.Net_Framework_Core/SonarScanner.MSBuild.dll begin /key:${config.projectKey} /d:sonar.login=%TOKEN%"
                bat 'dotnet build'
                bat "dotnet C:/Users/Administrator/Documents/tools/hudson.plugins.sonar.MsBuildSQRunnerInstallation/SonarScanner_.Net_Framework_Core/SonarScanner.MSBuild.dll end /d:sonar.login=%TOKEN%"
            }else{
                bat "C:\\Users\\Administrator\\Documents\\tools\\hudson.plugins.sonar.SonarRunnerInstallation\\SonarQube_Scanner\\bin\\sonar-scanner.bat -Dsonar.projectKey=${config.projectKey} -Dsonar.login=%TOKEN%"
            }
        }
    }
}