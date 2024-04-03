RELEASE := false
USERNAME := $(USER)
VAS_GIT := $(shell git rev-parse --show-toplevel)
BUILD_DIR := $(VAS_GIT)/build
VAR_DIR := $(BUILD_DIR)/var

# Clean the repository
clean:
	@echo "Clean Repository"
	./vas.sh clean
# init the repository
init:
	@echo "mkdir build and variables directory"
	./vas.sh dir_est
	@echo "Get version"
	./vas.sh get_version > $(VAR_DIR)/.version
	@echo "Get commit hash"
	git rev-parse --short=7 HEAD > $(VAR_DIR)/.version
	@echo "Generate release version"
	@git tag | grep -v + | sort -V | tail -1 | sed 's/-/+/g' > $(VAR_DIR)/.released-version

#Build process 
build: build-authentication \
	build-socket-server 

build-authentication:
	@echo "build authentication Repository"
	./vas.sh build_repo --name=authentication
	sleep 5
build-socket-server:
	@echo "build socket-server"
	./vas.sh build_repo --name=socket-server

image: image-authentication \
	image-socket-server 

image-authentication:
	@echo "build authentication Image"
	./vas.sh build_image --name=authentication
	./vas.sh save_image --name=authentication
image-socket-server:
	@echo "build socket-server Image"
	./vas.sh build_image --name=socket-server
	./vas.sh save_image --name=socket-server

push: push-authentication \
	push-socket-server 

push-authentication:
	@echo "push image-authentication"
	./vas.sh push_image --name=authentication
push-socket-server:
	@echo "push image-socket-server"
	./vas.sh push_image --name=socket-server