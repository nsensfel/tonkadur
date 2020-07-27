package tonkadur.fate.v1.lang.computation;

import tonkadur.parser.Origin;

import tonkadur.fate.v1.lang.meta.ComputationVisitor;
import tonkadur.fate.v1.lang.meta.Reference;

import tonkadur.fate.v1.lang.type.Type;

public class ParameterReference extends Reference
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   public ParameterReference
   (
      final Origin origin,
      final Type reported_type,
      final String parameter_name
   )
   {
      super(origin, reported_type, parameter_name);
   }

   /**** Accessors ************************************************************/
   @Override
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      cv.visit_parameter_reference(this);
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(ParameterReference (");
      sb.append(type.get_name());
      sb.append(") ");
      sb.append(name);
      sb.append(")");

      return sb.toString();
   }
}
