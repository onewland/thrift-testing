all:
	thrift --out src/main/java --gen java:generated_annotations=suppress first.thrift
	thrift -r --gen 'js:node' first.thrift
	thrift --out src/ruby/thrift-classes --gen rb first.thrift