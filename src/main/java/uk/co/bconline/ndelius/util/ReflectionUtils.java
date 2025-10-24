package uk.co.bconline.ndelius.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@UtilityClass
public class ReflectionUtils {

    /*
     * Replace class-level annotation data using reflection. Tested in Java 8 and 11.
     * See https://rationaleemotions.wordpress.com/2016/05/27/changing-annotation-values-at-runtime/
     */
    public static void replaceClassLevelAnnotation(Class annotatedClass,
                                                   Class<? extends Annotation> annotationToReplace,
                                                   Annotation newAnnotation) throws ReflectiveOperationException {
        // Class has a private method called annotationData().
        // We first need to invoke it to obtain a reference to AnnotationData class which is a private class
        Method method = Class.class.getDeclaredMethod("annotationData");
        method.setAccessible(true);
        // Since AnnotationData is a private class we cannot create a direct reference to it. We will have to
        // manage with just Object
        Object annotationData = method.invoke(annotatedClass);
        // We now look for the map called "annotations" within AnnotationData object.
        Field annotations = annotationData.getClass().getDeclaredField("annotations");
        annotations.setAccessible(true);
        Map<Class<? extends Annotation>, Annotation> map =
            (Map<Class<? extends Annotation>, Annotation>) annotations.get(annotationData);
        map.put(annotationToReplace, newAnnotation);
    }

    public static List<Class<?>> findAllAnnotatedClasses(String packageName, Class<? extends Annotation> annotationType) {
        final List<Class<?>> result = new ArrayList<>();
        final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
        provider.addIncludeFilter(new AnnotationTypeFilter(annotationType));

        for (BeanDefinition beanDefinition : provider.findCandidateComponents(packageName)) {
            try {
                result.add(Class.forName(beanDefinition.getBeanClassName()));
            } catch (ClassNotFoundException e) {
                log.warn("Could not resolve class object for bean definition", e);
            }
        }
        return result;
    }
}
