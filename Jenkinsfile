pipeline {
    agent any

        tools {
            jdk 'JDK'
            maven 'Maven'
        }

    stages {

        stage('Build') {
            steps {
                bat 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }
    }

    post {
        always {
            junit testResults: 'target/surefire-reports/*.xml',
                  allowEmptyResults: true

            cucumber(
                  jsonReportDirectory: 'target/cucumber-reports',
                  fileIncludePattern: 'report.json'
            )

            powershell '''
                        $pidFile = "api\\api.pid"

                        if (Test-Path $pidFile) {

                            $apiPid = Get-Content $pidFile

                            Write-Host "Stopping API process $apiPid"

                            Stop-Process -Id $apiPid -Force -ErrorAction SilentlyContinue

                            Remove-Item $pidFile -Force
                        }
                    '''
        }
    }
}