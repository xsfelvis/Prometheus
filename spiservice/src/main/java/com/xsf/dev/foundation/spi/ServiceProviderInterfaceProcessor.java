package com.xsf.dev.foundation.spi;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.xsf.dev.spiannotation.ServiceProviderInterface;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;

/**
 * @author xsf
 *
 * @ServiceProviderInterface()
 * public interface PrivateServiceInterface {
 *     String getName();
 *
 *     String getSex();
 * }
 * 有关apt相关知识可以参考 https://xsfelvis.github.io/2018/06/06/%E8%B0%88%E8%B0%88APT%E5%92%8CJavaPoet%E7%9A%84%E4%B8%80%E4%BA%9B%E6%8A%80%E5%B7%A7/#more
 */
@AutoService(Processor.class)
public class ServiceProviderInterfaceProcessor extends AbstractProcessor {

    private Elements utils;
    private Logger logger;


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ServiceProviderInterface.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.utils = processingEnv.getElementUtils();
        logger = new Logger(processingEnv.getMessager());
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        for (final Element element : roundEnv.getElementsAnnotatedWith(ServiceProviderInterface.class)) { //解析注解元素
            if (element instanceof TypeElement) {
                final TypeElement typeElement = (TypeElement) element;
                final String packageName = getPackageName(typeElement); //获取包名
                //获取类名的几种方式
                final ClassName clazzSpi = ClassName.get(typeElement); //获取: PrivateServiceInterface
                final ClassName clazzLoader = ClassName.get(getClass().getPackage().getName(), "ServiceLoader"); //获取: ServiceLoader
                final ClassName clazzService = ClassName.get(packageName, getServiceName(typeElement)); //获取 PrivateServiceInterfaceService
                final ClassName clazzSingleton = ClassName.get(packageName, clazzService.simpleName(), "Singleton"); //获取 Singleton
                //MethodSpec 代表一个构造函数或方法声明。
                //TypeSpec 代表一个类，接口，或者枚举声明。
                //FieldSpec 代表一个成员变量，一个字段声明。
                //JavaFile包含一个顶级类的Java文件。
                final TypeSpec.Builder tsb = TypeSpec.classBuilder(clazzService)
                        .addJavadoc("Represents the service of {@link $T}\n", clazzSpi)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addSuperinterface(clazzSpi)
                        .addType(TypeSpec.classBuilder(clazzSingleton.simpleName())
                                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                                .addField(FieldSpec.builder(clazzService, "INSTANCE", Modifier.STATIC, Modifier.FINAL)
                                        .initializer("new $T()", clazzService)
                                        .build())
                                .build())
                        .addField(FieldSpec.builder(clazzSpi, "mDelegate", Modifier.PRIVATE, Modifier.FINAL)
                                .initializer("$T.load($T.class).get()", clazzLoader, clazzSpi)
                                .build())
                        .addMethod(MethodSpec.constructorBuilder()
                                .addModifiers(Modifier.PRIVATE)
                                .build())
                        .addMethod(MethodSpec.methodBuilder("getInstance")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                .addStatement("return $T.INSTANCE", clazzSingleton)
                                .returns(clazzService)
                                .build());

                //System.out.println("Generate " + clazzService.toString());
                logger.info("Generate " + clazzService.toString());
                //提取接口的函数
                for (final ExecutableElement method : ElementFilter.methodsIn(typeElement.getEnclosedElements())) {
                    System.out.println(" + " + method);
                    final String methodName = method.getSimpleName().toString();
                    //与描述Java程序中元素的信息，即Elment的元信息
                    final TypeMirror returnType = method.getReturnType();
                    final MethodSpec.Builder msb = MethodSpec.methodBuilder(methodName)
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                            .addAnnotation(Override.class)
                            .returns(TypeName.get(returnType));

                    for (final TypeMirror thrownType : method.getThrownTypes()) {
                        msb.addException(ClassName.get(thrownType));
                    }

                    final StringBuilder args = new StringBuilder();
                    final List<? extends VariableElement> parameterTypes = method.getParameters();
                    for (int i = 0, n = parameterTypes.size(); i < n; i++) {
                        final String argName = "arg" + i;
                        msb.addParameter(TypeName.get(parameterTypes.get(i).asType()), argName, Modifier.FINAL);
                        args.append(argName).append(i < n - 1 ? ", " : "");
                    }

                    switch (returnType.getKind()) {
                        case BOOLEAN:
                            msb.addStatement("return null != this.mDelegate && this.mDelegate.$L($L)", methodName, args);
                            break;
                        case BYTE:
                            msb.addStatement("return null != this.mDelegate ? this.mDelegate.$L($L) : (byte) 0", methodName, args);
                            break;
                        case SHORT:
                            msb.addStatement("return null != this.mDelegate ? this.mDelegate.$L($L) : (short) 0", methodName, args);
                            break;
                        case INT:
                        case FLOAT:
                        case LONG:
                        case DOUBLE:
                            msb.addStatement("return null != this.mDelegate ? this.mDelegate.$L($L) : 0", methodName, args);
                            break;
                        case CHAR:
                            msb.addStatement("return null != this.mDelegate ? this.mDelegate.$L($L) : '\0'", methodName, args);
                            break;
                        case VOID:
                            msb.beginControlFlow("if (null != this.mDelegate)")
                                    .addStatement("this.mDelegate.$L($L)", methodName, args)
                                    .endControlFlow();
                            break;
                        default:
                            msb.addStatement("return null != this.mDelegate ? this.mDelegate.$L($L) : null", methodName, args);
                            break;
                    }

                    tsb.addMethod(msb.build());
                }

                try {
                    JavaFile.builder(getPackageName(typeElement), tsb.build())
                            .indent("    ")
                            .addFileComment("\nAutomatically generated file. DO NOT MODIFY\n")
                            .skipJavaLangImports(true)
                            .build()
                            .writeTo(processingEnv.getFiler());
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    private String getServiceName(final TypeElement typeElement) {
        final String simpleName = typeElement.getSimpleName().toString();
        if (simpleName.endsWith("ServiceProvider")) {
            return simpleName.substring(0, simpleName.length() - 8);
        }

        return simpleName + "Service";
    }

    private String getPackageName(final TypeElement typeElement) {
        return this.utils.getPackageOf(typeElement).getQualifiedName().toString();
    }

}
