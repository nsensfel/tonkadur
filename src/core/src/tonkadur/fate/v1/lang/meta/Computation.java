package tonkadur.fate.v1.lang.meta;

import tonkadur.error.ErrorManager;

import tonkadur.parser.Origin;
import tonkadur.parser.ParsingError;

import tonkadur.fate.v1.error.NotAReferenceException;

import tonkadur.fate.v1.lang.type.Type;
import tonkadur.fate.v1.lang.type.FutureType;

public abstract class Computation extends Node
{
   /***************************************************************************/
   /**** MEMBERS **************************************************************/
   /***************************************************************************/
   protected final Type type;

   /***************************************************************************/
   /**** PROTECTED ************************************************************/
   /***************************************************************************/
   /**** Constructors *********************************************************/
   protected Computation (final Origin origin, final Type type)
   {
      super(origin);

      this.type = type;
   }

   /***************************************************************************/
   /**** PUBLIC ***************************************************************/
   /***************************************************************************/
   /**** Accessors ************************************************************/
   public void get_visited_by (final ComputationVisitor cv)
   throws Throwable
   {
      System.err.println("Unable to visit: " + toString());
   }

   public Type get_type ()
   {
      if (type instanceof FutureType)
      {
         return ((FutureType) type).get_current_type();
      }

      return type;
   }

   public void expect_non_string ()
   throws ParsingError
   {
   }

   public void expect_string ()
   throws ParsingError
   {
   }

   public void use_as_reference ()
   throws ParsingError
   {
      ErrorManager.handle
      (
         new NotAReferenceException(this)
      );
   }

   /**** Misc. ****************************************************************/
   @Override
   public String toString ()
   {
      final StringBuilder sb = new StringBuilder();

      sb.append(origin.toString());
      sb.append("(");
      sb.append(type.get_name());
      sb.append(" Value Node)");

      return sb.toString();
   }
}
