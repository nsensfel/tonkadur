package tonkadur.wyrd.v1.lang;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class Register
{
   protected final Type type;
   protected final String name;
   protected final Address address;
   protected final Computation value;
   protected boolean is_in_use;

   public Register (final Type type, final String name)
   {
      this.name = name;
      this.type = type;

      address = new Address(new Constant(Type.STRING, name), type);
      value = new ValueOf(address);
      is_in_use = false;
   }

   public Register
   (
      final Address address,
      final Type type,
      final String name
   )
   {
      this.address = address;
      this.name = name;
      this.type = type;

      value = new ValueOf(address);
      is_in_use = false;
   }

   public Type get_type ()
   {
      return type;
   }

   public String get_name ()
   {
      return name;
   }

   public Address get_address ()
   {
      return address;
   }

   public Computation get_value ()
   {
      return value;
   }

   public boolean get_is_in_use ()
   {
      return is_in_use;
   }

   public void set_is_in_use (final boolean val)
   {
      is_in_use = val;
   }
}
