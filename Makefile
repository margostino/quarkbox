#!make
SHELL = /bin/sh
.DEFAULT: help

-include .env .env.local .env.*.local

# Defaults
BUILD_VERSION ?= SNAPSHOT
IMAGE_NAME := ${DOCKER_REPO}/${SERVICE_NAME}:${BUILD_VERSION}
IMAGE_NAME_LATEST := ${DOCKER_REPO}/${SERVICE_NAME}:latest
DOCKER_COMPOSE = USERID=$(shell id -u):$(shell id -g) docker-compose ${compose-files}
ALL_ENVS := local ci
env ?= local
quarkbox-instances ?= 1
docker-snapshot ?= true

ifndef SERVICE_NAME
$(error SERVICE_NAME is not set)
endif

ifeq (${env}, ci)
compose-files=-f docker-compose.yml -f docker-compose.ci.yml
endif

.PHONY: help
help:
	@echo "QuarkBox build pipeline"
	@echo ""
	@echo "Usage:"
	@echo "  build                          - Build artifact"
	@echo "  test.unit                      - Run unit tests"
	@echo "  test.integration               - Run integration tests"
	@echo "  test.api                       - Run api tests"
	@echo "  test.resiliency                - Run resiliency tests"
	@echo "  docker.publish                 - Publish docker image (used for internal/external testing purposes) to artifactory. Receives parameter docker-snapshot (default true)"
	@echo "  docker.wait                    - Waits until all docker containers have exited successfully and/or are healthy. Timeout: 180 seconds"
	@echo "  docker.logs                    - Generate one log file per each service running in docker-compose"
	@echo "  git.tag                        - Creates a new tag and pushes it to the git repository. Used to tag the current commit as a released artifact"
	@echo ""
	@echo "  ** The following tasks receive an env parameter to determine the environment they are being executed in. Default env=${env}, possible env values: ${ALL_ENVS}:"
	@echo "  docker.run.all                 - Run QuarkBox service and all it's dependencies with docker-compose (default env=${env})"
	@echo "  docker.run.dependencies        - Run only QuarkBox dependencies with docker-compose (default env=${env})". Note that `build` might need to be executed prior.
	@echo "  docker.run.quarkbox            - Build and run QuarkBox container in detached mode. Recreate it if already running (default env=${env})"
	@echo "  docker.run.quarkbox.debug      - Build and run QuarkBox container in un-detached mode. Recreate it if already running (default env=${env})"
	@echo "  docker.stop                    - Stop and remove all running containers from this project using docker-compose down (default env=${env})"
	@echo ""
	@echo "Project-level environment variables are set in .env file:"
	@echo "  SERVICE_NAME=quarkbox"
	@echo "  DOCKER_PROJECT_NAME=quarkbox"
	@echo "  COMPOSE_PROJECT_NAME=quarkbox"
	@echo "  DOCKER_REPO=quarkbox"
	@echo "  COMPOSE_HTTP_TIMEOUT=360"
	@echo ""
	@echo "Note: Store protected environment variables in .env.local or .env.*.local"
	@echo ""

.PHONY: build
build: b.clean b.build

.PHONY: build.native
build.native: b.clean b.build.native

.PHONY: docker.build.jvm
docker.build.jvm: b.clean b.build d.build.jvm

.PHONY: docker.build.native
docker.build.native: b.clean b.build.native d.build.native

b.clean:
	./gradlew -no-build-cache clean

b.build:
	quarkus build

b.build.native:
	quarkus build -Dquarkus.profile=native

.PHONY: test.unit
test.unit:
	./gradlew test

.PHONY: test.integration
test.integration:
	./gradlew cleanIntegrationTest integrationTest

.PHONY: test.api
test.api:
	### TODO

.PHONY: test.resiliency
test.resiliency:
	### TODO

.PHONY: docker.run.all
docker.run.all: d.compose.down
	make d.compose.up docker-profile=native
	make docker.wait
	docker-compose ps

.PHONY: docker.run.dependencies
docker.run.dependencies: d.compose.down
	make d.compose.up docker-profile=dev
	make docker.wait
	docker-compose up -d
	docker-compose ps

.PHONY: docker.run.quarkbox
docker.run.quarkbox:
	$(call DOCKER_COMPOSE) up -d --force-recreate --build quarkbox
	docker-compose ps

.PHONY: docker.stop
docker.stop: d.compose.down

.PHONY: d.compose.up
d.compose.up:
	$(call DOCKER_COMPOSE) --profile ${docker-profile} up -d --remove-orphans --build

.PHONY: d.compose.down
d.compose.down:
	$(call DOCKER_COMPOSE) down -v || true
	$(call DOCKER_COMPOSE) rm --force || true
	docker rm "$(docker ps -a -q)" -f || true

### ------------------------
### Pipeline's utility tasks
### ------------------------

.PHONY: docker.publish
docker.publish:
ifeq (${docker-snapshot}, true)
docker.publish: d.publish.snapshot
else
docker.publish: d.publish
endif

d.publish.snapshot:
	$(info Publishing docker image: '${IMAGE_NAME}-SNAPSHOT' to artifactory)
	make build d.login
	make d.build.native image-name=${IMAGE_NAME}-SNAPSHOT
	make d.push image-name=${IMAGE_NAME}-SNAPSHOT

d.publish:
	$(info Publishing docker image: '${IMAGE_NAME}' and '${IMAGE_NAME_LATEST}' to artifactory)
	@if [ ${BUILD_VERSION} = "SNAPSHOT" ]; then printf "\033[91mBuild version can't be SNAPSHOT\033[0m\n"; exit 1; fi
	make d.login d.snapshot.tag.latest
	make d.push image-name=${IMAGE_NAME}
	make d.push image-name=${IMAGE_NAME_LATEST}

d.login:
	docker login -u ${DOCKER_USER} ${DOCKER_PASSWORD} ${DOCKER_REPO}

d.build.jvm:
	docker build -f src/main/docker/Dockerfile.jvm -t ${IMAGE_NAME_LATEST} .

d.build.native:
	docker build -f src/main/docker/Dockerfile.native -t ${IMAGE_NAME_LATEST} .

d.push:
	docker push ${image-name}

d.snapshot.tag.latest:
	docker tag ${IMAGE_NAME}-SNAPSHOT ${IMAGE_NAME}
	docker tag ${IMAGE_NAME}-SNAPSHOT ${IMAGE_NAME_LATEST}

.PHONY: docker.wait
docker.wait:
	./bin/docker-wait

.PHONY: docker.logs
docker.logs:
	./bin/docker-logs

.PHONY: git.tag
git.tag: g.tag g.push

g.tag:
	git tag -a -m ${BUILD_VERSION} ${BUILD_VERSION} $(git rev-parse HEAD)

g.push:
	git push origin refs/tags/${BUILD_VERSION}:refs/tags/${BUILD_VERSION}