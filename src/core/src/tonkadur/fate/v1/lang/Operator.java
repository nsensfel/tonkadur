package tonkadur.fate.v1.lang;

import java.util.Collections;
import java.util.Set;

public enum Operator
{
   PLUS("+", 0, Type.NUMBER_TYPES, null),
   MINUS("-", 0, Type.NUMBER_TYPES, null),
   TIMES("*", 0, Type.NUMBER_TYPES, null),
   DIVIDED("/", 0, Type.NUMBER_TYPES, null),
   POWER("^", 2, Type.NUMBER_TYPES, null),
   RANDOM("rand", 2, Collections.singleton(Type.INT), null),

   AND("and", 0, Collections.singleton(Type.BOOLEAN), null),
   OR("or", 0, Collections.singleton(Type.BOOLEAN), null),
   NOT("not", 1, Collections.singleton(Type.BOOLEAN), null),
   IMPLIES("implies", 1, Collections.singleton(Type.BOOLEAN), null),

   LOWER_THAN("<", 2, Type.NUMBER_TYPES, Type.BOOLEAN),
   LOWER_EQUAL_THAN("=<", 2, Type.NUMBER_TYPES, Type.BOOLEAN),
   GREATER_EQUAL_THAN(">=", 2, Type.NUMBER_TYPES, Type.BOOLEAN),
   GREATER_THAN(">", 2, Type.NUMBER_TYPES, Type.BOOLEAN);

   final private String name;
   final private int arity;
   final private Set<Type> valid_input_types;
   final private Type output_type_transform;

   private Operator
   (
      final String name,
      final int arity,
      final Set<Type> valid_input_types,
      final Type output_type_transform
   )
   {
      this.name = name;
      this.arity = arity;
      this.valid_input_types = valid_input_types;

      this.output_type_transform = output_type_transform;
   }

   public Set<Type> get_allowed_base_types ()
   {
      return valid_input_types;
   }

   public int get_arity ()
   {
      return arity;
   }

   public Type transform_type (final Type in)
   {
      if (output_type_transform == null)
      {
         return in;
      }

      return output_type_transform;
   }

   @Override
   public String toString ()
   {
      return name;
   }
}
