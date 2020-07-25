package tonkadur.wyrd.v1.lang;

import tonkadur.wyrd.v1.lang.type.Type;

public class Variable
{
   protected final String scope;
   protected final String name;
   protected final Type type;

   public Variable (final String name, final String scope, final Type type)
   {
      this.name = name;
      this.scope = scope;
      this.type = type;
   }

   public String get_name ()
   {
      return name;
   }

   public String get_scope ()
   {
      return scope;
   }

   public Type get_type ()
   {
      return type;
   }

   public Ref get_ref ()
   {
      return
         new Ref
         (
            Collections.singletonList(new Constant(Type.STRING, name)),
            type
         );
   }
}
