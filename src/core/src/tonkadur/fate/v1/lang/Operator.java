package tonkadur.fate.v1.lang;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public enum Operator
{
   PLUS("+", 0, Type.NUMBER_TYPES, null),
   MINUS("-", 0, Type.NUMBER_TYPES, null),
   TIMES("*", 0, Type.NUMBER_TYPES, null),
   DIVIDED("/", 0, Type.NUMBER_TYPES, null),
   POWER("^", 2, Type.NUMBER_TYPES, null),
   RANDOM("rand", 2, (new Type[]{Type.INT}), null),

   AND("and", 0, (new Type[]{Type.BOOLEAN}), null),
   OR("or", 0, (new Type[]{Type.BOOLEAN}), null),
   NOT("not", 1, (new Type[]{Type.BOOLEAN}), null),
   IMPLIES("implies", 1, (new Type[]{Type.BOOLEAN}), null),

   LOWER_THAN("<", 2, Type.NUMBER_TYPES, Type.BOOLEAN),
   LOWER_EQUAL_THAN("=<", 2, Type.NUMBER_TYPES, Type.BOOLEAN),
   GREATER_EQUAL_THAN(">=", 2, Type.NUMBER_TYPES, Type.BOOLEAN),
   GREATER_THAN(">", 2, Type.NUMBER_TYPES, Type.BOOLEAN);

   final private String name;
   final private int arity;
   final private Collection<Type> valid_input_types;
   final private Type output_type_transform;

   private Operator
   (
      final String name,
      final int arity,
      final Type[] valid_input_types,
      final Type output_type_transform
   )
   {
      this.name = name;
      this.arity = arity;
      this.valid_input_types =
         new HashSet<Type>(Arrays.asList(valid_input_types));

      this.output_type_transform = output_type_transform;
   }

   public Collection<Type> get_allowed_base_types ()
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
