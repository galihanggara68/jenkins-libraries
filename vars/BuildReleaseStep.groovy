def call(Map config = [:]){
    if(config.executableName){
        bat 'dotnet build -c Release'
        bat 'dotnet publish -c Release --output ./publish/release'
        configFileProvider([configFile(fileId: config.dockerfile ? config.dockerfile : 'dockerfile-be', targetLocation: 'publish/release/Dockerfile', variable: 'dockerfile'), configFile(fileId: 'swagger-xml', targetLocation: "publish/release/${config.executableName}.xml", variable: 'swagger')]) {
            bat "echo ENTRYPOINT [\"dotnet\", \"${config.executableName}.dll\"] >> publish\\release\\Dockerfile"
        }
        
        stash includes: 'publish/**', name: 'app'
    }else{
        bat "%TOOLS%\\jenkins.plugins.nodejs.tools.NodeJSInstallation\\node14\\node --max_old_space_size=8048 ./node_modules/@angular/cli/bin/ng build --configuration=https_env --base-href /"
        configFileProvider([configFile(fileId: config.dockerfile ? config.dockerfile : 'dockerfile-fe', targetLocation: 'dist/Dockerfile', variable: 'dockerfile'), configFile(fileId: config.nginxconfig ? config.nginxconfig : 'nginx-fe', targetLocation: "dist/default.conf", variable: 'nginx')]) {
            bat "env copied"
        }
        stash includes: 'dist/**', name: 'app'
    }
}