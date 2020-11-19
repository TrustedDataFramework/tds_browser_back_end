all: build

build:
	@docker build --tag=wisdomchain/tds_psql .

release: build
	@docker build --tag=wisdomchain/tds_psql:$(shell cat VERSION) .
