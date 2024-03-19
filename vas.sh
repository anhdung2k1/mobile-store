#! /bin/bash
##
## vas.sh
##--------

# Directory
test -n "$VAS_GIT" || export VAS_GIT=$(pwd -P)
test -n "$BUILD_DIR" || export BUILD_DIR="$VAS_GIT/build"
test -n "$API_DIR" || export API_DIR="$VAS_GIT/authentication/authentication"
test -n "$DOCKER_DIR" || export DOCKER_DIR="$VAS_GIT/docker"
test -n "$DOCKER_REGISTRY" || export DOCKER_REGISTRY="anhdung12399"

# Prequiste compiler
test -n "$MAVEN_IMAGE" || export MAVEN_IMAGE="maven:latest"


prg=$(basename $0) # vas.sh filename
dir=$(dirname $0); dir=$(cd $dir; pwd) #Get root dir
me=$dir/$prg #Get absolutely path vas.sh
vas=$me
#Get the release commit
git_commit=$(git --git-dir="$VAS_GIT/.git" rev-parse --short=7 HEAD)
change_id=$(git show $git_commit | grep '^\ *Change-Id' | awk '{print $2}')
release=$git_commit

# Dataset for Face Detection

clean() {
    echo "Remove build directory..."
    rm -rf "$VAS_GIT/build"
    echo "Remove sucessfully"
}

die() {
    echo "ERROR: $1" >&2
    exit 1
}

help() {
    grep '^##' $0 | cut -c3-
    exit 0
}

test -n "$1" || help
echo "$1" | grep -qi "^help\|-h" && help

dir_est() {
    echo "Create [BUILD, VARIABLES] Folder"
    mkdir "$BUILD_DIR"
    mkdir "$BUILD_DIR"/var
}

get_version() {
    suffix=$(git rev-parse HEAD | sed 's/^0*//g' | cut -c1-7 | tr 'a-f' '1-6')
    suffix+=$(git diff --quiet && git diff --cached --quiet || echo '9999')
    echo "$(<$VAS_GIT/VERSION_PREFIX)-${suffix}"
}

## build_image
## Build docker image from Dockerfile
##
## --name=<module name>
##
build_image() {
    test -n "$VAS_GIT" || die "Not set [VAS_GIT]"
    test -n "$__name" || die "Module name required"
    image_name=mb-$__name

    version=$(get_version)

    #remove the docker images before create new ones
    docker rmi -f $image_name:$version
    docker build $VAS_GIT/docker/$__name \
            --file $VAS_GIT/docker/$__name/Dockerfile \
            --tag "$DOCKER_REGISTRY/$image_name:$version" \
            --build-arg COMMIT=$git_commit \
            --build-arg APP_VERSION=$version \
            --build-arg BUILD_TIME=`date +"%d/%m/%Y:%H:%M:%S"` \
        || die "Failed to build docker images: $__name"
    
    ## Clean target file if exists
    if [[ $__name == "authentication" ]]; then
        rm -rf $DOCKER_DIR/$__name/*.jar
    fi
}

## save_image
## Save image from local build repository
##
## --name=<module name>
##
save_image() {
    test -n "$VAS_GIT" || die "Not set [VAS_GIT]"
    test -n "$__name" || die "Module name required"
    image_name=mb-$__name

    mkdir -p $BUILD_DIR/images
    cd $BUILD_DIR/images
    version=$(get_version)

    echo "Save image: $image_name"
    rm -rf ${image_name}:$version.tgz && rm -rf ${image_name}:$version.sha256
    docker save $DOCKER_REGISTRY/${image_name}:$version \
            | gzip -vf - > ${image_name}-$version.tgz
    sha256sum "${image_name}-$version.tgz" > "${image_name}-$version.sha256"
    cat "${image_name}-$version.sha256"
}

## build docker images and run docker container
## Run docker authentication and socket-server container
##
## --name=<module name>
##
build_repo() {
    test -n "$VAS_GIT" || die "Not set [VAS_GIT]"
    test -n "$__name" || die "Module name required"
    COMMON_DB="mobile"
    image_name=mb-$__name
    version=$(get_version)

    echo "##################################"
    echo "# Prepare the docker local build : #"
    echo "##################################"

    case $__name in
    "authentication")
        echo "Start to build Spring boot compile"
        # To compile the Spring boot, must start the mysql docker for temporaly -> remove after build
        
        mysql_container=$(docker ps -a --format "{{.Names}}" | grep -i mysql_container)
        if [[ -n "$mysql_container" ]]; then
            docker rm -f mysql_container
        fi
        # Start docker mysql container
        docker run -d --name mysql_container \
            -e MYSQL_ROOT_PASSWORD=root \
            -e MYSQL_DATABASE=${COMMON_DB} \
            -e MYSQL_USER=${COMMON_DB} \
            -e MYSQL_PASSWORD=${COMMON_DB} \
            -p 3306:3306 \
            mysql:latest \
        || die "[ERROR]: Failed to run mysql docker"
        mysql_IP=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' mysql_container)
        echo $mysql_IP
        chmod +x $VAS_GIT/test/application.properties
        cp -f $VAS_GIT/test/application.properties $API_DIR/src/main/resources/application.properties

        sed -i -e "s/REPLACE_WITH_DB_IP/${mysql_IP}/g" $API_DIR/src/main/resources/application.properties
        sed -i -e "s/REPLACE_WITH_DB_COMMON/${COMMON_DB}/g" $API_DIR/src/main/resources/application.properties

        ## Move to API directory
        pushd .
        cd $API_DIR

        echo "Start to build Spring boot compile"
        docker run -it --rm -v "$(pwd -P)":/app \
            -w /app \
            -e DB_HOST=${mysql_IP} \
            -e DB_USERNAME=${COMMON_DB} \
            -e DB_NAME=${COMMON_DB} \
            -e DB_PASSWORD=${COMMON_DB} \
            $MAVEN_IMAGE mvn clean install -Dskiptest \
            || die "[ERROR]: Failed to compile"

        rm -rf $API_DIR/src/main/resources/application.properties
        cp -f $API_DIR/target/*.jar $DOCKER_DIR/$__name/ \
            || die "Target file does not exists in $API_DIR/target/"
        popd

        docker rm -f $__name

        $vas build_image --name=$__name
        docker run -it -d --name $__name \
                ${DOCKER_REGISTRY}/${image_name}:${version} \
                || die "[ERROR]: Failed to compile"
    ;;
    "socket-server")
        echo "Copy folder $__name to docker"
        cp -rf $VAS_GIT/$__name/ $DOCKER_DIR/$__name \
            || die "Source directory does not exists $VAS_GIT/$__name"
        echo "Start to build socket-server docker image"
        #Need to build image first
        server_image=$(docker images | awk '$1 {print $1}' | grep -v -w "REPOSITORY" | grep -i "${DOCKER_REGISTRY}/${image_name}")
        if [[ -n "${server_image}" ]]; then 
            docker rmi -f ${server_image}:$version
        fi

        docker rm -f $__name
        
        API_HOST=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' authentication)
        mysql_IP=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' mysql_container)
        echo $API_HOST
        $vas build_image --name=$__name
        docker run -it -d --name $__name \
                -e API_HOST=${API_HOST} \
                -e DB_HOST=${mysql_IP} \
                -e DB_USERNAME=${COMMON_DB} \
                -e DB_NAME=${COMMON_DB} \
                -e DB_PASSWORD=${COMMON_DB} \
                ${DOCKER_REGISTRY}/${image_name}:${version} \
                || die "[ERROR]: Failed to compile"
    esac
}

## Push image
## Push docker image to Docker Registry
##
## --name=<module name>
##
push_image() {
   test -n "$VAS_GIT" || die "Not set [VAS_GIT]"
   test -n "$__name" || die "Module name required"
   test -n "$DOCKER_REGISTRY" || die "Not set [DOCKER_REGISTRY]"
   image_name=mb-$__name
   version=$(get_version)

   ## Docker push to docker registry
   docker push $DOCKER_REGISTRY/$image_name:$version \
	   || die "Failed to push docker registry: $DOCKER_REGISTRY"
}

#Get the command
cmd=$1
shift
grep -q "^$cmd()" $0 || die "Invalid command [$cmd]"

while echo "$1" | grep -q '^--'; do
    if echo $1 | grep -q =; then
        o=$(echo "$1" | cut -d= -f1 | sed -e 's,-,_,g')
        v=$(echo "$1" | cut -d= -f2-)
        eval "$o=\"$v\""
    else
        o=$(echo "$1" | sed -e 's,-,_,g')
		eval "$o=yes"
    fi
    shift
done
unset o
long_opts=`set | grep '^__' | cut -d= -f1`

#Execute command
trap "die Interrupted" INT TERM
$cmd "$@"
status=$?
exit $status
