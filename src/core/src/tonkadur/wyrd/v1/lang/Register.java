package tonkadur.wyrd.v1.lang;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Address;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class Register
{
   protected final Type type;
   protected final String scope;
   protected final String name;
   protected final Address address;
   protected final Computation value;

   public Register (final Type type, final String scope, final String name)
   {
      this.name = name;
      this.scope = scope;
      this.type = type;

      address = new Address(new Constant(Type.STRING, name), type);
      value = new ValueOf(address);
   }

   public Register
   (
      final Address address,
      final Type type,
      final String scope,
      final String name
   )
   {
      this.address = address;
      this.name = name;
      this.scope = scope;
      this.type = type;

      value = new ValueOf(address);
   }

   public Type get_type ()
   {
      return type;
   }

   public String get_name ()
   {
      return name;
   }

   public String get_scope ()
   {
      return scope;
   }

   public Address get_address ()
   {
      return address;
   }

   public Computation get_value ()
   {
      return value;
   }
}
