package tonkadur.wyrd.v1.lang.computation;

import java.util.List;

import tonkadur.wyrd.v1.lang.type.Type;

import tonkadur.wyrd.v1.lang.meta.Computation;
import tonkadur.wyrd.v1.lang.meta.ComputationVisitor;

public class ExtraComputation extends Computation
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final String name;
   protected final List<Computation> parameters;

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public ExtraComputation
   (
      final Type type,
      final String name,
      final List<Computation> parameters
   )
   {
      super(type);

      this.name = name;
      this.parameters = parameters;
   }

   /**** Accessors ************************************************************/
   public String get_name ()
   {
      return name;
   }

   public List<Computation> get_parameters ()
   {
      return parameters;
   }

   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_extra_computation(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb;

      sb = new StringBuilder();

      sb.append("(ExtraComputation ");
      sb.append(name);

      for (final Computation param: parameters)
      {
         sb.append(" ");
         sb.append(param.toString());
      }

      sb.append(")");

      return sb.toString();
   }
}
