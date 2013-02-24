// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 2/23/2013 11:42:25 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

import java.io.*;
import java.rmi.*;
import java.rmi.server.*;

public final class Hello_Skel
    implements Skeleton
{

    public Hello_Skel()
    {
    }

    public void dispatch(Remote remote, RemoteCall remotecall, int i, long l)
        throws Exception
    {
        if(l != 0x38bc97be171621e6L)
            throw new SkeletonMismatchException("interface hash mismatch");
        Hello hello = (Hello)remote;
        switch(i)
        {
        case 0: // '\0'
            String s;
            try
            {
                ObjectInput objectinput = remotecall.getInputStream();
                s = (String)objectinput.readObject();
            }
            catch(IOException ioexception1)
            {
                throw new UnmarshalException("error unmarshalling arguments", ioexception1);
            }
            catch(ClassNotFoundException classnotfoundexception)
            {
                throw new UnmarshalException("error unmarshalling arguments", classnotfoundexception);
            }
            finally
            {
                remotecall.releaseInputStream();
            }
            String s1 = hello.sayHello(s);
            try
            {
                ObjectOutput objectoutput = remotecall.getResultStream(true);
                objectoutput.writeObject(s1);
            }
            catch(IOException ioexception)
            {
                throw new MarshalException("error marshalling return", ioexception);
            }
            break;

        default:
            throw new UnmarshalException("invalid method number");
        }
    }

    public Operation[] getOperations()
    {
        return (Operation[])operations.clone();
    }

    private static final Operation operations[] = {
        new Operation("java.lang.String sayHello(java.lang.String)")
    };
    private static final long interfaceHash = 0x38bc97be171621e6L;

}

