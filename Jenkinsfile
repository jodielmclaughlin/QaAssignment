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
                    withEnv(['JENKINS_NODE_COOKIE=dontKillAPI']) {
                        powershell '''
                            $jar = Get-ChildItem -Path . -Filter "*.jar" -Recurse |
                                Where-Object {
                                    $_.FullName -match "\\\\target\\\\" -and
                                    $_.Name -notlike "*.original"
                                } |
                                Select-Object -First 1

                            if (-not $jar) {
                                throw "No API JAR found"
                            }

                            Write-Host "Starting API JAR:"
                            Write-Host $jar.FullName

                            $logFile = Join-Path $PWD "api-output.log"
                            $errorFile = Join-Path $PWD "api-error.log"

                            $process = Start-Process `
                                -FilePath "java" `
                                -ArgumentList "-jar", "`"$($jar.FullName)`"", "--server.port=8080" `
                                -RedirectStandardOutput $logFile `
                                -RedirectStandardError $errorFile `
                                -PassThru

                            $process.Id | Out-File "api.pid"

                            Write-Host "Started API process $($process.Id)"
                        '''
                    }
                }
            }
        }

        stage('Wait for API') {
            steps {
                powershell '''
                    $ready = $false

                    for ($i = 1; $i -le 30; $i++) {

                        Write-Host "Checking API - attempt $i of 30"

                        if (Test-NetConnection localhost -Port 8080 -InformationLevel Quiet) {
                            Write-Host "API is ready on localhost:8080"
                            $ready = $true
                            break
                        }

                        Start-Sleep -Seconds 2
                    }

                    if (-not $ready) {

                        Write-Host "API did not start"

                        if (Test-Path "api\\api-output.log") {
                            Write-Host "=== API OUTPUT ==="
                            Get-Content "api\\api-output.log"
                        }

                        if (Test-Path "api\\api-error.log") {
                            Write-Host "=== API ERRORS ==="
                            Get-Content "api\\api-error.log"
                        }

                        throw "API did not become available on localhost:8080"
                    }
                '''
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