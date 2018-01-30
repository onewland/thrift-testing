all:
	thrift --out src/main/java --gen java:generated_annotations=suppress first.thrift
	thrift --out src/js/thrift-classes --gen js:node first.thrift
	thrift --out src/ruby/thrift-classes --gen rb first.thrift