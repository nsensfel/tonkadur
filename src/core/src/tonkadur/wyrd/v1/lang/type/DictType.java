package tonkadur.wyrd.v1.lang.type;

import java.util.Map;

public class DictType extends Type
{
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
}
