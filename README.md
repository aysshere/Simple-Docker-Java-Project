step1- docker build -t java-todo-no-maven .
else step1 is not run u should have run this comment- DOCKER_BUILDKIT=0 docker build -t java-todo-no-maven .
step2-docker run -p 8080:8080 java-todo-no-maven
after all comment is success we can go localhost http://localhost:8080
"# Simple-Docker-Java-Project" 
