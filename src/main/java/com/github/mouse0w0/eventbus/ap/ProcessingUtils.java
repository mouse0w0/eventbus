package com.github.mouse0w0.eventbus.ap;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class ProcessingUtils {

    public static boolean hasModifier(Element element, Modifier modifier) {
        return element.getModifiers().stream().anyMatch(m -> modifier == m);
    }

    public static Name getQualifiedName(TypeMirror typeMirror) {
        return ((TypeElement) ((DeclaredType) typeMirror).asElement()).getQualifiedName();
    }
}
