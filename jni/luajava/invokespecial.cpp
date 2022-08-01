/*
 * This file implements luaJ_invokespecial
 */

// Lua pollutes the global namespaces way too much, so we have to avoid ::std usages.
#include <cstring>

// java.lang.Boolean
jclass    java_lang_boolean_class   = NULL;
jmethodID boolean_boolvalue         = NULL;
jmethodID boolean_constructor       = NULL;
// java.lang.Character
jclass    java_lang_character_class = NULL;
jmethodID character_charvalue       = NULL;
jmethodID character_constructor     = NULL;
// java.lang.Number
jclass    java_lang_number_class    = NULL;
jmethodID number_bytevalue          = NULL;
jmethodID number_doublevalue        = NULL;
jmethodID number_floatvalue         = NULL;
jmethodID number_intvalue           = NULL;
jmethodID number_longvalue          = NULL;
jmethodID number_shortvalue         = NULL;

// Manual boxing classes
jclass    java_lang_byte_class      = NULL;
jmethodID byte_constructor          = NULL;
jclass    java_lang_double_class    = NULL;
jmethodID double_constructor        = NULL;
jclass    java_lang_float_class     = NULL;
jmethodID float_constructor         = NULL;
jclass    java_lang_integer_class   = NULL;
jmethodID integer_constructor       = NULL;
jclass    java_lang_long_class      = NULL;
jmethodID long_constructor          = NULL;
jclass    java_lang_short_class     = NULL;
jmethodID short_constructor         = NULL;

/**
 * @return -1 on failure
 */
int initBoxingBindings(JNIEnv * env) {
  java_lang_boolean_class   = bindJavaClass(env, "java/lang/Boolean");
  java_lang_character_class = bindJavaClass(env, "java/lang/Character");
  java_lang_number_class    = bindJavaClass(env, "java/lang/Number");
  java_lang_byte_class      = bindJavaClass(env, "java/lang/Byte");
  java_lang_double_class    = bindJavaClass(env, "java/lang/Double");
  java_lang_float_class     = bindJavaClass(env, "java/lang/Float");
  java_lang_integer_class   = bindJavaClass(env, "java/lang/Integer");
  java_lang_long_class      = bindJavaClass(env, "java/lang/Long");
  java_lang_short_class     = bindJavaClass(env, "java/lang/Short");

  if (java_lang_boolean_class      == NULL
      || java_lang_character_class == NULL
      || java_lang_number_class    == NULL
      || java_lang_byte_class      == NULL
      || java_lang_double_class    == NULL
      || java_lang_float_class     == NULL
      || java_lang_integer_class   == NULL
      || java_lang_long_class      == NULL
      || java_lang_short_class     == NULL) {
    return -1;
  }

  boolean_boolvalue   = bindJavaMethod(env, java_lang_boolean_class,
          "booleanValue", "()Z");

  character_charvalue = bindJavaMethod(env, java_lang_character_class,
          "charValue",    "()C");

  number_bytevalue    = bindJavaMethod(env, java_lang_number_class,
          "byteValue",    "()B");
  number_doublevalue  = bindJavaMethod(env, java_lang_number_class,
          "doubleValue",  "()D");
  number_floatvalue   = bindJavaMethod(env, java_lang_number_class,
          "floatValue",   "()F");
  number_intvalue     = bindJavaMethod(env, java_lang_number_class,
          "intValue",     "()I");
  number_longvalue    = bindJavaMethod(env, java_lang_number_class,
          "longValue",    "()J");
  number_shortvalue   = bindJavaMethod(env, java_lang_number_class,
          "shortValue",   "()S");

  if (boolean_boolvalue      == NULL
      || character_charvalue == NULL
      || number_bytevalue    == NULL
      || number_doublevalue  == NULL
      || number_floatvalue   == NULL
      || number_intvalue     == NULL
      || number_longvalue    == NULL
      || number_shortvalue   == NULL) {
    return -1;
  }

  boolean_constructor   = bindJavaMethod(env, java_lang_boolean_class,
          "<init>", "(Z)V");
  character_constructor = bindJavaMethod(env, java_lang_character_class,
          "<init>", "(C)V");
  byte_constructor      = bindJavaMethod(env, java_lang_byte_class,
          "<init>", "(B)V");
  double_constructor    = bindJavaMethod(env, java_lang_double_class,
          "<init>", "(D)V");
  float_constructor     = bindJavaMethod(env, java_lang_float_class,
          "<init>", "(F)V");
  integer_constructor   = bindJavaMethod(env, java_lang_integer_class,
          "<init>", "(I)V");
  long_constructor      = bindJavaMethod(env, java_lang_long_class,
          "<init>", "(J)V");
  short_constructor     = bindJavaMethod(env, java_lang_short_class,
          "<init>", "(S)V");

  if (byte_constructor       == NULL
      || double_constructor  == NULL
      || float_constructor   == NULL
      || integer_constructor == NULL
      || long_constructor    == NULL
      || short_constructor   == NULL) {
    return -1;
  }

  return 0;
}

jvalue convertFromJobject(JNIEnv * env, jobject obj, char target) {
  jvalue value;
  if (obj == NULL) {
    value.l = NULL;
    return value;
  }
  switch (target) {
    case 'V': // Void
      value.l = NULL;
      break;
    case 'Z': // Boolean
      value.z = env->CallBooleanMethod(obj, boolean_boolvalue);
      break;
    case 'C': // Character
      value.c = env->CallCharMethod(obj, character_charvalue);
      break;
    case 'B': // Number: Byte
      value.b = env->CallByteMethod(obj, number_bytevalue);
      break;
    case 'D': // Number: Double
      value.d = env->CallDoubleMethod(obj, number_doublevalue);
      break;
    case 'F': // Number: Float
      value.f = env->CallFloatMethod(obj, number_floatvalue);
      break;
    case 'I': // Number: Integer
      value.i = env->CallIntMethod(obj, number_intvalue);
      break;
    case 'J': // Number: Long
      value.j = env->CallLongMethod(obj, number_longvalue);
      break;
    case 'S': // Number: Short
      value.s = env->CallShortMethod(obj, number_shortvalue);
      break;
    default:
      value.l = obj;
      break;
  }
  return value;
}

// We return -1 instead of lua_error,
// since we allow luaJ_invokespecial to be called in an unprotected environment.
#define RETURN_IF_ERROR(env, L) \
    if (checkIfError(env, L)) { return -1; }

#define CALL_NONVIRTUAL(TYPE, FIELD, CLAZZ, CONSTRUCTOR)                    \
    v.FIELD = env->CallNonvirtual##TYPE##MethodA(obj, clazz, method, args); \
    RETURN_IF_ERROR(env, L);                                                \
    v.l = env->NewObject(CLAZZ, CONSTRUCTOR, v.FIELD);                      \
    luaJ_pushobject(env, L, v.l);                                           \
    break

int luaJ_invokespecial(JNIEnv * env, lua_State * L,
                       jclass clazz, const char * methodName, const char * sig,
                       jobject obj, const char * params) {
  jmethodID method = bindJavaMethod(env, clazz, methodName, sig);
  // The last character signifies the type of return value
  int paramCount = std::strlen(params) - 1;
  char returnType = params[paramCount];

  // No, it is not standard C++, but using `new` would easily leak memory since lua_error longjmps away.
  jvalue args[paramCount];
  for (int i = 0; i < paramCount; ++i) {
    // Arguments are pushed on stack in reverse order
    args[i] = convertFromJobject(env, luaJ_toobject(L, -i - 1), params[i]);
    RETURN_IF_ERROR(env, L);
  }
  lua_pop(L, paramCount);

  jvalue v;
  switch (returnType) {
    case 'V':
      env->CallNonvirtualVoidMethodA(obj, clazz, method, args);
      return checkIfError(env, L) ? -1 : 0;
    case 'Z': // Boolean
      CALL_NONVIRTUAL(Boolean, z, java_lang_boolean_class,   boolean_constructor);
    case 'C': // Character
      CALL_NONVIRTUAL(Char,    c, java_lang_character_class, character_constructor);
    case 'B': // Number: Byte
      CALL_NONVIRTUAL(Byte,    b, java_lang_byte_class,      byte_constructor);
    case 'D': // Number: Double
      CALL_NONVIRTUAL(Double,  d, java_lang_double_class,    double_constructor);
    case 'F': // Number: Float
      CALL_NONVIRTUAL(Float,   f, java_lang_float_class,     float_constructor);
    case 'I': // Number: Integer
      CALL_NONVIRTUAL(Int,     i, java_lang_integer_class,   integer_constructor);
    case 'J': // Number: Long
      CALL_NONVIRTUAL(Long,    j, java_lang_long_class,      long_constructor);
    case 'S': // Number: Short
      CALL_NONVIRTUAL(Short,   s, java_lang_short_class,     short_constructor);
    default:
      v.l = env->CallNonvirtualObjectMethodA(obj, clazz, method, args);
      RETURN_IF_ERROR(env, L);
      luaJ_pushobject(env, L, v.l);
      break;
  }

  return 1;
}