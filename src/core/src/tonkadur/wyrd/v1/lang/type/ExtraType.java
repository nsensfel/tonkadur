package tonkadur.wyrd.v1.lang.type;

public class ExtraType extends Type
{
   public ExtraType (final String name)
   {
      super(name);
   }

   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(ExtraType ");
      sb.append(name);
      sb.append(")");

      return sb.toString();
   }
}
