.PHONY: up
up:
	@podman compose up;

.PHONY: down
down:
	@podman compose down --volumes;
