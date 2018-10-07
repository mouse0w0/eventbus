package com.github.mouse0w0.eventbus.asm;

import com.github.mouse0w0.eventbus.Event;
import com.github.mouse0w0.eventbus.WrappedListener;
import com.github.mouse0w0.eventbus.WrappedListenerFactory;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.RETURN;

public final class AsmWrappedListenerFactory implements WrappedListenerFactory {

    public static AsmWrappedListenerFactory create() {
        return new AsmWrappedListenerFactory();
    }

    private static final String wrappedListenerTypeName = WrappedListener.class.getTypeName().replace('.', '/');
    private static final String eventTypeDesc = Type.getDescriptor(Event.class);

    private AtomicInteger uniqueId = new AtomicInteger(1);

    @Override
    public WrappedListener create(Object owner, Method handler, Class<?> eventType) throws Exception {
        Class<?> ownerType = owner.getClass();
        String ownerName = ownerType.getTypeName().replace('.', '/');
        String ownerDesc = Type.getDescriptor(ownerType);
        String eventName = eventType.getTypeName().replace('.', '/');
        String handlerDesc = Type.getMethodDescriptor(handler);
        String handlerName = handler.getName();
        String className = getUniqueName(ownerType.getSimpleName(), handlerName, eventType.getSimpleName());

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", new String[] {wrappedListenerTypeName});

        cw.visitSource(".dynamic", null);

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "owner", ownerDesc, null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                    "(" + ownerDesc + ")V",
                    "(" + ownerDesc + ")V", null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, className, "owner", ownerDesc);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "post", "(" + eventTypeDesc + ")V", null, new String[]{"java/lang/Exception"});
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, className, "owner", ownerDesc);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, eventName);
            mv.visitMethodInsn(INVOKEVIRTUAL, ownerName, handlerName, handlerDesc, false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();

        Class<?> executorType = SafeClassDefiner.INSTANCE.defineClass(ownerType.getClassLoader(), className,
                cw.toByteArray());
        Constructor<?> executorConstructor = executorType.getConstructors()[0];
        return (WrappedListener) executorConstructor.newInstance(owner);
    }

    private String getUniqueName(String ownerName, String handlerName, String eventName) {
        return "AsmDynamicListener_" + Integer.toHexString(this.hashCode()) + "_" + ownerName + "_" + handlerName + "_" + eventName + "_" + uniqueId.getAndIncrement();
    }
}
