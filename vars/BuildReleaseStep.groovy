def call(Map config = [:]){
    if(isUnix()){
        if(config.executableName){
            sh "DOTNET_SYSTEM_GLOBALIZATION_INVARIANT=1 $DOTNET/dotnet build -c Release"
            sh "DOTNET_SYSTEM_GLOBALIZATION_INVARIANT=1 $DOTNET/dotnet publish -c Release --output ./publish/release"
            configFileProvider([configFile(fileId: config.dockerfile ? config.dockerfile : 'dockerfile-be', targetLocation: 'publish/release/Dockerfile', variable: 'dockerfile'), configFile(fileId: 'swagger-xml', targetLocation: "publish/release/${config.executableName}.xml", variable: 'swagger')]) {
                sh "echo ENTRYPOINT [\"dotnet\", \"${config.executableName}.dll\"] >> publish\\release\\Dockerfile"
            }
            
            stash includes: 'publish/**', name: 'app'
        }else{
            def baseHref = config.baseHref ? config.baseHref : "/"
            sh "$NODE/node --max_old_space_size=8048 ./node_modules/@angular/cli/bin/ng build --base-href ${baseHref} --deploy-url ${baseHref}"
            configFileProvider([configFile(fileId: config.dockerfile ? config.dockerfile : 'dockerfile-fe', targetLocation: 'dist/Dockerfile', variable: 'dockerfile'), configFile(fileId: config.nginxconfig ? config.nginxconfig : 'nginx-fe', targetLocation: "dist/default.conf", variable: 'nginx')]) {
                sh "echo env copied"
            }
            stash includes: 'dist/**', name: 'app'
        }
    }else{
        if(config.executableName){
            bat 'dotnet build -c Release'
            bat 'dotnet publish -c Release --output ./publish/release'
            configFileProvider([configFile(fileId: config.dockerfile ? config.dockerfile : 'dockerfile-be', targetLocation: 'publish/release/Dockerfile', variable: 'dockerfile'), configFile(fileId: 'swagger-xml', targetLocation: "publish/release/${config.executableName}.xml", variable: 'swagger')]) {
                bat "echo ENTRYPOINT [\"dotnet\", \"${config.executableName}.dll\"] >> publish\\release\\Dockerfile"
            }
            
            stash includes: 'publish/**', name: 'app'
        }else{
            def baseHref = config.baseHref ? config.baseHref : "/"
            bat "%NODE%/node --max_old_space_size=8048 ./node_modules/@angular/cli/bin/ng build --base-href ${baseHref} --deploy-url ${baseHref}"
            configFileProvider([configFile(fileId: config.dockerfile ? config.dockerfile : 'dockerfile-fe', targetLocation: 'dist/Dockerfile', variable: 'dockerfile'), configFile(fileId: config.nginxconfig ? config.nginxconfig : 'nginx-fe', targetLocation: "dist/default.conf", variable: 'nginx')]) {
                bat "echo env copied"
            }
            stash includes: 'dist/**', name: 'app'
        }
    }
    
}