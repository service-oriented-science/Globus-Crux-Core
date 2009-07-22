package org.camel.systest;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;

import javax.jws.WebService;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.Oneway;
import javax.jws.WebResult;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;
import javax.xml.ws.Response;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.MTOM;
import javax.xml.namespace.QName;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.annotation.Annotation;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @author turtlebender
 */
public class DummyServiceGenerator {
    boolean writeClassFile = true;

    
}
