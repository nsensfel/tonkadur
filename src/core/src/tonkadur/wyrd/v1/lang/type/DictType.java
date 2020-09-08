package tonkadur.wyrd.v1.lang.type;

import java.util.Map;
import java.util.HashMap;

public class DictType extends Type
{
   public static final DictType WILD;

   static
   {
      WILD = new DictType("wild dict", new HashMap<String, Type>());
   }

   protected Map<String, Type> fields;

   public DictType (final String name, final Map<String, Type> fields)
   {
      super(name);

      this.fields = fields;
   }

   public Map<String, Type> get_fields ()
   {
      return fields;
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(DictType ");
      sb.append(name);
      sb.append(")");

      return sb.toString();
   }
}
