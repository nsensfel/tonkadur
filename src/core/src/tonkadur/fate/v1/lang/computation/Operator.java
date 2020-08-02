package tonkadur.fate.v1.lang.computation;

import java.util.Collections;
import java.util.Set;

import tonkadur.fate.v1.lang.type.Type;

/*
 * Yes, it *could* have been an enum. In fact, it used to be one. Except that
 * unless you want to ensure coverage of all cases in a switch, Java enums are
 * clearly inferior to classes in everyway. Having this be a class will, at
 * the very least, let you extend it to add new operators in extensions.
 */
public class Operator
{
   public static final Operator PLUS;
   public static final Operator MINUS;
   public static final Operator TIMES;
   public static final Operator DIVIDE;
   public static final Operator MODULO;
   public static final Operator POWER;
   public static final Operator RANDOM;

   public static final Operator AND;
   public static final Operator OR;
   public static final Operator NOT;
   public static final Operator IMPLIES;
   public static final Operator ONE_IN;

   public static final Operator EQUALS;

   public static final Operator LOWER_THAN;
   public static final Operator LOWER_EQUAL_THAN;
   public static final Operator GREATER_EQUAL_THAN;
   public static final Operator GREATER_THAN;


   static
   {
      PLUS = new Operator("+", 2, 0, Type.NUMBER_TYPES, null);
      MINUS = new Operator("-", 2, 0, Type.NUMBER_TYPES, null);
      TIMES = new Operator("*", 2, 0, Type.NUMBER_TYPES, null);
      DIVIDE = new Operator("/", 2, 2, Type.NUMBER_TYPES, null);
      POWER = new Operator("^", 2, 2, Type.NUMBER_TYPES, null);
      MODULO = new Operator("%", 2, 2, Collections.singleton(Type.INT), null);
      RANDOM =
         new Operator("rand", 2, 2, Collections.singleton(Type.INT), null);

      AND =
         new Operator("and", 2, 0, Collections.singleton(Type.BOOLEAN), null);
      OR =
         new Operator("or", 2, 0, Collections.singleton(Type.BOOLEAN), null);
      NOT =
         new Operator("not", 1, 1, Collections.singleton(Type.BOOLEAN), null);
      IMPLIES =
         new Operator
         (
            "implies",
            2,
            2,
            Collections.singleton(Type.BOOLEAN),
            null
         );
      ONE_IN =
         new Operator
         (
            "one_in",
            1,
            0,
            Collections.singleton(Type.BOOLEAN),
            null
         );

      EQUALS =
         new Operator("equals", 2, 0, Type.ALL_TYPES, Type.BOOLEAN);
      LOWER_THAN =
         new Operator("<", 2, 2, Type.SIMPLE_BASE_TYPES, Type.BOOLEAN);
      LOWER_EQUAL_THAN =
         new Operator("=<", 2, 2, Type.SIMPLE_BASE_TYPES, Type.BOOLEAN);
      GREATER_EQUAL_THAN =
         new Operator(">=", 2, 2, Type.SIMPLE_BASE_TYPES, Type.BOOLEAN);
      GREATER_THAN =
         new Operator(">", 2, 2, Type.SIMPLE_BASE_TYPES, Type.BOOLEAN);
   }

   final protected String name;
   final protected int min_arity;
   final protected int max_arity;
   final protected Set<Type> valid_input_types;
   final protected Type output_type_transform;

   protected Operator
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

   public String get_name ()
   {
      return name;
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
      return get_name();
   }
}
