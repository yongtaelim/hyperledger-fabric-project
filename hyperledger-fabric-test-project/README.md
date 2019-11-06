# README #

* hyperledger-api 프로젝트 설치 및 설정 정보
	- 하이퍼레저와 통신
	- tomcat Ver : 8.53
	- PORT : 8082
	- dev 설정파일 경로 추가 
		- /home/ubuntu/hyperledger-api/bin/catalina.sh
	 	- 262라인에 추가: JAVA_OPTS="$JAVA_OPTS -Ddtso=dev"
	
* JDK 설치
	# sudo apt-get update
	# sudo apt-get install openjdk-8-jdk

* 프로세스 확인 : 
	# ps -ef | grep hyperledger-api

* 소스 업로드 및 실행 방법 : 
	1. 이클립스에서 war 파일로 export
	2. /home/ubuntu/hyperledger-api/webapps 위치에 ROOT.war 파일로 업로드
	3. /home/ubuntu/hyperledger-api/bin/startup.sh 실행
