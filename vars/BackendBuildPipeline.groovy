def call(Map config = [:]){
  bat "Hello ${config.name}, today is ${config.day}"
}
