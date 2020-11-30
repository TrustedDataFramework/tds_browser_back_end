all: build

build:
	@docker build --tag=registry.cn-hangzhou.aliyuncs.com/tdf_hangzhou/tds_psql .

release: build
	@docker build --tag=registry.cn-hangzhou.aliyuncs.com/tdf_hangzhou/tds_psql:$(shell cat VERSION) .
