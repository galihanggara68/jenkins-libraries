def call(Map config = [:]){
  bat "echo Hello ${config.name}, today is ${config.day}"
}
