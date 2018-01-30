"use strict";

var thrift = require('thrift');
var ttypes = require('./thrift-classes/first_types');

let x = new ttypes.IntAndString();
x.name = 'Oliver'
x.count = 100
console.log(x);

let writerTransport = new thrift.TBufferedTransport(
    new Buffer(""),
    msg => console.log(readBuf(msg))
);

let writerProto = new thrift.TBinaryProtocol(writerTransport);

function readBuf(msg) {
    let y = new ttypes.IntAndString();
    let readerTransport = new thrift.TFramedTransport(
        new Buffer(msg)
    )
    let readerProto = new thrift.TBinaryProtocol(readerTransport);

    readerTransport.write(msg);
    y.read(readerProto);
    return y;
}

x.write(writerProto);
writerProto.flush();
