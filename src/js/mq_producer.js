"use strict";

var thrift = require('thrift');
var ttypes = require('./thrift-classes/first_types');
var amqp = require('amqplib');

amqp.connect("amqp://guest:guest@localhost:5672");

async function generateMessages(messageCount)  {
    let conn = await amqp.connect('amqp://localhost');
    let ch = await conn.createChannel();
    let q = 'abcdefghi';
    let ok = await ch.assertQueue(q, {durable: false, autoDelete: true});

    for(let i = 0; i < messageCount; i++) {
        let x = new ttypes.IntAndString();
        x.name = 'Oliver';
        x.count = i;

        let writerTransport = new thrift.TBufferedTransport(
            new Buffer(""),
            msg => ch.sendToQueue(q, msg)
        );
        let writerProto = new thrift.TBinaryProtocol(writerTransport);
        x.write(writerProto);
        writerProto.flush();
    }

    setTimeout(function() { conn.close(); process.exit(0) }, 500);
}

generateMessages(100);