"use strict";

var thrift = require('thrift');
var ttypes = require('./thrift-classes/first_types');
var amqp = require('amqplib');

amqp.connect("amqp://guest:guest@localhost:5672");

async function consumeMessages() {
    let conn = await amqp.connect('amqp://localhost');
    let ch = await conn.createChannel();
    let q = 'abcdefghi';
    let ok = await ch.assertQueue(q, {durable: false, autoDelete: true});

    ch.consume(q, msg => {
        let y = new ttypes.IntAndString();
        let readerTransport = new thrift.TFramedTransport(
            new Buffer(msg.content)
        );
        let readerProto = new thrift.TBinaryProtocol(readerTransport);
        y.read(readerProto);
        console.log(y);
    }, {noAck: true});
}

consumeMessages();