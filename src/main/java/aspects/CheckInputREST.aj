package aspects;

import aspects.annotations.ProtoInput;
import com.google.protobuf.GeneratedMessageV3.Builder;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import messages.server.ConnectionInfoMsgOuterClass;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//This aspect check if the input of the aspect is correct
public aspect CheckInputREST {

    private pointcut checkProto(InputStream inputStream, ProtoInput protoInput):
            execution(* *..*.*(..)) && !within(aspects.CheckInputREST) && args(inputStream)
            && @annotation(protoInput);

    Response around(InputStream inputStream, ProtoInput protoInput): checkProto(inputStream, protoInput){
        try {
            //Get the builder of the class passed
            Builder builder = (Builder) protoInput.proto().getDeclaredMethod("newBuilder").invoke(new Object[0]);

            //Parser of th class contained in protoInput
            Parser parser = ((Parser) protoInput.proto().getDeclaredMethod("parser").invoke(new Object[0]));
            Object message = parser.parseFrom(inputStream);

            //if the messages are equals the input is wrong
            if (builder.build().equals(message))
                return Response.status(400).build();
        } catch (IllegalAccessException ex) {
            System.out.println(ex);
            return Response.status(500).build();
        } catch (InvocationTargetException ex) {
            System.out.println(ex);
            return Response.status(500).build();
        } catch (NoSuchMethodException ex) {
            System.out.println(ex);
            return Response.status(500).build();
        }
        catch (IOException ex){
            System.out.println(ex);
            return Response.status(500).build();
        }
        System.out.println("corretto");
        return proceed(inputStream, protoInput);
    }
}
