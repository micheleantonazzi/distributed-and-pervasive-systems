package aspects.annotations;

import com.google.protobuf.GeneratedMessageV3;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//This annotation is used to defined the proto accepted by a rest method
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProtoInput {
    Class<? extends GeneratedMessageV3> proto();
}
