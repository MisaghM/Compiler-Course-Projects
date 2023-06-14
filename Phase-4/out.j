.class public Program
.super java/lang/Object

.method public <init>()V
.limit stack 1
.limit locals 1
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

.method public static calculate()I
.limit stack 4
.limit locals 6
    bipush 20
    istore_0
    iconst_4
    istore_1
    iconst_0
    istore_3
    iload_0
    iload_1
    imul
    iload_3
    isub
    istore_3
    iload_0
    iload_1
    idiv
    istore 4
    iload_0
    iconst_3
    irem
    istore 5
    iload_3
    iload 4
    iload 5
    iconst_2
    isub
    iadd
    iadd
    istore_2
    iload_2
    ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 3
.limit locals 2
    invokestatic Program/calculate()I
    istore_1
    getstatic java/lang/System/out Ljava/io/PrintStream;
    iload_1
    invokevirtual java/io/PrintStream/println(I)V
    return
.end method

