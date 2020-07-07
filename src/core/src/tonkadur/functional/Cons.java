package tonkadur.functional;

public class Cons <T0, T1>
{
   protected T0 car;
   protected T1 cdr;

   public Cons (final T0 car, final T1 cdr)
   {
      this.car = car;
      this.cdr = cdr;
   }

   public T0 get_car ()
   {
      return car;
   }

   public T1 get_cdr ()
   {
      return cdr;
   }

   public void set_car (final T0 car)
   {
      this.car = car;
   }

   public void set_cdr (final T1 cdr)
   {
      this.cdr = cdr;
   }

   @Override
   public boolean equals (final Object o)
   {
      if (o instanceof Cons)
      {
         final Cons c;

         c = (Cons) o;

         return (c.car.equals(car) && c.cdr.equals(cdr));
      }

      return false;
   }

   @Override
   public int hashCode ()
   {
      return toString().hashCode();
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("(cons ");
      sb.append(car.toString());
      sb.append(" ");
      sb.append(cdr.toString());
      sb.append(")");

      return sb.toString();
   }
}
