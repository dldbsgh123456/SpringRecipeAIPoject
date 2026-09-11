pipeline {
	agent any
	environment {
		APP_DIR= "~/app"
		JAR_NAME= "SpringRecipeAIPoject-0.0.1-SNAPSHOT.jar"
		DOCKER_IMAGE = "leeyunho1234/ai-app:latest"
		SERVER_USER= "ubuntu"
		SERVER_IP= "16.184.48.221"
		APP_DIR= "/home/ubuntu/app"
	}
	stages {
		/*
		    git push = commit (main) 
		       |
		    web hook / poll
		       |
		     Jenkins (local) = EC2
		       |
		     build 
		       |
		     docker build 
		     docker push 
		        |
		     docker pull 
		     docker run
		     
		     -name: = stage 
		      run: 명령어 => steps
		*/
		/*
		    Repository : 소스파일 => Git URL
		*/
		stage('Check Out') {
			steps {
				echo 'Git Checkout'
				checkout scm
			}
		}
		// 2. Java = JDK 확인
		stage("JDK21 확인"){
			steps {
				sh '''
					java -version
					./gradlew --version
				   '''
			}
		}
		// 임시 
		
		// gradlew build => permission  처리 
		stage('Gradlew Permission'){
			steps {
				sh '''
				    chmod +x gradlew
				   '''
			}
		}
		
		// gradlew build
		stage('Gradlew Build'){
			steps {
				sh '''
				    ./gradlew clean build -x test
				   '''
			}
		}
		// Docker Build 
		stage('Docker Build'){
			steps {
				sh '''
				     docker build -t leeyunho1234/ai-app:latest .
				   '''
			}
		}
		// DockerHub Login
		stage('DockerHub Login'){
			steps {
				withCredentials([usernamePassword(
					credentialsId: 'dockerhub_info',
					usernameVariable: 'DH_USER',
					passwordVariable: 'DH_PASS'
				)]){
					sh '''
					    echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin
					   '''
				}
			}
		}
		
		stage('Docker Push'){
			steps {
				sh '''
				    docker push leeyunho1234/ai-app:latest
				   '''
			}
		}
		// 8. SSH KEY 설정 SERVER_SSH_KEY
		stage('SSH Key Setting'){
			steps {
				withCredentials([
					sshUserPrivateKey(
						credentialsId: 'SERVER_SSH_KEY',
						keyFileVariable: 'SSH_KEY',
						usernameVariable: 'SSH_USER'
					)
				]){
					sh '''
						mkdir -p ~/.ssh
						cp "$SSH_KEY" ~/.ssh/id_ed25519
						chmod 600 ~/.ssh/id_ed25519
					   '''
				}
			}
		}
		// 9. AWS 접근
		stage("Known Hosts"){
			steps {
				sh '''
					mkdir -p ~/.ssh
					ssh-keyscan -H 16.184.48.221 >> ~/.ssh/known_hosts
					
					chmod 644 ~/.ssh/known_hosts
				   '''
			}
		}
		// .env 생성
		stage('Create .env') {
			steps {
				withCredentials([
					string(
						credentialsId: 'post-url',
						variable: 'POST_URL'
					),
					string(
						credentialsId: 'gen-key',
						variable: 'GEN_KEY'
					),
					sshUserPrivateKey(
						credentialsId: 'SERVER_SSH_KEY',
						keyFileVariable: 'SSH_KEY',
						usernameVariable: 'SSH_USER'
					)
				]){
					sh '''
						ssh -i "$SSH_KEY" -o StrickHostKeyChecking=no ubuntu@16.184.48.221<<EOF
						mkdir -p /home/ubuntu/app
						
						cd /home/ubuntu/app
						
						rm -f .env
				        echo "SPRING_PROFILES_ACTIVE=prod" > .env
			            echo "POST_URL=${POST_URL}" >> .env
			            echo "GEN_KEY=${GEN_KEY}" >> .env
			            
			            chmod 600 .env
			            
			            EOF
					   '''
				}
			}
		}
		// 8. docker-compose.yml 이동
		stage("Copy Docker-Compose"){
			steps {
				withCredentials([
					string(
						credentialsId: 'post-url',
						variable: 'POST_URL'
					),
					string(
						credentialsId: 'gen-key',
						variable: 'GEN_KEY'
					),
					sshUserPrivateKey(
						credentialsId: 'SERVER_SSH_KEY',
						keyFileVariable: 'SSH_KEY',
						usernameVariable: 'SSH_USER'
					)
				]){
					sh '''
						ssh -i "$SSH_KEY" -o StrickHostKeyChecking=no ubuntu@16.184.48.221 "mkdir -p /home/ubuntu/app"
						
						scp -i "$SSH_KEY" -o StrickHostKeyChecking=no ubuntu@16.184.48.221 docker-compose.yml ubuntu@16.184.48.221:/home/ubuntu/app/docker-compose.yml
					   '''
				}
			}
		}
		stage("Deploy"){
			steps {
				withCredentials([
					string(
						credentialsId: 'post-url',
						variable: 'POST_URL'
					),
					string(
						credentialsId: 'gen-key',
						variable: 'GEN_KEY'
					),
					sshUserPrivateKey(
						credentialsId: 'SERVER_SSH_KEY',
						keyFileVariable: 'SSH_KEY',
						usernameVariable: 'SSH_USER'
					)
				]){
					sh '''
						ssh -i "$SSH_KEY" -o StrickHostKeyChecking=no ubuntu@16.184.48.221<<EOF
						cd /home/ubuntu/app
						docker-compose down
						docker-compose pull
						docker-compose up -d
						
						EOF
					   '''
				}
			}
		}
	}
		post {
	success {
		echo '==================='
		echo 'Docker Compose 배포 성공'
		echo '==================='
	   }
	failure {
		echo '==================='
		echo 'Docker Compose 배포 실패'
		echo '==================='
		sh '''
			docker compose ps || true
		   '''
	}
 }
}