pipeline {
    agent any

        tools {
            jdk 'JDK'
            maven 'Maven'
        }

    stages {

        stage('Checkout API') {
            steps {
                script {

                    def apiBranch

                    if (env.BRANCH_NAME == 'metadataTests') {
                        apiBranch = 'features/metadata'
                    } else {
                        apiBranch = 'main'
                    }

                    echo "QA branch: ${env.BRANCH_NAME}"
                    echo "API branch: ${apiBranch}"

                    dir('api') {
                        git branch: apiBranch,
                            url: 'https://github.com/imaginethinking/file-processor'
                    }
                }
            }
        }

        stage('Build API') {
            steps {
                dir('api'){
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Start API') {
            steps {
                dir('api') {
                    powershell '''
                        $jar = Get-ChildItem -Path "file-processing-service\\target" -Filter *.jar |
                            Where-Object { $_.Name -notlike "*.original" } |
                            Select-Object -First 1

                        if (-not $jar) {
                            throw "No API JAR found in file-processing-service/target"
                        }

                        Write-Host "Starting API: $($jar.FullName)"

                        $process = Start-Process `
                            -FilePath "java" `
                            -ArgumentList "-jar", $jar.FullName `
                            -PassThru

                        $process.Id | Out-File -FilePath api.pid

                        Write-Host "API started with process ID $($process.Id)"
                    '''
                }
            }
        }

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