.PHONY: up
up:
	@podman compose up;

.PHONY: down
down:
	@podman compose down --volumes;

.PHONY: init_server
init_server:
	@podman run --rm -it -v ./server:/workspace:rw,Z -w /workspace -u 1000:1000 --userns=keep-id \
		docker.io/library/gradle:9.8.2-jdk25 gradle init;
