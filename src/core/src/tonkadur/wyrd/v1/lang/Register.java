package tonkadur.wyrd.v1.lang;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.computation.Constant;
import tonkadur.wyrd.v1.lang.computation.Address;
import tonkadur.wyrd.v1.lang.computation.RelativeAddress;
import tonkadur.wyrd.v1.lang.computation.ValueOf;

import tonkadur.wyrd.v1.lang.meta.Computation;

public class Register
{
   protected final String name;
   protected final Address address_prefix;
   protected Address address;
   protected Computation value;
   protected Type current_type;

   public Register (final String name)
   {
      this.name = name;

      address_prefix = null;
      address = null;
      value = null;
      current_type = Type.INT;
   }

   public Register (final Address address_prefix, final String name)
   {
      this.address_prefix = address_prefix;
      this.name = name;

      address = null;
      value = null;
      current_type = Type.INT;
   }

   public Type get_type ()
   {
      return current_type;
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

   public boolean is_active ()
   {
      return (address != null);
   }

   public void activate (final Type type)
   {
      this.current_type = type;

      if (address_prefix == null)
      {
         address = new Address(new Constant(Type.STRING, name), type);
      }
      else
      {
         address =
            new RelativeAddress
            (
               address_prefix,
               new Constant(Type.STRING, name),
               type
            );
      }

      value = new ValueOf(address);
   }

   public void deactivate ()
   {
      address = null;
      value = null;
   }
}
