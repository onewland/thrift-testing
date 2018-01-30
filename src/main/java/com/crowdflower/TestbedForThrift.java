package com.crowdflower;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TMemoryBuffer;

public class TestbedForThrift {
    public static void main(String[] args) throws TException {
        IntAndString ias = new IntAndString("abc", 1);
        System.out.println(ias);

        TMemoryBuffer tMemoryBuffer = new TMemoryBuffer(1024);
        TBinaryProtocol tbp = new TBinaryProtocol(tMemoryBuffer);

        ias.write(tbp);
        System.out.println(tMemoryBuffer.inspect());

        IntAndString ias2 = new IntAndString();
        ias2.read(tbp);
        System.out.println(ias2);
    }
}
