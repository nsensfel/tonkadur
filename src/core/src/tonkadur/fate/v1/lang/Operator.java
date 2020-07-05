package tonkadur.fate.v1.lang;

import java.util.Collections;
import java.util.Set;

public enum Operator
{
   PLUS("+", 2, 0, Type.NUMBER_TYPES, null),
   MINUS("-", 2, 0, Type.NUMBER_TYPES, null),
   TIMES("*", 2, 0, Type.NUMBER_TYPES, null),
   DIVIDE("/", 2, 2, Type.NUMBER_TYPES, null),
   POWER("^", 2, 2, Type.NUMBER_TYPES, null),
   RANDOM("rand", 2, 2, Collections.singleton(Type.INT), null),

   AND("and", 2, 0, Collections.singleton(Type.BOOLEAN), null),
   OR("or", 2, 0, Collections.singleton(Type.BOOLEAN), null),
   NOT("not", 1, 1, Collections.singleton(Type.BOOLEAN), null),
   IMPLIES("implies", 2, 2, Collections.singleton(Type.BOOLEAN), null),
   ONE_IN("one_in", 1, 0, Collections.singleton(Type.BOOLEAN), null),

   EQUALS("equals", 2, 0, Type.COLLECTION_COMPATIBLE_TYPES, Type.BOOLEAN),

   LOWER_THAN("<", 2, 2, Type.NUMBER_TYPES, Type.BOOLEAN),
   LOWER_EQUAL_THAN("=<", 2, 2, Type.NUMBER_TYPES, Type.BOOLEAN),
   GREATER_EQUAL_THAN(">=", 2, 2, Type.NUMBER_TYPES, Type.BOOLEAN),
   GREATER_THAN(">", 2, 2, Type.NUMBER_TYPES, Type.BOOLEAN);

   final private String name;
   final private int min_arity;
   final private int max_arity;
   final private Set<Type> valid_input_types;
   final private Type output_type_transform;

   private Operator
   (
      final String name,
      final int min_arity,
      final int max_arity,
      final Set<Type> valid_input_types,
      final Type output_type_transform
   )
   {
      this.name = name;
      this.min_arity = min_arity;
      this.max_arity = max_arity;
      this.valid_input_types = valid_input_types;

      this.output_type_transform = output_type_transform;
   }

   public Set<Type> get_allowed_base_types ()
   {
      return valid_input_types;
   }

   public int get_minimum_arity ()
   {
      return min_arity;
   }

   public int get_maximum_arity ()
   {
      return max_arity;
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
