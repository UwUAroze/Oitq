package me.aroze.oitq.util

import me.aroze.oitq.Oitq
import java.io.File
import java.util.jar.JarInputStream

fun getClassesInPackage(pkg: String, predicate: (Class<*>) -> Boolean = { true }): List<Class<*>> {
    val classes = mutableListOf<Class<*>>()
    val instanceClass = Oitq::class.java
    val path = instanceClass.protectionDomain.codeSource.location.toURI()
    val jar = File(path)
    val stream = JarInputStream(jar.inputStream())
    val directory = pkg.replace('.', '/')

    while (true) {
        val entry = stream.nextJarEntry ?: break
        val entryName = entry.name

        if (!entryName.startsWith(directory) || !entryName.endsWith(".class"))
            continue

        val clazz = instanceClass.classLoader.loadClass(entryName
            .replace('/', '.')
            .replace(".class", ""))

        if (!predicate.invoke(clazz))
            continue

        classes.add(clazz)
    }

    return classes
}